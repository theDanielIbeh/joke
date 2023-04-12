package com.example.jokes.fragments.favourites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.jokes.databinding.FragmentFavouritesBinding

class FavouritesFragment : Fragment() {

    private lateinit var viewModel: FavouritesViewModel
    private lateinit var viewModelFactory: FavouritesViewModel.FavouritesViewModelFactory
    private lateinit var binding: FragmentFavouritesBinding
    private lateinit var adapter: FavouritesPagingDataAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritesBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        viewModelFactory = activity?.let {
            FavouritesViewModel.FavouritesViewModelFactory(
                application = it.application
            )
        }!!
        viewModel = ViewModelProvider(this, viewModelFactory)[FavouritesViewModel::class.java]

        initialiseRecycler()

        return binding.root
    }

    private fun initialiseRecycler() {
        adapter = FavouritesPagingDataAdapter()

        viewModel.jokesData.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = adapter
        (binding.recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }
}