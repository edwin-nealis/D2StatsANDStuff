package com.example.d2statsnstuff.data

import com.example.d2statsnstuff.APICall
import org.json.JSONObject
import java.io.Serializable

class StatsDTO(
    val bngName: String,
    char1: CharacterStats?,
    char2: CharacterStats?,
    char3: CharacterStats?
) : Serializable {
    private var char1: CharacterStats?
    private var char2: CharacterStats?
    private var char3: CharacterStats?

    init {
        this.char1 = char1
        this.char2 = char2
        this.char3 = char3

    }

    //returns the CharacterStats object with given id
    fun getById(char : String): CharacterStats? {
        return if(char1?.characterId.equals(char)) {
            char1
        } else if (char2?.characterId.equals(char)) {
            char2
        } else if (char3?.characterId.equals(char)) {
            char3
        } else {
            char1
        }
    }
    // "0"-Titan "1"-Hunter  or "2"-Warlock
    fun getByRace(race : String): CharacterStats? {
        return if(char1?.classType.equals(race)) {
            char1
        } else if(char2?.classType.equals(race)) {
            char2
        } else if(char3?.classType.equals(race)) {
            char3
        } else {
            char1
        }
    }
}