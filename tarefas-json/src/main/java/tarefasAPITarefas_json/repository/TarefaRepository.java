package tarefasAPITarefas_json.repository;

import tarefasAPITarefas_json.model.Tarefa;
import java.util.List;
import java.util.Optional;

public interface TarefaRepository {
    List<Tarefa> findAll();
    Optional<Tarefa> findById(Long id);
    Tarefa save(Tarefa tarefa);
    void deleteById(Long id);
}