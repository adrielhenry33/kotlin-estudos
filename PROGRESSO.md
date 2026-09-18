# Regras e Objetivos: Kotlin + Mobile

> Este arquivo é a fonte da verdade do progresso. Sempre que perder o contexto da conversa (novo chat, sessão limpa), leia este arquivo primeiro.

---

## OBJETIVO GERAL

Dominar Kotlin nativo com Jetpack Compose para desenvolvimento Android/Mobile. Aplicar conhecimentos em projetos reais (GodiTrack e Orchestror). Transicionar de React Native/TypeScript pra Kotlin com compreensão profunda de padrões, genéricos e clean architecture.

---

## MÉTODO DE APRENDIZADO

Scaffold progressivo: começar com dicas leves, aumentar especificidade conforme necessário. Nunca entregar código pronto a menos que explicitamente pedido. Tom franco e técnico. Conectar padrões a genéricos (`<T, K, R>`) sempre que relevante. Sinalizar soluções over-engineered. Avisar quando a conversa aproximar do limite de tokens (migrar pra novo chat).

Estrutura: Teoria fundamentada + Exercícios práticos + Aplicações em projetos reais. Cada tópico tem Nível 1 (Fundações), Nível 2 (Prático Simples), Nível 3+ (Avançado), Nível 4 (Projetos Reais).

---

## ROADMAP KOTLIN

- [x] **1. Tratamento de Erros** (Try/Catch, `Result<T>`) — CONCLUÍDO
- [x] **2. Collections Avançadas** (fold, reduce) — CONCLUÍDO
- [x] **3. Coroutines** (suspend, scopes, dispatchers, launch, async, await, timeout) — CONCLUÍDO
- [ ] **4. Delegação** (`by lazy`, `by delegate`) — EM PROGRESSO
  - [x] Nível 1: Fundações
  - [x] Nível 2: Prático Simples (2.1, 2.2, 2.4)
  - [x] Nível 3.4: Cache + Lazy (`Exercicio4.kt` — CacheDelegate, com expiração)
  - [ ] Nível 4: Aplicações Reais
