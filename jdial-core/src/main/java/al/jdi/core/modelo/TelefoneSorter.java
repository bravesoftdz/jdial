package al.jdi.core.modelo;

import java.util.List;

import al.jdi.dao.model.Telefone;

public interface TelefoneSorter {

  List<Telefone> sort(List<Telefone> telefones);

}