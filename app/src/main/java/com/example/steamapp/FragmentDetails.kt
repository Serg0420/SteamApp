package com.example.steamapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.steamapp.databinding.FragmentDetailsBinding

class FragmentDetails : Fragment() {
    private var _binding: FragmentDetailsBinding? = null
    private val binding
        get() = requireNotNull(_binding) { "View was destroyed" }

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

        val args: FragmentDetailsArgs by navArgs()
        val status: String

        with(binding) {
            avatarImgv.load(args.avatarFull)
            nameTxtv.append(args.personaName)
            idTxtv.append(args.steamid)

            status = when (args.personaState.toInt()) {
                //сетевые статусы игрока
                1 -> ONLINE
                2 -> BUSY
                3 -> AWAY
                4 -> SNOOZE
                5 -> LOOKING_TO_TRADE
                6 -> LOOKING_TO_PLAY
                else -> OFFLINE
            }

            statusTxtv.append(status)


            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        private const val ONLINE = "Online"
        private const val BUSY = "Busy"
        private const val AWAY = "Away"
        private const val SNOOZE = "Snooze"
        private const val LOOKING_TO_TRADE = "looking to trade"
        private const val LOOKING_TO_PLAY = "looking to play"
        private const val OFFLINE = "Offline"
    }
}