package com.example.cryptocurrencytracker

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.cryptocurrencytracker.ui.theme.CryptoCurrencyTrackerTheme
import coil.compose.rememberAsyncImagePainter

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        setContent {
            CryptoCurrencyTrackerTheme {
                // A surface container using the 'background' color from the theme
                CurrencySwitcherApp(viewModel)
            }
        }
    }
}

@Composable
fun CurrencySwitcherApp(viewModel: MainViewModel) {
    val usdList by viewModel.getUsdList().observeAsState(initial = emptyList())
    val rubList by viewModel.getRubList().observeAsState(initial = emptyList())

    viewModel.loadUsd()
    viewModel.loadRub()
    var isUsdSelected by remember { mutableStateOf(true) }
    val successDownloading = viewModel.getSuccessfulDownload()
    CustomToolbar(
        currencyList = if (isUsdSelected) usdList else rubList,
        onChipSelected = { selectedCurrency ->
            isUsdSelected = selectedCurrency == "USD"
        },
        successDownloading = successDownloading
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomToolbar(
    currencyList: List<Currency>,
    onChipSelected: (String) -> Unit,
    successDownloading: Boolean
) {
    var selectedCurrency by remember { mutableStateOf("USD") }
    var isUSD: Boolean = true

    Box(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
            modifier = Modifier.height(85.dp),

            title = {
                Column {
                    Text(text = stringResource(id = R.string.currency_list))
                    Spacer(modifier = Modifier.height(8.dp))
                    Row() {
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
            actions = {
            }
        )
        if (successDownloading) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 80.dp)
            ) {
                itemsIndexed(
                    currencyList
                ) { _, item ->
                    CurrencyCard(item, isUSD)
                }

            }
        }else {
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
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        containerColor = Color(0xFFFFC107))
                ) {
                    Text(text = "Попробовать")
                }
            }

        }
    }
}

@Composable
fun CurrencyCard(currency: Currency, isUSD: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { /* Handle click here */ },
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(currency.image),
                contentDescription = null,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
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

            Column {
                Text(text = currentPrice, color = Color.Black)
                Text(text = pricePercentage, color = color)
            }
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


