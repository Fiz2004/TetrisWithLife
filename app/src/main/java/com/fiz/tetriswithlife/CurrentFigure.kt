package com.fiz.tetriswithlife

private const val START_STEP_MOVE_AUTO = 0.03
private const val  ADD_STEP_MOVE_AUTO = 0.1
private const val  STEP_MOVE_KEY_X = 1
private const val  STEP_MOVE_KEY_Y = 4

class CurrentFigure(val grid:Grid, val figure:Figure):Figure() {
    var stepMoveAuto = START_STEP_MOVE_AUTO
    val position = createStartPosition()
    init{
        cells = figure.cells.clone()
    }

    fun createStartPosition():Point {
        val width = cells.reduce{a, b -> if (a.x > b.x ) a else b}.x
        val height = cells.reduce{a, b -> if (a.y > b.y)  a else b}.y
        return Point((0 until (grid.width - width.toInt())).shuffled().first().toInt(), -1 - height.toInt())
    }

    fun getPositionTile(x: Float = position.x, y: Float = position.y): Array<Point> {
        return cells.map { cell-> Point(cell.x + x.toInt(), cell.y + y.toInt())}.toTypedArray()
    }

    fun fixation(scores:Int) {
        val scoresForLevel = 300
        stepMoveAuto = ADD_STEP_MOVE_AUTO
        + ADD_STEP_MOVE_AUTO * (scores / scoresForLevel.toFloat())
    }

    fun isCollission(x:Int, y:Int):Boolean {
        if (getPositionTile(x.toFloat(), y.toFloat()).any { p ->
                p.x < 0 || p.x > this.grid.width - 1 || p.y > this.grid.height - 1
            } )
            return true

        if (getPositionTile(x.toFloat(), y.toFloat())
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

    fun moves( controller: Controller ):String {
        if (controller.Left) moveLeft()
        if (controller.Right) moveRight()
        if (controller.Up) rotate()
        val step:Float=if (controller.Down) STEP_MOVE_KEY_Y.toFloat() else stepMoveAuto.toFloat()
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

    fun moveDown(stepY:Float):String {
        val yStart = Math.ceil(position.y.toDouble())
        val yEnd = Math.ceil(position.y + stepY.toDouble())
        val yMax = getYMax(yStart.toInt(), yEnd.toInt())

        if (isCheckCollisionIfMoveDown(yStart.toInt(), yEnd.toInt())) {
            if (getPositionTile(position.x, yMax.toFloat())
                    .any{ p -> (p.y - 1) < 0})
            return "endGame"
            position.y = yMax.toFloat()

            return "fixation"
        }

        position.y += (if (stepY < 1 ) stepY else yMax - yStart).toFloat()
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
