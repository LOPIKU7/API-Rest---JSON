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

        // 1. Tenta carregar o arquivo usando o ClassPathResource (melhor para leitura)
        ClassPathResource resource = new ClassPathResource("tarefas.json");

        // 2. Define o dataFile para o caminho relativo de escrita (para desenvolvimento)
        File relativeFile = new File("src/main/resources/tarefas.json");

        // Verifica se o recurso existe no classpath (significa que já foi compilado/copiado)
        if (resource.exists()) {
            // Se já existe e está acessível, usa o arquivo do ClassPathResource
            this.dataFile = resource.getFile();
        } else {
            // Se o arquivo não for encontrado no classpath de execução (ou não existe no projeto):

            // A) Define o caminho do arquivo no diretório de desenvolvimento
            this.dataFile = relativeFile;

            // B) ⭐️ CRIA O DIRETÓRIO PAI SE NÃO EXISTIR! (CORREÇÃO DE ERRO)
            File parentDir = this.dataFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                // Tenta criar os diretórios necessários (src/main/resources)
                if (!parentDir.mkdirs()) {
                    throw new IOException("Falha ao criar o diretório de recursos: " + parentDir.getAbsolutePath());
                }
            }

            // C) ⭐️ CRIA O ARQUIVO SE NÃO EXISTIR
            if (!this.dataFile.exists()) {
                if (this.dataFile.createNewFile()) {
                    System.out.println("Arquivo tarefas.json criado em: " + this.dataFile.getAbsolutePath());
                } else {
                    throw new IOException("Falha ao criar o arquivo tarefas.json.");
                }
            }
        }

        // 3. Carrega os dados
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