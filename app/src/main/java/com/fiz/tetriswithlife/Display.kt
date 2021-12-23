package com.fiz.tetriswithlife

import android.content.res.Resources
import android.graphics.*
import android.util.Log


private const val NUMBER_IMAGES_FIGURE = 4
private const val SIZE_TILES = 30

// Объект рисования
class Display(resources: Resources, val widthCanvas: Int, val heightCanvas: Int) {
    private val paint: Paint = Paint()

    private val bmpFon: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.fon)
    private val bmpCharacter: Bitmap =
        BitmapFactory.decodeResource(resources, R.drawable.character)
    private val bmpKv: Array<Bitmap> = arrayOf(
        BitmapFactory.decodeResource(resources, R.drawable.kvadrat1),
        BitmapFactory.decodeResource(resources, R.drawable.kvadrat2),
        BitmapFactory.decodeResource(resources, R.drawable.kvadrat3),
        BitmapFactory.decodeResource(resources, R.drawable.kvadrat4),
        BitmapFactory.decodeResource(resources, R.drawable.kvadrat5)
    )
//    private val canvas = document.querySelector("#canvasId")
//    private val ctx = canvas.getContext("2d")
//    private val canvasNextFigure = document.querySelector("#canvasNextFigureId")
//    private val ctxNextFigure = canvasNextFigure.getContext("2d")
//
//    private val txtScores = document.querySelector("#scores")
//    private val txtRecord = document.querySelector("#record")
//    private val elementTimeBreath = document.querySelector("#Breath")
//    private val elementDivBreath = document.querySelector("#infoID")