- [ ] **5. Flow & StateFlow** — INICIADO (adicionado ao roadmap por ser pré-requisito direto do Compose)
  - **Regra especial pros exercícios de Flow (pedido em 2026-09-14):** modo "sofrer um pouco" — dar só o enunciado, SEM scaffold de dicas não solicitadas. Só informar nome de função/método do Kotlin/Java se explicitamente perguntado. Não dizer o que fazer, a não ser que peça. Isso substitui o scaffold progressivo padrão (dica 1→2→3) apenas para este tópico.
  - [x] Nível 1: Teoria (cold vs hot, `flow{}`, `emit`, `collect`, `map`/`filter`, `MutableStateFlow`/`StateFlow`)
  - [x] Nível 2: Prático Simples (`FlowEx1.kt` pipeline; `FlowEx2.kt`/`FlowEx2b.kt` StateFlow; `FlowEx3.kt` combinação Flow+StateFlow; `FlowEx4.kt` checkpoint final — sensor de temperatura, feito sem ajuda/scaffold em 2026-09-17, confirma domínio do padrão StateFlow + launch/collect)
  - [ ] Nível 3+: Avançado (SharedFlow, operators avançados — combine, flatMapLatest, debounce) — **ATUAL** (decidido em 2026-09-17: fechar Flow/StateFlow antes de ir pra Compose)
    - Teoria de `SharedFlow`/`combine`/`flatMapLatest`/`debounce` explicada em 2026-09-17.
    - [ ] `FlowEx5.kt` criado (evolução do `BuscaViewModel` do Ex3 com `debounce`+`flatMapLatest`, simulando busca assíncrona) — sem scaffold, ainda pendente.
    - **`SharedFlow`** — teoria detalhada dada em 2026-09-17 (definição, `replay`/buffer, diferença de `StateFlow`, `MutableSharedFlow`/`.asSharedFlow()`, exemplo de múltiplos coletores, caso de uso GodiTrack "corrida cancelada" vs "status da corrida"). Continuação em 2026-09-18: `extraBufferCapacity`, `onBufferOverflow`, `BufferOverflow.SUSPEND`/`DROP_OLDEST`/`DROP_LATEST`, e `emit()` vs `tryEmit()` explicados. Exercício prático ainda não criado — **PRÓXIMO PASSO**.
    - **`combine`** — teoria dada em 2026-09-18 (definição, precisa que todos os flows já tenham emitido pra rodar, diferença de `zip`, ponte com `combineLatest` do RxJS, caso de uso Orchestror: validação de formulário com campos independentes). Exercício prático ainda não criado.
    - **Exercícios de `SharedFlow` criados em 2026-09-18** em `src/Flow/SharedFlow/` (pasta/pacote próprio, separado dos exercícios genéricos de Flow) — `SharedFlowEx1.kt` a `SharedFlowEx5.kt`, nível fundação, crescente, modo "sofrer um pouco": Ex1 broadcast básico multi-coletor — **CONCLUÍDO** (bug encontrado e corrigido pelo próprio usuário: coletores nunca cancelados travavam o `runBlocking`, já que `collect` de `SharedFlow` nunca completa sozinho — resolvido guardando os `Job` e cancelando no fim). Ex2 `replay` com coletor tardio, Ex3 `emit` vs `tryEmit` + `extraBufferCapacity`, Ex4 comparação das 3 estratégias de `onBufferOverflow`, Ex5 caso de uso GodiTrack (`StateFlow` status + `SharedFlow` evento) — pendentes, **PRÓXIMO PASSO**.
    - **Ordem combinada em 2026-09-18:** terminar `SharedFlowEx2` a `SharedFlowEx5` → `FlowEx5.kt` (debounce/flatMapLatest) → exercício de `combine` (fecha Nível 3+ de Flow) → só depois entrar em `callbackFlow`/`shareIn` (padrão de mercado pra conectar fontes externas tipo SSE/WebSocket a um `SharedFlow`, discutido em 2026-09-18 mas propositalmente adiado).
  - [ ] Nível 4: Aplicações Reais
- [ ] **6. Jetpack Compose**
- [ ] **7. Clean Architecture**
- [ ] **8. Room Database**

---

## REGRAS DE PROGRESSO

Não avançar de nível sem dominar o anterior. "Dominar" = conseguir explicar e implementar sem scaffold. Exercícios são obrigatórios, não opcionais (mesmo que pareça trivial). Desafios extras são opcionais, mas fazer fortalece aprendizado. Se ficar perdido, voltar pra teoria antes de prosseguir com novo exercício. Conectar cada tópico com TypeScript/React Native que você já conhece (ponte entre linguagens).

Dúvidas durante exercícios = oportunidade de aprender, não bloqueio. Avisar quando estiver aprofundando demais em um ponto (risco de over-engineering). Sessões devem respeitar seu tempo e evitar token burnout.

---

## ESTRUTURA DE CADA TÓPICO

1. Definição técnica sólida (sem código ainda).
2. Conceitos isolados com exemplos simples.
3. Fluxos visuais e diagramas.
4. Genéricos e padrões (conexão com TypeScript).
5. Casos de uso reais (GodiTrack/Orchestror).
6. Exercícios com scaffold progressivo.

---

## CRITÉRIO DE CONCLUSÃO POR NÍVEL

- **Nível 1:** Entender definição, interfaces, contratos. Responder perguntas sem consultar código.
- **Nível 2:** Implementar 4+ exercícios (validação, logging, genérico, transformação) sem skeleton.
- **Nível 3.4 (Delegação):** Implementar cache com expiração. Demonstrar por que lazy e cache são diferentes.
- **Nível 4:** Aplicar em 2+ cenários reais (GodiTrack + Orchestror). Código funcionando em produção ou simulado.

---

## REGRAS COM DÚVIDAS

