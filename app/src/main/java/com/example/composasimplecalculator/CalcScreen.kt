package com.example.composasimplecalculator

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Divider
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
import com.example.composasimplecalculator.ui.theme.CalcViewModel
import com.example.composasimplecalculator.ui.theme.screenparts.CalcButtonLayout

@Preview
@Composable
fun Preview() {
    CalcScreen(
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
fun CalcScreen(
    modifier: Modifier = Modifier,
    calcViewModel: CalcViewModel = viewModel(),
) {
    val calcUiState by calcViewModel.calcUiState.collectAsState()
    val screenBackgroundColor = if(isSystemInDarkTheme()) Color(0xff2d2f42) else Color(0xffffffff)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(screenBackgroundColor),
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Column {
            Text(
                text = calcUiState.currentFormula,
                textAlign = TextAlign.End,
                maxLines = 1,
                fontSize = 40.sp,
                color = if (isSystemInDarkTheme()) {
                    Color(0xffc4c5dd)
                } else {
                    Color(0xff000000)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp, bottom = 5.dp)
                    .horizontalScroll(rememberScrollState())
            )
            Text(
                text = calcUiState.currentResult,
                textAlign = TextAlign.End,
                maxLines = 1,
                fontSize = 40.sp,
                color = if (isSystemInDarkTheme()) {
                    Color(0xffc4c5dd)
                } else {
                    Color(0xff000000)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp, bottom = 5.dp)
                    .horizontalScroll(rememberScrollState())
            )
        }
        Divider(
            thickness = 1.dp,
            modifier = Modifier
                .width(350.dp)
                .padding(15.dp)
        )
        CalcButtonLayout(
            modifier = Modifier.padding(
                bottom = 12.dp
            )
        )
    }
}
