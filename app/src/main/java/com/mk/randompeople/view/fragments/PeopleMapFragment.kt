package com.mk.randompeople.view.fragments

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.mk.randompeople.R
import com.mk.randompeople.databinding.FragmentPeopleMapBinding
import com.mk.randompeople.model.network.*
import com.mk.randompeople.viewmodel.PeopleMapViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL
const val MAX_RADIUS = 500F
const val LAT = "lat"
const val LNG = "lng"
const val RAD = "radius"
class PeopleMapsFragment : Fragment() {

    lateinit var mMap:GoogleMap
    lateinit var binding:FragmentPeopleMapBinding
    lateinit var viewModel : PeopleMapViewModel
    private val overlaySize : Float
    get() = viewModel.radius * 2500F
    val args :PeopleMapsFragmentArgs by navArgs()

    lateinit var sharedPrefs:SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPeopleMapBinding.inflate(inflater,container,false)
        viewModel = ViewModelProvider(this)[PeopleMapViewModel::class.java]
        binding.lifecycleOwner = viewLifecycleOwner
        sharedPrefs = requireActivity().getSharedPreferences("sharedPref",MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(){
            mMap = it
            //show map
            binding.mapGroup.visibility = View.VISIBLE
            fetchProfiles()
            //if plot all is true then don't dont add click listener to show people in an area
            if(!args.plotAll){
                Toast.makeText(context, "tap to see people in a area\nlong press to see all people", Toast.LENGTH_SHORT).show()
                mMap.setOnMapClickListener {
                    markArea(it)
                    //show radius controls
                    binding.controlsGroup.visibility = View.VISIBLE
                    plotNearbyPeoples(it)
                }
                mMap.setOnMapLongClickListener {
                    binding.controlsGroup.visibility = View.GONE
                    plotAllPeoplesOnMap()
                }
            }
        }
        setObservers()
        setUpSlider()
    }

    private fun saveLocationSharedPref() {
        lifecycleScope.launch(Dispatchers.IO){
            viewModel.locationSelected?.let{ location->
                with(sharedPrefs.edit()) {
                    putFloat(RAD, viewModel.radius)
                    putString(LAT, location.latitude.toString())
                    putString(LNG, location.longitude.toString())
                    apply()
                }
            }
        }
    }

    private fun plotAllPeoplesOnMap() {
        viewModel.profiles.value?.let {
            plotPeoplesOnMap(it)
        }
    }

    private fun setUpSlider() {
        with(binding.radiusRangeSlider){
            setMinSeparationValue(1000F)
            valueFrom = 5F
            values = listOf(5F)
            valueTo = MAX_RADIUS

            addOnChangeListener{
                    slider,value,_->
                    viewModel.radius = value
                    binding.radiusValueTv.text = String.format("%.1f km",viewModel.radius)
                    viewModel.locationSelected?.let {
                        markArea(it)
                        plotNearbyPeoples(it)
                    }
            }
        }
    }

    private fun markArea(location:LatLng) {

        viewModel.locationSelected = location

        mMap.clear()
        mMap.addGroundOverlay(
            GroundOverlayOptions()
                .position(location,overlaySize)
                .image(BitmapDescriptorFactory.fromResource(R.drawable.circle_black_512))
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,5F ))
        saveLocationSharedPref()
    }

    private fun plotNearbyPeoples(location: LatLng) {
        viewModel.profiles.value?.let {  profiles->
            val nearbyProfiles = profiles.filter { (it.location.coordinates.toLatLng().isNearby(location,viewModel.radius.toDouble())) }

            binding.countValueTv.text = nearbyProfiles.size.toString()
            plotPeoplesOnMap(nearbyProfiles)
        }
    }

    private fun setObservers() {
        viewModel.profiles.observe(viewLifecycleOwner){
            it?.let{
                if(args.plotAll){
                    plotPeoplesOnMap(it)
                }
                else
                {
                    plotLastMarkedLocation()
                }
            }

        }
        viewModel.status.observe(viewLifecycleOwner){
            it?.let {
                Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                binding.progressBar.isVisible = false
            }
        }
    }

    private fun plotLastMarkedLocation() {
        with(sharedPrefs){
            getString(LAT,null)?.let{ lat->
                getString(LNG,null)?.let{ lng->
                    val latLng = LatLng(lat.toDouble(),lng.toDouble())
                    viewModel.radius = getFloat(RAD,5F)
                    markArea(latLng)

                    plotNearbyPeoples(latLng)
                    //ui
                    binding.controlsGroup.isVisible = true
                    binding.radiusRangeSlider.values = listOf(viewModel.radius)
                    "${(viewModel.radius)} km".also { binding.radiusValueTv.text = it }
                }
            }
        }
    }

    private fun plotPeoplesOnMap(profiles:List<Profile>) {
        binding.countValueTv.text = profiles.size.toString()
        binding.progressBar.isVisible=false

        lifecycleScope.launch(Dispatchers.IO){
            profiles.forEach { profile ->
                val title = "${profile.name.title}${profile.name.first}${profile.name.last}"
                //snippet
                //TODO:Add details
                val snippet = "phone : ${profile.cell}"
                //load from api
                val iconUrl = URL(profile.picture.medium)
                //TODO:handle error
                var icon = BitmapFactory.decodeStream(iconUrl.openConnection().getInputStream())

                icon = RoundedBitmapDrawableFactory.create(resources,icon).let {
                    it.isCircular=true;
                    it.cornerRadius=10.0F;
                    it.bitmap
                }

                launch(Dispatchers.Main){
                    mMap.addMarker(
                        MarkerOptions()
                            .position(profile.location.coordinates.toLatLng())
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.fromBitmap(icon))
                            .title(title)
                    )

                }
            }
        }
    }

    private fun fetchProfiles() {
        if(requireContext().checkInternet())
            viewModel.fetchProfiles()
        else {
            viewModel.status.value = States.NO_INTERNET
            showNoInternet()
        }
    }

    private fun showNoInternet() {

    }
}