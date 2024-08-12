@file:Suppress("DEPRECATION")

package com.example.cryptocurrencytracker

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.cryptocurrencytracker.ui.theme.CryptoCurrencyTrackerTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var navController: NavHostController

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setContent {
            CryptoCurrencyTrackerTheme {
                navController = rememberNavController()
                NavHost(navController = navController, startDestination = "CurrencySwitcherApp") {
                    composable("CurrencySwitcherApp") {
                        CurrencySwitcherApp(viewModel, applicationContext, navController)
                    }
                    composable("CurrencyInfoScreen/{currency}/{url}") { backStackEntry ->
                        val currencyId = backStackEntry.arguments?.getString("currency")
                        val url = backStackEntry.arguments?.getString("url")
                        if (currencyId!=null&& url!=null){
                            CurrencyInfoLogic(viewModel,
                                application,
                                navController,
                                currencyId,
                                url
                            )
                            }
                        }
                    }
                }

            }
        }
    }

@Composable
fun CurrencySwitcherApp(
    viewModel: MainViewModel,
    context: Context,
    navController: NavHostController
) {
    val networkStatus = NetworkStatus(context)
    val usdList by viewModel.getUsdList().observeAsState(initial = emptyList())
    val rubList by viewModel.getRubList().observeAsState(initial = emptyList())

    var isUsdSelected by remember { mutableStateOf(true) }
    val successDownload by viewModel.getSuccessfulDownload().observeAsState(initial = false)

    if (networkStatus.isConnected()) {
        if (!successDownload) {
            viewModel.loadUsd()
            viewModel.loadRub()
        }
    }

    val onRetry = {
        viewModel.loadUsd()
        viewModel.loadRub()
    }

    CustomToolbar(
        currencyList = if (isUsdSelected) usdList else rubList,
        onChipSelected = { selectedCurrency ->
            isUsdSelected = selectedCurrency == "USD"
        },
        successDownloading = successDownload,
        onRetry,networkStatus.isConnected(),
        navController = navController
    )
}


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomToolbar(
    currencyList: List<Currency>,
    onChipSelected: (String) -> Unit,
    successDownloading: Boolean,
    onRetry: () -> Unit,
    isConnected: Boolean,
    navController: NavHostController
) {
    var selectedCurrency by remember { mutableStateOf("USD") }
    var isUSD by remember { mutableStateOf(true) }
    var refreshing by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = refreshing),
        onRefresh = {
            refreshing = true
            onRetry()
            refreshing = false
        }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            TopAppBar(
                modifier = Modifier.height(85.dp),
                title = {
                    Column {
                        Text(text = stringResource(id = R.string.currency_list))
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            ChipSwitch(
                                currency = "USD",
                                isSelected = selectedCurrency == "USD",
                                onClick = {
                                    selectedCurrency = "USD"
                                    onChipSelected("USD")
                                    isUSD = true
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            ChipSwitch(
                                currency = "RUB",
                                isSelected = selectedCurrency == "RUB",
                                onClick = {
                                    selectedCurrency = "RUB"
                                    onChipSelected("RUB")
                                    isUSD = false
                                }
                            )
                        }
                    }
                },
                actions = {}
            )

            when {
                successDownloading && !isConnected->{
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            itemsIndexed(currencyList) { _, item ->
                                CurrencyCard(item, isUSD, navController)
                            }
                        }
                        SnackbarHost(
                            hostState = snackbarHostState,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp),
                            snackbar = { data ->
                                Snackbar(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color.Red),
                                    action = {
                                    },
                                    content = {
                                        Text(text = "Произошла ошибка при загрузке", color = Color.White)
                                    }
                                )
                            }
                        )
                    }
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "Произошла ошибка при загрузке",
                            withDismissAction = true,
                            duration = SnackbarDuration.Short
                        )
                    }

                }

                successDownloading-> {
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 80.dp)
                    ) {
                        itemsIndexed(currencyList) { _, item ->
                            CurrencyCard(item, isUSD, navController)
                        }
                    }
                }

                !successDownloading&&!isConnected -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.bitcoin_image),
                            contentDescription = "bitcoin image"
                        )
                        Text(text = "Произошла какая-то ошибка :(\nПопробуем снова?")
                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                containerColor = Color(0xFFFFC107)
                            )
                        ) {
                            Text(text = "Попробовать")
                        }
                    }
                }
                else ->{
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFFFFC107),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CurrencyCard(currency: Currency, isUSD: Boolean, navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("CurrencyInfoScreen/${currency.id}/${Uri.encode(currency.image)}")
            },
        verticalAlignment = Alignment.CenterVertically,

        ) {
        Image(
            painter = rememberAsyncImagePainter(currency.image),
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = currency.name, color = Color.Black)
            Text(text = currency.symbol.uppercase(), color = Color.Gray)
        }
        Spacer(modifier = Modifier.width(16.dp))
        var color: Color
        var pricePercentage = currency.priceChangePercentage24h.toString()
        if (currency.priceChangePercentage24h > 0) {
            color = Color.Green
            pricePercentage = "+$pricePercentage"
        } else color = Color.Red
        var currentPrice = currency.currentPrice.toString()
        if (isUSD == true)
            currentPrice = "$ $currentPrice"
        else
            currentPrice = "₽ $currentPrice"

        Column(horizontalAlignment = Alignment.End) {
            Text(text = currentPrice, color = Color.Black)
            Text(text = pricePercentage, color = color)
        }
    }

}

