package com.example.d2statsnstuff.data

class CharacterStats(
    classType: String,
    characterId: String,
    iBStats: IornBannerStats,
    compStats: CompStats,
    trialsStats: TrialsStats,
    controlStats: ControlStats
) {
    private val compStats: CompStats
    private val trialsStats: TrialsStats
    private val controlStats: ControlStats
    private val iBStats: IornBannerStats
    // "0"-Titan "1"-Hunter  or "2"-Warlock
    val classType: String
    val characterId: String

    init {
        this.characterId = characterId
        this.classType = classType
        this.iBStats = iBStats
        this.iBStats.charID = characterId
        this.compStats = compStats
        this.compStats.charID = characterId
        this.trialsStats = trialsStats
        this.trialsStats.charID = characterId
        this.controlStats = controlStats
        this.controlStats.charID = characterId
    }
    //pass control, comp, trials, or ib
    fun getByMode(mode: String): CrucibleMatchStats {
        if (mode == "control") {
            return controlStats
        } else if (mode == "comp") {
            return compStats
        } else if (mode == "trials") {
            return trialsStats
        } else {
            return iBStats
        }
    }

}