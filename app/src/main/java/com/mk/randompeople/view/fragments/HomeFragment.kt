package com.mk.randompeople.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.mk.randompeople.R
import com.mk.randompeople.databinding.FragmentHomeBinding
import com.mk.randompeople.model.network.States
import com.mk.randompeople.model.network.checkInternet
import com.mk.randompeople.view.adapters.ProfileListAdapter
import com.mk.randompeople.viewmodel.HomeViewModel


class HomeFragment : Fragment() {

    private lateinit var binding : FragmentHomeBinding
    private lateinit var viewModel : HomeViewModel
    private lateinit var adapter : ProfileListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setOnClickListeners()
        setObservers()
        loadProfiles()
    }

    private fun setupRecyclerView() {
        adapter = ProfileListAdapter()
        binding.profilesRv.adapter = adapter
    }

    private fun setObservers() {
        viewModel.profiles.observe(viewLifecycleOwner){
            it?.let{

                adapter.submitList(it)
                binding.contentGroup.visibility = View.VISIBLE
            }

        }
        viewModel.status.observe(viewLifecycleOwner){
            it?.let {
                binding.progressBar.isVisible = false
                if(it == States.SUCCESS) return@let
                Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadProfiles() {
        if(requireContext().checkInternet()) {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.fetchProfiles()
        }
        else
            onNetworkUnavailable()
    }

    private fun onNetworkUnavailable() {
        viewModel.status.value = States.NO_INTERNET
        showNoInternetLayout()
    }

    private fun showNoInternetLayout() {
        binding.noInternetGroup.visibility = View.VISIBLE
        binding.contentGroup.visibility = View.GONE
    }

    private fun setOnClickListeners() {
        binding.allPeopleMapBtn.setOnClickListener{
            if(!requireContext().checkInternet()) {
                Toast.makeText(context, "no internet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPeopleMapsFragment(true))
        }
        binding.peopleInMapBtn.setOnClickListener{
            if(!requireContext().checkInternet()) {
                Toast.makeText(context, "no internet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            findNavController().navigate(R.id.action_homeFragment_to_peopleMapsFragment)
        }
        binding.reloadIv.setOnClickListener{
            if(!requireContext().checkInternet()) {
                Toast.makeText(context, "no internet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            binding.noInternetGroup.visibility = View.GONE
            loadProfiles()
        }
    }


}