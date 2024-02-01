package com.example.d2statsnstuff.data

import org.json.JSONArray

class IornBannerStats(IBStats: JSONArray, charId: String, manifest: Manifest) : CrucibleMatchStats(IBStats, charId,
    manifest
)