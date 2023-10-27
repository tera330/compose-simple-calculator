package com.example.composasimplecalculator.ui.theme

import android.util.Log
import androidx.core.text.isDigitsOnly
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

    private fun extractNumbersAndOperator(): Pair<MutableList<String>, MutableList<String>> {
        val numbers = currentFormula.split("+", "-", "×", "÷")
            .filter { it.isNotEmpty() }.toMutableList()
        val operator = currentFormula.split("[0-9.]+".toRegex())
            .filter { it.isNotEmpty() }.toMutableList()
        return Pair(numbers, operator)
    }

    fun addNum(inputNum: String) {
        // todo 01の入力を1にする
        // todo 1.0(小数点を挟んでinputが00の時)のエラーを直し、1.00を表示する。
        // todo 演算子を挟んだ右側の数値の連続した０を無効にする　例1 + 000

        if ((currentFormula == "0" && inputNum == "00")) {
            currentFormula = "0"
        } else if (inputNum == "00") {
            if (currentFormula.last() == '.' || currentFormula.last().isDigit()) {
                currentFormula += "00"
            } else if (!currentFormula.last().isDigit()) {
                currentFormula += "0"
            }
            currentResult = calc(extractNumbersAndOperator().first, extractNumbersAndOperator().second)
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
        if (currentFormula != "0") {
            val numbers = currentFormula.split("+", "-", "×", "÷")
            val lastNumber = numbers[numbers.size - 1]

            if (!lastNumber.contains(".") && currentFormula.last().isDigit()) {
                currentFormula += "."
                if (!currentResult.contains(".")) {
                    currentResult += "."
                }
            }
        } else {
            currentFormula += "."
        }
        updateState()
    }

    fun addOperator(inputOperator: String) {
        if (currentFormula.isNotBlank() && currentFormula.last().isDigit()) {
            currentFormula += inputOperator
            Log.d("result", currentFormula)
        }
        updateState()
    }

    fun toPercentage() {
        if (currentFormula.last().isDigit()) {
            val (numbers, operator) = extractNumbersAndOperator()
            val lastNumber = numbers[numbers.size - 1]
            val percentageNum = (lastNumber.toDouble() * 0.01)

            numbers[numbers.size - 1] = percentageNum.toString()

            val combinedList = numbers.zip(operator) { a, b -> "$a$b" }
            currentFormula = combinedList.joinToString("") + numbers[numbers.size - 1]
            currentResult = calc(numbers, operator)
        }
        updateState()
    }

    fun allClear() {
        currentFormula = "0"
        currentResult = ""
        updateState()
    }

    fun backSpace() {
        if (currentFormula.length > 1) {
            currentFormula = currentFormula.dropLast(1)
            if (!currentFormula.contains("0÷")) {
                zeroFlag = false
            }
            val (numbers, operator) = extractNumbersAndOperator()
            currentResult = if (!currentFormula.last().isDigit()) {
                if (operator.isNotEmpty()) {
                    operator.removeAt(operator.size - 1).map { it.toString() }.toMutableList()
                }
                calc(numbers, operator)
            } else {
                calc(numbers, operator)
            }
        } else {
            currentFormula = "0"
            currentResult = ""
        }
        updateState()
    }

    fun equal() {
        if (currentFormula == "0") {
            currentResult = "0"
        } else if (currentResult != "0では割れません") {
            currentFormula = currentResult.replace("=", "")
        }
        updateState()
    }

    private fun intOrDouble(num: String): Number {
        val result = if (num.toDouble() % 1 == 1.0) {
            num.toInt()
        } else {
            num.toDouble()
        }
        return result
    }

    private fun calc(numbers: MutableList<String>, operator: MutableList<String>): String {
        val countList = mutableListOf<Int>()

        for (i in 0 until operator.size) {
            if (operator[i] == "×") {
                numbers[i + 1] = (numbers[i].toDouble() * numbers[i + 1].toDouble()).toString()
                countList.add(i)
            } else if (operator[i] == "÷") {
                if (numbers[i + 1].toDouble() != 0.0) {
                    numbers[i + 1] = (numbers[i].toDouble() / numbers[i + 1].toDouble()).toString() //i番目の演算子の右側を計算結果に更新
                    countList.add(i)
                } else {
                    // 0で割ったときの処理。
                    currentResult = "0で割れません。"
                    zeroFlag = true
                }
            }
        }

        operator.removeAll { it == "×" || it == "÷" } //リストから×と/を消去

        if (operator.isNotEmpty() && numbers.isNotEmpty()) { //通常時（掛け算・割り算が終わったとき）
            if (countList.isNotEmpty()) {
                countList.sortedDescending().forEach {
                    numbers.removeAt(it)
                }
            }
            var tmp: String
            for (i in 0 until operator.size) {
                if (operator[i] == "+") {
                    tmp = (numbers[i].toDouble() + numbers[i + 1].toDouble()).toString()
                    numbers[i + 1] = tmp

                } else if (operator[i] == "-") {
                    tmp = (numbers[i].toDouble() - numbers[i + 1].toDouble()).toString()
                    numbers[i + 1] = tmp
                }
            }
        }
        val result = if (numbers.isEmpty()) {
            "0"
        } else if (zeroFlag) {
            "0では割れません"
        } else {
            numbers[numbers.size - 1].toString()
        }
        zeroFlag = false
        return result
    }

    private fun isNumericWithDot(number: String): Boolean {
        for (char in number) {
            if (!(char.isDigit() || char == '.')) {
                return false
            }
        }
        return true
    }

    private fun updateState() {
        if (currentResult == "") {
            currentResult = ""
        } else if (isNumericWithDot(currentResult) && currentResult.toDouble() % 1.0 == 0.0) {
            currentResult = "=" + currentResult.toDouble().toInt().toString()
        } else if (isNumericWithDot(currentResult)) {
            currentResult = "=${currentResult}"
        }

        _calcUiState.update { currentState ->
            currentState.copy(
                currentFormula = currentFormula,
                currentResult = currentResult
            )
        }
    }
}

