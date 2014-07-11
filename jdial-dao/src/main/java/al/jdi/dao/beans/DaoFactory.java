package al.jdi.dao.beans;

import org.joda.time.DateTime;

import al.jdi.dao.model.Campanha;
import al.jdi.dao.model.DefinicaoPadrao;
import al.jdi.dao.model.EstadoCliente;
import al.jdi.dao.model.Filtro;
import al.jdi.dao.model.Grupo;
import al.jdi.dao.model.HistoricoCliente;
import al.jdi.dao.model.InformacaoCliente;
import al.jdi.dao.model.Log;
import al.jdi.dao.model.MotivoFinalizacao;
import al.jdi.dao.model.RestricaoHorario;
import al.jdi.dao.model.Rota;
import al.jdi.dao.model.WebLog;

public interface DaoFactory {

  void beginTransaction();

  void close();

  void commit();

  AgendamentoDao getAgendamentoDao();

  AgenteDao getAgenteDao();

  AreaAreaDao getAreaAreaDao();

  Dao<Campanha> getCampanhaDao();

  ClienteDao getClienteDao();

  ClienteDaoTsa getClienteDaoTsa();

  ClienteDaoTsa getClienteDaoTsaCRM();

  DateTime getDataBanco();

  DefinicaoDao getDefinicaoDao();

  Dao<DefinicaoPadrao> getDefinicaoPadraoDao();

  Dao<EstadoCliente> getEstadoClienteDao();

  Dao<Filtro> getFiltroDao();

  Dao<Grupo> getGrupoDao();

  Dao<HistoricoCliente> getHistoricoClienteDao();

  HistoricoLigacaoDao getHistoricoLigacaoDao();

  Dao<InformacaoCliente> getInformacaoClienteDao();

  Dao<Log> getLogDao();

  MailingDao getMailingDao();

  Dao<MotivoFinalizacao> getMotivoFinalizacaoDao();

  Dao<RestricaoHorario> getRestricaoHorarioDao();

  ResultadoLigacaoDao getResultadoLigacaoDao();

  Dao<Rota> getRotaDao();

  ServicoDao getServicoDao();

  TelefoneDao getTelefoneDao();

  UsuarioDao getUsuarioDao();

  Dao<WebLog> getWebLog();

  boolean hasTransaction();

  void rollback();

  void trocaSession(String identifier);

}