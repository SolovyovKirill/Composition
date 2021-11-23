package com.solovyov.composition.presentation.viewmodel.gameviewmodel

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.solovyov.composition.R
import com.solovyov.composition.data.GameRepositoryImpl
import com.solovyov.composition.domain.entity.GameResult
import com.solovyov.composition.domain.entity.GameSettings
import com.solovyov.composition.domain.entity.Level
import com.solovyov.composition.domain.entity.Question
import com.solovyov.composition.domain.usecases.GenerateQuestionUseCase
import com.solovyov.composition.domain.usecases.GetGameSettingsUseCase

class GameViewModel(
    private val application: Application,
    private val level: Level
) : ViewModel() {

    private lateinit var gameSettings: GameSettings
    private val repository = GameRepositoryImpl

    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private val getGameSettingsUseCase = GetGameSettingsUseCase(repository)

    private val _formattedTime = MutableLiveData<String>()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private val _question = MutableLiveData<Question>()
    val question: LiveData<Question>
        get() = _question

    private val _percentOfRightAnswers = MutableLiveData<Int>()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    private val _progressAnswers = MutableLiveData<String>()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    private val _enoughCountOfRightAnswer = MutableLiveData<Boolean>()
    val enoughCountOfRightAnswer: LiveData<Boolean>
        get() = _enoughCountOfRightAnswer

    private val _enoughPercentOfRightAnswer = MutableLiveData<Boolean>()
    val enoughPercentOfRightAnswer: LiveData<Boolean>
        get() = _enoughPercentOfRightAnswer

    private val _minPercent = MutableLiveData<Int>()
    val minPercent: LiveData<Int>
        get() = _minPercent

    private val _gameResult = MutableLiveData<GameResult>()
    val gameResult: LiveData<GameResult>
        get() = _gameResult

    private var timer: CountDownTimer? = null

    private var countOfRightAnswer = 0
    private var countOfQuestions = 0

    init {
        startGame()
    }

    private fun startGame() {
        getGameSettings()
        startTimer()
        generateQuestion()
        updateProgress()
    }

    fun chooseAnswer(number: Int) {
        checkAnswer(number)
        updateProgress()
        generateQuestion()
    }

    private fun updateProgress() {
        val percent = calculatePercentOfRigtnAnswer()
        _percentOfRightAnswers.value = percent
        _progressAnswers.value = String.format(
            application.resources.getString(R.string.progress_answers),
            countOfRightAnswer,
            gameSettings.minCountOfRightAnswers
        )
        _enoughCountOfRightAnswer.value = countOfRightAnswer >= gameSettings.minCountOfRightAnswers
        _enoughPercentOfRightAnswer.value = percent >= gameSettings.minPercentOfRightAnswers

    }

    private fun calculatePercentOfRigtnAnswer(): Int {
        if (countOfQuestions == 0) {
            return 0
        }
        return ((countOfRightAnswer / countOfQuestions.toDouble()) * 100).toInt()
    }

    private fun checkAnswer(number: Int) {
        val rightAnswer = question.value?.rightAnswer
        if (number == rightAnswer) {
            countOfRightAnswer++
        }
        countOfQuestions++
    }

    private fun getGameSettings() {
        this.gameSettings = getGameSettingsUseCase(level)
        _minPercent.value = gameSettings.minPercentOfRightAnswers
    }

    private fun startTimer() {
        timer = object : CountDownTimer(
            gameSettings.gameTimeInSeconds * MILLIS_IN_SECONDS,
            MILLIS_IN_SECONDS
        ) {
            override fun onTick(millisUntilFinished: Long) {
                _formattedTime.value = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                finishGame()
            }

        }
        timer?.start()
    }

    private fun generateQuestion() {
        _question.value = generateQuestionUseCase(gameSettings.maxSumValue)
    }

    private fun formatTime(millisUntilFinished: Long): String {
        val seconds = millisUntilFinished / MILLIS_IN_SECONDS
        val minutes = seconds / SECONDS_IN_MINUTES
        val leftSeconds = seconds - (minutes * SECONDS_IN_MINUTES)
        return String.format("%02d:%02d", minutes, leftSeconds)
    }

    private fun finishGame() {
        _gameResult.value = GameResult(
            enoughCountOfRightAnswer.value == true
                    && enoughPercentOfRightAnswer.value == true,
            countOfRightAnswer,
            countOfQuestions,
            gameSettings
        )
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object {
        private const val SECONDS_IN_MINUTES = 60
        private const val MILLIS_IN_SECONDS = 1000L
    }
}