package com.example.steamapp.presentation.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.steamapp.BuildConfig
import com.example.steamapp.databinding.FragmentMyProfileBinding
import com.example.steamapp.presentation.model.LCE
import com.example.steamapp.presentation.ui.getStatus
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MyProfileFragment : Fragment() {
    private var _binding: FragmentMyProfileBinding? = null
    private val binding
        get() = requireNotNull(_binding) { "View was destroyed" }

    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val viewModel by inject<MyProfileViewModel> {
        parametersOf( sharedPreferences.getString(KEY_STEAM_ID, BuildConfig.MY_STEAM_ID))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentMyProfileBinding.inflate(inflater, container, false)
            .also { binding ->
                _binding = binding
            }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            viewModel
                .dataFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .onEach { lce ->
                    when (lce) {
                        is LCE.Error -> {
                            handleError(lce.throwable.toString())
                        }
                        is LCE.Content -> {
                            avatarImgv.load(lce.data.avatar)
                            nameTxtv.append(lce.data.nickName)
                            idTxtv.append(lce.data.steamId)
                            statusTxtv.append(lce.data.getStatus(requireContext()))
                        }
                    }
                }
                .launchIn(viewLifecycleOwner.lifecycleScope)

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