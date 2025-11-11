package tarefasAPITarefas_json.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import tarefasAPITarefas_json.model.Tarefa;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class JsonTarefaRepository implements TarefaRepository {

    private final ObjectMapper objectMapper;
    private List<Tarefa> tarefas;
    private final AtomicLong counter = new AtomicLong();
    private final File dataFile;

    public JsonTarefaRepository() throws IOException {
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        // ➡️ NOVO DIRETÓRIO: Carrega o arquivo tarefas.json da pasta 'resources' (classpath)
        ClassPathResource resource = new ClassPathResource("tarefas.json");

        // Esta linha tenta obter o caminho físico do arquivo.
        // ATENÇÃO: Isso funciona bem em ambientes de desenvolvimento (IDE),
        // mas pode falhar se o projeto for empacotado como um JAR executável.
        if (resource.exists()) {
            this.dataFile = resource.getFile();
        } else {
            // Se o recurso não existir, tenta criá-lo no diretório de execução/recursos,
            // ou você pode criar o arquivo "tarefas.json" manualmente em src/main/resources
            // e garantir que ele seja copiado para o classpath.
            // Para simplicidade na IDE:
            this.dataFile = new File("src/main/resources/tarefas.json");
            if (!this.dataFile.exists()) {
                this.dataFile.createNewFile();
            }
        }

        // Verifica se o arquivo existe e não está vazio para carregar dados
        if (dataFile.exists() && dataFile.length() > 0) {
            this.tarefas = objectMapper.readValue(dataFile, new TypeReference<List<Tarefa>>() {});
            this.tarefas.stream().mapToLong(Tarefa::getId).max().ifPresent(maxId -> counter.set(maxId));
        } else {
            this.tarefas = new ArrayList<>();
        }
    }

    private void saveToFile() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(dataFile, tarefas);
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados no arquivo JSON: " + dataFile.getAbsolutePath());
            e.printStackTrace();
        }
    }

    @Override
    public List<Tarefa> findAll() {
        return tarefas;
    }

    @Override
    public Optional<Tarefa> findById(Long id) {
        return tarefas.stream().filter(t -> t.getId().equals(id)).findFirst();
    }

    @Override
    public Tarefa save(Tarefa tarefa) {
        if (tarefa.getId() == null) {
            tarefa.setId(counter.incrementAndGet());
            tarefas.add(tarefa);
        } else {
            tarefas = tarefas.stream()
                    .map(t -> t.getId().equals(tarefa.getId()) ? tarefa : t)
                    .collect(Collectors.toList());
        }
        saveToFile();
        return tarefa;
    }

    @Override
    public void deleteById(Long id) {
        tarefas.removeIf(t -> t.getId().equals(id));
        saveToFile();
    }
}