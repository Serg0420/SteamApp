package com.example.steamapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.steamapp.databinding.FragmentFriendsBinding
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class FragmentFriends : Fragment() {

    private val playerInformationLst= mutableListOf<PlayerInfo>()

    private var currentRequest2: Call<PlayersResponse>? = null
    private var currentRequest: Call<FriendList>? = null
    private val adapter by lazy { UserAdapter(requireContext()) }
    private var _binding: FragmentFriendsBinding? = null

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

        val retrofit = Retrofit.Builder()
            .baseUrl(" http://api.steampowered.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val steamApi = retrofit.create<SteamApi>()

        exRequest(

            steamApi.getUsersFriends(
                "EA8CD62FA7C21007003DEF64FF96E20C",
                76561198277362398,
                "friend"
            ),
            steamApi

        )

        with(binding) {
            friendsLst.adapter = adapter
        }
    }

    private fun exRequest(currentRequest: Call<FriendList>, steamApi: SteamApi) {

        currentRequest.enqueue(object : Callback<FriendList> {

            override fun onResponse(call: Call<FriendList>, response: Response<FriendList>) {

                val steamidStrLst= mutableListOf<String>()

                if (response.isSuccessful) {

                    val friendLst = response.body() ?: return//ПЕРЕПРОВЕРИТЬ!!!!!тут ошибки выкинуть

                    (friendLst.friendsList.friends).forEach {
                        steamidStrLst.add(it.steamid)
                    }

                    steamidStrLst.forEach {

                        exRequest2(
                            steamApi.getUser(
                                "EA8CD62FA7C21007003DEF64FF96E20C",
                                it
                            )
                        )

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

    private fun exRequest2(currentRequest2: Call<PlayersResponse>) {

        currentRequest2.enqueue(object : Callback<PlayersResponse> {

            override fun onResponse(
                call: Call<PlayersResponse>,
                response: Response<PlayersResponse>
            ) {

                if (response.isSuccessful) {

                    val playersResp =
                        response.body() ?: return//ПЕРЕПРОВЕРИТЬ!!!!!тут ошибки выкинуть

                    playerInformationLst.add(playersResp.response.players[0])
                    adapter.submitList(playerInformationLst)

                } else {
                    //тут ошибки выкинуть
                    errorToasting(response.errorBody().toString())
                }

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
        currentRequest2?.cancel()
        currentRequest?.cancel()
    }

    fun errorToasting(errorStr: String) {
        Toast.makeText(requireContext(), errorStr, Toast.LENGTH_LONG).show()
    }

}