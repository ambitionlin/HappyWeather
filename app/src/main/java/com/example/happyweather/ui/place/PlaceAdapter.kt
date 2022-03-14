package com.example.happyweather.ui.place

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.happyweather.R
import com.example.happyweather.databinding.PlaceItemBinding
import com.example.happyweather.logic.model.PlaceResponse

class PlaceAdapter(private val fragment: Fragment, private val placeList:List<PlaceResponse.Place>) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>(){

    inner class ViewHolder(binding: PlaceItemBinding) : RecyclerView.ViewHolder(binding.root){
        val placeName: TextView = binding.placeName
        val placeAddress: TextView = binding.placeAddress
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PlaceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
    }

    override fun getItemCount() = placeList.size
}