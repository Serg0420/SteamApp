package com.example.steamapp

import android.content.ClipData
import android.os.Bundle
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
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class FragmentFriends : Fragment() {

    private val playerInformationLst= mutableListOf<InputItem.PlayerInfo>()
    private var isLoading=false

    private val adapter by lazy { UserAdapter(requireContext()) }
    private var _binding: FragmentFriendsBinding? = null
    private var currentPageSize=0

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

            val linearLM=LinearLayoutManager(requireContext())

            getDataFromSteamToRv(0, currentPageSize,true)
            currentPageSize= PAGE_SIZE

            friendsLst.layoutManager=linearLM
            friendsLst.addVerticalSeparation()
            friendsLst.addPagination(linearLM,ELEMENTS_BEFORE_END){

                getDataFromSteamToRv(DOWNLOAD_ELEMENTS_COUNT, currentPageSize,false)
                currentPageSize=+DOWNLOAD_ELEMENTS_COUNT

            }
            friendsLst.adapter = adapter
        }
    }


    private fun getDataFromSteamToRv(downloadMore: Int, currentPSize: Int, isFirstCall:Boolean){

        if(isLoading)return
       // isLoading=true
        val retrofit = Retrofit.Builder()
            .baseUrl(" http://api.steampowered.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val steamApi = retrofit.create<SteamApi>()


        getFriendsToRv(

            steamApi.getUsersFriends(
                "EA8CD62FA7C21007003DEF64FF96E20C",
                76561198277362398,
                "friend"
            ),
            steamApi,
            downloadMore,
            currentPSize,
            isFirstCall
        )

    }

    private fun getFriendsToRv(currentRequest: Call<FriendList>, steamApi: SteamApi,downloadMore: Int,currentPSize:Int,isFirstCall:Boolean) {

        currentRequest.enqueue(object : Callback<FriendList> {

            override fun onResponse(call: Call<FriendList>, response: Response<FriendList>) {

                val steamidStrLst= mutableListOf<String>()

                if (response.isSuccessful) {

                    val friendLst = response.body() ?: return//ПЕРЕПРОВЕРИТЬ!!!!!тут ошибки выкинуть

                    if(isFirstCall){
                        (friendLst.friendsList.friends).forEach {
                            steamidStrLst.add(it.steamid)
                        }
                    }


                    val lstSize=steamidStrLst.size
                    var downloadingElem:Int=currentPSize
                    var toDownload=downloadMore


                    if(toDownload==0 && currentPSize<=0){toDownload=PAGE_SIZE}
                    if(toDownload+currentPSize>lstSize){toDownload= lstSize-currentPSize}


                    steamidStrLst.forEachIndexed{ index,it->

                        if ((toDownload!=0)&&
                           // (downloadingElem-currentPageSize >= 0) &&
                            (index>=downloadingElem) &&
                            (downloadingElem <= currentPSize + toDownload-1)
                        ) {

                            getFrendsInfoToRv(
                                steamApi.getUser(
                                    "EA8CD62FA7C21007003DEF64FF96E20C",
                                    it
                                )
                            )
                            downloadingElem++
                        }

                    }

                } else {

                    errorToasting(response.errorBody().toString())
                    //тут ошибки выкинуть
                }

            }

            override fun onFailure(call: Call<FriendList>, t: Throwable) {

                if (!call.isCanceled) {

                    //tyt vivedem oshibky
                    errorToasting(t.toString())

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

                    val prevItems=adapter.currentList.dropLastWhile {
                        it==InputItem.LoadingElement
                    }
                    val newItems=
                        playerInformationLst.map{it}.minus(prevItems)+InputItem.LoadingElement
                    adapter.submitList(prevItems+newItems)

                } else {
                    //тут ошибки выкинуть
                    errorToasting(response.errorBody().toString())
                }

             //   isLoading=false
            }

            override fun onFailure(call: Call<PlayersResponse>, t: Throwable) {

                if (!call.isCanceled) {

                    //tyt vivedem oshibky
                    errorToasting(t.toString())

                }

            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun errorToasting(errorStr: String) {
        Toast.makeText(requireContext(), errorStr, Toast.LENGTH_SHORT).show()
    }

    companion object{
        private const val ELEMENTS_BEFORE_END=2
        private const val PAGE_SIZE=8
        private const val DOWNLOAD_ELEMENTS_COUNT=2
    }
}