# Processo de Desenvolvimento - Trabalho 3 (Persistência com Room)

## Integrante
- Henrique [Seu Sobrenome] - RA: [Seu RA]

## Tema
**HabitTracker** - Evolução do app de rastreamento de hábitos para suportar persistência de dados local via Room.

## 1. Como estava no Trabalho 2 vs. O que mudou no Trabalho 3

### Trabalho 2 (MAF)
- Dados viviam em memória RAM usando `mutableStateListOf`.
- Ao fechar o app, todos os hábitos e conquistas eram perdidos.
- Telas recebiam objetos prontos via parâmetros de navegação.

### Trabalho 3 (Room)
- Dados agora sobrevivem ao fechamento do app usando SQLite via Room.
- Substituímos `mutableStateListOf` por `MutableState<List<T>>` alimentado pelo banco.
- Todas as operações de I/O são assíncronas (`suspend fun`) rodando em `Dispatchers.IO`.
- A tela de detalhes busca o item diretamente do banco pelo ID (não recebe objeto pronto).

## 2. Estrutura do Room Criada

### Entities
Criamos 2 entidades adaptadas das data classes originais:
- **`Habito`**: Tabela `habitos`. Campos: `id` (PK auto), `nome`, `descricao`, `diasSemana` (String), `concluidoHoje`.
- **`Conquista`**: Tabela `conquistas`. Campos: `id` (PK auto), `titulo`, `descricao`, `desbloqueada`, `icone`.

> **Decisão técnica:** O campo `diasSemana` foi convertido de `Set<Int>` para `String` ("1,2,3") porque o Room não armazena coleções complexas nativamente sem TypeConverters. Optamos pela simplicidade de String para este escopo.

### DAOs
Dois interfaces com CRUD completo (`@Insert`, `@Query`, `@Update`, `@Delete`):
- **`HabitoDao`**: Inclui consulta extra `buscarPorId(id: Int)` para a tela de detalhes.
- **`ConquistaDao`**: Mesma estrutura, garantindo consistência entre os dois CRUDs.

### Database Singleton
Classe `AppDatabase` seguindo o padrão oficial (Singleton com `@Volatile` + `synchronized`), expondo os dois DAOs. Nome do banco: `habittracker_database`.

## 3. Dificuldades na Migração e Soluções

### 3.1 Erro "Composable function call without Compose plugin"
Ao adicionar o KSP para o Room, o IDE perdeu a referência ao plugin do Compose, gerando ~40 erros falsos positivos.
**Solução:** Identificamos que era cache do IDE dessincronizado após troca de JDK. Usamos `Build > Compile` via terminal como fonte da verdade e invalidamos caches.

### 3.2 Adaptação de Set<Int> para String
O `NovoHabitoScreen` passava `Set<Int>` para o ViewModel, mas o Room esperava String.
**Solução:** Centralizamos a conversão dentro do `ViewModel.adicionarHabito()` usando `joinToString(",")`. Na leitura, fazemos o split inverso. Isso manteve a UI limpa e isolou a lógica de persistência.

### 3.3 Estado Reativo Assíncrono
Trocar listas síncronas por chamadas de banco exigiu mudar de `mutableStateListOf` para `mutableStateOf<List<T>>` + `LaunchedEffect` nas telas de lista.
**Solução:** Criamos funções `carregarHabitos()` e `carregarConquistas()` no ViewModel que rodam em `viewModelScope.launch(Dispatchers.IO)` e atualizam o estado na Main Thread.

## 4. Prova de Persistência (Teste de Aceitação)

## 5. Lições Aprendidas
- Room exige disciplina com threads: esquecer `Dispatchers.IO` trava a UI instantaneamente.
- O padrão Singleton do Database é crucial para evitar múltiplas instâncias do banco.
- Migrar de memória para banco não muda a UX, mas transforma completamente a arquitetura interna.
- Testar persistência real (fechar/reabrir) é diferente de testar funcionalidade em tempo de execução.