package com.example.d2statsnstuff


import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import co.yml.charts.common.components.Legends
import co.yml.charts.common.model.AccessibilityConfig
import co.yml.charts.common.model.LegendLabel
import co.yml.charts.common.model.LegendsConfig
import co.yml.charts.common.model.PlotType
import co.yml.charts.ui.piechart.charts.DonutPieChart
import co.yml.charts.ui.piechart.models.PieChartConfig
import co.yml.charts.ui.piechart.models.PieChartData
import com.example.d2statsnstuff.data.StatsDTO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class SessionFragment : Fragment() {
    private var mAllStats: StatsDTO? = null
    private var context: Context? = null
    private var selectedChar: String = "0"
    private var selectedGameMode = "trials"
    private lateinit var playerData: PieChartData
    private lateinit var teamMateData: PieChartData
    private lateinit var enemyData: PieChartData
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mAllStats = requireArguments().getSerializable(ARG_PARAM_ALLSTATS) as StatsDTO?
            val data = setUpData()
            playerData = data[0]
            enemyData = data[1]
            teamMateData = data[2]
        }

    }

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
                PageView()
            }
        }
    }

    @Preview
    @Composable
    fun PreviewSessionFrag() {
        testData()
        PageView()
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun PageView() {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            var playerData by remember {
                mutableStateOf(playerData)
            }
            var enemyData by remember {
                mutableStateOf(enemyData)
            }
            var teamMateData by remember {
                mutableStateOf(teamMateData)
            }
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Row(modifier = Modifier.weight(1f)) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedChar = "0"
                            val data = setUpData()
                            playerData = data[0]
                            enemyData = data[1]
                            teamMateData = data[2]
                        },
                        content = {
                            Text(text = "Titan")
                        },
                        shape = CutCornerShape(5.dp)
                    )
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedChar = "1"
                            val data = setUpData()
                            playerData = data[0]
                            enemyData = data[1]
                            teamMateData = data[2]
                        },
                        content = {
                            Text(text = "Hunter")
                        },
                        shape = CutCornerShape(5.dp)
                    )
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedChar = "2"
                            val data = setUpData()
                            playerData = data[0]
                            enemyData = data[1]
                            teamMateData = data[2]
                        },
                        content = {
                            Text(text = "Warlock")
                        },
                        shape = CutCornerShape(5.dp)
                    )
                }
                Row(modifier = Modifier.weight(1f)) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedGameMode = "comp"
                            val data = setUpData()
                            playerData = data[0]
                            enemyData = data[1]
                            teamMateData = data[2]
                        },
                        content = {
                            Text(
                                text = "Comp",
                                softWrap = false

                            )
                        },
                        shape = CutCornerShape(5.dp)
                    )
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedGameMode = "ib"
                            val data = setUpData()
                            playerData = data[0]
                            enemyData = data[1]
                            teamMateData = data[2]
                        },
                        content = {
                            Text(text = "Iron Ba")
                        },
                        shape = CutCornerShape(5.dp)
                    )
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedGameMode = "trials"
                            val data = setUpData()
                            playerData = data[0]
                            enemyData = data[1]
                            teamMateData = data[2]
                        },
                        content = {
                            Text(text = "Trails")
                        },
                        shape = CutCornerShape(5.dp)
                    )
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            selectedGameMode = "control"
                            val data = setUpData()
                            playerData = data[0]
                            enemyData = data[1]
                            teamMateData = data[2]
                        },
                        content = {
                            Text(text = "Control")
                        },
                        shape = CutCornerShape(5.dp)
                    )
                }

                Legends(legendsConfig = legendsConfig)
            }
            val pagerState = rememberPagerState(pageCount = {
                2
            })
            val coroutineScope = rememberCoroutineScope()
            HorizontalPager(modifier = Modifier.weight(4f), state = pagerState) {
                if(pagerState.currentPage == 0) {
                    graphPage(playerData, enemyData, teamMateData)
                } else {
                    statsPage()
                }
            }
            PageIndicator(
                pagerState,
                coroutineScope
            )
        }
    }

    @Composable
    fun statsPage() {
        Column(modifier = Modifier) {
            val stats = mAllStats?.getByRace(selectedChar)?.getByMode(selectedGameMode)
            Text(color = Color.White, text = "stats")
        }
    }
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun PageIndicator(
        pagerState: PagerState,
        coroutineScope: CoroutineScope
    ) {
        Row(modifier = Modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom) {
            repeat(pagerState.pageCount) { page ->

                val color = pagerState.currentPage.let { if(page == it){
                    Color.LightGray
                } else{
                    Color.DarkGray
                }
                }
                // draw indicator
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(color)
                        .size(15.dp)
                        .clickable( onClick = { coroutineScope.launch {  pagerState.animateScrollToPage(page)} })
                )
            }
        }
    }

    @Composable
    fun graphPage(playerData: PieChartData, enemyData: PieChartData, teamMateData: PieChartData) {
        Column(
        ) {
            DonutPieChart(
                modifier = Modifier.weight(1f),
                pieChartData = playerData,
                pieChartConfig = pieChartConfig
            )
            DonutPieChart(
                modifier = Modifier.weight(1f),
                pieChartData = enemyData,
                pieChartConfig = pieChartConfig
            )
            DonutPieChart(
                modifier = Modifier.weight(1f),
                pieChartData = teamMateData,
                pieChartConfig = pieChartConfig,
                onSliceClick = {

                }
            )

        }
    }

    private val legendsConfig = LegendsConfig(
        legendLabelList = listOf(
            LegendLabel(name = "Primary", color = Color.White),
            LegendLabel(name = "Special", color = Color(0xFF5C7A41)),
            LegendLabel(name = "Heavy", color = Color.Magenta),
            LegendLabel(name = "Melee", color = Color(0xFFF7F600)),
            LegendLabel(name = "Grenade", color = Color(0xFF58C8F2)),
            LegendLabel(name = "Super", color = Color(0xFFED9CBF)),
        ),
        textStyle = TextStyle(color = Color.White),
        legendsArrangement = Arrangement.Center,
        gridColumnCount = 3,

        )

    private fun testData() {
        playerData = PieChartData(
            slices = listOf(
                PieChartData.Slice(label = "Primary", value = 12f, color = Color.White),
                PieChartData.Slice(label = "Special", value = 5f, color = Color(0xFF5C7A41)),
                PieChartData.Slice(label = "Heavy", value = 2f, color = Color.Magenta),
                PieChartData.Slice(label = "Melee", value = 1f, color = Color(0xFFF7F600)),
                PieChartData.Slice(label = "Grenade", value = 1f, color = Color(0xFF58C8F2)),
                PieChartData.Slice(label = "Super", value = 3f, color = Color(0xFFED9CBF)),
            ),
            plotType = PlotType.Donut
        )
        teamMateData = PieChartData(
            slices = listOf(
                PieChartData.Slice(label = "Primary", value = 12f, color = Color.White),
                PieChartData.Slice(label = "Special", value = 5f, color = Color(0xFF5C7A41)),
                PieChartData.Slice(label = "Heavy", value = 2f, color = Color.Magenta),
                PieChartData.Slice(label = "Melee", value = 1f, color = Color(0xFFF7F600)),
                PieChartData.Slice(label = "Grenade", value = 1f, color = Color(0xFF58C8F2)),
                PieChartData.Slice(label = "Super", value = 3f, color = Color(0xFFED9CBF)),
            ),
            plotType = PlotType.Donut
        )
        enemyData = PieChartData(
            slices = listOf(
                PieChartData.Slice(label = "Primary", value = 12f, color = Color.White),
                PieChartData.Slice(label = "Special", value = 5f, color = Color(0xFF5C7A41)),
                PieChartData.Slice(label = "Heavy", value = 2f, color = Color.Magenta),
                PieChartData.Slice(label = "Melee", value = 1f, color = Color(0xFFF7F600)),
                PieChartData.Slice(label = "Grenade", value = 1f, color = Color(0xFF58C8F2)),
                PieChartData.Slice(label = "Super", value = 3f, color = Color(0xFFED9CBF)),
            ),
            plotType = PlotType.Donut
        )
    }

    private fun setUpData(): List<PieChartData> {
        val modeStats = mAllStats?.getByRace(selectedChar)?.getByMode(selectedGameMode)
        if (modeStats != null) {
            return listOf(
                PieChartData(
                    slices = listOf(

                        PieChartData.Slice(
                            label = "Primary",
                            value = modeStats.playerPrimary.toFloat(),
                            color = Color.White
                        ),
                        PieChartData.Slice(
                            label = "Special",
                            value = modeStats.playerSpecial.toFloat(),
                            Color(0xFF5C7A41)
                        ),
                        PieChartData.Slice(
                            label = "Heavy",
                            value = modeStats.playerHeavy.toFloat(),
                            color = Color.Magenta
                        ),
                        PieChartData.Slice(
                            label = "Melee",
                            value = modeStats.playerMelee.toFloat(),
                            color = Color(0xFFF7F600)
                        ),
                        PieChartData.Slice(
                            label = "Grenade",
                            value = modeStats.playerGrenade.toFloat(),
                            color = Color(0xFF58C8F2)
                        ),
                        PieChartData.Slice(
                            label = "Super",
                            value = modeStats.playerSuper.toFloat(),
                            color = Color(0xFFED9CBF)
                        ),
                    ),
                    plotType = PlotType.Donut
                ),
                PieChartData(
                    slices = listOf(
                        PieChartData.Slice(
                            label = "Primary",
                            value = modeStats.enemyPrimary.toFloat(),
                            color = Color.White
                        ),
                        PieChartData.Slice(
                            label = "Special",
                            value = modeStats.enemySpecial.toFloat(),
                            Color(0xFF5C7A41)
                        ),
                        PieChartData.Slice(
                            label = "Heavy",
                            value = modeStats.enemyHeavy.toFloat(),
                            color = Color.Magenta
                        ),
                        PieChartData.Slice(
                            label = "Melee",
                            value = modeStats.enemyMelee.toFloat(),
                            color = Color(0xFFF7F600)
                        ),
                        PieChartData.Slice(
                            label = "Grenade",
                            value = modeStats.enemyGrenade.toFloat(),
                            color = Color(0xFF58C8F2)
                        ),
                        PieChartData.Slice(
                            label = "Super",
                            value = modeStats.enemySuper.toFloat(),
                            color = Color(0xFFED9CBF)
                        ),
                    ),
                    plotType = PlotType.Donut
                ),
                PieChartData(
                    slices = listOf(
                        PieChartData.Slice(
                            label = "Primary",
                            value = modeStats.teamMatePrimary.toFloat(),
                            color = Color.White
                        ),
                        PieChartData.Slice(
                            label = "Special",
                            value = modeStats.teamMateSpecial.toFloat(),
                            Color(0xFF5C7A41)
                        ),
                        PieChartData.Slice(
                            label = "Heavy",
                            value = modeStats.teamMateHeavy.toFloat(),
                            color = Color.Magenta
                        ),
                        PieChartData.Slice(
                            label = "Melee",
                            value = modeStats.teamMateMelee.toFloat(),
                            color = Color(0xFFF7F600)
                        ),
                        PieChartData.Slice(
                            label = "Grenade",
                            value = modeStats.teamMateGrenade.toFloat(),
                            color = Color(0xFF58C8F2)
                        ),
                        PieChartData.Slice(
                            label = "Super",
                            value = modeStats.teamMateSuper.toFloat(),
                            color = Color(0xFFED9CBF)
                        ),
                    ),
                    plotType = PlotType.Donut
                )
            )
        }
        return listOf()
    }

    private val pieChartConfig = PieChartConfig(
        isSumVisible = true,
        backgroundColor = Color.Black,
        sliceLabelTextColor = Color.Black,
        showSliceLabels = true,
        sliceLabelTextSize = 5.sp,
        sliceLabelEllipsizeAt = TextUtils.TruncateAt.END,
        //animationDuration = 500,
        // isAnimationEnable = true,
        isEllipsizeEnabled = true,
        isClickOnSliceEnabled = true,
        strokeWidth = 100f,
        labelType = PieChartConfig.LabelType.PERCENTAGE,
        labelVisible = true,
        activeSliceAlpha = .9f,
        accessibilityConfig = AccessibilityConfig(chartDescription = "Kill spread"),
        sumUnit = "Kills",
        labelColorType = PieChartConfig.LabelColorType.SPECIFIED_COLOR


    )


    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM_ALLSTATS = "ARG_PARAM_ALLSTATS"
        fun newInstance(allStats: StatsDTO?): SessionFragment {
            val fragment = SessionFragment()
            val args = Bundle()
            args.putSerializable(ARG_PARAM_ALLSTATS, allStats)
            fragment.arguments = args
            return fragment
        }
    }
}
