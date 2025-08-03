# The Parchment throne

**The Parchment throne** is a text-based grand strategy game powered by multiple AI agents.  
You lead a nation through diplomacy, warfare, internal politics, and economic development in a world that evolves dynamically through agent-based simulation and player interaction.

---

## 🎯 Project Goals

- Enable deep, emergent storytelling through AI-driven world simulation.
- Combine structured game mechanics with natural language interaction.
- Create a modular, extensible system for AI-based narrative games.

---

## 🧠 Architecture Overview

This project is powered by a multi-agent system:

- **Game Engine Agent**:  
  Stateless, LLM-based turn processor. Queries world state via tool-calls, analyzes risks/opportunities, and triggers dynamic events.

- **Conversational Agents** _(planned)_:  
  Persona-based temporary agents representing advisors, diplomats, generals, etc. Interact with the player and return structured, machine-readable outcomes.

- **Data Backends**:
  - **MCP** (SQL): Structured, authoritative game state (nations, armies, resources, etc.)
  - **VectorDB** (planned): Semantic embeddings of cultural, historical, and personality data
  - **NoSQL** (planned): Dynamic documents (memories, conversations, agent state)

---

## 🚧 Current Features

- ✅ Game Engine prompts for "status & analysis" phase
- ✅ Tool API design for structured world querying
- 🧪 Turn processing model with incremental queries
- 🧠 Mental energy system for conversational load (planned)
- 🧱 Modular YAML-based architecture description

---

## 🔜 Planned Features

- Event generation system based on context and player state
- Subchat integration with context memory and energy tracking
- Memory-aware character agents
- UI frontend (CLI or web)

---

## 📦 Repository Structure (planned)

```

ai-grand-strategy/
├── engine/              # LLM prompts, orchestration logic
├── api/                 # Tool handlers (e.g. MCP SQL API)
├── data/                # Game state seeds and schemas
├── docs/                # Architecture docs, diagrams
├── LICENSE              # Apache 2.0 license
├── README.md            # You are here

```

---

## 📝 License

This project is licensed under the **Apache License 2.0**.  
See the [LICENSE](./LICENSE) file for details.

---

## 🤝 Contributing

Contributions, ideas, and feedback are welcome!  
This project is in early stages – if you'd like to contribute, build a UI, design events, or model a new agent, open an issue or start a discussion.

---

## 💬 Contact

Want to talk design, narrative AI, or agent-based world models?  
Open a discussion or get in touch via GitHub.

---

## 🧪 Disclaimer

This project is a **creative experiment** in dynamic storytelling with LLMs.  
Many components are in exploration phase and subject to change.
