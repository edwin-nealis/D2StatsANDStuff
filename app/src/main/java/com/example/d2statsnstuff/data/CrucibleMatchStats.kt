package com.example.d2statsnstuff.data

import com.example.d2statsnstuff.APICall
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.stream.Collector
import java.util.stream.Collectors

abstract class CrucibleMatchStats(stats: JSONArray, var charID: String, manifest: Manifest) {
    private val executor = Executors.newCachedThreadPool()
    var numMercy = 0
    var numMatches = 0
    var playerKills = 0
    var teamMateKills = 0
    var enemyKills = 0
    var teamMateDeaths = 0
    var playerDeaths = 0
    var enemyDeaths = 0
    var numWins = 0
    var numLoses = 0
    var numTies = 0
    var numGamesWithDF = 0
    var playerDFs = 0
    var enemyDFs = 0
    var teamMateDFs = 0
    var playerKD = 0.0
    var playerKDA = 0.0
    var teamMateKD = 0.0
    var teamMateKDA = 0.0
    var enemyKD = 0.0
    var enemyKDA = 0.0
    var playerAccountKD = 0.0
    var teamMateAccountKD = 0.0
    var enemyAccountKD = 0.0
    var playerAccountKDA = 0.0
    var teamMateAccountKDA = 0.0
    var enemyAccountKDA = 0.0
    var playerPrimary = 0
    var playerSpecial = 0
    var playerHeavy = 0
    var playerSuper = 0
    var playerGrenade = 0
    var playerAbility = 0
    var teamMatePrimary = 0
    var teamMateSpecial = 0
    var teamMateHeavy = 0
    var teamMateSuper = 0
    var teamMateGrenade = 0
    var teamMateAbility = 0
    var playerMelee = 0
    var teamMateMelee = 0
    var enemyMelee = 0
    var playerTotalWeapon = 0
    var enemyTotalWeapon = 0
    var teamMateTotalWeapon = 0
    var enemyPrimary = 0
    var enemySpecial = 0
    var enemyHeavy = 0
    var enemySuper = 0
    var enemyGrenade = 0
    var enemyAbility = 0

    init {
        var mercyCount = 0
        val calls = ArrayList<Callable<String>>()
        for (i in 0 until stats.length()) {
            val match = stats.getJSONObject(i)
            if (match.getJSONObject("values").getJSONObject("completionReason")
                    .getJSONObject("basic").getString("displayValue") == "4"
            ) {
                mercyCount++
            }
            println(match)
            calls.add(APICall(                   "https://www.bungie.net/Platform/Destiny2/Stats/PostGameCarnageReport/" + match.getJSONObject(
                        "activityDetails"
                   ).getString("instanceId") + "?definitions=true"))
        }

        val results = executor.invokeAll(calls)
        val th = results.stream().map { JSONObject(it.get()) }.collect(Collectors.toList())
        var teamMates = JSONArray()
        var enemys = JSONArray()
        val player = JSONArray()
        var winCount = 0
        var loseCount = 0
        var tieCount = 0
        for (i in th.indices) {
            val team1 = JSONArray()
            val team2 = JSONArray()
            val team1Name = th[0].getJSONObject("Response").getJSONArray("teams").getJSONObject(0)
                .getString("teamName")
            var isPlayerTeam1 = false
            for (ii in 0 until th[i].getJSONObject("Response").getJSONArray("entries").length()) {
                val teamName = th[i].getJSONObject("Response").getJSONArray("entries").getJSONObject(ii)
                    .getJSONObject("values").getJSONObject("team").getJSONObject("basic").getString("displayValue")
                if (teamName == team1Name) {
                    val charIdTemp =
                        th[i].getJSONObject("Response").getJSONArray("entries").getJSONObject(ii).getString("characterId")
                    val standing =
                        th[i].getJSONObject("Response").getJSONArray("entries").getJSONObject(ii)
                            .getString("standing")
                    if (charIdTemp == charID) {
                        when (standing) {
                            "0" -> {
                                winCount++
                            }
                            "1" -> {
                                loseCount++
                            }
                            else -> {
                                tieCount++
                            }
                        }
                        player.put(
                            th[i].getJSONObject("Response").getJSONArray("entries")
                                .getJSONObject(ii)
                        )
                        isPlayerTeam1 = true
                    } else {
                        team1.put(
                            th[i].getJSONObject("Response").getJSONArray("entries")
                                .getJSONObject(ii)
                        )
                    }
                } else {
                    val charIdTemp =
                        th[i].getJSONObject("Response").getJSONArray("entries").getJSONObject(ii)
                            .getString("characterId")
                    if (charIdTemp == charID) {
                        val standing = th[i].getJSONObject("Response").getJSONArray("entries")
                            .getJSONObject(ii).getString("standing")
                        when (standing) {
                            "0" -> {
                                winCount++
                            }
                            "1" -> {
                                loseCount++
                            }
                            else -> {
                                tieCount++
                            }
                        }
                        player.put(
                            th[i].getJSONObject("Response").getJSONArray("entries")
                                .getJSONObject(ii)
                        )
                        isPlayerTeam1 = false
                    } else {
                        team2.put(
                            th[i].getJSONObject("Response").getJSONArray("entries")
                                .getJSONObject(ii)
                        )
                    }
                }
            }
            if (i == 0) {
                if (isPlayerTeam1) {
                    teamMates = team1
                    enemys = team2
                } else {
                    teamMates = team2
                    enemys = team1
                }
            } else {
                if (isPlayerTeam1) {
                    addArray(teamMates, team1)
                    addArray(enemys, team2)
                } else {
                    addArray(teamMates, team2)
                    addArray(enemys, team1)
                }
            }
        }
        numTies = tieCount
        numWins = winCount
        numLoses = loseCount
        numMercy = mercyCount
        getStats(player, "p", manifest)
        getStats(enemys, "E", manifest)
        getStats(teamMates, "TM", manifest)
    }

