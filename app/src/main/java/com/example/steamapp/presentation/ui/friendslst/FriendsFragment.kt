package com.example.steamapp.presentation.ui.friendslst

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.steamapp.databinding.FragmentFriendsBinding
import com.example.steamapp.domain.repository.AppUserIdRepository
import com.example.steamapp.presentation.model.LCE
import com.example.steamapp.presentation.ui.addVerticalSeparation
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class FriendsFragment : Fragment() {
    private var _binding: FragmentFriendsBinding? = null
    private val binding
        get() = requireNotNull(_binding) { "View was destroyed" }

    private val adapter by lazy {
        UserAdapter(
            requireContext(),
            onUserElemClicked = {
                findNavController().navigate(
                    FriendsFragmentDirections.toFragmentDetails(
                        it.nickName, it.avatar, it.steamId, it.state
                    )
                )
            }
        )
    }

    private val appUserIdRep by inject<AppUserIdRepository> {
        parametersOf(requireContext())
    }

    private val viewModel by inject<FriendsViewModel> {
        parametersOf(appUserIdRep.getAppUserId())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentFriendsBinding.inflate(inflater, container, false)
            .also {
                _binding = it
            }
            .root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val linearLM = LinearLayoutManager(requireContext())

            swipeRefresh.setOnRefreshListener {
                refresh()
            }

            friendsLst.layoutManager = linearLM
            //добавляем разделитель между элементами списка
            friendsLst.addVerticalSeparation()
            friendsLst.adapter = adapter

            viewModel
                .dataFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .onEach { swipeRefresh.isRefreshing = false }
                .onEach { lce ->
                    when (lce) {
                        is LCE.Error -> {
                            tryAgainBtn.setOnClickListener { refresh() }
                            tryAgainBtn.isVisible = true
                            progressIndicator.isVisible = true
                            handleError(lce.throwable.toString())
                        }
                        is LCE.Content -> {
                            adapter.submitList(lce.data)
                            progressIndicator.isVisible = false
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

    private fun refresh() {
        adapter.submitList(emptyList())
        binding.tryAgainBtn.isVisible = false
        binding.progressIndicator.isVisible = false
        viewModel.onRefreshed()
    }
}