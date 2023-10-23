package com.example.composasimplecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composasimplecalculator.ui.theme.CalcuViewModel
import com.example.composasimplecalculator.ui.theme.ComposaSimpleCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposaSimpleCalculatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScreen()
                }
            }
        }
    }
}

@Preview
@Composable
fun Preview() {
    AppScreen(
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
fun AppScreen(
    modifier: Modifier = Modifier,
    calcuViewModel: CalcuViewModel = viewModel(),
    ) {
    val CalcUiState by calcuViewModel.calcUiState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFFFFF)),
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = CalcUiState.currentFormula,
            textAlign = TextAlign.End,
            maxLines= 1,
            fontSize = 60.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp, bottom = 5.dp)
        )

        Text(
            text = CalcUiState.currentResult,
            textAlign = TextAlign.End,
            maxLines= 1,
            fontSize = 60.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp, bottom = 5.dp)
        )

        Divider(
            // thickness = dimensionResource(R.dimen.thickness_divider),
            thickness = 1.dp,
            // modifier = Modifier.padding(bottom = dimensionResource(R.dimen.padding_medium))
            modifier = Modifier
                .width(350.dp)
        )
        ButtonLayout(
            modifier = Modifier.padding(
                bottom = 12.dp
            )
        )
    }
}

@Composable
fun ButtonLayout(modifier: Modifier = Modifier) {
    val calcuViewModel: CalcuViewModel = viewModel()
    val symbolList: List<String> = listOf( // todo modelとして定義する？
        "C", "%", "\u232B", "÷",
        "7", "8", "9", "×",
        "4", "5", "6", "-",
        "1", "2", "3", "+",
        "0", "00", ".", "="
    )
    Column(
        modifier = Modifier.padding(bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp) // todo dpを別のファイルに定義
    ) {
        for (i in 0 until 5) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp) // todo dpを別のファイルに定義
            ) {

                symbolList.subList(i * 4, i * 4 + 4).forEachIndexed { index, symbol ->
                    val adjustedIndex = index + 1
                    val buttonColor: Color
                    val textColor: Color

                    // ボタンカラーとテキストカラー
                    if (i == 4 && index == 3) {
                        buttonColor = Color(0xFF00F99A)
                        textColor = Color(0xFFFFFFFF)
                    } else if (adjustedIndex % 4 == 0) {
                        buttonColor = Color(0xFFFFFFFF)
                        textColor = Color(0xFF00F99A)
                    } else {
                        buttonColor = Color(0xFFFFFFFF)
                        textColor = Color(0xFF000000)
                    }

                    CalcButton(
                        symbol = symbol,
                        color = ButtonDefaults.buttonColors(buttonColor),
                        textColor = textColor,
                        modifier = Modifier,
                        onClick = {
                            when {
                                i == 4 && index == 3 -> calcuViewModel.equal()
                                i == 4 && index == 2 -> calcuViewModel.addDecimal()
                                i == 0 && index == 0 -> calcuViewModel.allClear()
                                i == 0 && index == 1 -> calcuViewModel.toPercentage()
                                i == 0 && index == 2 -> calcuViewModel.backSpace()
                                adjustedIndex % 4 == 0 -> calcuViewModel.addOperator(symbol)
                                else -> calcuViewModel.addNum(symbol)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CalcButton(
    symbol: String,
    color: ButtonColors,
    textColor: Color,
    modifier: Modifier,
    onClick: () -> Unit,
    ) {
    Button(
        onClick = { onClick() },
        shape = CircleShape,
        colors = color,
        modifier = modifier
            .size(85.dp),
        ) {
        Text(
            text = symbol,
            fontSize = 32.sp,
            color = textColor
        )
    }
}



