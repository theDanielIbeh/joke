package com.example.jokes.fragments.home

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jokes.R
import com.example.jokes.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var viewModelFactory: HomeViewModel.HomeViewModelFactory
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        viewModelFactory = activity?.let {
            HomeViewModel.HomeViewModelFactory(
                application = it.application
            )
        }!!
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        observeLiveDataValues()
        binding.nextButton.setOnClickListener {
            viewModel.getJoke()
        }
        binding.configureButton.setOnClickListener {
            navigate()
        }

        return binding.root
    }

    private fun observeLiveDataValues() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading joke, please wait")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            viewModel.joke.observe(viewLifecycleOwner) {
                binding.isSingle = it?.type == "single"
                it?.setup?.let { i -> binding.textViewSetup.text = i }
                binding.textViewDelivery.text = if (it?.type == "single") {
                    it.joke
                } else {
                    it?.delivery
                }
                progressDialog.dismiss()
            }
        } catch (e: Exception) {
            progressDialog.dismiss()
        }
    }

    private fun navigate() {
        findNavController().navigate(R.id.settingsFragment)
    }
}