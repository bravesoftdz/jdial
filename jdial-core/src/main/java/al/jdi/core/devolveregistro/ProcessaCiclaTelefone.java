package al.jdi.core.devolveregistro;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.slf4j.Logger;

import al.jdi.core.modelo.Ligacao;
import al.jdi.core.modelo.Providencia;
import al.jdi.core.modelo.Providencia.ClienteSemTelefoneException;
import al.jdi.core.modelo.Providencia.NaoPodeReiniciarRodadaTelefoneException;
import al.jdi.core.modelo.Providencia.SemProximoTelefoneException;
import al.jdi.core.modelo.Providencia.SomenteCelularException;
import al.jdi.core.tenant.Tenant;
import al.jdi.core.tratadorespecificocliente.TratadorEspecificoCliente;
import al.jdi.dao.beans.DaoFactory;
import al.jdi.dao.model.Cliente;
import al.jdi.dao.model.ResultadoLigacao;

class ProcessaCiclaTelefone implements ProcessoDevolucao {

  private static final Logger logger = getLogger(ProcessaCiclaTelefone.class);

  private final TratadorEspecificoCliente.Factory tratadorEspecificoClienteFactory;
  private final ProcessaFimDaFila processaFimDaFila;
  private final Map<Providencia.Codigo, Providencia> providencias;

  @Inject
  ProcessaCiclaTelefone(TratadorEspecificoCliente.Factory tratadorEspecificoClienteFactory,
      ProcessaFimDaFila processaFimDaFila, Map<Providencia.Codigo, Providencia> providencias) {
    this.tratadorEspecificoClienteFactory = tratadorEspecificoClienteFactory;
    this.processaFimDaFila = processaFimDaFila;
    this.providencias = providencias;
  }

  @Override
  public boolean accept(Tenant tenant, DaoFactory daoFactory, Ligacao ligacao,
      ResultadoLigacao resultadoLigacao) {
    Cliente cliente = ligacao.getDiscavel().getCliente();
    ligacao.setTelefoneOriginal(cliente.getTelefone());
    if (!resultadoLigacao.isCiclaTelefone()) {
      logger.info("Nao vai ciclar telefone {}", cliente);
      return false;
    }
    return true;
  }

  @Override
  public boolean executa(Tenant tenant, DaoFactory daoFactory, Ligacao ligacao,
      ResultadoLigacao resultadoLigacao) {
    Cliente cliente = ligacao.getDiscavel().getCliente();
    logger.info("Ciclando telefone {}", cliente);
    Providencia.Codigo codigo =
        Providencia.Codigo.fromValue(cliente.getInformacaoCliente().getProvidenciaTelefone());
    if (codigo.equals(Providencia.Codigo.MANTEM_ATUAL))
      codigo = Providencia.Codigo.PROXIMO_TELEFONE;

    Providencia providencia = providencias.get(codigo);

    try {
      cliente.setTelefone(providencia.getTelefone(tenant, daoFactory, cliente));
    } catch (NaoPodeReiniciarRodadaTelefoneException e) {
      logger.info("Nao pode passar pro proximotelefone ainda {}", cliente);
      processaFimDaFila.executa(tenant, daoFactory, ligacao, null);
    } catch (ClienteSemTelefoneException e) {
      logger.info("Cliente sem telefone {}", cliente);
      processaFimDaFila.executa(tenant, daoFactory, ligacao, null);
    } catch (SomenteCelularException e) {
      logger.info("Somente celulares {}", cliente);
      processaFimDaFila.executa(tenant, daoFactory, ligacao, null);
    } catch (SemProximoTelefoneException e) {
      logger.info("Nao possui proximo telefone {}", cliente);
      processaFimDaFila.executa(tenant, daoFactory, ligacao, null);
    }
    logger.info("Consegui ciclar telefone {}", cliente);
    tratadorEspecificoClienteFactory.create(tenant, daoFactory).obtemClienteDao().atualiza(cliente);
    return true;
  }

  @Override
  public int compareTo(ProcessoDevolucao o) {
    return new CompareToBuilder().append(getOrdem(), o.getOrdem()).toComparison();
  }

  @Override
  public int getOrdem() {
    return 9;
  }

}
