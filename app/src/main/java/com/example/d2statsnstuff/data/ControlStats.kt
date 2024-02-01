package com.example.d2statsnstuff.data

import org.json.JSONArray

class ControlStats(controlStats: JSONArray, charId: String, manifest: Manifest) :
    CrucibleMatchStats(controlStats, charId, manifest)