package com.example.composasimplecalculator.ui.theme

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalcuViewModel: ViewModel() {

    private val _calcUiState = MutableStateFlow(CalcUiState())
    val calcUiState: StateFlow<CalcUiState> = _calcUiState.asStateFlow()

    private var currentFormula: String = "0"
    private var currentResult: String = ""
    private var oldNum: String = "0"

    fun addNum(inputNum: String) {
        if ((currentFormula == "0" && inputNum == "00")) {
            currentFormula = "0"
            currentResult = "=0"
        } else if (inputNum == "00" && !currentFormula.last().isDigit() && currentFormula.first().toString() != "0" ) {
            currentFormula += "0"
        } else if (currentFormula == "0") {
            currentFormula = inputNum
            currentResult = "=$inputNum"
        } else {
            currentFormula += inputNum
            currentResult = calc()
        }
        updateState()
    }

    fun addDecimal() {
        val numbers = currentFormula.split("+", "-", "×", "÷")
        val lastNumber = numbers[numbers.size - 1]

        if (!lastNumber.contains(".") && currentFormula.last().isDigit()) {
            currentFormula += "."
            if (!currentResult.contains(".")) {
                currentResult += "."
            }
        }
        updateState()
    }

    fun addOperator(inputOperator: String) {
        if (currentFormula.isNotBlank() && currentFormula.last().isDigit()) {
            currentFormula += inputOperator
        }
        updateState()
    }

    fun toPercentage() {
        if (currentFormula != "0") {

            val numbers = currentFormula.split("+", "-", "×", "÷")
                .map { it.toDouble() }.toMutableList()
            val operator = currentFormula.split("[0-9.]+".toRegex())
                .filter { it.isNotEmpty() }.toMutableList()

            val lastNumber = numbers[numbers.size - 1]
            val percentageNum = (lastNumber * 0.01)

            numbers[numbers.size - 1] = percentageNum

            val combinedList = numbers.zip(operator) { a, b -> "$a$b" }
            currentFormula = combinedList.joinToString("") + numbers[numbers.size - 1]
        } else {
            currentFormula = "0"
        }
        _calcUiState.value = CalcUiState(currentFormula)
    }

    fun allClear() {
        currentFormula = "0"
        _calcUiState.value = CalcUiState(currentFormula)
    }

    fun backSpace() {
        if (currentFormula.length > 1) {
            currentFormula = currentFormula.dropLast(1)
            currentResult = calc()
        } else if(!currentFormula.last().isDigit()) {
            currentResult = oldNum
        } else {
            Log.d("result", "else実行")
            currentFormula = "0"
        }
        updateState()
    }

    fun equal() {
        currentFormula = currentResult.replace("=", "")
        currentResult = currentResult
        updateState()
    }

    private fun calc(): String {
        var result = "0"

        if (currentFormula != "0" && currentFormula.last().isDigit()) {
            val numbers = currentFormula.split("+", "-", "×", "÷")
                .map { it.toDouble() }.toMutableList()
            val operator = currentFormula.split("[0-9.]+".toRegex())
                .filter { it.isNotEmpty() }.toMutableList()

            Log.d("result", numbers.toString())
            Log.d("result", operator.toString())

            for (i in 0 until operator.size) {
                if (operator[i] == "×") {
                    numbers[i + 1] = numbers[i] * numbers[i + 1]
                    numbers.removeAt(i)
                } else if (operator[i] == "÷") {
                    if (numbers[i + 1] != 0.0) {
                        numbers[i + 1] = numbers[i] / numbers[i + 1] //i番目の演算子の右側を計算結果に更新
                        numbers.removeAt(i)
                    } else {
                        // 0で割ったときの処理。
                    }
                }
            }

            operator.removeAll { it == "×" }
            operator.removeAll { it == "÷" } //リストから×と/を消去

            if (operator.isNotEmpty() && numbers.isNotEmpty()) { //通常時（掛け算・割り算が終わったとき）
                var tmp: Double
                for (i in 0 until operator.size) {
                    if (operator[i] == "+") {
                        tmp = (numbers[i] + numbers[i + 1])
                        numbers[i + 1] = tmp

                    } else if (operator[i] == "-") {
                        tmp = numbers[i] - numbers[i + 1]
                        numbers[i + 1] = tmp
                    }
                }
            }
            if (numbers.isEmpty()) {
                result = "0"
            } else {
                result = numbers[numbers.size - 1].toString()
            }
        }
        oldNum = currentFormula
        return result
    }

    private fun updateState() {
        Log.d("result", currentResult)

        val a = currentResult.replace("=", "")
        Log.d("result", a)

        if (a.toDouble() % 1.0 == 0.0) {
            currentResult = a.toDouble().toInt().toString()
        }

        _calcUiState.update { currentState ->
            currentState.copy(
                currentFormula = currentFormula,
                currentResult = "=${currentResult}"
            )
        }
    }
}