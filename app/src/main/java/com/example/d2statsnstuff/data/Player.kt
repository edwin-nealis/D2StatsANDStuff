package com.example.d2statsnstuff.data


import org.json.JSONArray
import org.json.JSONObject

class Player(info: JSONObject) {
    var iconPath = ""
    var name = ""

    init{
        name = info.getString("bungieGlobalDisplayName")
        iconPath = info.getJSONArray("destinyMemberships").getJSONObject(0).getString("iconPath")
    }

}