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
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class FriendsFragment : Fragment() {

    private var playerInformationLst = mutableListOf<InputItem.PlayerInfo>()
    private var errorMsg: String? = null
    private var getFriendsIdsRequest: Call<FriendList>? = null
    private var getUserProfileRequest: Call<PlayersResponse>? = null
    private var _binding: FragmentFriendsBinding? = null

    private val adapter by lazy {
        UserAdapter(
            requireContext(),
            {
                errorMsg = null
                loadDataFromSteamToRv()
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
                getFriendsIdsRequest?.cancel()
                getUserProfileRequest?.cancel()
                getFriendsIdsRequest = null
                getUserProfileRequest = null
                adapter.submitList(mutableListOf<InputItem>())
                playerInformationLst = mutableListOf()
                errorMsg = null
                //загружаем страницу пользователей
                loadDataFromSteamToRv { swipeRefresh.isRefreshing = false }
            }

            friendsLst.layoutManager = linearLM
            //добавляем разделитель между элементами списка
            friendsLst.addVerticalSeparation()

            loadDataFromSteamToRv()

            friendsLst.adapter = adapter

        }
    }

    private fun loadDataFromSteamToRv(
           onRefreshFinished: () -> Unit = {}
    ) {

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

        getFriendsToRv(
            steamApi,
        ) { onRefreshFinished() }
    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null
        getFriendsIdsRequest?.cancel()
        getUserProfileRequest?.cancel()

    }

    private fun getFriendsToRv(
        steamApi: SteamApi,
        onRefreshFinished: () -> Unit = {}
    ) {

        getFriendsIdsRequest?.enqueue(object : Callback<FriendList> {

            override fun onResponse(call: Call<FriendList>, response: Response<FriendList>) {
                val steamidStrLst = mutableListOf<String>()

                if (response.isSuccessful) {

                    val friendLst = response.body() ?: run {
                        handleError("Null Response")
                        return
                    }

                    (friendLst.friendsList.friends).forEach {
                        steamidStrLst.add(it.steamid)
                    }

                    steamidStrLst.forEach {it ->
                        getUserProfileRequest = steamApi.getUser(STEAM_API_KEY, it)
                        getFriendsInfoToRv()
                    }

                    onRefreshFinished()

                } else {

                    handleError(response.errorBody().toString())

                }

            }

            override fun onFailure(call: Call<FriendList>, t: Throwable) {

                if (!call.isCanceled) {

                    handleError(t.toString())

                }

            }
        })

    }

    //работаем со списком друзей, кидаем новых в адаптер, добавляем эл-т загрузки либо ошибку
    private fun getFriendsInfoToRv() {

        getUserProfileRequest?.enqueue(object : Callback<PlayersResponse> {

            override fun onResponse(
                call: Call<PlayersResponse>,
                response: Response<PlayersResponse>
            ) {

                if (response.isSuccessful) {

                    val playersResp = response.body() ?: run {
                        handleError("Null Response")
                        return
                    }

                    //стим возвращает список из одного пользователя, по этому и [0]
                    playerInformationLst.add(playersResp.response.players[0])

                    if (errorMsg == null) {

                        val prevItems = adapter.currentList
                        val newItems = playerInformationLst.map { it }.minus(prevItems)

                        adapter.submitList(prevItems + newItems)

                    } else {

                        val prevItems = adapter.currentList

                        adapter.submitList(
                            prevItems.minus(InputItem.ErrorElement) + InputItem.ErrorElement
                        )

                    }


                } else {

                    handleError(response.errorBody().toString())

                }
            }

            override fun onFailure(call: Call<PlayersResponse>, t: Throwable) {

                if (!call.isCanceled) {
                    handleError(t.toString())
                }

            }
        })
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