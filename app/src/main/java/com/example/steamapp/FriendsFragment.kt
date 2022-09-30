package com.example.steamapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class FriendsFragment : Fragment() {

    private var errorMsg: String? = null
    private var _binding: FragmentFriendsBinding? = null
    private val handler= CoroutineExceptionHandler { _, exception ->
        handleError(exception.toString())
    }
    private val scope = CoroutineScope(Dispatchers.IO+handler+ SupervisorJob())

    private val adapter by lazy {
        UserAdapter(
            requireContext(),
            onTryAgainBtnClicked={
                errorMsg = null
                refresh()
            },
            onUserElemClicked={
                findNavController().navigate(
                    /*пока инфы не так много можно и передать как аргументы,
                    потом лучше сделаю в DetailsFragment отдельный запрос,
                    а передавать буду только id
                    */
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
                    retroDataSource=ServiceLocator.provideDataSource()
                )
            }
        }
    }

    private val binding
        get() = requireNotNull(_binding) {
            handleError("View was destroyed")
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
                //viewModel.onRefreshed()
                refresh()
            }

            friendsLst.layoutManager = linearLM
            //добавляем разделитель между элементами списка
            friendsLst.addVerticalSeparation()

            //scope.launch { loadDataInRV() }

            friendsLst.adapter = adapter

            viewModel
                .dataFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle,Lifecycle.State.STARTED)
                .onEach {lst->
                    val deferred=scope.async {
                        lst
                    }
                    scope.launch {
                        val d=deferred.await()
                        withContext(Dispatchers.Main){adapter.submitList(d)}
                    }

                }
                .launchIn(viewLifecycleOwner.lifecycleScope)
        }
    }

    private fun refresh(){
        adapter.submitList(mutableListOf<InputItem>())
        errorMsg = null
       //scope.launch { loadDataInRV{binding.swipeRefresh.isRefreshing = false} }
    }





    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null
        //getFriendsIdsRequest?.cancel()
        //getUserProfileRequest?.cancel()

    }



   fun handleError(errorStr: String) {

        errorMsg = errorStr
        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show()

    }


}