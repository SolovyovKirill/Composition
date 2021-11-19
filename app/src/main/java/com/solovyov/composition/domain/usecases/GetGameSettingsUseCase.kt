package com.solovyov.composition.domain.usecases

import com.solovyov.composition.domain.entity.GameSettings
import com.solovyov.composition.domain.entity.Level
import com.solovyov.composition.domain.repository.GameRepository

class GetGameSettingsUseCase(
    private val repository: GameRepository
) {
    operator fun invoke(level: Level): GameSettings =
        repository.getGameSettings(level)
}