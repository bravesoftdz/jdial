package al.jdi.core.devolveregistro;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import al.jdi.core.configuracoes.Configuracoes;
import al.jdi.core.modelo.Discavel;
import al.jdi.core.modelo.Ligacao;
import al.jdi.core.modelo.Providencia;
import al.jdi.core.tenant.Tenant;
import al.jdi.dao.beans.Dao;
import al.jdi.dao.beans.DaoFactory;
import al.jdi.dao.beans.ResultadoLigacaoDao;
import al.jdi.dao.model.Campanha;
import al.jdi.dao.model.Cliente;
import al.jdi.dao.model.InformacaoCliente;
import al.jdi.dao.model.Mailing;
import al.jdi.dao.model.MotivoSistema;
import al.jdi.dao.model.ResultadoLigacao;

public class ProcessaRetornaProvidenciaTest {

  private static final Integer PROVIDENCIA = 0;

  private ProcessaRetornaProvidencia processaRetornaProvidencia;
  @Mock
  private Ligacao ligacao;
  @Mock
  private Cliente cliente;
  @Mock
  private ResultadoLigacao resultadoLigacao;
  @Mock
  private DaoFactory daoFactory;
  @Mock
  private ResultadoLigacaoDao resultadoLigacaoDao;
  @Mock
  private Dao<InformacaoCliente> informacaoClienteDao;
  @Mock
  private Mailing mailing;
  @Mock
  private Campanha campanha;
  @Mock
  private ResultadoLigacao resultadoLigacaoSemProximoTelefone;
  @Mock
  private InformacaoCliente informacaoCliente;
  @Mock
  private Configuracoes configuracoes;
  @Mock
  private Tenant tenant;
  @Mock
  private Discavel discavel;

  @Before
  public void setUp() throws Exception {
    initMocks(this);
    when(daoFactory.getResultadoLigacaoDao()).thenReturn(resultadoLigacaoDao);
    when(daoFactory.getInformacaoClienteDao()).thenReturn(informacaoClienteDao);
    when(cliente.getMailing()).thenReturn(mailing);
    when(mailing.getCampanha()).thenReturn(campanha);
    when(resultadoLigacaoDao.procura(MotivoSistema.SEM_PROXIMO_TELEFONE.getCodigo(), campanha))
        .thenReturn(resultadoLigacaoSemProximoTelefone);
    when(cliente.getInformacaoCliente()).thenReturn(informacaoCliente);
    when(informacaoCliente.getProvidenciaTelefone()).thenReturn(PROVIDENCIA);
    when(tenant.getConfiguracoes()).thenReturn(configuracoes);
    when(ligacao.getDiscavel()).thenReturn(discavel);
    when(discavel.getCliente()).thenReturn(cliente);
    processaRetornaProvidencia = new ProcessaRetornaProvidencia();
  }

  @Test
  public void getOrdemDeveriaRetornar() throws Exception {
    assertThat(processaRetornaProvidencia.getOrdem(), is(13));
  }

  @Test
  public void acceptDeveriaRetornarFalse() throws Exception {
    assertThat(processaRetornaProvidencia.accept(tenant, daoFactory, ligacao, resultadoLigacao),
        is(false));
  }

  @Test
  public void acceptDeveriaRetornarTrue() throws Exception {
    assertThat(processaRetornaProvidencia.accept(tenant, daoFactory,
        ligacao, resultadoLigacaoSemProximoTelefone), is(true));
  }

  @Test
  public void processaDeveriaRetornarProvidencia() throws Exception {
    processaRetornaProvidencia.executa(tenant, daoFactory, ligacao, resultadoLigacao);
    verify(informacaoCliente).setProvidenciaTelefone(Providencia.Codigo.MANTEM_ATUAL.getCodigo());
  }

  @Test
  public void processaDeveriaAtualizarInformacaoCliente() throws Exception {
    processaRetornaProvidencia.executa(tenant, daoFactory, ligacao, resultadoLigacao);
    verify(informacaoClienteDao).atualiza(informacaoCliente);
  }

  @Test
  public void processaDeveriaRetornarTrue() throws Exception {
    assertThat(processaRetornaProvidencia.executa(tenant, daoFactory, ligacao, resultadoLigacao),
        is(true));
  }
}
