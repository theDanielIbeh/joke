package com.example.jokes.fragments.home

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setMenu()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setMenu() {
        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()

        // Add menu items without using the Fragment Menu APIs
        // Note how we can tie the MenuProvider to the viewLifecycleOwner
        // and an optional Lifecycle.State (here, RESUMED) to indicate when
        // the menu should be visible
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.home_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.favourites_item -> {
                        Navigation.findNavController(requireActivity(), R.id.my_nav_host_fragment)
                            .navigate(R.id.favouritesFragment)
                        true
                    }
                    R.id.favourite_joke_item -> {
                        lifecycleScope.launch {
                            viewModel.insertJoke()
                            viewModel.getJoke()
                            Navigation.findNavController(requireActivity(), R.id.my_nav_host_fragment)
                                .navigate(R.id.favouritesFragment)
                        }
                        true
                    }
                    R.id.share_item -> {
                        shareJoke()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun shareJoke() {
        val joke = if (viewModel.joke.value?.type == "single") {
            viewModel.joke.value?.joke
        } else {
            "${viewModel.joke.value?.setup}\n${viewModel.joke.value?.delivery}"
        }

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, joke)
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
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