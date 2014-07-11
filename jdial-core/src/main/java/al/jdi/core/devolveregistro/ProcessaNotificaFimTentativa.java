package al.jdi.core.devolveregistro;

import javax.inject.Inject;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import al.jdi.core.modelo.Ligacao;
import al.jdi.core.tratadorespecificocliente.TratadorEspecificoCliente;
import al.jdi.dao.beans.DaoFactory;
import al.jdi.dao.model.Cliente;
import al.jdi.dao.model.ResultadoLigacao;

class ProcessaNotificaFimTentativa implements ProcessoDevolucao {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final TratadorEspecificoCliente tratadorEspecificoCliente;

  @Inject
  ProcessaNotificaFimTentativa(TratadorEspecificoCliente tratadorEspecificoCliente) {
    this.tratadorEspecificoCliente = tratadorEspecificoCliente;
  }

  @Override
  public int compareTo(ProcessoDevolucao o) {
    return new CompareToBuilder().append(getOrdem(), o.getOrdem()).toComparison();
  }

  @Override
  public int getOrdem() {
    return 14;
  }

  @Override
  public boolean accept(Ligacao ligacao, Cliente cliente, ResultadoLigacao resultadoLigacao,
      DaoFactory daoFactory) {
    if (!resultadoLigacao.isNotificaFimTentativa()) {
      logger.info("Nao vai notificar fim tentativa motivo {} {}", resultadoLigacao, cliente);
      return false;
    }
    return true;
  }

  @Override
  public boolean executa(Ligacao ligacao, Cliente cliente, ResultadoLigacao resultadoLigacao,
      DaoFactory daoFactory) {
    logger.info("Vai notificar fim tentativa motivo {} {}", resultadoLigacao, cliente);
    tratadorEspecificoCliente.notificaFimTentativa(daoFactory, ligacao, cliente, cliente
        .getMailing().getCampanha(), daoFactory.getDataBanco(), ligacao.getTelefoneOriginal(),
        resultadoLigacao, ligacao.isInutilizaComMotivoDiferenciado());
    return true;
  }

}