    @Throws(JSONException::class)
    private fun addArray(a: JSONArray, b: JSONArray) {
        for (i in 0 until b.length()) {
            a.put(b.getJSONObject(i))
        }
    }

    @Throws(JSONException::class)
    private fun getStats(player: JSONArray, team: String, manifest: Manifest) {

        var primaryKills = 0
        var specialKills = 0
        var heavyKills = 0
        var allKills = 0
        var grenadeKills = 0
        var meleeKills = 0
        var superKills = 0
        var otherAbilities = 0
        var deaths = 0
        var kd = 0.0
        var kda = 0.0
        var quit = 0
        val size = player.length().toDouble()
        var i = 0
        while (i < size) {
            if (player.getJSONObject(i).getJSONObject("extended").has("weapons")) {
                for (j in 0..<player.getJSONObject(i).getJSONObject("extended")
                    .getJSONArray("weapons").length()) {
                    val name = manifest.manifest.getJSONObject(
                        player.getJSONObject(i).getJSONObject("extended").getJSONArray("weapons")
                            .getJSONObject(j).getString("referenceId")
                    )?.getJSONObject("displayProperties")?.getString("name")
                    if (name == "Classified") {
                        continue
                    }
                    val itemAmmoType = manifest.manifest.getJSONObject(
                        player.getJSONObject(i).getJSONObject("extended").getJSONArray("weapons")
                            .getJSONObject(j).getString("referenceId")
                    )?.getJSONObject("equippingBlock")?.getString("ammoType")

                    val weaponKills =
                        player.getJSONObject(i).getJSONObject("extended").getJSONArray("weapons")
                            .getJSONObject(j).getJSONObject("values")
                            .getJSONObject("uniqueWeaponKills").getJSONObject("basic")
                            .getString("displayValue").toInt()
                    println(weaponKills)
                    when (itemAmmoType) {
                        "1" -> {
                            primaryKills += weaponKills
                        }
                        "2" -> {
                            specialKills += weaponKills
                        }
                        "3" -> {
                            heavyKills += weaponKills
                        }
                    }
                }
            }
                allKills += player.getJSONObject(i).getJSONObject("values").getJSONObject("kills")
                    .getJSONObject("basic").getString("displayValue").toInt()
                grenadeKills += player.getJSONObject(i).getJSONObject("extended")
                    .getJSONObject("values").getJSONObject("weaponKillsGrenade")
                    .getJSONObject("basic").getString("displayValue").toInt()
                meleeKills += player.getJSONObject(i).getJSONObject("extended")
                    .getJSONObject("values").getJSONObject("weaponKillsMelee")
                    .getJSONObject("basic").getString("displayValue").toInt()
                superKills += player.getJSONObject(i).getJSONObject("extended")
                    .getJSONObject("values").getJSONObject("weaponKillsSuper")
                    .getJSONObject("basic").getString("displayValue").toInt()
                otherAbilities += player.getJSONObject(i).getJSONObject("extended")
                    .getJSONObject("values").getJSONObject("weaponKillsAbility")
                    .getJSONObject("basic").getString("displayValue").toInt()
                deaths += player.getJSONObject(i).getJSONObject("values").getJSONObject("deaths")
                    .getJSONObject("basic").getString("displayValue").toInt()
                kd += player.getJSONObject(i).getJSONObject("values")
                    .getJSONObject("killsDeathsRatio").getJSONObject("basic")
                    .getString("displayValue").toDouble()
                kda += player.getJSONObject(i).getJSONObject("values")
                    .getJSONObject("killsDeathsAssists").getJSONObject("basic")
                    .getString("displayValue").toDouble()
                if (player.getJSONObject(i).getJSONObject("values").getJSONObject("completed")
                        .getJSONObject("basic").getString("displayValue") == "0"
                ) {
                    quit++
                }
            i++
        }
        when (team) {
            "p" -> {
                playerKills = allKills
                playerKD = kd / size
                playerAbility = otherAbilities
                playerDeaths = deaths
                playerKDA = kda / size
                playerDFs = quit
                playerGrenade = grenadeKills
                playerHeavy = heavyKills
                playerPrimary = primaryKills
                playerSpecial = specialKills
                playerSuper = superKills
                playerMelee = meleeKills
            }
            "E" -> {
                enemyKills = allKills
                enemyKD = kd / size
                enemyAbility = otherAbilities
                enemyDeaths = deaths
                enemyKDA = kda / size
                enemyDFs = quit
                enemyGrenade = grenadeKills
                enemyHeavy = heavyKills
                enemyPrimary = primaryKills
                enemySpecial = specialKills
                enemySuper = superKills
                enemyMelee = meleeKills
            }
            else -> {
                teamMateKills = allKills
                teamMateKD = kd / size
                teamMateAbility = otherAbilities
                teamMateDeaths = deaths
                teamMateKDA = kda / size
                teamMateDFs = quit
                teamMateGrenade = grenadeKills
                teamMateHeavy = heavyKills
                teamMatePrimary = primaryKills
                teamMateSpecial = specialKills
                teamMateSuper = superKills
                teamMateMelee = meleeKills
            }
        }
    }

