package com.example.steamapp

import android.content.ClipData
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.steamapp.databinding.FragmentFriendsBinding
import com.google.android.material.divider.MaterialDividerItemDecoration
import okhttp3.OkHttpClient
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class FragmentFriends : Fragment() {

    private val playerInformationLst = mutableListOf<InputItem.PlayerInfo>()
    private var isLoading = false
    private var errorMsg: String? = null



    private val adapter by lazy {
        UserAdapter(
            requireContext(),
            {
                errorMsg = null
                getDataFromSteamToRv(DOWNLOAD_ELEMENTS_COUNT, currentPageSize)
            },
            {
                findNavController().navigate(
                    FragmentFriendsDirections.toFragmentDetails(it.personaName)
                )
            }

        )

    }
    private var _binding: FragmentFriendsBinding? = null
    private var currentPageSize = 0

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

//            toolbar.setNavigationOnClickListener {
//                findNavController().navigate(
//                    FragmentFriendsDirections.toFragmentDetails()
//
//                )
//            }

            val linearLM = LinearLayoutManager(requireContext())

            getDataFromSteamToRv(0, currentPageSize)
            currentPageSize = PAGE_SIZE

            friendsLst.layoutManager = linearLM
            friendsLst.addVerticalSeparation()
            friendsLst.addPagination(linearLM, ELEMENTS_BEFORE_END) {

                getDataFromSteamToRv(DOWNLOAD_ELEMENTS_COUNT, currentPageSize)
                //около суток ковырял баг, оказалось тут вместо += написал =+, я чуть моник не разбил...
                currentPageSize += DOWNLOAD_ELEMENTS_COUNT
                Log.d("getDataFromSteamToRv", "getDataFromSteamToRv: $currentPageSize")

            }
            friendsLst.adapter = adapter

        }
    }


    private fun getDataFromSteamToRv(downloadMore: Int, currentPSize: Int) {

        if (isLoading) return
        isLoading = true

        val retrofit = Retrofit.Builder()
            .baseUrl(STEAM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val steamApi = retrofit.create<SteamApi>()

        getFriendsToRv(

            steamApi.getUsersFriends(
                STEAM_API_KEY,
                MY_STEAM_ID,
                "friend"
            ),
            steamApi,
            downloadMore,
            currentPSize
        )
    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null

    }

    private fun getFriendsToRv(
        currentRequest: Call<FriendList>,
        steamApi: SteamApi,
        downloadMore: Int,
        currentPSize: Int
    ) {

        currentRequest.enqueue(object : Callback<FriendList> {

            override fun onResponse(call: Call<FriendList>, response: Response<FriendList>) {

                val steamidStrLst = mutableListOf<String>()

                if (response.isSuccessful) {

                    val friendLst = response.body() ?: return//ПЕРЕПРОВЕРИТЬ!!!!!тут ошибки выкинуть

                    (friendLst.friendsList.friends).forEach {
                        steamidStrLst.add(it.steamid)
                        Log.d("debug2", "steamidStrLst: $steamidStrLst")

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
                        //isLoading=true

                        if ((toDownload != 0) &&
                            (index >= downloadingElem) &&
                            (downloadingElem <= currentPSize + toDownload - 1)
                        ) {

                            getFrendsInfoToRv(
                                steamApi.getUser(STEAM_API_KEY, it)
                            )

                            downloadingElem++
                        }
                    }


                } else {

                    handleError(response.errorBody().toString())
                    isLoading = false

                }

            }

            override fun onFailure(call: Call<FriendList>, t: Throwable) {

                if (!call.isCanceled) {

                    handleError(t.toString())
                    isLoading = false

                }

            }
        })
    }

    private fun getFrendsInfoToRv(currentRequest: Call<PlayersResponse>) {

        currentRequest.enqueue(object : Callback<PlayersResponse> {

            override fun onResponse(
                call: Call<PlayersResponse>,
                response: Response<PlayersResponse>
            ) {

                if (response.isSuccessful) {

                    val playersResp =
                        response.body() ?: return//ПЕРЕПРОВЕРИТЬ!!!!!тут ошибки выкинуть

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

                    } else {

                        val prevItems = adapter.currentList.dropLastWhile {
                            it == InputItem.LoadingElement
                        }

                        adapter.submitList(prevItems.minus(InputItem.ErrorElement) + InputItem.ErrorElement)

                    }

                    isLoading = false

                } else {

                    handleError(response.errorBody().toString())
                    isLoading = false

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

        private const val STEAM_BASE_URL = " http://api.steampowered.com/"
        private const val STEAM_API_KEY = "EA8CD62FA7C21007003DEF64FF96E20C"
        private const val MY_STEAM_ID = 76561198277362398
        private const val ELEMENTS_BEFORE_END = 1
        private const val PAGE_SIZE = 10
        private const val DOWNLOAD_ELEMENTS_COUNT = 2

    }
}