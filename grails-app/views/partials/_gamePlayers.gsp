<g:each in="${playerBreakDown}" status="i" var="game">
    <div class="game_players ${game.key.toString().replaceAll(' ', '').replaceAll(":", "")}" style="display: none;">
        <h3 style="color: #ffffff">${game.key}</h3>
        <br/>
        <g:each in="${game.value}" status="x" var="player">
            %{--persona name is what comes back from the steam api - its not a typo--}%
            <span style="color: #ffffff">${player[0].personaname} - ${player[1]["playtime_2weeks"]}</span>
            </br>
        </g:each>
    </div>
</g:each>