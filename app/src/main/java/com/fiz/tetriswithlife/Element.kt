package com.fiz.tetriswithlife

data class Status(var L:Int, var R:Int, var U:Int)

class Element (val background:Int,var block:Int=0,var status: Status =Status(0,0,0)){
    fun getSpaceStatus():Char {
        if (status.L !== 0) return 'L';
        if (status.R !== 0) return 'R';
        if (status.U !== 0) return 'U';

        return '0';
    }

    fun setZero() {
        block = 0;
        status = Status(0,0,0);
    }

    fun setElement(element:Element) {
        block = element.block;
        status = Status(element.status.L, element.status.R, element.status.U );
    }
}
