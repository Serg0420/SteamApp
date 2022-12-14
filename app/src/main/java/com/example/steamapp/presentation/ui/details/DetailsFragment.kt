package com.example.steamapp.presentation.ui.details

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import coil.load
import com.example.steamapp.R
import com.example.steamapp.databinding.FragmentDetailsBinding
import com.example.steamapp.domain.model.UserLocation
import com.example.steamapp.presentation.ui.getStatus
import com.example.steamapp.presentation.ui.getUser
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailsFragment : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding
        get() = requireNotNull(_binding) { "View was destroyed" }

    private val viewModel by viewModel<DetailsViewModel>()
    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentDetailsBinding.inflate(inflater, container, false)
            .also { binding ->
                _binding = binding
            }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val user = args.getUser()

        with(binding) {
            avatarImgv.load(user.avatar)
            nameTxtv.append(user.nickName)
            idTxtv.append(user.steamId)

            val status = user.getStatus(requireContext())

            statusTxtv.append(status)
            toolbar.setupWithNavController(findNavController())

            editBtn.setOnClickListener {
                editLayout.isVisible = true
                editBtn.isVisible = false
            }

            addBtn.setOnClickListener {
                try {
                    viewModel.addLocation(
                        UserLocation(
                            steamId = user.steamId,
                            nickName = user.nickName,
                            avatarUrl = user.avatar,
                            latitude = latitudeEditTxtv.text.toString().toDouble(),
                            longitude = longitudeEditTxtv.text.toString().toDouble()
                        )
                    )
                    editLayout.isGone = true
                    editBtn.isVisible = true

                } catch (exception: Exception) {
                    handleError(getString(R.string.not_valid_input_value) + exception)
                }
            }

            startTrackingBtn.setOnClickListener {
                sendCommandToService(ServiceState.START, user.steamId, user.nickName)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun sendCommandToService(
        serviceState: ServiceState,
        trackingID: String,
        trackingNick: String
    ) {
        val intent = Intent(requireContext(), StateTrackerService::class.java)
            .putExtra(StateTrackerService.SERVICE_STATE_COMMAND, serviceState)
            .putExtra(StateTrackerService.TRACKING_ID, trackingID)
            .putExtra(StateTrackerService.TRACKING_NICK, trackingNick)
        ContextCompat.startForegroundService(requireContext(), intent)
    }

    private fun handleError(errorStr: String) {
        Toast.makeText(requireContext(), errorStr, Toast.LENGTH_SHORT).show()
    }
}