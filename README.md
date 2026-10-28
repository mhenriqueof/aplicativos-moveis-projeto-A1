# HabitTracker — Trabalho 4 (MVVM + Room Reativo)

Aplicativo Android individual de rastreamento de hábitos, evoluído para arquitetura MVVM completa com Room reativo (Flow), Repository pattern e separação por features.

## Integrante
- Marcio Henrique

## Funcionalidades Implementadas

### CRUD Completo de Hábitos (MVVM)
- ✅ Criar hábito (nome, descrição, dias da semana)
- ✅ Listar hábitos reativamente (Flow + StateFlow)
- ✅ Editar hábito (tela separada, atualização via Repository)
- ✅ Deletar hábito (remoção persistente)
- ✅ Marcar/desmarcar conclusão (estado persistido)

### CRUD Completo de Conquistas (MVVM)
- ✅ Criar conquista (título, descrição)
- ✅ Listar conquistas reativamente
- ✅ Editar conquista (tela separada)
- ✅ Deletar conquista
- ✅ Toggle desbloqueio/bloqueio

### Navegação e UI
- ✅ BottomNavigationBar (Home, Conquistas, Perfil)
- ✅ Navegação condicional (BottomBar some em telas secundárias)
- ✅ Tela de detalhes com estatísticas calculadas no ViewModel
- ✅ Complexidade extra: taxa de conclusão + barra de progresso visual

## Arquitetura (MVVM)

| Camada | Componentes | Responsabilidade |
|--------|-------------|------------------|
| **Model** | Entity, DAO, Database, Repository | Dados + acesso ao banco |
| **ViewModel** | HabitoViewModel, ConquistaViewModel, UiState, Factory | Estado da tela + lógica de negócio |
| **View** | Telas Compose (@Composable) | Só exibe uiState + dispara eventos |

### Estrutura de Pacotes
```
data/local/          → Room (Entity, DAO, Database)
data/repository/     → Repositories (única porta de entrada)
ui/habitos/          → Feature completa de Hábitos
ui/conquistas/       → Feature completa de Conquistas
ui/comum/            → Telas compartilhadas (Perfil, Config)
navigation/          → Rotas + NavHost
```

## Tecnologias
- Kotlin + Jetpack Compose (Material 3)
- Navigation Compose
- Room (SQLite) + KSP
- ViewModel + StateFlow + Flow
- lifecycle-viewmodel-compose + lifecycle-runtime-compose
- Android Studio

## Como Rodar o Projeto

### Pré-requisitos
- Android Studio Hedgehog/Iguana ou superior
- JDK 17 (configurado no Gradle Settings)
- Emulador API 24+ ou dispositivo físico

### Passo a Passo
1. Clone o repositório:
   ```bash
   git clone https://github.com/mhenriqueof/aplicativos-moveis-projeto-A2.git
   cd aplicativos-moveis-projeto-A2
   ```

2. Abra no Android Studio e aguarde o Sync do Gradle.

3. Execute o app (▶️ Run).

4. **Teste de Persistência Reativa:**
   - Adicione um hábito → feche o app → abra novamente → item continua lá.
   - Edite um hábito → salve → a lista atualiza sozinha (sem recarregar).

## Documentação do Processo
Veja o arquivo [PROCESSO.md](PROCESSO.md) para detalhes sobre:
- Migração de Trabalho 3 (Room básico) para Trabalho 4 (MVVM completo)
- Decisões técnicas (separação em 2 ViewModels, Flow vs suspend, UiState)
- Dificuldades enfrentadas e soluções aplicadas
