package com.solovyov.composition.presentation.view.chooselevelfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.solovyov.composition.R
import com.solovyov.composition.databinding.FragmentChooseLevelBinding
import com.solovyov.composition.domain.entity.Level
import com.solovyov.composition.presentation.view.gamefragment.GameFragment
import java.lang.RuntimeException

class ChooseLevelFragment : Fragment() {

    private var _binding: FragmentChooseLevelBinding? = null
    private val binding: FragmentChooseLevelBinding
        get() = _binding ?: throw RuntimeException("FragmentChooseLevelBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseLevelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnLevelTest.setOnClickListener { lunchGameFragment(Level.TEST) }
            btnLevelEasy.setOnClickListener { lunchGameFragment(Level.EASY) }
            btnLevelNormal.setOnClickListener { lunchGameFragment(Level.NORMAL) }
            btnLevelHard.setOnClickListener { lunchGameFragment(Level.HARD) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun lunchGameFragment(level: Level) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GameFragment.newInstance(level))
            .addToBackStack(GameFragment.NAME)
            .commit()
    }

    companion object {
        const val NAME = "ChooseLevelFragment"

        fun newInstance(): ChooseLevelFragment {
            return ChooseLevelFragment()
        }
    }
}