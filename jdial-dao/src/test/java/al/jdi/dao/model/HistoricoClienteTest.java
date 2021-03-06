package al.jdi.dao.model;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class HistoricoClienteTest {

  private HistoricoCliente historicoCliente;

  @Test
  public void defaultCriacaoModificacao() {
    assertThat(historicoCliente.getCriacaoModificacao(),
        is(not(nullValue(CriacaoModificacao.class))));
  }

  @Before
  public void setUp() {
    historicoCliente = new HistoricoCliente();
  }

}
