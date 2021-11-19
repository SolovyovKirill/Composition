package com.solovyov.composition.domain.repository

import com.solovyov.composition.domain.entity.GameSettings
import com.solovyov.composition.domain.entity.Level
import com.solovyov.composition.domain.entity.Question

interface GameRepository {

    fun generateQuestion(maxSumValue: Int, countOfOptions: Int) : Question

    fun getGameSettings(level: Level) : GameSettings
}