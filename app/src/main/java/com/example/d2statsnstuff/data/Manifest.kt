package com.example.d2statsnstuff.data

import com.example.d2statsnstuff.APICall
import org.json.JSONObject

class Manifest() {
    var manifest:JSONObject
    init {
        this.manifest = findManifest()
    }
    private fun findManifest(): JSONObject {
        val getOverall = APICall("https://www.bungie.net/Platform/Destiny2/Manifest")
        val overall = getOverall.makeCall()
        val getManifest = APICall(
            "https://www.bungie.net" + overall.getJSONObject("Response")
                .getJSONObject("jsonWorldComponentContentPaths").getJSONObject("en")
                .getString("DestinyInventoryItemLiteDefinition")
        )
        return getManifest.makeCall()
    }
}