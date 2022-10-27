package com.example.steamapp.presentation.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.example.steamapp.BuildConfig
import com.example.steamapp.R
import com.example.steamapp.databinding.FragmentSettingsBinding
import com.example.steamapp.presentation.ui.details.ServiceState
import com.example.steamapp.presentation.ui.details.StateTrackerService

class SettingFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null

    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val binding
        get() = requireNotNull(_binding) {
            "View was destroyed"
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentSettingsBinding.inflate(inflater, container, false)
            .also {
                _binding = it
            }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            steamidEditTxtv.setText(
                sharedPreferences.getString(KEY_STEAM_ID, BuildConfig.MY_STEAM_ID)
            )

            enterBtn.setOnClickListener {
                if (steamidEditTxtv.text != null && steamidEditTxtv.text.toString() != "") {
                    sharedPreferences.edit {
                        putString(KEY_STEAM_ID, steamidEditTxtv.text.toString())
                    }
                } else {
                    handleError(getString(R.string.null_field))
                    steamidEditTxtv.setText(BuildConfig.MY_STEAM_ID)
                }
            }

            //эта кнопка только для наглядного примера что приложение работает с другими ID
            testFriendIdBtn.setOnClickListener {
                steamidEditTxtv.setText(testFriendIdBtn.text)
            }

            stopTrackingBtn.setOnClickListener {
                if (StateTrackerService.isServiceRunning) {
                    val intent = Intent(requireContext(), StateTrackerService::class.java)
                        .putExtra(StateTrackerService.SERVICE_STATE_COMMAND, ServiceState.STOP)
                    ContextCompat.startForegroundService(requireContext(), intent)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleError(errorStr: String) {
        Toast.makeText(requireContext(), errorStr, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val PREFS_NAME = "settings"
        private const val KEY_STEAM_ID = "STEAM_ID"
    }
}