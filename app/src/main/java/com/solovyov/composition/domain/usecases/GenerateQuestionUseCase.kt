package com.solovyov.composition.domain.usecases

import com.solovyov.composition.domain.entity.Question
import com.solovyov.composition.domain.repository.GameRepository

class GenerateQuestionUseCase(
    private val repository: GameRepository
) {

    operator fun invoke(maxSumValue: Int) : Question =
        repository.generateQuestion(maxSumValue, COUNT_OF_OPTIONS)


    private companion object{
        private const val COUNT_OF_OPTIONS = 6
    }
}