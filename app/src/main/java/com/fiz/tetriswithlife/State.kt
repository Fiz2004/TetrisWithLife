package com.fiz.tetriswithlife

import android.service.controls.Control

private const val NUMBER_FRAMES_ELEMENTS = 4

// Время без дыхания для проигрыша
private const val TIMES_BREATH_LOSE = 60

class State(val width:Int, val height:Int) {
    val grid = Grid(width, height)
    //val character = CharacterBreath(grid)

    var scores = 0

    //val record = localStorage.getItem('Record') || 0
    var status = "playing"

    val pauseTime: Float? = null

    var nextFigure: Figure = Figure()
    var currentFigure:CurrentFigure = CurrentFigure(grid, nextFigure)

    fun createCurrentFigure() {
        nextFigure = if (nextFigure!=null) nextFigure else Figure()
        currentFigure = CurrentFigure(grid, nextFigure)
        nextFigure = Figure()
    }

    fun update(deltaTime:Float, controller:Controller):Boolean
    {
        if (actionsControl(controller) === false
//            || (!character.isBreath(this.grid) && (checkLose() || isCrushedBeetle()))
            || status === "new game"
        ) {
            ifRecord()
            return false
        }

//        val statusCharacter = character.update(this.grid);
//        if (statusCharacter === 'eat') {
//            val tile = character.posTile;
//            this.grid.space[tile.y][tile.x].setZero();
//            this.scores += 50;
//        } else if (statusCharacter === 'eatDestroy') {
//            changeGridDestroyElement();
//        }

        return true
    }

    fun actionsControl(controller:Controller):Boolean
    {
        val status = currentFigure.moves(controller)
        if (status === "endGame"
            // Фигура достигла препятствия
            || (status === "fall" && isCrushedBeetle())
        )
        // Стакан заполнен игра окончена
            return false

        if (status === "fixation") {
            fixation()
            createCurrentFigure()
        }

        return true
    }

    fun isCrushedBeetle():Boolean
    {
//        val tile = character.posTile;
//        for ( elem in currentFigure.getPositionTile())
//        if ((elem.x === tile.x && elem.y === tile.y)
//            || (grid.isNotFree(tile) && character.eat === 0)
//        )
//            return true;

        return false
    }

    fun fixation()
    {
        val tile=currentFigure.getPositionTile()
        for (index in tile.indices)
            grid.space[tile[index].y.toInt()][tile[index].x.toInt()].block = currentFigure.cells[index].view

        val countRowFull = grid.getCountRowFull()
        if (countRowFull!=0)
            grid.deleteRows()

        val scoresForRow = 100
        for (i in 1.. countRowFull)
        scores += i * scoresForRow

        currentFigure.fixation(scores)
        ifRecord()

//        character.deleteRow = 1;
//        character.isBreath(this.grid);
    }

    fun ifRecord()
    {
//        val record = localStorage.getItem ("Record") || 0;
//        if (scores > record) {
//            record = scores;
//            localStorage.setItem('Record', this.scores);
//        }
    }

    fun changeGridDestroyElement()
    {
//        val offset = { ...character.move };
//        if (offset.x === -1) offset.x = 0;
//
//        val tile = Point(
//            Math.floor(character.position.x)+offset.x,
//            Math.round(character.position.y)+offset.y,
//        )
//        grid.space[tile.y][tile.x].status[character.getDirectionEat()] =
//            getStatusDestroyElement() + 1;
    }

    fun getStatusDestroyElement():Int
    {
//        if (character.angle === 0)
//            return Math.floor((character.position.x % 1) * NUMBER_FRAMES_ELEMENTS);
//        if (character.angle === 180)
//            return 3 - Math.floor((this.character.position.x % 1) * NUMBER_FRAMES_ELEMENTS);

        return 0
    }

    fun checkLose():Boolean
    {
//        if ((this.grid.isNotFree(character.posTile) && character.eat === 0)
//            || TIMES_BREATH_LOSE - Math.ceil((Date.now() - character.timeBreath) / 1000) <= 0
//        )
//            return true;

        return false
    }

    fun clickPause()
    {
        if (status === "playing") {
            status = "pause"
//            pauseTime = Date.now();
        } else {
            status = "playing"
//            character.timeBreath += Date.now() - this.pauseTime
        }
    }
}
