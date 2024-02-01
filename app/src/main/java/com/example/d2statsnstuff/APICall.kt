package com.example.d2statsnstuff

import com.google.ar.core.Future
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request

import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors

class APICall : Callable<String> {
    @Volatile
    private var json: String = ""
    private val apiKey = "c89e72284bc84b759fb941779ae84a3f"
    private val executor = Executors.newCachedThreadPool()
    private val client = OkHttpClient()
    private var url: String
    private var post = ""
    private var query = ""

    constructor(url: String) {
        this.url = url
    }

    constructor(url: String, post: String, query: String) {
        this.url = url
        this.post = post
        this.query = query
    }

    fun makeCall(): JSONObject {
        val jsonReturn: JSONObject
        val fute = executor.submit(this)
        println("  " + json)
        println(fute.get().toString())
        jsonReturn = try {
            fute.get()
            JSONObject(json)
        } catch (e: ExecutionException) {
            throw RuntimeException(e)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        return jsonReturn
    }

    override fun call(): String? {
        println("making call     $url")
        var request: Request? = null
        if (!post.isEmpty()) {
            val body: RequestBody = post.toRequestBody(JSON)
            request = Request.Builder()
                .url(url)
                .header("X-API-Key", apiKey)
                .post(body)
                .build()
        } else if (!query.isEmpty()) {
        } else {
            request = Request.Builder()
                .url(url)
                .header("X-API-Key", apiKey)
                .build()
        }
//        if (request != null) {
//            client.newCall(request).enqueue(responseCallback = object: Callback
//                    {
//                override fun onFailure(call: Call, e: IOException) {
//                    println(e.message)
//                }
//
//                        override fun onResponse(call: Call, response: Response) {
//                            println(json)
//                            json = response.body.toString()
//                        }
//                    })
//
//        }

        json = try {
            val responce = client.newCall(request!!).execute()
            responce.body!!.string()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        return json
    }

    companion object {
        val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    }
}