# Processo de Desenvolvimento — Evolução para MVVM (Trabalho 4)

## Integrante
- Marcio Henrique

## Tema
**HabitTracker** — Refatoração completa da arquitetura para MVVM + Room Reativo.

## 1. Como estava no Trabalho 3 vs. O que mudou no Trabalho 4

### Trabalho 3 (Room Básico)
- Dados persistiam via Room, mas a UI acessava o DAO diretamente.
- Estado da tela espalhado em múltiplas variáveis `remember` / `mutableStateOf`.
- Um único `HabitViewModel` gigante cuidava de Hábitos E Conquistas.
- Listas eram recarregadas manualmente após cada operação (`carregarHabitos()`).
- Pacotes organizados por tipo (`data/model`, `data/dao`, `ui/screens`).

### Trabalho 4 (MVVM Completo)
- Camada de Repository como única porta de entrada para dados.
- DAOs retornam `Flow<List<T>>` — a lista se atualiza sozinha quando o banco muda.
- Estado unificado em `UiState` (data class única por feature).
- Separação em 2 ViewModels independentes: `HabitoViewModel` e `ConquistaViewModel`.
- Telas "burras": só observam `uiState` e disparam eventos `onEvento()`.
- Fluxo Unidirecional (UDF): estado desce, eventos sobem.
- Pacotes organizados por feature (`ui/habitos`, `ui/conquistas`) + camadas (`data/local`, `data/repository`).

## 2. Estrutura Final do Projeto

Seguindo exatamente o padrão ensinado nas aulas de MVVM:

```
com.example.habittracker/
├── MainActivity.kt              → Só cria NavController + BottomBar
├── data/
│   ├── local/                   → Entity + DAO + Database (Room)
│   │   ├── Habito.kt            → @Entity(tableName = "habitos")
│   │   ├── Conquista.kt         → @Entity(tableName = "conquistas")
│   │   ├── HabitoDao.kt         → buscarTodos(): Flow<List<Habito>>
│   │   ├── ConquistaDao.kt      → buscarTodos(): Flow<List<Conquista>>
│   │   └── AppDatabase.kt       → Singleton RoomDatabase
│   └── repository/              → Única fonte de verdade
│       ├── HabitoRepository.kt  → getAllHabitos(), inserir(), atualizar(), deletar()
│       └── ConquistaRepository.kt → getAllConquistas(), ...
── ui/
│   ├── habitos/                 → Feature completa de Hábitos
│   │   ├── HabitoUiState.kt     → Lista + campos form + item em edição
│   │   ├── HabitoViewModel.kt   → StateFlow + onNomeChange/onSave/onDelete
│   │   ├── HabitoViewModelFactory.kt → Injeta HabitoRepository
│   │   ├── HomeScreen.kt        → collectAsStateWithLifecycle()
│   │   ├── NovoHabitoScreen.kt
│   │   ├── EditarHabitoScreen.kt
│   │   └── DetalheHabitoScreen.kt
│   ├── conquistas/              → Feature completa de Conquistas
│   │   ├── ConquistaUiState.kt
│   │   ├── ConquistaViewModel.kt
│   │   ├── ConquistaViewModelFactory.kt
│   │   ├── RecompensasScreen.kt
│   │   ├── NovaConquistaScreen.kt
│   │   ├── EditarConquistaScreen.kt
│   │   └── DetalheConquistaScreen.kt
│   └── comum/                   → Telas compartilhadas
│       ├── PerfilScreen.kt
│       └── ConfiguracoesScreen.kt
└── navigation/
├── Rotas.kt
└── AppNavigation.kt
```

## 3. Dificuldades na Migração e Soluções

### 3.1 Perda da BottomNavigationBar após refatoração
Ao simplificar o `MainActivity.kt` durante a transição MVVM, a BottomBar sumiu porque foi removida junto com a lógica antiga.
**Solução:** Restauramos o `Scaffold` com `NavigationBar` no `MainActivity`, usando `currentBackStackEntryAsState()` para esconder a barra em telas secundárias (detalhes, formulários). Renomeamos "Recompensas" para "Conquistas" para alinhar com a nomenclatura da feature.

### 3.2 Adaptação de Set<Int> para String no Flow
O `diasSemana` era `Set<Int>` na UI, mas o Room salva como String `"1,2,3"`. Com `Flow`, a conversão precisava acontecer dentro do `UiState` e não mais no ViewModel manual.
**Solução:** Mantivemos a conversão `joinToString(",")` no `onSave()` e `split(",")` no `onEdit()`. O `Flow` garante que, após salvar, a lista reemitida já vem com o dado correto do banco, sem necessidade de recarregar manualmente.

### 3.3 Separação em 2 ViewModels
O `HabitViewModel` antigo cuidava dos dois CRUDs. Separar exigiu duplicar a estrutura de Factory + UiState + ViewModel.
**Solução:** Criamos `HabitoViewModel` e `ConquistaViewModel` independentes, cada um com seu próprio Repository e Factory. A `PerfilScreen` agora recebe ambos via parâmetros opcionais com `viewModel(factory = ...)`, mantendo a coesão alta e acoplamento baixo.

### 3.4 Tela "Burra" vs. Lógica de Negócio
Antes, a `DetalheHabitoScreen` calculava taxa de conclusão e buscava dados sozinha.
**Solução:** Movemos `calcularTaxaConclusao()` para o `HabitoViewModel`. A tela só lê `uiState.listaDeHabitos` e chama `viewModel.onToggleConcluido()`. Zero lógica de negócio no Composable.

## 4. Prova de Funcionamento (Teste de Aceitação)

### Roteiro Demonstrado
1.  **Persistência Reativa:** Adicionei "Beber água" → fechei app → abri → item persistiu (via Room).
2.  **Fluxo Unidirecional:** Editei o nome para "Beber 3L" → salvei → a lista atualizou sozinha (via Flow, sem recarregar manualmente).
3.  **CRUD Completo:** Deletei uma conquista → fechei app → abri → conquista sumiu definitivamente.
4.  **Navegação Correta:** BottomBar aparece nas telas principais (Home, Conquistas, Perfil) e some nas telas de detalhes/formulários.
5.  **Separação de Responsabilidades:** Nenhuma tela acessa DAO ou Repository diretamente. Toda interação passa pelo ViewModel.

## 5. Lições Aprendidas
-   **Flow elimina recarregamentos manuais:** Uma vez inscrito no `init{}`, a lista nunca mais precisa ser "pedida".
-   **UiState atômico previne bugs:** Ter um único objeto representando o estado da tela evita inconsistências entre variáveis soltas.
-   **Repository desacopla o ViewModel do Room:** Se amanhã trocarmos SQLite por API, só o Repository muda.
-   **MVVM não é sobre criar telas novas, é sobre organizar as existentes:** O comportamento visual do app é idêntico ao Trabalho 3, mas a manutenibilidade aumentou drasticamente.