    override fun toString(): String {
        return "CrucibleMatchStats(charID='$charID', numMercy=$numMercy, numMatches=$numMatches, playerKills=$playerKills, teamMateKills=$teamMateKills, enemyKills=$enemyKills, teamMateDeaths=$teamMateDeaths, playerDeaths=$playerDeaths, enemyDeaths=$enemyDeaths, numWins=$numWins, numLoses=$numLoses, numTies=$numTies, numGamesWithDF=$numGamesWithDF, playerDFs=$playerDFs, enemyDFs=$enemyDFs, teamMateDFs=$teamMateDFs, playerKD=$playerKD, playerKDA=$playerKDA, teamMateKD=$teamMateKD, teamMateKDA=$teamMateKDA, enemyKD=$enemyKD, enemyKDA=$enemyKDA, playerAccountKD=$playerAccountKD, teamMateAccountKD=$teamMateAccountKD, enemyAccountKD=$enemyAccountKD, playerAccountKDA=$playerAccountKDA, teamMateAccountKDA=$teamMateAccountKDA, enemyAccountKDA=$enemyAccountKDA, playerPrimary=$playerPrimary, playerSpecial=$playerSpecial, playerHeavy=$playerHeavy, playerSuper=$playerSuper, playerGrenade=$playerGrenade, playerAbility=$playerAbility, teamMatePrimary=$teamMatePrimary, teamMateSpecial=$teamMateSpecial, teamMateHeavy=$teamMateHeavy, teamMateSuper=$teamMateSuper, teamMateGrenade=$teamMateGrenade, teamMateAbility=$teamMateAbility, playerMelee=$playerMelee, teamMateMelee=$teamMateMelee, enemyMelee=$enemyMelee, playerTotalWeapon=$playerTotalWeapon, enemyTotalWeapon=$enemyTotalWeapon, teamMateTotalWeapon=$teamMateTotalWeapon, enemyPrimary=$enemyPrimary, enemySpecial=$enemySpecial, enemyHeavy=$enemyHeavy, enemySuper=$enemySuper, enemyGrenade=$enemyGrenade, enemyAbility=$enemyAbility)"
    }

}