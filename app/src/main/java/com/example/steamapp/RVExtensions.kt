package com.example.steamapp


import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration

fun RecyclerView.addVerticalSeparation(){

    this.addItemDecoration(

        MaterialDividerItemDecoration(
            this.context,
            MaterialDividerItemDecoration.VERTICAL
        )

    )
}

fun RecyclerView.addPagination(
    linearLM: LinearLayoutManager,
    elementsBeforeEnd:Int,
    pointReached:()->Unit
){
    addOnScrollListener(object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            val lastVisElemPos=linearLM.findLastVisibleItemPosition()
            val countOfElem=linearLM.itemCount

            if(elementsBeforeEnd+lastVisElemPos>=countOfElem){
                pointReached()
                Log.d("debug4","4: $pointReached")
            }
        }
    })
}