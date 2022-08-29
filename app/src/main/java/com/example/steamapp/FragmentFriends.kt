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

class FragmentFriends : Fragment() {

    private var playerInformationLst = mutableListOf<InputItem.PlayerInfo>()
    private var isLoading = false
    private var errorMsg: String? = null
    private var getFriendsIdsRequest: Call<FriendList>? = null
    private var getUserProfileRequest: Call<PlayersResponse>? = null
    private var _binding: FragmentFriendsBinding? = null
    private var currentPageSize = 0

    private val adapter by lazy {
        UserAdapter(
            requireContext(),
            {
                errorMsg = null
                getDataFromSteamToRv(DOWNLOAD_ELEMENTS_COUNT, currentPageSize)
            },
            {
                findNavController().navigate(
                    /*пока инфы не так много можно и передать как аргументы,
                    потом лучше сделаю в FragmentDetails отдельный запрос,
                    а передавать буду только id
                    */
                    FragmentFriendsDirections.toFragmentDetails(
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
                isLoading = false
                errorMsg = null
                //загружаем первую страницу пользователей
                getDataFromSteamToRv(0, 0) { swipeRefresh.isRefreshing = false }
                currentPageSize = PAGE_SIZE
            }

            //загружаем первую страницу пользователей
            getDataFromSteamToRv(0, currentPageSize)
            currentPageSize = PAGE_SIZE

            friendsLst.layoutManager = linearLM
            //добавляем разделитель между элементами списка
            friendsLst.addVerticalSeparation()
            /*догружаем друзей юзера при достижении элемента списка
            currentPageSize-ELEMENTS_BEFORE_END
            */
            friendsLst.addPagination(linearLM, ELEMENTS_BEFORE_END) {

                getDataFromSteamToRv(DOWNLOAD_ELEMENTS_COUNT, currentPageSize)
                /*около 20 часов суммарно ковырял баг, оказалось,
                тут, вместо += написал =+, я чуть моник не разбил...
                ну, зато лучше разобрался))
                */
                currentPageSize += DOWNLOAD_ELEMENTS_COUNT

            }
            friendsLst.adapter = adapter

        }
    }

    private fun getDataFromSteamToRv(
        downloadMore: Int,
        currentPSize: Int,
        onRefreshFinished: () -> Unit = {}
    ) {

        if (isLoading) return
        isLoading = true

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
            downloadMore,
            currentPSize
        ) { onRefreshFinished() }
    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null
        getFriendsIdsRequest?.cancel()
        getUserProfileRequest?.cancel()

    }

    /*получаем список друзей пользователя, и по id каждого выполняем запрос на получение информации.
    К сожалению, steam не принимает список id пользователей, по этому дёргаю по одному, возможно, это
    планировалось сделать, т.к. возвращает он список из одного элемента или я делал что-то не так.
    Реализацию этой и последующей функции лучше, наверно, вынести в отдельный файл...но тогда всё
    станет ещё запутанней...в будущем точно придётся
    */
    private fun getFriendsToRv(
        steamApi: SteamApi,
        downloadMore: Int,
        currentPSize: Int,
        onRefreshFinished: () -> Unit = {}
    ) {

        getFriendsIdsRequest?.enqueue(object : Callback<FriendList> {

            override fun onResponse(call: Call<FriendList>, response: Response<FriendList>) {

                val steamidStrLst = mutableListOf<String>()

                if (response.isSuccessful) {

                    if (response.body() == null) handleError("Null Response")
                    val friendLst = response.body() ?: return

                    (friendLst.friendsList.friends).forEach {

                        steamidStrLst.add(it.steamid)

                    }

                    val lstSize = steamidStrLst.size
                    var downloadingElem: Int = currentPSize
                    var toDownload = downloadMore


                    if (toDownload == 0 && currentPSize <= 0) {
                        toDownload = PAGE_SIZE
                    }
                    if (toDownload + currentPSize > lstSize) {
                        toDownload = lstSize - currentPSize
                    }

                    steamidStrLst.forEachIndexed { index, it ->

                        if ((toDownload != 0) &&
                            (index >= downloadingElem) &&//чтобы не получать тех-же юзеров
                            (downloadingElem <= currentPSize + toDownload - 1)
                        ) {

                            getUserProfileRequest = steamApi.getUser(STEAM_API_KEY, it)
                            getFriendsInfoToRv()
                            downloadingElem++
                        }
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

                    if (response.body() == null) handleError("Null Response")
                    val playersResp = response.body() ?: return

                    //стим возвращает список из одного пользователя, по этому и [0]
                    playerInformationLst.add(playersResp.response.players[0])

                    if (errorMsg == null) {

                        val prevItems = adapter.currentList.dropLastWhile {
                            it == InputItem.LoadingElement
                        }
                        val newItems = playerInformationLst.map { it }
                            .minus(prevItems).dropLastWhile {
                                it == InputItem.ErrorElement
                            } + InputItem.LoadingElement

                        adapter.submitList(prevItems + newItems)
                        isLoading = false

                    } else {

                        val prevItems = adapter.currentList.dropLastWhile {
                            it == InputItem.LoadingElement
                        }

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
        isLoading = false

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

        //друзей в стиме мало, по этому и такие маленькие параметры задал
        private const val ELEMENTS_BEFORE_END = 2
        private const val PAGE_SIZE = 10
        private const val DOWNLOAD_ELEMENTS_COUNT = 3

    }
}