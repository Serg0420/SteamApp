package com.example.steamapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.steamapp.databinding.FragmentFriendsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class FriendsFragment : Fragment() {

    private var playerInformationLst = mutableListOf<InputItem.PlayerInfo>()
    private var errorMsg: String? = null
    private var getFriendsIdsRequest: FriendList? = null
    private var getUserProfileRequest: PlayersResponse? = null
    private var _binding: FragmentFriendsBinding? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val adapter by lazy {
        UserAdapter(
            requireContext(),
            {
                errorMsg = null
                //loadDataFromSteamToRv()
            },
            {
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
                //getFriendsIdsRequest?.cancel()
                //getUserProfileRequest?.cancel()
                getFriendsIdsRequest = null
                getUserProfileRequest = null
                adapter.submitList(mutableListOf<InputItem>())
                playerInformationLst = mutableListOf()
                errorMsg = null
                scope.launch { LoadDataInRV{swipeRefresh.isRefreshing = false} }
                //загружаем страницу пользователей
                //loadDataFromSteamToRv { swipeRefresh.isRefreshing = false }
            }

            friendsLst.layoutManager = linearLM
            //добавляем разделитель между элементами списка
            friendsLst.addVerticalSeparation()

            //loadDataFromSteamToRv()


            scope.launch { LoadDataInRV() }




            friendsLst.adapter = adapter

        }
    }

    private suspend fun LoadDataInRV(onRefreshFinished: () -> Unit = {}) {

        val steamidStrLst = mutableListOf<String>()
        val playerInfoLst = mutableListOf<InputItem.PlayerInfo>()
        scope.launch {

            val lstOfFriends = getRetrofitData()

            if (lstOfFriends != null) {
                (lstOfFriends.friendsList.friends).forEach {
                    steamidStrLst.add(it.steamid)
                }
            }



            steamidStrLst.forEach {
                val playersResp = getRetrofitData2(it)
                if (playersResp != null) {
                    playerInfoLst.add(playersResp.response.players[0])

                    if (errorMsg == null) {

                        val prevItems = adapter.currentList
                        val newItems = playerInfoLst.map { it }.minus(prevItems)
                        withContext(Dispatchers.Main) {
                            adapter.submitList(prevItems + newItems)
                        }

                    } else {

                        val prevItems = adapter.currentList
                        withContext(Dispatchers.Main) {
                            adapter.submitList(
                                prevItems.minus(InputItem.ErrorElement) + InputItem.ErrorElement
                            )

                        }


                    }
                }
            }
        }
        onRefreshFinished()
    }

    private suspend fun getRetrofitData(): FriendList? {

        val retrofit = Retrofit.Builder()
            .baseUrl(STEAM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val steamApi = retrofit.create<SteamApi>()

        getFriendsIdsRequest = steamApi.getUsersFriends(
            STEAM_API_KEY,
            MY_STEAM_ID,
            RELATIONSHIP
        )
        return getFriendsIdsRequest
    }

    private suspend fun getRetrofitData2(friendId: String): PlayersResponse? {

        val retrofit = Retrofit.Builder()
            .baseUrl(STEAM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val steamApi = retrofit.create<SteamApi>()

        getUserProfileRequest = steamApi.getUser(STEAM_API_KEY, friendId)
        return getUserProfileRequest
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

    companion object {

        /*ссылка на кореь апи и ключ, его стим даёт бесплатно, но только зареганым
        юзерам с активированным аккаунтом
        */
        private const val STEAM_BASE_URL = " http://api.steampowered.com/"
        private const val STEAM_API_KEY = "EA8CD62FA7C21007003DEF64FF96E20C"

        //это мой стим ID, соотвественно и друзей выводит именно моих
        private const val MY_STEAM_ID = 76561198277362398
        private const val RELATIONSHIP = "friend"

    }
}