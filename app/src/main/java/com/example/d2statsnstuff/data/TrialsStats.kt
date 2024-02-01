package com.example.d2statsnstuff.data

import org.json.JSONArray

class TrialsStats(stats: JSONArray, charId: String, manifest: Manifest) : CrucibleMatchStats(stats, charId,
    manifest
)