package com.example.jokes.fragments.settings

import android.R
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.jokes.databinding.FragmentSettingsBinding
import com.example.jokes.utils.Constants
import com.google.android.material.chip.Chip

class SettingsFragment : Fragment() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var viewModelFactory: SettingsViewModel.SettingsViewModelFactory
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.lifecycleOwner = this

        viewModelFactory = activity?.let {
            SettingsViewModel.SettingsViewModelFactory(
                application = it.application
            )
        }!!
        viewModel = ViewModelProvider(this, viewModelFactory)[SettingsViewModel::class.java]

        initializeFields()
        observeLiveDataValues()
        setOnItemClickListener()
        setupEditableFields()
        viewModel.updateFiltersLive()

        return binding.root
    }

    private fun initializeFields() {
        val categoryType = viewModel.settings.getString(Constants.CATEGORY_TYPE, "Any")
        if (binding.anyRadioButton.text == categoryType) {
            binding.radioGroup.check(binding.anyRadioButton.id)
        } else {
            binding.radioGroup.check(binding.customRadioButton.id)
        }
        setRadioButton(binding.radioGroup.checkedRadioButtonId)

        binding.categoryListAutoCompleteTextView.setText(
            if (!viewModel.selectedCategoryString.isNullOrEmpty())
                viewModel.selectedCategoryString else ""
        )
        viewModel.selectedCategoryString?.split(",")?.forEach { addCategoryChipToGroup(it) }

        binding.flagListAutoCompleteTextView.setText(
            if (!viewModel.selectedFlagString.isNullOrEmpty())
                viewModel.selectedFlagString else ""
        )
        viewModel.selectedFlagString?.split(",")?.forEach { addFlagChipToGroup(it) }

        checkCategoryField()
        setCategoryError()
    }

    private fun setCategoryError() {
        if (viewModel.selectedCategoryString == null) {
            binding.categoryListLayout.isErrorEnabled = true
            binding.categoryListLayout.error =
                getString(com.example.jokes.R.string.category_field_error)
        } else {
            binding.categoryListLayout.isErrorEnabled = false
        }
    }

    private fun setOnItemClickListener() {
        binding.categoryListAutoCompleteTextView.setOnItemClickListener { _, _, i, _ ->
            val selectedCategory = viewModel.unselectedCategoryList[i]
            viewModel.selectedCategoryList.add(selectedCategory)
            addCategoryChipToGroup(selectedCategory)
            viewModel.unselectedCategoryList.remove(selectedCategory)
            viewModel.selectedCategoryString = viewModel.selectedCategoryList.joinToString(
                ","
            )
            viewModel.editor.putString(Constants.CATEGORIES, viewModel.selectedCategoryString)
            binding.categoryListAutoCompleteTextView.setText(
                viewModel.selectedCategoryString
            )
            setCategoryDropdown()
            setCategoryError()
        }

        binding.categoryListAutoCompleteTextView.setOnClickListener {
            setCategoryDropdown()
        }

        binding.flagListAutoCompleteTextView.setOnItemClickListener { _, _, i, _ ->
            val selectedFlag = viewModel.unselectedFlagList[i]
            viewModel.selectedFlagList.add(selectedFlag)
            addFlagChipToGroup(selectedFlag)
            viewModel.unselectedFlagList.remove(selectedFlag)
            viewModel.selectedFlagString = viewModel.selectedFlagList.joinToString(
                ","
            )
            viewModel.editor.putString(Constants.FLAGS, viewModel.selectedFlagString)
            binding.flagListAutoCompleteTextView.setText(
                viewModel.selectedFlagString
            )
            setFlagDropdown()
        }

        binding.flagListAutoCompleteTextView.setOnClickListener {
            setFlagDropdown()
        }

        binding.button.setOnClickListener {
            navigate()
        }
    }

    private fun addCategoryChipToGroup(category: String) {
        val chip = configureChip(category)
        binding.categoryListChipGroup.addView(chip as View)
        chip.setOnCloseIconClickListener {
            binding.categoryListChipGroup.removeView(chip as View)
            viewModel.selectedCategoryList.remove(chip.text.toString())
            viewModel.unselectedCategoryList.add(chip.text.toString())
            viewModel.selectedCategoryString = if (viewModel.selectedCategoryList.size == 0)
                null
            else
                viewModel.selectedCategoryList.joinToString(
                    ","
                )
            viewModel.editor.putString(Constants.CATEGORIES, viewModel.selectedCategoryString)
            binding.categoryListAutoCompleteTextView.setText(
                viewModel.selectedCategoryString
            )
            setCategoryDropdown()
            setCategoryError()
            checkCategoryField()
        }
    }

    private fun addFlagChipToGroup(flag: String) {
        val chip = configureChip(flag)
        binding.flagListChipGroup.addView(chip as View)
        chip.setOnCloseIconClickListener {
            binding.flagListChipGroup.removeView(chip as View)
            viewModel.selectedFlagList.remove(chip.text.toString())
            viewModel.unselectedFlagList.add(chip.text.toString())
            viewModel.selectedFlagString = if (viewModel.selectedFlagList.size == 0)
                null
            else
                viewModel.selectedFlagList.joinToString(
                    ","
                )
            viewModel.editor.putString(Constants.FLAGS, viewModel.selectedFlagString)
            binding.flagListAutoCompleteTextView.setText(
                viewModel.selectedFlagString
            )
            setFlagDropdown()
        }
    }

    private fun configureChip(category: String): Chip {
        val chip = Chip(context)
        chip.text = category
        chip.chipIcon =
            ContextCompat.getDrawable(requireContext(), R.drawable.divider_horizontal_dark)
        chip.isChipIconVisible = false
        chip.isCloseIconVisible = true
        // necessary to get single selection working
        chip.isClickable = true
        chip.isCheckable = false
        return chip
    }

    private fun setCategoryDropdown() {
        binding.categoryListAutoCompleteTextView
            .setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.simple_dropdown_item_1line,
                    viewModel.unselectedCategoryList.toTypedArray()
                )
            )
    }

    private fun setFlagDropdown() {
        binding.flagListAutoCompleteTextView
            .setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.simple_dropdown_item_1line,
                    viewModel.unselectedFlagList.toTypedArray()
                )
            )
    }

    private fun setupEditableFields() {
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            setRadioButton(checkedId)
        }

        binding.categoryListAutoCompleteTextView.setOnDismissListener {
            checkCategoryField()
        }

        binding.categoryListChipGroup.setOnClickListener {
            checkCategoryField()
        }
    }

    private fun checkCategoryField() {
        val isCategorySelected = !viewModel.selectedCategoryString.isNullOrEmpty()
        viewModel.filters.categories = isCategorySelected
        viewModel.updateFiltersLive()
    }

    private fun setRadioButton(checkedId: Int) {
        val isCategorySelected = checkedId != -1
        viewModel.filters.categorySelected = isCategorySelected

        when (checkedId) {
            binding.anyRadioButton.id -> {
                binding.categoryListLayout.visibility =
                    View.GONE
                viewModel.editor.putString(
                    Constants.CATEGORY_TYPE,
                    binding.anyRadioButton.text.toString()
                )
                viewModel.editor.putString(Constants.CATEGORIES, null)
            }
            else -> {
                binding.categoryListLayout.visibility = View.VISIBLE
                viewModel.editor.putString(
                    Constants.CATEGORY_TYPE,
                    binding.customRadioButton.text.toString()
                )
            }
        }

        viewModel.updateFiltersLive()
    }

    private fun observeLiveDataValues() {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading existing settings, please wait")
        progressDialog.setCancelable(false)
        progressDialog.show()
        try {
            viewModel.categoryList.observe(viewLifecycleOwner) {
                Log.i("Home", it.toString())
                (it as ArrayList<String>).remove("Any")
                binding.categoryListAutoCompleteTextView.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        R.layout.simple_list_item_1,
                        it
                    )
                )
                viewModel.unselectedCategoryList = viewModel.categoryList.value as ArrayList<String>
                viewModel.selectedCategoryList =
                    viewModel.selectedCategoryString?.split(",") as ArrayList<String>
                viewModel.unselectedCategoryList.removeAll(viewModel.selectedCategoryList.toSet())
                viewModel.unselectedCategoryList.remove("Any")
            }
            viewModel.flagList.observe(viewLifecycleOwner) {
                Log.i("Home", it.toString())
                binding.flagListAutoCompleteTextView.setAdapter(
                    ArrayAdapter(
                        requireContext(),
                        R.layout.simple_list_item_1,
                        it
                    )
                )
                viewModel.unselectedFlagList = viewModel.flagList.value as ArrayList<String>
                viewModel.selectedFlagList =
                    viewModel.selectedFlagString?.split(",") as ArrayList<String>
                viewModel.unselectedFlagList.removeAll(viewModel.selectedFlagList.toSet())
            }
//        viewModel.joke.observe(viewLifecycleOwner) {
//            Log.i("JokeString", it.toString())
//        }
            viewModel.filtersLive.observe(viewLifecycleOwner) {
//            Log.i("HomeFragment", it.toString())
                val isAnySelected =
                    binding.radioGroup.checkedRadioButtonId == binding.anyRadioButton.id
                val isCategories = it.categories == true

                binding.isAllFieldValid = (isAnySelected || isCategories)
            }

            viewModel.liveDataMerger.observe(viewLifecycleOwner) {
                progressDialog.dismiss()
            }
        } catch (e: Exception) {
            progressDialog.dismiss()
        }
    }

    private fun navigate() {
        viewModel.editor.commit()
        findNavController().navigateUp()
    }
}