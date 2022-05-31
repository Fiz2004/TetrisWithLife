package com.fiz.tetriswithlife.gameScreen.ui

import com.fiz.tetriswithlife.gameScreen.ui.models.*
import java.io.Serializable

data class GameState(
    val backgroundsUi: List<BackgroundUi>,
    val blocksUi: List<BlockUi>,
    val characterUi: CharacterUi,
    val blocksCurrentFigureUi: List<CurrentFigureUi>,
    val blocksNextFigureUi: List<NextFigureUi>,
    val scores: Int
) : Serializable


