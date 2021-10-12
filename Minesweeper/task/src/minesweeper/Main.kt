package minesweeper

import kotlin.math.max
import kotlin.math.min

data class Cells(var area: Int){
    companion object{
        var cntArea = 0
    }
    var isMine = 0 // 0 - пустая ячейка, 1 - мина
    var cntMinesAround = 0 // количество мин вокруг
    var isMarked = false
    var isOpen = false

    fun show(pHide: Boolean = true): String{
        return when{
            isOpen -> if (cntMinesAround > 0) cntMinesAround.toString() else "/"
            (isMine == 1 && !pHide) -> "X"
            isMarked -> "*"
            else -> "."
        }
    }

    fun isEmpty(): Boolean {
        return this.isMine == 0 && this.cntMinesAround == 0
    }
}

class MineField(private val pColumns: Int, private val pRows: Int, private val pCntMine: Int){
    private val field = Array(pRows) { Array(pColumns) { Cells(++Cells.cntArea) } }
    private var notInitialized = true

    private fun initField(vRow: Int, vColumn: Int){
        var vCnt = pCntMine
        var vCel: Cells
        val arRand = mutableListOf<Cells>()

        for (i in 0 until pRows){
            for (j in 0 until pColumns) {
                if (i == vRow && j == vColumn) continue
                arRand.add(field[i][j])
            }
        }
        while (vCnt > 0){
            vCel = arRand.random()
            vCel.isMine = 1
            arRand.remove(vCel)
            vCnt--
        }

        for (i in 0 until pRows) {
            for (j in 0 until pColumns) {
                if (field[i][j].isMine == 1) continue
                field[i][j].cntMinesAround = cntMineAround(i, j)
            }
        }
        normalizeArea()
        notInitialized = false
    }

    private fun cntMineAround(indI: Int, indJ: Int): Int {
        var rez = 0
        for (i in max((indI - 1),0)..min((indI + 1), pRows-1)){
            for (j in max((indJ - 1),0)..min((indJ + 1), pColumns-1)){
                if ((i == indI) && (j == indJ)) continue
                rez += field[i][j].isMine
            }
        }
        return rez
    }

    private fun setArea(vArea1: Int, vArea2: Int): Int{
        val vNew = min(vArea1, vArea2)
        val vOld = max(vArea1, vArea2)
        for (row in field){
            for (cel in row)
                if (cel.area == vOld) cel.area = vNew
        }
        return vNew
    }

    private fun normalizeArea(){
        for (i in field.indices) {
            for (j in field[i].indices) {
                if (!field[i][j].isEmpty()) continue
                for(indI in max((i - 1),0)..i){
                    for (indJ in max((j - 1),0)..min(j+1, pColumns-1)){
                        if ((i == indI) && (j == indJ)) continue
                        if (field[indI][indJ].isEmpty()){
                            if (field[indI][indJ].area == field[i][j].area ) continue
                            field[i][j].area = setArea(field[i][j].area, field[indI][indJ].area)
                        }
                    }
                }
            }
        }
    }

    fun prnField(pHide: Boolean = true){
        val sep = ""
        println()
        println(" |$sep${(1..pColumns).joinToString(sep)}$sep|")
        if (sep == " ") println("-|${"-".repeat(pColumns*2+1)}|") else println("-|${"-".repeat(pColumns)}|")
        for (vRow in field){
            print("${field.indexOf(vRow)+1}|$sep")
            for (vCell in vRow){
                print("${vCell.show(pHide)}$sep")
            }
            println("|")
        }
        if (sep == " ") println("-|${"-".repeat(pColumns*2+1)}|") else println("-|${"-".repeat(pColumns)}|")
    }

    fun open(vRow: String, vColumn: String): Boolean{
        val vR = vRow.toInt() - 1
        val vC = vColumn.toInt() - 1

        if (notInitialized) initField(vR, vC)
        if (field[vR][vC].isMine == 1) {
            prnField(false)
            println("You stepped on a mine and failed!")
            return false
        }
        if (field[vR][vC].isOpen) return true
        field[vR][vC].isOpen = true
        if (field[vR][vC].cntMinesAround > 0)  return true
        openEmptyCells(field[vR][vC].area)
        return true
    }

    private fun openEmptyCells(pArea: Int) {
        for (i in field.indices){
            for (j in field[i].indices) {
                if (field[i][j].area == pArea) {
                    for (indI in max(i - 1, 0)..min(i + 1, pRows - 1)) {
                        for (indJ in max(j - 1, 0)..min(j + 1, pColumns - 1)) {
                            field[indI][indJ].isOpen = true
                        }
                    }
                }
            }
        }
    }

    private fun checkWin(pFlag: Int): Boolean {
        for (i in 0 until pRows) for (j in 0 until pColumns) {
            when(pFlag){
                1 -> {
                    if (field[i][j].isMine == 0 && field[i][j].isMarked) return false
                    if (field[i][j].isMine == 1 && !field[i][j].isMarked) return false
                    continue
                }
                0 -> {
                    if (field[i][j].isMine == 0 && !field[i][j].isOpen) return false
                    continue
                }
            }
        }
        return true
    }

    fun checkGameOver(): Boolean{
        if (checkWin(0) || checkWin(1)) {
            this.prnField()
            println("Congratulations! You found all the mines!")
            return true
        }
        return false
    }

    fun mark(vRow: String, vColumn: String) {
        val i = vRow.toInt() - 1
        val j = vColumn.toInt() - 1
        field[i][j].isMarked = !field[i][j].isMarked
    }
}

fun main(){
    var inStr: MutableList<String>
    print("How many mines do you want on the field? ")
    val myField = MineField(9,9, readLine()!!.toInt())
    do {
        myField.prnField()
        print("Set/unset mine marks or claim a cell as free: ")
        inStr = readLine()!!.split(" ").toMutableList()
        when(inStr[2]){
            "free" -> if (!myField.open(inStr[1], inStr[0])) break
            "mine" -> myField.mark(inStr[1], inStr[0])
        }
    } while (!myField.checkGameOver())
}