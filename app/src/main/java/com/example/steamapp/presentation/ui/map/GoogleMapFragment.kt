package com.example.steamapp.presentation.ui.map


import com.example.steamapp.databinding.FragmentMapBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.steamapp.R
import com.example.steamapp.presentation.model.LCE
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.android.ext.android.inject

class GoogleMapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = requireNotNull(_binding)

    private var googleMap: GoogleMap? = null
    private var locationListener: LocationSource.OnLocationChangedListener? = null
    private val locationService by lazy {
        LocationService(requireContext())
    }

    private val viewModel by inject<GoogleMapViewModel>()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isEnabled ->
        setLocationEnabled(isEnabled)
        if (isEnabled) {
            viewLifecycleOwner.lifecycleScope.launch {
                locationService.getLocation()?.let(::moveCameraToLocation)
            }

            locationService
                .locationFlow
                .onEach { location ->
                    locationListener?.onLocationChanged(location)
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentMapBinding.inflate(inflater, container, false)
            .also { _binding = it }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        with(binding) {
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetFrame)

            mapView.getMapAsync { map ->
                googleMap = map.apply {

                    uiSettings.isCompassEnabled = true
                    uiSettings.isZoomControlsEnabled = true
                    uiSettings.isMyLocationButtonEnabled = true

                    @SuppressLint("MissingPermission")
                    isMyLocationEnabled = hasLocationPermission()

                    setLocationSource(object : LocationSource {
                        override fun activate(listener: LocationSource.OnLocationChangedListener) {
                            locationListener = listener
                        }

                        override fun deactivate() {
                            locationListener = null
                        }
                    })

                    map.setOnMapClickListener {
                        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }

                    setOnMarkerClickListener {
                        viewModel.onMarkerClicked(it.position.latitude, it.position.longitude)
                        true
                    }
                }

                viewModel
                    .dataFlow
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                    .onEach { lce ->
                        when (lce) {
                            is LCE.Error -> {
                                handleErrorOrMessage(lce.throwable.toString())
                            }
                            is LCE.Content -> {
                                lce.data.forEach {
                                    map.addMarker(
                                        MarkerOptions().position(LatLng(it.latitude, it.longitude))
                                    )
                                }
                            }
                        }
                    }
                    .launchIn(viewLifecycleOwner.lifecycleScope)
            }

            viewModel
                .selectedMarkerFlow
                .onEach { lce ->
                    when (lce) {
                        is LCE.Error -> {
                            handleErrorOrMessage(lce.throwable.toString())
                        }
                        is LCE.Content -> {
                            with(bottomSheetDetails) {
                                avatarPreviewImgv.load(lce.data.avatarUrl)
                                nickTxtv.text = lce.data.nickName

                                deleteBtn.setOnClickListener {
                                    viewModel.onDeleteBtnClicked(lce.data)
                                    handleErrorOrMessage(getString(R.string.tag_deleted))
                                }
                            }
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                        }
                    }
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)

            mapView.onCreate(savedInstanceState)
        }
    }

    private fun handleErrorOrMessage(errorStr: String) {
        Toast.makeText(requireContext(), errorStr, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
        googleMap = null
        _binding = null
    }

    @SuppressLint("MissingPermission")
    private fun setLocationEnabled(enabled: Boolean) {
        googleMap?.isMyLocationEnabled = enabled
        googleMap?.uiSettings?.isMyLocationButtonEnabled = enabled
    }

    private fun moveCameraToLocation(location: Location) {
        val current = LatLng(location.latitude, location.longitude)
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(current, DEFAULT_CAMERA_ZOOM)
        )
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val DEFAULT_CAMERA_ZOOM = 1f
    }
}