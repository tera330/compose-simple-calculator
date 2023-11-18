package com.example.composasimplecalculator.ui.theme.screenparts

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composasimplecalculator.ui.theme.CalcViewModel

@Composable
fun CalcButtonLayout(modifier: Modifier = Modifier) {
    val calcViewModel: CalcViewModel = viewModel()
    val symbolList: List<String> = listOf(
        "C", "%", "\u232B", "÷",
        "7", "8", "9", "×",
        "4", "5", "6", "-",
        "1", "2", "3", "+",
        "0", "00", ".", "="
    )
    Column(
        modifier = Modifier.padding(bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        for (numberOfRows in 0 until 5) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                symbolList.subList(numberOfRows * 4, numberOfRows * 4 + 4).forEachIndexed { index, symbol ->
                    val adjustedIndex = index + 1
                    val buttonColor: Color
                    val textColor: Color

                    if (numberOfRows == 4 && index == 3) {
                        buttonColor = Color(0xff6a7dff)
                        textColor = if (isSystemInDarkTheme()) Color(0xffc4c5dd) else Color(0xffffffff)
                    } else if (adjustedIndex % 4 == 0) {
                        buttonColor = if(isSystemInDarkTheme()) Color(0xff434559) else Color(0xffffffff)
                        textColor = Color(0xff6a7dff)
                    } else {
                        buttonColor = if(isSystemInDarkTheme()) Color(0xff434559) else Color(0xffffffff)
                        textColor = if (isSystemInDarkTheme()) Color(0xffc4c5dd) else Color(0xff000000)
                    }

                    CalcButton(
                        symbol = symbol,
                        buttonColor = ButtonDefaults.buttonColors(buttonColor),
                        textColor = textColor,
                        modifier = Modifier,
                        onClick = {
                            when {
                                numberOfRows == 4 && index == 3 -> calcViewModel.equal()
                                numberOfRows == 4 && index == 2 -> calcViewModel.addDecimal()
                                numberOfRows == 0 && index == 0 -> calcViewModel.allClear()
                                numberOfRows == 0 && index == 1 -> calcViewModel.toPercentage()
                                numberOfRows == 0 && index == 2 -> calcViewModel.backSpace()
                                adjustedIndex % 4 == 0 -> calcViewModel.addOperator(symbol)
                                else -> calcViewModel.addNum(symbol)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewCalcButtonLayout() {
    CalcButtonLayout()
}