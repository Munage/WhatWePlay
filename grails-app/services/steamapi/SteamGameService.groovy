package steamapi

import timeUtils.TimeUtils

class SteamGameService {

    def steamUserService


    def getMyRecentlyPlayed(String id){
        def result = steamUserService.getRecentlyPlayed(id)
        def final_result = [:]

        result["response"]["games"].each {
            final_result.put(it["name"], it)
        }

        //change the format of the time to hours and minutes
        final_result.each {
            it.value["playtime_2weeks"] = TimeUtils.prettifyTime(it.value["playtime_2weeks"])
        }

        return final_result
    }

    /*
        Iterate through all friends and populate games-played-list as well as profile-information
     */
    def getFriendsGamesPlayed2weeks(Map friendList) {
        if(!friendList){
            return null
        }

        Map allGamesPlayed = [:]
        Map players = [:]
        def friendGames

        //Loop through all friends on friends-list
        friendList.friendslist.friends.each {
            def profile = steamUserService.getProfileInformation(it.steamid)["response"]["players"]
            friendGames = steamUserService.getRecentlyPlayed(it.steamid)

            friendGames["response"]["games"].each{
                if(allGamesPlayed.containsKey(it.name)){
                    allGamesPlayed[it.name]["playtime_2weeks"] += it["playtime_2weeks"]
                    allGamesPlayed[it.name]["playtime_forever"] += it["playtime_forever"]

                    List playerList = players[it.name]
                    playerList.add(profile.personaname[0])
                    players[it.name] = playerList

                } else {
                    allGamesPlayed.put(it.name, it)
                    List player = []
                    player.add(profile.personaname[0])
                    players.put(it.name, player)
                }
            }
        }

        //Sort by time played in desc order
        allGamesPlayed = allGamesPlayed.sort { a, b ->
            b.value["playtime_2weeks"]<=> a.value["playtime_2weeks"]
        }

        //change the format of the time to hours and minutes
        allGamesPlayed.each {
            it.value["playtime_2weeks"] = TimeUtils.prettifyTime(it.value["playtime_2weeks"])
        }

        return ["allGamesPlayed":allGamesPlayed, "playerBreakdown": players]
    }
}
