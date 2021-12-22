package com.fiz.tetriswithlife

class CharacterBreath extends CharacterEat {
    constructor(grid) {
        super(grid);

        this.timeBreath = Date.now();
        this.breath = true;
    }

    fun update(grid) {
        return super.update(grid);
    }

    fun isBreath(grid) {
        this.breath = this.findWay(this.posTile, [], grid);

        if (this.breath) this.timeBreath = Date.now();

        return this.breath;
    }

    fun findWay(tile:Point, cash:Array<Point>, grid:Grid):Boolean {
        if (tile.y == 0)
            return true;

        cash.push(Point( tile.x, tile.y ))

        for (const element of [{ x: 0, y: -1 }, { x: 1, y: 0 }, { x: -1, y: 0 }, { x: 0, y: 1 }])
        if (grid.isInside(tile.plus(element)) && grid.isFree(tile.plus(element))
            && !cash.find(({ x, y }) => tile.x + element.x === x && tile.y + element.y === y)
                && this.findWay(tile.plus(element), cash, grid))
        return true;

        return false;
    }
}
