package com.example.composasimplecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.composasimplecalculator.ui.theme.CalcViewModel
import com.example.composasimplecalculator.ui.theme.ComposaSimpleCalculatorTheme
import com.example.composasimplecalculator.ui.theme.components.CalcButtonLayout

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
    calcViewModel: CalcViewModel = viewModel(),
    ) {
    val calcUiState by calcViewModel.calcUiState.collectAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFFFFF)),
    ) {
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = calcUiState.currentFormula,
            textAlign = TextAlign.End,
            maxLines= 1,
            fontSize = 40.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp, bottom = 5.dp)
        )

        Text(
            text = calcUiState.currentResult,
            textAlign = TextAlign.End,
            maxLines= 1,
            fontSize = 40.sp,
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
        CalcButtonLayout(
            modifier = Modifier.padding(
                bottom = 12.dp
            )
        )
    }
}






