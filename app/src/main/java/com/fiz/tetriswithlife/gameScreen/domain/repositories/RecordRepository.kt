package com.fiz.tetriswithlife.gameScreen.domain.repositories

interface RecordRepository {
    fun loadRecord(): Int

    fun saveRecord(scores: Int)
}