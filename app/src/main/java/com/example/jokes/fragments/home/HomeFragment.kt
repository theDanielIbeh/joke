package com.example.jokes.fragments.home

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.jokes.R
import com.example.jokes.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var viewModelFactory: HomeViewModel.HomeViewModelFactory
    private lateinit var binding: FragmentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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
        binding.favouritesButton.setOnClickListener {
            lifecycleScope.launch {
                viewModel.insertJoke()
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.favourites_item -> {
                Navigation.findNavController(requireActivity(), R.id.my_nav_host_fragment)
                    .navigate(R.id.favouritesFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    private fun isNetworkConnected(): Boolean {
        val cm =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return cm?.activeNetworkInfo != null && cm.activeNetworkInfo?.isConnected == true
    }
}