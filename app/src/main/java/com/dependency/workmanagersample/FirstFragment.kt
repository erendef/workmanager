package com.dependency.workmanagersample

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.work.WorkInfo
import com.dependency.workmanagersample.databinding.FragmentFirstBinding
import com.dependency.workmanagersample.utils.makeStatusNotification
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FirstViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        binding.vm = viewModel
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startWorkButton.setOnClickListener {
            if(binding.startWorkButton.text == getString(R.string.start_work)) {
                viewModel.startIconWork()
                binding.startWorkButton.text = getString(R.string.stop_work)
            } else {
                viewModel.stopIconWork()
                binding.startWorkButton.text = getString(R.string.start_work)
            }
        }
        setWorkManager()
        setWorkObserver()
    }

    private fun setWorkManager(){
        val context = activity?.applicationContext
        if (context != null) {
            viewModel.setWorkManager(context)
        }
    }

    private fun setWorkObserver(){
        val context = activity?.applicationContext
        if (context != null) {
            viewModel.outputWorkInfos.observe(viewLifecycleOwner, workInfosObserver())
        }
    }

    private fun workInfosObserver(): Observer<List<WorkInfo>> {
        return Observer { listOfWorkInfo ->

            if (listOfWorkInfo.isNullOrEmpty()) {
                return@Observer
            }

            val workInfo = listOfWorkInfo[0]

            viewModel.isOnWork.value = !workInfo.state.isFinished
            if (workInfo.state == WorkInfo.State.FAILED) {
                makeStatusNotification(resources.getString(R.string.something_went_wrong),
                    requireContext().applicationContext)
            }
            else if (workInfo.state.isFinished) {
                viewModel.createInputData(requireActivity().applicationContext)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}