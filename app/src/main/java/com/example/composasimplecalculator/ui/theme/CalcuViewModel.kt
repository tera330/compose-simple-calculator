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

    fun addNum(inputNum: String) {
        if ((currentFormula == "0" && inputNum == "00")) {
            currentFormula = "0"
            currentResult = "=0"
        } else if (currentFormula == "0") {
            currentFormula = inputNum
            currentResult = "=$inputNum"
        } else {
            Log.d("result", "0の入力")
            currentFormula += inputNum
            Log.d("result", "計算開始")
            currentResult = "=${calc()}"
        }
        updateState()
    }

    fun addDecimal() {
        val numbers = currentFormula.split("+", "-", "×", "÷")
        val lastNumber = numbers[numbers.size - 1]

        if (!lastNumber.contains(".") && currentFormula.last().isDigit()) {
            currentFormula += "."
            currentResult += "."
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
            Log.d("resultList", numbers.toString())

            numbers[numbers.size - 1] = percentageNum
            Log.d("resultList", numbers.toString())

            val combinedList = numbers.zip(operator) { a, b -> "$a$b" }
            Log.d("resultList", combinedList.toString())
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
        } else {
            currentFormula = "0"
        }
        _calcUiState.value = CalcUiState(currentFormula)
    }

    fun equal() {
        currentFormula = currentResult.replace("=", "")
        currentResult = currentResult
        updateState()
    }

    fun calc(): String {
        Log.d("result", "calc関数の始まり")
        var result = "0"

        if (currentFormula != "0" && currentFormula.last().isDigit()) {
            val numbers = currentFormula.split("+", "-", "×", "÷")
                .map { it.toDouble() }.toMutableList()
            val operator = currentFormula.split("[0-9.]+".toRegex())
                .filter { it.isNotEmpty() }.toMutableList()

            Log.d("result", numbers.toString())
            Log.d("result", operator.toString())


            for (i in 0 until operator.size) {
                Log.d("result", "for文開始")
                if (operator[i] == "×") {
                    numbers[i + 1] = numbers[i] * numbers[i + 1]
                    numbers[i] = 0.0
                } else if (operator[i] == "÷") {
                    if (numbers[i + 1] != 0.0) {
                        numbers[i + 1] = numbers[i] / numbers[i + 1] //i番目の演算子の右側を計算結果に更新
                        numbers[i] = 0.0
                    } else {
                        // 0で割ったときの処理。
                    }
                }
            }

            numbers.removeAll { it == 0.0 } //リストから０を消去
            operator.removeAll { it == "×" }
            operator.removeAll { it == "÷" } //リストから×と/を消去

            if (operator.isNotEmpty()) { //通常時（掛け算・割り算が終わったとき）
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
                result = numbers[numbers.size - 1].toString()
        }
        // _calcUiState.value = CalcUiState(currentFormula)
        return result
    }

    fun updateState() {
        _calcUiState.update { currentState ->
            currentState.copy(
                currentFormula = currentFormula,
                currentResult = currentResult
            )
        }
    }
}