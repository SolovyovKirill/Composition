package com.solovyov.composition.presentation.view.gamefragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.navigation.fragment.findNavController
import com.solovyov.composition.R
import com.solovyov.composition.databinding.FragmentGameBinding
import com.solovyov.composition.domain.entity.GameResult
import com.solovyov.composition.domain.entity.GameSettings
import com.solovyov.composition.domain.entity.Level
import com.solovyov.composition.presentation.view.gamefinishedfragment.GameFinishedFragment
import com.solovyov.composition.presentation.viewmodel.gameviewmodel.GameViewModel
import com.solovyov.composition.presentation.viewmodel.gameviewmodel.GameViewModelFactory
import java.lang.RuntimeException

class GameFragment : Fragment() {

    private lateinit var level: Level

    private val viewModelFactory by lazy {
        GameViewModelFactory(level, requireActivity().application)
    }

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[GameViewModel::class.java]
    }

    private val tvOptions by lazy {
        mutableListOf<TextView>().apply {
            add(binding.tvOption1)
            add(binding.tvOption2)
            add(binding.tvOption3)
            add(binding.tvOption4)
            add(binding.tvOption5)
            add(binding.tvOption6)
        }
    }

    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        setOnClickListenersToOptions()
    }

    private fun setOnClickListenersToOptions() {
        for (tvOption in tvOptions) {
            tvOption.setOnClickListener {
                viewModel.chooseAnswer(tvOption.text.toString().toInt())
            }
        }
    }

    private fun observeViewModel() {
        viewModel.question.observe(viewLifecycleOwner) {
            binding.tvSum.text = it.sum.toString()
            binding.tvLeftNumber.text = it.visibleNumber.toString()
            for (i in 0 until tvOptions.size) {
                tvOptions[i].text = it.options[i].toString()
            }
        }
        viewModel.percentOfRightAnswers.observe(viewLifecycleOwner) {
            binding.progressBar.setProgress(it, true)
        }
        viewModel.formattedTime.observe(viewLifecycleOwner) {
            binding.tvTimer.text = it
        }
        viewModel.enoughCountOfRightAnswer.observe(viewLifecycleOwner) {

            binding.tvAnswerProgress.setTextColor(getColorByState(it))
        }
        viewModel.enoughPercentOfRightAnswer.observe(viewLifecycleOwner) {
            val color = getColorByState(it)
            binding.progressBar.progressTintList = ColorStateList.valueOf(color)
        }
        viewModel.minPercent.observe(viewLifecycleOwner) {
            binding.progressBar.secondaryProgress = it
        }
        viewModel.gameResult.observe(viewLifecycleOwner) {
            launchGameFinishedFragment(it)
        }
        viewModel.progressAnswers.observe(viewLifecycleOwner) {
            binding.tvAnswerProgress.text = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getColorByState(goodState: Boolean): Int {
        val colorResId = if (goodState) {
            android.R.color.holo_green_light
        } else {
            android.R.color.holo_red_light
        }
        return ContextCompat.getColor(requireContext(), colorResId)
    }

    private fun parseArgs() {
        requireArguments().getParcelable<Level>(KEY_LEVEL)?.let {
            level = it
        }
    }

    private fun launchGameFinishedFragment(gameResult: GameResult) {
        val args = Bundle().apply {
            putParcelable(GameFinishedFragment.KEY_GAME_RESULT, gameResult)
        }
        findNavController().navigate(R.id.action_gameFragment_to_gameFinishedFragment, args)
    }

    companion object {

        const val NAME = "GameFragment"
        const val KEY_LEVEL = "level"

        fun newInstance(level: Level): GameFragment {
            return GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LEVEL, level)
                }
            }
        }
    }

}