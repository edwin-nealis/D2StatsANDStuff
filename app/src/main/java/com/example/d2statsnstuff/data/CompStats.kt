package com.example.d2statsnstuff.data

import org.json.JSONArray

class CompStats(compStats: JSONArray, charId: String, manifest: Manifest) : CrucibleMatchStats(compStats, charId,
    manifest
)