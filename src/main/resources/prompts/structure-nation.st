Generate a JSON object containing detailed information about a nation's political, economic, military, and diplomatic status, following the provided structure. Use the given `<startYear>` and `<playerNationName>` as the basis for the output. All data should be coherent and consistent with the historical context of the specified year and nation.

**Instructions:**

1.  **Strictly adhere to the JSON structure provided in the example. Do not add any descriptive text, not before or after the JSON itself**
2.  **`nation_id` and `name`:** Use the three-letter code and full name of the `<playerNationName>`.
3.  **`general_info`:** Provide a capital city, government type, head of state, head of government, culture, and dominant religion for the `<playerNationName>` in the `<startYear>`.
4.  **`political_system`:** Describe the political system, legislative body, election cycles, major political parties, and governing coalition of the `<playerNationName>` in the `<startYear>`.
      * For `political_parties`, generate at least 4 distinct parties. Include their IDs, names, leaders, ideologies, governing roles (e.g., "Majority party," "Main opposition," "Coalition partner"), the number of seats they hold in the primary legislative body, and a `popularity_score` (a floating-point number between 0.0 and 1.0) based on their electoral performance in the `<startYear>` or the most recent election prior to it.
      * For the `governing_coalition`, identify the ruling coalition, its members (using party IDs), its head, and a `mandate_strength_score` (a floating-point number between 0.0 and 1.0) representing its strength in the legislature.
5.  **`domestic_factions`:** Identify 3-5 influential domestic factions within the `<playerNationName>` in the `<startYear>`. For each, provide a name, an `influence_score` (0.0-1.0), relevant `ideology_tags`, and a `relationship_to_government_strength` score (-1.0 to 1.0, where 1.0 is strong support and -1.0 is strong opposition).
6.  **`economy`:** Provide key economic data for the `<playerNationName>` in the `<startYear>`. This includes `gdp_usd` (as an integer), `gdp_growth_rate`, `inflation_rate`, `unemployment_rate` (all as floating-point numbers), `public_debt_as_gdp_percent` (floating-point number), a list of `major_industries`, and an `economic_stability_score` (0.0-1.0).
7.  **`military`:** Detail the military status of the nation in the `<startYear>`. Include `active_personnel` and `reserve_personnel` (as integers), `defense_budget_usd` (as an integer), `budget_as_gdp_percent` (floating-point number), a `military_power_score` (0.0-1.0), a list of military `branches`, and any notable `foreign_deployments`.
8.  **`diplomacy`:** Describe the nation's key diplomatic relationships in the `<startYear>`.
      * **`alliances`:** List 3-5 major allies. For each, include `nation_id`, `type` (e.g., "strategic," "major\_power"), and `strength` (0.0-1.0).
      * **`rivals`:** List 1-3 major rivals. For each, include `nation_id`, `type` (e.g., "great\_power\_rivalry"), and `strength` (-1.0 to 0.0).
      * **`affiliations`:** List 2-4 key international organizations the nation is a member of. Include the `organization_id`, `status`, and `influence_score` (0.0-1.0).
9.  **`key_actions`:** Generate 1-2 significant historical events or policy changes that occurred in or around the `<startYear>`. For each, include an `action_id`, a `description`, a `date` (formatted as YYYY.MM.DD), a `type`, an `impact_score` (0.0-1.0), and relevant `tags`.

**Example JSON Structure (for reference):**

```json
{
  "nation_id": "FRA",
  "name": "France",
  "general_info": {
    "capital": "Paris",
    "government_type": "Semi-presidential Republic",
    "head_of_state": "Emmanuel Macron",
    "head_of_government": "François Bayrou",
    "culture": "French",
    "religion": "Secular (historically Catholic)"
  },
  "political_system": {
    "system_type": "Semi-presidential republic",
    "legislative_body": "Parliament (National Assembly and Senate)",
    "election_cycle": {
      "presidential": {
        "next_election_date": "April 2027",
        "system": "Two-round system",
        "term_length_years": 5
      },
      "legislative": {
        "next_election_date": "2029",
        "system": "Two-round system",
        "term_length_years": 5
      }
    },
    "political_parties": [
      {
        "id": "re",
        "name": "Renaissance",
        "leader": "Gabriel Attal",
        "ideology": ["liberalism", "centrism"],
        "governing_role": "Majority party (within Ensemble coalition)",
        "national_assembly_seats": 99,
        "popularity_score": 0.20
      },
      ...
    ],
    "governing_coalition": {
      "name": "Bayrou government",
      "members": ["re", "lr", "modem", "horizons"],
      "head": "François Bayrou",
      "mandate_strength_score": 0.36
    }
  },
  "domestic_factions": [
    ...
  ],
  "economy": {
    ...
  },
  "military": {
    ...
  },
  "diplomacy": {
    ...
  },
  "key_actions": [
    ...
  ]
}
```