package es.upm.macroscore.presentation.home.statistics

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import es.upm.macroscore.R
import es.upm.macroscore.databinding.FragmentProfileBinding
import es.upm.macroscore.databinding.FragmentStatisticsBinding


class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}