package com.example.composasimplecalculator.ui.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

                    if (i == 4 && index == 3) {
                        buttonColor = Color(0xff6a7dff)
                        textColor = Color(0xFFFFFFFF)
                    } else if (adjustedIndex % 4 == 0) {
                        buttonColor = Color(0xFFFFFFFF)
                        textColor = Color(0xff6a7dff)
                    } else {
                        buttonColor = Color(0xFFFFFFFF)
                        textColor = Color(0xFF000000)
                    }

                    CalcButton(
                        symbol = symbol,
                        buttonColor = ButtonDefaults.buttonColors(buttonColor),
                        textColor = textColor,
                        modifier = Modifier,
                        onClick = {
                            when {
                                i == 4 && index == 3 -> calcViewModel.equal()
                                i == 4 && index == 2 -> calcViewModel.addDecimal()
                                i == 0 && index == 0 -> calcViewModel.allClear()
                                i == 0 && index == 1 -> calcViewModel.toPercentage()
                                i == 0 && index == 2 -> calcViewModel.backSpace()
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