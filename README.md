#Exemplo de API REST com JAVA SPRING, JAVA WEB e JSON, Interface feita com HTML, JS e CSS.

#Validar com PostMan

*ENDPOINT - CRIAR TAREFA
-POST http://localhost:8080/api/tarefas

-JSON - {
  "nome": "Criar Usuário Trinith",
  "dataEntrega": "2027-10-25",
  "responsavel": "Ciclano de Beutrano"
}

*ENDPOINT - Mostrar Tarefas
- GET http://localhost:8080/api/tarefas

*ENDPOINT - Atualizar Tarefa
- PUT http://localhost:8080/api/tarefas/2 (Ex: Tarefa 2)

-JSON - {
  "nome": "Criar Usuário Trinith 2",
  "dataEntrega": "2027-10-25",
  "responsavel": "Fulano de Ciclano"
}

#ENDPOINT - Deletar Tarefa
- DELETE http://localhost:8080/api/tarefas/3 (Ex: Tarefa 3)
