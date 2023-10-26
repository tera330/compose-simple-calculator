package com.example.composasimplecalculator.ui.theme

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalcViewModel: ViewModel() {

    private val _calcUiState = MutableStateFlow(CalcUiState())
    val calcUiState: StateFlow<CalcUiState> = _calcUiState.asStateFlow()

    private var currentFormula: String = "0"
    private var currentResult: String = ""
    private var zeroFlag = false

    private fun extractNumbersAndOperator(): Pair<MutableList<Double>, MutableList<String>> {
        val numbers = currentFormula.split("+", "-", "×", "÷")
            .map { it.toDouble() }.toMutableList()
        val operator = currentFormula.split("[0-9.]+".toRegex())
            .filter { it.isNotEmpty() }.toMutableList()
        return Pair(numbers, operator)
    }

    fun addNum(inputNum: String) {
        if ((currentFormula == "0" && inputNum == "00")) {
            currentFormula = "0"
            currentResult = "0"
        } else if (inputNum == "00" && !currentFormula.last().isDigit() && currentFormula.first().toString() != "0") {
            currentFormula += "0"
            calc(extractNumbersAndOperator().first, extractNumbersAndOperator().second)
        } else if (currentFormula == "0") {
            currentFormula = inputNum
            currentResult = inputNum
        } else {
            currentFormula += inputNum
            if (currentFormula != "0" && currentFormula.last().isDigit()) {
                currentResult = calc(extractNumbersAndOperator().first, extractNumbersAndOperator().second)
            }
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

            val (numbers, operator) = extractNumbersAndOperator()

            val lastNumber = numbers[numbers.size - 1]
            val percentageNum = (lastNumber * 0.01)

            numbers[numbers.size - 1] = percentageNum

            val combinedList = numbers.zip(operator) { a, b -> "$a$b" }
            currentFormula = combinedList.joinToString("") + numbers[numbers.size - 1]
            currentResult = calc(numbers, operator)
        } else {
            currentFormula = "0"
        }
        updateState()
    }

    fun allClear() {
        currentFormula = "0"
        currentResult = "0"
        _calcUiState.value = CalcUiState(currentFormula)
    }

    fun backSpace() {
        if (currentFormula.length > 1) {
            currentFormula = currentFormula.dropLast(1)

            if (!currentFormula.contains("0÷")) {
                zeroFlag = false
            }

            val (numbers, operator) = extractNumbersAndOperator()

            currentResult = if (!currentFormula.last().isDigit()) {

                operator.removeAt(operator.size - 1).map { it.toString() }.toMutableList()
                calc(numbers, operator)

            } else {
                calc(numbers, operator)
            }
        } else {
            currentFormula = "0"
            currentResult = "0"
        }
        updateState()
    }

    fun equal() {
        if (currentResult != "0では割れません") {
            currentFormula = currentResult
        }
        updateState()
    }

    private fun calc(numbers: MutableList<Double>, operator: MutableList<String>): String {
        val countList = mutableListOf<Int>()

        for (i in 0 until operator.size) {
            if (operator[i] == "×") {
                numbers[i + 1] = numbers[i] * numbers[i + 1]
                countList.add(i)
            } else if (operator[i] == "÷") {
                if (numbers[i + 1] != 0.0) {
                    Log.d("result", "true")
                    numbers[i + 1] = numbers[i] / numbers[i + 1] //i番目の演算子の右側を計算結果に更新
                    Log.d("result", numbers[i + 1].toString())
                    countList.add(i)
                } else {
                    // 0で割ったときの処理。
                    currentResult = "0で割れません。"
                    zeroFlag = true
                }
            }
        }

        operator.removeAll { it == "×" }
        operator.removeAll { it == "÷" } //リストから×と/を消去

        if (operator.isNotEmpty() && numbers.isNotEmpty()) { //通常時（掛け算・割り算が終わったとき）
            if (countList.isNotEmpty()) {
                countList.sortedDescending().forEach {
                    numbers.removeAt(it)
                }
            }
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
            val result = if (numbers.isEmpty()) {
                "0"
            } else if(zeroFlag) {
                "0では割れません"
            }
            else {
                numbers[numbers.size - 1].toString()
            }
        return result
    }

    private fun updateState() {
        val a = currentResult.replace("=", "")

        if (a != "0では割れません" && a.toDouble() % 1.0 == 0.0) {
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

