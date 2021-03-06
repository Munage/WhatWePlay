package steamapi

import timeUtils.TimeUtils

/**
 * This service is responsible for formatting/modifying/extracting information obtained from the Steam API and
 * providing usable service methods for the controller layer.
 */
//TODO: Refactor service name to be more indicative of it's purpose.
class SteamGameService {

    def steamUserService

    /**
     *  Gets a list of games played, in the past two weeks, by a specific user.
     * @param id The user's steam ID.
     * @return A list of games played.
     */
    def getRecentlyPlayed(String id) {
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

    /**
     * Iterate through all friends and calculate the total time played for each game and a per player breakdown.
     * @param PlayerMap A map of players of which to collate play information of.
     * @return A map containing allGamesPlayed and playerBreakdown
     */
    //TODO: Refactor. Seriously. Split up into 2 methods? DRY.
    def getFriendsGamesPlayed2weeks(Map PlayerMap) {
        if (!PlayerMap) {
            return null
        }

        Map allGamesPlayed = [:]
        Map players = [:]
        def friendGames

        //Iterate through all players
        PlayerMap.friendslist.friends.each {
            def profile = steamUserService.getProfileInformation(it.steamid)["response"]["players"]
            friendGames = steamUserService.getRecentlyPlayed(it.steamid)

            //Iterate through each game the user played
            friendGames["response"]["games"].each {
                if (allGamesPlayed.containsKey(it.name)) {
                    allGamesPlayed[it.name]["playtime_2weeks"] += it["playtime_2weeks"]
                    allGamesPlayed[it.name]["playtime_forever"] += it["playtime_forever"]

                    List playerList = players[it.name]
                    playerList.add([profile[0], ["playtime_2weeks": TimeUtils.prettifyTime(it["playtime_2weeks"])]])
                    players[it.name] = playerList
                } else {
                    allGamesPlayed.put(it.name, it)
                    List player = []
                    player.add([profile[0], ["playtime_2weeks": TimeUtils.prettifyTime(it["playtime_2weeks"])]])
                    players.put(it.name, player)
                }
            }
        }

        //Sort by time played in desc order
        allGamesPlayed = allGamesPlayed.sort { a, b ->
            b.value["playtime_2weeks"]<=>a.value["playtime_2weeks"]
        }

        //change the format of the time to hours and minutes
        allGamesPlayed.each {
            it.value["playtime_2weeks"] = TimeUtils.prettifyTime(it.value["playtime_2weeks"])
        }

        return ["allGamesPlayed": allGamesPlayed, "playerBreakdown": players]
    }
}
