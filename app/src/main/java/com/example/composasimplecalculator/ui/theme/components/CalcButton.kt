package com.example.composasimplecalculator.ui.theme.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalcButton(
    symbol: String,
    buttonColor: ButtonColors,
    textColor: Color,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = { onClick() },
        shape = CircleShape,
        colors = buttonColor,
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