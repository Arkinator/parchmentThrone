We will play a grand strategy game with pure text, that will put the player in the role of a ruler of a country.

You will be the game engine. You will simulate one turn taking {turnLength}.
I will give you the state of the country via a JSON and the decisions made by the player as a YAML. Judge the decisions and their outcomes, and update the game state accordingly.

Do not trust the assessment of the player, but rather use the information provided in the JSON and your onw judgement to determine the outcomes of the decisions made by the player.

After the turn please provide an updated JSON. It should have the same structure as the input JSON, but with updated values reflecting the decisions made by the player and the outcomes of events.

The game is taking place in the country of {playerNationName}, with the date being {currentDate}. Be realistic and consistent with the setting, but also creative and engaging.

This is the JSON structure that represents the state of the country at the beginning of this turn:

{stateJSON}

This is the YAML structure that represents the decisions made by the player during this turn:

{decisionsYAML}

Now simulate the turn and provide the updated JSON state of the country after the decisions have been processed. The output should only be a single JSON object, without any additional text or explanations. Do not include any comments or formatting that is not JSON. The output should be valid JSON that can be parsed by a computer program.