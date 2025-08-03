# The Parchment Throne – Systemarchitektur

Ein textbasiertes Grand Strategy Spiel, bei dem der Spieler eine Nation führt. Die Spielwelt wird von einer Engine-LLM und spezialisierten Agenten simuliert. Die Struktur ist modular, zustandslos pro Zug und stark datengetrieben.

---

## 🎯 Ziel des Spiels

Das Spiel bietet eine tiefgehende, emergente Narration, in der:
- Spieler Entscheidungen treffen,
- die Spielwelt dynamisch auf Ereignisse reagiert,
- KI-Agenten glaubhafte Persönlichkeiten verkörpern.

---

## 🧠 Hauptkomponenten

### 1. Game Engine Agent
- Führt den Spielzug aus
- Wird pro Zug neu initialisiert (**stateless**)
- Führt Tool-Calls aus, bewertet Situationen, generiert Ereignisse
- Startet bei Bedarf Subchats mit spezialisierten Agents

### 2. Conversational Agents _(geplant)_
- Verkörpern Personen wie Berater, Diplomaten, Generäle
- Interagieren mit dem Spieler in natürlichen Dialogen
- Produzieren maschinenlesbare Entscheidungen + Energieverbrauch

---

## 🗄️ Weltmodell – Datenquellen

| Name              | Typ       | Inhalt                                      |
|-------------------|-----------|---------------------------------------------|
| MCP               | SQL       | Quantifizierbare Spielinformationen         |
| VectorDB          | Vektor    | Semantisches Wissen über Welt & Charaktere |
| NoSQL DB          | Dokumente | Erinnerungen, Konversationsverläufe         |

---

## ⚙️ Zugablauf (Pipeline)

```text
┌────────────┐
│ Zugstart   │
└────┬───────┘
     ▼
1. Status & Analyse  
   - Tool: get_basic_status  
   - Danach: gezielte Detail-Queries  
   - Ziel: Fokusbereiche & Analyse-JSON

2. (geplant) Ereignisgenerierung  
   - Reaktive & proaktive Events  

3. (geplant) Agenteninteraktion  
   - Subchats mit YAML-basiertem Output  

4. (geplant) Aktionsauswertung  
   - update_mcp_db, add_to_memory

5. (geplant) Statusbericht an Spieler
````

---

## 🔌 Tool-API für Game Engine

```json
[
  {
    "name": "get_basic_status",
    "description": "Allgemeiner Überblick über Nation"
  },
  {
    "name": "get_resource_status",
    "description": "Ressourcenproduktion, Lager, Verbrauch"
  },
  {
    "name": "get_military_status",
    "description": "Armeen, Tech, Moral"
  },
  {
    "name": "get_diplomatic_relations",
    "description": "Beziehungen zu anderen Nationen"
  },
  {
    "name": "get_internal_stability",
    "description": "Zustand der inneren Ordnung"
  },
  {
    "name": "get_recent_events",
    "description": "Letzte Ereignisse"
  }
]
```

---

## 🧪 Mentale Energie (geplant)

Subchats annotieren Aktionen mit YAML:

```yaml
energy_consumed: 15
reason: "Intensives Gespräch über Kriegsvorbereitungen"
```

* Der Server aktualisiert den Energielevel
* Agenten können auf Erschöpfung reagieren
* Noch offen: Sichtbarkeit für Spieler

---

## ❓ Offene Fragen

| Thema                     | Status        | Kommentar                          |
| ------------------------- | ------------- | ---------------------------------- |
| Subchat Integration       | geplant       | Wo genau im Zugablauf?             |
| Energie-Feedback sichtbar | unentschieden | Spieler sehen mentale Erschöpfung? |
| Eventsystem               | in Planung    | Regelbasiert vs. LLM-generiert     |
