package com.fiz.tetriswithlife

const val START_STEP_MOVE_AUTO = 0.03
const val  ADD_STEP_MOVE_AUTO = 0.1
const val  STEP_MOVE_KEY_X = 1
const val  STEP_MOVE_KEY_Y = 4

class CurrentFigure(val grid:Grid, val figure:Figure):Figure() {
    var stepMoveAuto = START_STEP_MOVE_AUTO
    val position = createStartPosition()
    init{
        cells = figure.cells.clone()
    }

    fun createStartPosition():Point {
        val width = cells.reduce{a, b -> if (a.x > b.x ) a else b}.x
        val height = cells.reduce{a, b -> if (a.y > b.y)  a else b}.y
        return Point(Math.floor(Math.random() * (grid.width - 1 - width)), -1 - height)
    }

    fun getPositionTile(x: Double = position.x, y: Double = position.y): Array<Point> {
        return cells.map { cell-> Point(cell.x + Math.ceil(x), cell.y + Math.ceil(y))}.toTypedArray()
    }

    fun fixation(scores:Int) {
        val scoresForLevel = 300
        stepMoveAuto = ADD_STEP_MOVE_AUTO
        + ADD_STEP_MOVE_AUTO * (scores / scoresForLevel.toDouble())
    }

    fun isCollission(x:Int, y:Int):Boolean {
        if (getPositionTile(x.toDouble(), y.toDouble()).any { p ->
                p.x < 0 || p.x > this.grid.width - 1 || p.y > this.grid.height - 1
            } )
            return true

        if (getPositionTile(x.toDouble(), y.toDouble())
                .any { point ->
                    grid.isInside(point) && grid.space[point.y.toInt()][point.x.toInt()].block !== 0
                })
        return true

        return false
    }

    fun rotate() {
        val oldCells = cells
        cells = cells.map{ cell-> Cell(3 - cell.y, cell.x,cell.view)}.toTypedArray()
        if (isCollission(position.x.toInt(), position.y.toInt()))
            cells = oldCells
    }

    fun moves( left:Boolean=false, right:Boolean=false, up:Boolean=false, down:Boolean=false ):String {
        if (left) moveLeft()
        if (right) moveRight()
        if (up) rotate()
        val step:Double=if (down) STEP_MOVE_KEY_Y.toDouble() else stepMoveAuto
        return moveDown(step)
    }

    fun moveLeft() {
        if (!isCollission((position.x - STEP_MOVE_KEY_X).toInt(), position.y.toInt()))
            position.x -= STEP_MOVE_KEY_X
    }

    fun moveRight() {
        if (!isCollission((position.x + STEP_MOVE_KEY_X).toInt(), position.y.toInt()))
            position.x += STEP_MOVE_KEY_X
    }

    fun moveDown(stepY:Double):String {
        val yStart = Math.ceil(position.y)
        val yEnd = Math.ceil(position.y + stepY)
        val yMax = getYMax(yStart.toInt(), yEnd.toInt())

        if (isCheckCollisionIfMoveDown(yStart.toInt(), yEnd.toInt())) {
            if (getPositionTile(position.x, yMax.toDouble())
                    .any{ p -> (p.y - 1) < 0})
            return "endGame"
            position.y = yMax.toDouble()

            return "fixation"
        }

        this.position.y += if (stepY < 1 ) stepY else yMax - yStart
        return "fall"
    }

    fun getYMax (yStart:Int, yEnd:Int) :Int {
        for ( y in yStart..yEnd)
        if (isCollission(position.x.toInt(), y))
            return y - 1

        return yEnd
    }

    fun isCheckCollisionIfMoveDown (yStart:Int, yEnd:Int):Boolean{
        for ( y in yStart..yEnd)
        if (isCollission(position.x.toInt(), y))
            return true

        return false
    }
}