@Composable
fun ChipSwitch(currency: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color(0xFFFFF3E0) else Color(0xFFE8E8E8)
    val contentColor = if (isSelected) Color(0xFFFFC107) else MaterialTheme.colorScheme.onSurface
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(percent = 50),
        modifier = Modifier
            .clickable(onClick = onClick)
    ) {
        Text(
            text = currency, color = contentColor,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
        )
    }
}
@Composable
fun CurrencyInfoLogic(
    viewModel: MainViewModel,
    context: Context,
    navController: NavHostController,
    id: String,
    image: String
) {
    val networkStatus = NetworkStatus(context)
    val currency by viewModel.getCurrencyInfo()
        .observeAsState(
            initial = CurrencyInfo("Loading", listOf("item1"),
                Description("now"))
        )
    val successDownload by viewModel.getSuccessfulDownload().observeAsState(initial = false)

    LaunchedEffect(id) {
        if (networkStatus.isConnected()) {
            viewModel.loadCurrencyInfo(id)
        }
    }

    val onRetry = {
        viewModel.loadCurrencyInfo(id)
    }

    CurrencyInfoScreen(
        navController,
        currency,
        image,
        onRetry,
        networkStatus.isConnected(),
        id,
        successDownload
    )
}


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyInfoScreen(
    navController: NavHostController,
    currency: CurrencyInfo,
    image:String,
    onRetry: () -> Unit,
    isConnected: Boolean,
    id:String,
    successDownload:Boolean
) {
    Scaffold(
        topBar = @Composable {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(1f)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_action_arrow_back),
                            contentDescription = "back",
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("CurrencySwitcherApp") {
                                        popUpTo("CurrencySwitcherApp") {
                                            inclusive = true
                                        }
                                    }
                                }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            id.capitalize(),
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            )
        }
    ) {
        when{
            isConnected&&!successDownload-> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFFFC107),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            !isConnected->{
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bitcoin_image),
                        contentDescription = "bitcoin image"
                    )
                    Text(text = "Произошла какая-то ошибка :(\nПопробуем снова?")
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.White,
                            containerColor = Color(0xFFFFC107)
                        )
                    ) {
                        Text(text = "Попробовать")
                    }
                }
            }
            else ->
            {
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 64.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Image(
                            painter = rememberAsyncImagePainter(image),
                            contentDescription = null,
                            modifier = Modifier.size(90.dp)
                        )
                        Text(
                            "Описание",
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            currency.description.englishDescription,
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Категории",
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            currency.categories.joinToString(", "),
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 16.sp
                        )
                    }
                }

            }

        }


    }
}


