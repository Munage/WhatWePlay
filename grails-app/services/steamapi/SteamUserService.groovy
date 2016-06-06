package steamapi

/**
 * This is the base service that interfaces directly with the Steam API.
 */
//TODO: Refactor service name to be more indicative of it's purpose.
class SteamUserService {

    def restApiService
    def grailsApplication

    def getFriendsList(String id){
        if(!id){
            return null
        }

        Map q = [key: grailsApplication.config.steam.api.key, steamid:id, "relationship":"friend", format:"json"]
        def response = restApiService.request(grailsApplication.config.steam.api.url, "/ISteamUser/GetFriendList/v0001", q)
    }

    def getProfileInformation(def id){
        if(!id){
            return null
        }

        Map q = [key: grailsApplication.config.steam.api.key, steamids:id, format:"json"]
        def response = restApiService.request(grailsApplication.config.steam.api.url, "/ISteamUser/GetPlayerSummaries/v0002", q)
        return response
    }

    def getRecentlyPlayed(String id){
        if(!id){
            return null
        }

        Map q = [key: grailsApplication.config.steam.api.key, steamid:id, format:"json"]
        def response = restApiService.request(grailsApplication.config.steam.api.url, "/IPlayerService/GetRecentlyPlayedGames/v0001", q)
    }

    Map createResponseMap(Map params){
        Map responseMap = [:]

        params.each {
            if(it.key.toString().contains("openid")){
                responseMap.put(it.key, it.value)
            }
        }

        return responseMap
    }
}
