<%@ page contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>What We Play</title>
        <meta name="layout" content="customLayout"/>
    </head>
<body>


    <g:if test="${session.steamId}">
        <g:link style="float: right; padding-right: 25px;" action="logout"><h4>Log out</h4></g:link>
    </g:if>

    <g:if test="${!session.steamId}">
        <div class="center">
            <h2>Please sign in with Steam to see what you and your friends have been playing</h2>
            <g:link controller="home" action="login"><asset:image src="sits_large_border.png"/></g:link>
        </div>
    </g:if>
    <g:else>
        <div class="left_col">
            <g:link controller="home" action="refresh">
                <h2>What you've been playing - Past 2 weeks</h2>
            </g:link>
            <table class="games">
                <thead>
                    <tr>
                        <td></td>
                        <td></td>
                        <td>Time Played</td>

                    </tr>
                </thead>

                <g:each in="${result}">
                        <tr>
                            <td><img src="${grailsApplication.config.steam.images.url + "/" + it.value.appid + "/" + it.value.img_logo_url + ".jpg"}"></td>
                            <td>${it?.key}</td>
                            <td>${it?.value.playtime_2weeks}</td>
                        </tr>
                    </g:each>

            </table>

            <h2>What your friends are playing - Past 2 weeks</h2>
            <table class="games">
                <thead>
                <tr>
                    <td></td>
                    <td></td>
                    <td>Time Played</td>
                </tr>
                </thead>



                <g:each in="${friendsGames}">
                    <tr onclick="displayPlayersForGame('${it.key.toString().replaceAll(' ', '').replaceAll(":", "")}');">
                        <td><img src="${grailsApplication.config.steam.images.url + "/" + it.value.appid + "/" + it.value.img_logo_url + ".jpg"}"></td>
                        <td>${it.key}</td>
                        <td>${it.value.playtime_2weeks}</td>
                    </tr>
                </g:each>
            </table>
        </div>

        <div class="right_col">
            <table class="players">
                <g:render template="/partials/gamePlayers" bean="${playerBreakDown}" />
            </table>
        </div>
    </g:else>
</body>
</html>