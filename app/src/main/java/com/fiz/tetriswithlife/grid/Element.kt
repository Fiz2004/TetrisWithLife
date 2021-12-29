package com.fiz.tetriswithlife.grid

class Element(
    val background: Int,
    var block: Int = 0,
    var status: MutableMap<Char, Int?> =
        mutableMapOf('L' to 0, 'R' to 0, 'U' to 0)
) {
    fun getSpaceStatus(): Char {
        for ((key, value) in status)
            if (value != 0) return key

        return '0'
    }

    fun setZero() {
        block = 0
        status = mutableMapOf('L' to 0, 'R' to 0, 'U' to 0)
    }

    fun setElement(element: Element) {
        block = element.block
        status = mutableMapOf(
            'L' to element.status['L'],
            'R' to element.status['R'],
            'U' to element.status['U']
        )
    }
}
