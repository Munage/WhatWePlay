package steamapi

class HomeController {

    def steamUserService
    def steamGameService

    def index() {
        session.steamId = "76561198041210011" //TODO: remove
        String userId = "76561198041210011" //session.steamId ?: params.steamId
        if(!userId) {
            return
        }

        def userRecentlyPlayed
        if(session.userRecentlyPlayed){
            userRecentlyPlayed = session.userRecentlyPlayed
        } else {
            userRecentlyPlayed = steamGameService.getRecentlyPlayed(userId)
            session.userRecentlyPlayed = userRecentlyPlayed
        }

        def friendList
        Map friendsRecentlyPlayed
        if(session.friendsRecentlyPlayed){
            friendsRecentlyPlayed = session.friendsRecentlyPlayed
            friendList = session.friendList
        } else {
            friendList = steamUserService.getFriendsList(userId)

            friendsRecentlyPlayed = steamGameService.getFriendsGamesPlayed2weeks(friendList)

            session.friendList = friendList
            session.friendsRecentlyPlayed = friendsRecentlyPlayed
        }

        [result: userRecentlyPlayed, friendsGames: friendsRecentlyPlayed.allGamesPlayed,
                playerBreakDown: friendsRecentlyPlayed.playerBreakdown]
    }

    def login(){
        SteamOpenID steamOpenID = new SteamOpenID()
        redirect(url: steamOpenID.login(grailsApplication.config.grails.serverURL + "/home/verify"))
    }

    def verify(){
        SteamOpenID steamOpenID = new SteamOpenID()
        Map responseMap = steamUserService.createResponseMap(params)
        session.steamId = steamOpenID.verify(grailsApplication.config.grails.serverURL + "/home/verify", ((Map)responseMap))
        Utilities.setCookie(response, grailsApplication.config.preference.cookieName, session.steamId)

        redirect(action: "index")
    }

    def logout(){
        session.invalidate()
        Utilities.removeCookie(response, grailsApplication.config.preference.cookieName)
        redirect(action: "index")
    }

    def refresh(){
        session.invalidate()
        redirect(action: "index")
    }
}

