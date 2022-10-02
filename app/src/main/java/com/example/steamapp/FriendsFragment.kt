package com.example.steamapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.steamapp.databinding.FragmentFriendsBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null

    private val adapter by lazy {
        UserAdapter(
            requireContext(),
            onTryAgainBtnClicked = { refresh() },
            onUserElemClicked = {
                findNavController().navigate(
                    FriendsFragmentDirections.toFragmentDetails(
                        it.personaName, it.avatarFull, it.steamid, it.personaState
                    )
                )
            }
        )
    }

    private val viewModel by viewModels<SteamViewModel> {
        viewModelFactory {
            initializer {
                SteamViewModel(
                    retroDataSource = ServiceLocator.provideDataSource()
                )
            }
        }
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
                            adapter.submitList(
                                adapter.currentList + InputItem.ErrorElement
                            )
                            handleError(lce.throwable.toString())
                        }
                        is LCE.Content -> {
                            adapter.submitList(lce.data)
                            progressIndicator.isVisible = false
                            swipeRefresh.isRefreshing = false
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
        viewModel.onRefreshed()
    }

}