package com.example.steamapp.presentation.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.steamapp.databinding.FragmentDetailsBinding
import com.example.steamapp.presentation.ui.getStatus
import com.example.steamapp.presentation.ui.getUser

class DetailsFragment : Fragment() {
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

        val args: DetailsFragmentArgs by navArgs()
        val user = args.getUser()

        with(binding) {
            avatarImgv.load(user.avatar)
            nameTxtv.append(user.nickName)
            idTxtv.append(user.steamId)

            val status = user.getStatus(requireContext())

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
}