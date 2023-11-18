package com.example.composasimplecalculator.ui.theme

import androidx.lifecycle.ViewModel
import com.example.composasimplecalculator.ui.theme.data.CalcUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalcViewModel: ViewModel() {

    private val _calcUiState = MutableStateFlow(CalcUiState())
    val calcUiState: StateFlow<CalcUiState> = _calcUiState.asStateFlow()

    private var currentFormula: String = "0"
    private var currentResult: String = ""
    private var divisionByZeroFlag = false

    private fun extractNumbersAndOperators(): Pair<MutableList<String>, MutableList<String>> {
        val numbers = currentFormula.split("+", "-", "×", "÷")
            .filter { it.isNotEmpty() }.toMutableList()
        val operators = currentFormula.split("[0-9.]+".toRegex())
            .filter { it.isNotEmpty() }.toMutableList()
        return Pair(numbers, operators)
    }

    fun addNum(inputNum: String) {
        if (inputNum == "00") {
            if (extractNumbersAndOperators().first.last() == "0" &&  currentFormula.last() == '0') {
                currentFormula += ""
            } else if (currentFormula.last() == '.' || currentFormula.last().isDigit()) {
                currentFormula += "00"
            } else if (!currentFormula.last().isDigit()) {
                currentFormula += "0"
            }
            currentResult = calc(extractNumbersAndOperators().first, extractNumbersAndOperators().second)
        } else if (currentFormula == "0") {
            currentFormula = inputNum
            currentResult = inputNum
        } else {
            if (currentFormula.last() == '0' && extractNumbersAndOperators().first.last() == "0") {
                val (numbers, operators) = extractNumbersAndOperators()
                numbers[numbers.size - 1] = inputNum
                val combinedList = numbers.zip(operators) { a, b -> "$a$b" } // 変更後のnumbersとoperatorの組み合わせ
                currentFormula = combinedList.joinToString("") + numbers[numbers.size - 1]
                currentResult = calc(numbers, operators)
            } else {
                currentFormula += inputNum
                currentResult = calc(extractNumbersAndOperators().first, extractNumbersAndOperators().second)
            }
        }
        updateState()
    }

    fun addDecimal() {
        if (currentFormula != "0") {
            val numbers = extractNumbersAndOperators().first
            val lastNumber = numbers[numbers.size - 1]

            if (!lastNumber.contains(".") && currentFormula.last().isDigit()) {
                currentFormula += "."
                currentResult += "."
            }
        } else {
            currentFormula += "."
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
        if (currentFormula.last().isDigit()) {
            val (numbers, operators) = extractNumbersAndOperators()
            val lastNumber = numbers[numbers.size - 1]
            val percentageNum = (lastNumber.toDouble() * 0.01)
            numbers[numbers.size - 1] = percentageNum.toString()
            val combinedList = numbers.zip(operators) { a, b -> "$a$b" } // 変更後のnumbersとoperatorの組み合わせ

            currentFormula = combinedList.joinToString("") + numbers[numbers.size - 1]
            currentResult = calc(numbers, operators)
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
            val (numbers, operators) = extractNumbersAndOperators()

            currentResult = if (currentFormula.last().toString() == ".") {
                numbers.toString().substring(0, numbers.toString().length - 1)
                calc(numbers, operators)
            } else if (!currentFormula.last().isDigit()) {
                if (operators.isNotEmpty()) {
                    operators.removeAt(operators.size - 1).map { it.toString() }.toMutableList()
                }
                calc(numbers, operators)
            } else {
                calc(numbers, operators)
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
        } else {
            currentFormula = currentResult.replace("=", "")
        }
        updateState()
    }

    private fun calc(numbers: MutableList<String>, operators: MutableList<String>): String {
        val unusedIndex = mutableListOf<Int>() // 計算が終わり使わなくなったnumbers要素のインデックスを記録

        for (i in 0 until operators.size) {
            if (operators[i] == "×") {
                numbers[i + 1] = (numbers[i].toDouble() * numbers[i + 1].toDouble()).toString()
                unusedIndex.add(i)
            } else if (operators[i] == "÷") {
                if (numbers[i + 1].toDouble() != 0.0) {
                    numbers[i + 1] = (numbers[i].toDouble() / numbers[i + 1].toDouble()).toString()
                    unusedIndex.add(i)
                } else {
                    // 0で割ったときの処理。
                    currentResult = "0で割れません。"
                    divisionByZeroFlag = true
                }
            }
        }

        operators.removeAll { it == "×" || it == "÷" } // operatorsから×と/を消去（掛け算と割り算が終了）

        if (operators.isNotEmpty() && numbers.isNotEmpty()) {
            if (unusedIndex.isNotEmpty()) {
                unusedIndex.sortedDescending().forEach {
                    numbers.removeAt(it)
                }
            }
            for (i in 0 until operators.size) {
                if (operators[i] == "+") {
                    numbers[i + 1] =  (numbers[i].toDouble() + numbers[i + 1].toDouble()).toString()

                } else if (operators[i] == "-") {
                    numbers[i + 1] = (numbers[i].toDouble() - numbers[i + 1].toDouble()).toString()
                }
            }
        }

        val calcResult = if (numbers.isEmpty()) {
            "0"
        } else if (divisionByZeroFlag) {
            "0では割れません"
        } else {
            numbers[numbers.size - 1]
        }
        divisionByZeroFlag = false
        return calcResult
    }

    private fun isNumericWithDot(number: String): Boolean { // 数字or小数点からなる文字列ならばtrue
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