Sempre responder com scaffold: dica 1 (leve) → dica 2 (média) → dica 3 (específica) → solução completa (só se pedir "código pronto"). Dúvida sobre genéricos? Ponte com TypeScript que você conhece. Dúvida sobre padrão? Conectar com Android/mobile real.

Não deixar dúvida sem resolver. Se algo não ficar claro após 2 explicações, reformular completamente (mudar analogia, exemplo, abordagem).

---

## REGRAS COM PROJETOS REAIS

- **GodiTrack:** foco em performance (lazy loading rotas), logging de transações motorista, sincronização servidor.
- **Orchestror:** foco em validação (email, telefone, CPF), auditoria (quem mudou contato), integridade de dados (status com transições).

Código deve ser reutilizável: 1 delegate pra múltiplas propriedades. Considerar thread-safety quando aplicável (múltiplos motoristas/usuários simultâneos). Documentar padrão usado e por quê.

**Regra fixa (pedido em 2026-09-14):** todo projeto/exercício de aplicação real (Nível 4 de qualquer tópico, GodiTrack, Orchestror) deve seguir **MVVM com princípios de Clean Architecture** (separação View / ViewModel / (Use Cases) / Repository / Data Source). Sempre que aplicável, indicar também qual é o **padrão de mercado/indústria** pra aquele problema específico (ex: como empresas resolvem isso normalmente em produção Android/Kotlin), não só a solução didática do exercício.

**Regra ampliada (pedido em 2026-09-17, ajustada no mesmo dia):** isso não é fixo/automático — avaliar o nível de aprendizado no momento. Se o conceito ainda está sendo fundamentado (ex: primeiro contato com `launch`+`collect`), ficar no básico primeiro. A versão de mercado/produção (ex: `viewModelScope`, `collectAsStateWithLifecycle()`, separação Repository) entra como evolução progressiva depois que o básico foi entendido, não como resposta obrigatória em toda pergunta de curiosidade. Avaliar a situação, não aplicar de forma rígida.

---

## PACING E RITMO

Cada sessão: máximo 2 novos exercícios ou 1 tópico completo. Equilibrar teoria (30%) com prático (70%). Se a conversa ficar muito longa, sugerir novo chat (sem perder contexto — tudo salvo aqui).

Não perder tempo em sub-tópicos que emergem naturalmente depois. Exemplo: Thread-Safety volta quando estudar Coroutines + Compose, não agora isolado.

---

## FERRAMENTAS E RECURSOS

Código roda em Kotlin local (IntelliJ). Exercícios começam com TODOs, você preenche. Desafios extras são opcionais (fortalecem, não obrigam). Sempre ter acesso a scaffold progressivo (dicas aninhadas, revelar conforme precisa).

---

## SUCESSO = QUANDO

- Você consegue explicar delegate (`by lazy` vs `by delegate`) pra alguém sem consultar código.
- Implementar validador que funciona com `String`, `Int`, `Boolean` (genéricos).
- Aplicar em GodiTrack (cache de rotas) e Orchestror (validação de contato).
- Reconhecer padrão delegate em código existente e saber quando usar.

---

## LOG DE PROGRESSO

> Cada entrada nova vai no topo, com data.

- **2026-09-14** — Delegação Nível 3.4 concluído: `Exercicio4.kt` (CacheDelegate com expiração) implementado e explicado (getValue/setValue, `!!` vs `as T`, diferença lazy vs cache com TTL). Discussão em aberto sobre `setValue` ser write-through (aceita valor direto) vs invalidate-only (força recarregar via `carregar()`) — decisão de design registrada no exercício, não fechada como certo/errado.

- **2026-09-11** — Teoria de Flow/StateFlow explicada; criados `src/Flow/FlowEx1.kt`, `FlowEx2.kt`, `FlowEx3.kt` (pipeline, StateFlow isolado, combinação — pendentes). Delegação Nível 3.4 (`Exercicio4.kt`, CacheDelegate) segue em progresso.