//    val width
//        get() = canvas.width / SIZE_TILES
//
//    val height
//        get() = canvas.height / SIZE_TILES

    fun load() {
        // Переменные для отслеживания загрузки изображений
        val numberImg = NUMBER_IMAGES_FIGURE + 1;
        var currentImg = 0;

        // Формируем картинки для фигур
//        imgKv = Array(NUMBER_IMAGES_FIGURE, {})
//        for (i in 0 until imgKv.size)
//        imgKv[i] = Image()

//        imgFon = Image()
//        imgBeetle = Image()

//        return new Promise ((resolve) => {
//        const loadImage =() => { currentImg < numberImg ? currentImg += 1 : resolve(); };
//
//        for (let i = 0; i < this.imgKv.length; i++) {
//        this.imgKv[i].src = `${DIRECTORY_IMG}Kvadrat${i + 1}.png`;
//        this.imgKv[i].onload = loadImage;
//    }
//
//        this.imgFon.src = `${DIRECTORY_IMG}Fon.png`;
//        this.imgFon.onload = loadImage;
//
//        this.imgBeetle.src = `${DIRECTORY_IMG}Beetle.png`;
//        this.imgBeetle.onload = loadImage;
//    })
    }

    fun drawNextFigure(nextFigure: Figure) {
//        ctxNextFigure.clearRect(
//            0,
//            0,
//            this.canvasNextFigure.width,
//            this.canvasNextFigure.height
//        );
//        for (const cell of nextFigure.cells)
//        ctxNextFigure.drawImage(
//            this.imgKv[cell.view - 1],
//            0, 0,
//            SIZE_TILES, SIZE_TILES,
//            cell.x * SIZE_TILES, cell.y * SIZE_TILES,
//            SIZE_TILES, SIZE_TILES,
//        );
    }

    fun render(state: State, canvas: Canvas) {
        drawGridElements(state.grid, canvas)
        drawCurrentFigure(state.currentFigure, canvas)
        drawCharacter(state.character, canvas);
//        drawNextFigure(nextFigure);
//
//        txtScores.textContent = String(scores).padStart(6, '0');
//        txtRecord.textContent = String(record).padStart(6, '0');
//
//        if (status === 'pause')
//            document.getElementById('pause').textContent = 'Продолжить';
//        else
//            document.getElementById('pause').textContent = 'Пауза';
//
//        if (status !== 'pause') {
//            let sec;
//            if (!character.breath)
//                sec = Math.max(
//                    TIMES_BREATH_LOSE - Math.ceil((Date.now() - character.timeBreath) / 1000),
//                    0
//                );
//            else
//                sec = TIMES_BREATH_LOSE;
//            if (!character.breath) {
//                if (!this.elementTimeBreath) {
//                    const element = document . createElement ('h1');
//                    element.id = 'Breath';
//                    document.querySelector('#infoID').append(element);
//                    this.elementTimeBreath = document.querySelector('#Breath');
//                }
//                this.elementTimeBreath.innerHTML = `Задыхаемся<br/>Осталось секунд: ${sec}`;
//            } else if (this.elementTimeBreath) {
//                this.elementTimeBreath.parentNode.removeChild(this.elementTimeBreath);
//                this.elementTimeBreath = null;
//            }
//
//            const int =(Math.floor(sec) * 255) / TIMES_BREATH_LOSE;
//            document.querySelector('#infoID').style.backgroundColor = `rgb(255, ${int}, ${int})`;
//        }
    }

    fun drawGridElements(grid: Grid, canvas: Canvas) {
        val tile = bmpFon.width / 4
        val newTile = (tile / 1.5).toFloat()
        for (y in 0 until grid.height)
            for (x in 0 until grid.width) {
                val screenX = x * newTile
                val screenY = y * newTile
                val NUMBER_COLUMNS_IMAGES_FON = 4
                val NUMBER_ROWS_IMAGES_FON = 4
                val offsetX = (grid.space[y][x].background / NUMBER_COLUMNS_IMAGES_FON) * tile;
                val offsetY = (grid.space[y][x].background % NUMBER_ROWS_IMAGES_FON) * tile;

                canvas.drawBitmap(
                    bmpFon,
                    Rect(offsetX, offsetY, offsetX + tile, offsetY + tile),
                    RectF(screenX, screenY, screenX + newTile, screenY + newTile),
                    paint
                )
            }

        for (y in 0 until grid.height)
            for (x in 0 until grid.width)
                if (grid.space[y][x].block != 0) {
                    val screenX = x * newTile
                    val screenY = y * newTile
                    val offset: Point = getOffset(grid.space[y][x])
                    canvas.drawBitmap(
                        bmpKv[grid.space[y][x].block - 1],
                        Rect(
                            offset.x.toInt() * tile,
                            offset.y.toInt() * tile,
                            offset.x.toInt() * tile + tile,
                            offset.y.toInt() * tile + tile
                        ),
                        RectF(screenX, screenY, screenX + newTile, screenY + newTile),
                        paint
                    )
                }
    }

    fun drawCurrentFigure(currentFigure: CurrentFigure, canvas: Canvas) {
        val tile = bmpFon.width / 4
        val newTile = (tile / 1.5).toFloat()
        for (cell in currentFigure.cells) {
            val screenX = (cell.x + currentFigure.position.x) * newTile
            val screenY = (cell.y + currentFigure.position.y) * newTile
            canvas.drawBitmap(
                bmpKv[cell.view - 1],
                Rect(0, 0, tile, tile),
                RectF(screenX, screenY, screenX + newTile, screenY + newTile),
                paint
            );
        }
    }

    fun drawCharacter(character:Character, canvas: Canvas) {
        val tile = bmpFon.width / 4
        val newTile = (tile / 1.5).toFloat()
        val offset = character.getSprite();
        offset.x *=tile
        offset.y *= tile
        val screenX = character.position.x * newTile
        val screenY = character.position.y * newTile
        canvas.drawBitmap(
            bmpCharacter,
            Rect(
                offset.x.toInt(),
                offset.y.toInt() ,
                offset.x.toInt()  + tile,
                offset.y.toInt()  + tile
            ),
            RectF(screenX, screenY, screenX + newTile, screenY + newTile),
            paint
        )
    }
}

// Получить смещение по тайлам в зависимости от статуса элемента
fun getOffset(element: Element): Point {
    if (element.getSpaceStatus() == 'R')
        return Point((element.status.R - 1).toFloat(), 1.0F)

    if (element.getSpaceStatus() == 'L')
        return Point((element.status.L - 1).toFloat(), 2.0F)

    if (element.getSpaceStatus() == 'U')
        return Point((element.status.U - 1).toFloat(), 3.0F)

    return Point(0.0F, 0.0F)
}
