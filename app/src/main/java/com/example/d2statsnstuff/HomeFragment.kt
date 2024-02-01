package com.example.d2statsnstuff

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.fragment.app.Fragment
import com.example.d2statsnstuff.data.*
import com.example.d2statsnstuff.data.Manifest
import kotlinx.coroutines.Runnable
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.Future

class HomeFragment : Fragment() {
    private val executor = Executors.newCachedThreadPool()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // In Compose world
                autoFill()
            }
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun autoFill() {


        //Collecting states from ViewModel
        var searchText by remember { mutableStateOf("") }
        var isSearching by remember { mutableStateOf(false) }
        val names = remember {
            mutableStateListOf<Player>()
        }

        Scaffold(
            topBar = {
                SearchBar(
                    query = searchText,//text showed on SearchBar
                    onQueryChange = {
                        names.clear()
                        val task = Runnable { val userInfo = APICall(
                            "https://www.bungie.net/platform/User/Search/GlobalName/0/",
                            "{ \"displayNamePrefix\": \"$it\" }",
                            ""
                        )
                            try {
                                val result = executor.submit(userInfo)
                                        val jsonObjUser = JSONObject(result.get())
                                        val returnArray = jsonObjUser.getJSONObject("Response").getJSONArray("searchResults")
                                var length = returnArray.length()
                                if(length > 5) {
                                    length = 5
                                }
                                for (i in 0..<length) {
                                    names.add(Player(returnArray.getJSONObject(i)))
                                }

                            }catch (e: JSONException) {
                                print(e.message)
                            }

                            }
                        Handler(Looper.getMainLooper()).post(task)

                        searchText = it
                    }, //update the value of searchText
                    onSearch = {
                        if(names.size != 0) {
                            search(searchText)
                            isSearching = true
                        }
                    }, //the callback to be invoked when the input service triggers the ImeAction.Search action
                    active = isSearching, //whether the user is searching or not
                    onActiveChange = {
                        isSearching = it
                                     }, //the callback to be invoked when this search bar's active state is changed
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    placeholder = {
                        Text(text = "rawr_XD (TIMELOST)")
                    },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
                    },
                    trailingIcon = {
                        if(isSearching) {
                            Icon(
                                modifier = Modifier.clickable {
                                    if (searchText.isEmpty()) {
                                        searchText = ""
                                    } else {
                                        isSearching = false
                                    }
                                },
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Icon"
                            )
                        }
                    }
                ) {
                    names.forEach {
                       Row(modifier = Modifier.padding(all = 11.dp)) {

//                           AsyncImage(
//                                model = "https://www.bungie.net" + it.iconPath,
//                               contentDescription = "player Icon"
//
//                           )
                           Text(
                               text = it.name,
                               modifier = Modifier.clickable {
                                   searchText = it.name
                                   ImeAction.Search
                               },
                           )
                       }
                    }
                }
            }
        ) {

        }

    }


    @Preview
    @Composable
    fun PreviewHomeMenu() {
        autoFill()
    }


     private fun search(bngName: String) {
        executor.shutdown()
         val userInfo = APICall(
            "https://www.bungie.net/platform/User/Search/GlobalName/0/",
            "{ \"displayNamePrefix\": \"$bngName\" }",
            ""
        )
        try {

            val jsonObjUser = userInfo.makeCall()
            val displayName = jsonObjUser.getJSONObject("Response").getJSONArray("searchResults").getJSONObject(0).getString("bungieGlobalDisplayName")
            var membershipId = jsonObjUser.getJSONObject("Response").getJSONArray("searchResults").getJSONObject(0).getString("bungieNetMembershipId")
            val profile = getUserProfile(membershipId)
            val membershipType = profile.getJSONObject("Response").getJSONArray("destinyMemberships").getJSONObject(0).getString("LastSeenDisplayNameType")
            membershipId =
                if (profile.getJSONObject("Response").getJSONArray("destinyMemberships").length() == 1) {
                    profile.getJSONObject("Response").getJSONArray("destinyMemberships").getJSONObject(0).getString("membershipId")
                } else {
                    profile.getJSONObject("Response").getString("primaryMembershipId")
                }
            //overall stats
            val accountJson = getAccount(membershipType, membershipId)
            val characterJson = getCharacters(membershipType, membershipId)
            val numChars = accountJson.getJSONObject("Response").getJSONArray("characters").length()
            var char1: CharacterStats? = null
            var char2: CharacterStats? = null
            var char3: CharacterStats? = null
            val manifest = Manifest()
            for (i in 0 until numChars) {
                when (i) {
                    0 -> {
                        val tempId = accountJson.getJSONObject("Response").getJSONArray("characters").getJSONObject(0).getString("characterId")
                        val tempCharType = characterJson.getJSONObject("Response").getJSONObject("characters").getJSONObject("data").getJSONObject(tempId).getString("classType")
                        char1 = CharacterStats(
                            classType = tempCharType,
                            characterId = tempId,
                            iBStats = IornBannerStats( getActivityIds(IORNBANNER, tempId, membershipType, membershipId), tempId, manifest ),
                            compStats = CompStats( getActivityIds(COMP, tempId, membershipType, membershipId), tempId, manifest ),
                            trialsStats = TrialsStats( getActivityIds(TRIALS, tempId, membershipType, membershipId), tempId, manifest ),
                            controlStats = ControlStats( getActivityIds(CONTROL, tempId, membershipType, membershipId), tempId, manifest )
                        )
                    }
                    1 -> {
                        val tempId = accountJson.getJSONObject("Response").getJSONArray("characters").getJSONObject(1).getString("characterId")
                        val tempCharType = characterJson.getJSONObject("Response").getJSONObject("characters").getJSONObject("data").getJSONObject(tempId).getString("classType")
                        char2 = CharacterStats(
                            classType = tempCharType,
                            characterId = tempId,
                            iBStats = IornBannerStats( getActivityIds(IORNBANNER, tempId, membershipType, membershipId), tempId, manifest ),
                            compStats = CompStats( getActivityIds(COMP, tempId, membershipType, membershipId), tempId , manifest),
                            trialsStats = TrialsStats( getActivityIds(TRIALS, tempId, membershipType, membershipId), tempId, manifest ),
                            controlStats = ControlStats( getActivityIds(CONTROL, tempId, membershipType, membershipId), tempId , manifest)
                        )
                    }
                    else -> {
                        val tempId = accountJson.getJSONObject("Response").getJSONArray("characters").getJSONObject(2).getString("characterId")
                        val tempCharType = characterJson.getJSONObject("Response").getJSONObject("characters").getJSONObject("data").getJSONObject(tempId).getString("classType")
                        char3 = CharacterStats(
                            classType = tempCharType,
                            characterId = tempId,
                            iBStats = IornBannerStats( getActivityIds(IORNBANNER, tempId, membershipType, membershipId), tempId, manifest ),
                            compStats = CompStats( getActivityIds(COMP, tempId, membershipType, membershipId), tempId , manifest),
                            trialsStats = TrialsStats( getActivityIds(TRIALS, tempId, membershipType, membershipId), tempId , manifest),
                            controlStats = ControlStats( getActivityIds(CONTROL, tempId, membershipType, membershipId), tempId , manifest)
                        )
                    }
                }
            }
            val allStats = StatsDTO(displayName, char1, char2, char3)
            mListener!!.onSearch(allStats)
        } catch (e: JSONException) {
            println(e.message)
        } catch (e: ParseException) {
            println(e.message)
            }

    }



    private fun getCharacters(memType: String, memId: String): JSONObject {
        val getAccount =
            APICall("https://www.bungie.net/platform/Destiny2/$memType/Profile/$memId/?components=200")
        return getAccount.makeCall()
    }

    private fun getAccount(memType: String, memId: String): JSONObject {
        val getAccount =
            APICall("https://www.bungie.net/platform/Destiny2/$memType/Account/$memId/Stats/")
        return getAccount.makeCall()
    }

    private fun getUserProfile(memId: String): JSONObject {
        val getUserProfile =
            APICall("https://www.bungie.net/platform/User/GetMembershipsById/$memId/254/")
        return getUserProfile.makeCall()
    }

    @Throws(JSONException::class, ParseException::class)
    private fun getActivityIds(
        mode: Int,
        charId: String,
        memType: String,
        memId: String
    ): JSONArray {
        var page = 0
        println(charId)
        val json = getActivityHistory(mode, charId, memType, memId, page)
        println(json)
        val activities = json.getJSONObject("Response").getJSONArray("activities")
        var gotAll = false
        var lastInSession = 0
        val out = JSONArray()
        while (!gotAll) {
            for (i in 0 until activities.length() - 1) {
                println(activities.getJSONObject(i).getString("period"))
                println(activities.getJSONObject(i + 1).getString("period"))
                val formater = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                val lastDate = formater.parse(activities.getJSONObject(i).getString("period"))
                val thisDate = formater.parse(activities.getJSONObject(i + 1).getString("period"))
                val minus2Hours = lastDate.time - 7200000
                lastInSession = if (thisDate.after(Date(minus2Hours))) {
                    i
                } else {
                    break
                }
            }
            when (lastInSession) {
                activities.length() - 1 -> {
                    val newJson = getActivityHistory(mode, charId, memType, memId, ++page)
                    val newActivities = newJson.getJSONObject("Response").getJSONArray("activities")
                    for (j in 0 until newActivities.length()) {
                        activities.put(newActivities.getJSONObject(j))
                    }
                }
                0 -> {
                    gotAll = true
                    out.put(activities.getJSONObject(0))
                }
                else -> {
                    gotAll = true
                    for (i in 0 until lastInSession) {
                        out.put(activities.getJSONObject(i))
                    }
                }
            }
        }
        return out
    }

    private fun getActivityHistory(
        mode: Int,
        charId: String,
        memType: String,
        memId: String,
        page: Int
    ): JSONObject {
        var modeParam = "&mode=$mode"
        if (mode == 0) {
            modeParam = ""
        }
        val getActivityHistory =
            APICall("https://www.bungie.net/platform/Destiny2/$memType/Account/$memId/character/$charId/Stats/Activities/?page=$page$modeParam")
        return getActivityHistory.makeCall()
    }

    private var mListener: HomeFragmentListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mListener = if (context is HomeFragmentListener) {
            context
        } else {

            throw RuntimeException("$context must implement ProductsListener")
        }
    }

    internal interface HomeFragmentListener {
        fun onLogIn()
        fun onSearch(allStats: StatsDTO)
    }
    
    
    

    companion object {
        const val IORNBANNER = 19
        const val TRIALS = 84
        const val COMP = 69
        const val CONTROL = 10
    }
}