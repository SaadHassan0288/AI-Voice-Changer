package com.example.aivoicechanger.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.aivoicechanger.adapters.FreeVoicesAdapter
import com.example.aivoicechanger.databinding.FragmentVideoVoicesBinding
import com.example.aivoicechanger.interfaces.OnVoiceCallback
import com.example.aivoicechanger.models.GenericModel
import com.example.aivoicechanger.utils.Constants


class VideoVoices : Fragment(), OnVoiceCallback {

    val binding: FragmentVideoVoicesBinding by lazy {
        FragmentVideoVoicesBinding.inflate(layoutInflater)
    }

    var adapter: FreeVoicesAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        adapter = FreeVoicesAdapter(Constants.arrayFreeVoices, this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        return binding.root
    }

    override fun onItemClick(position: Int) {
        sendDataToActivity(Constants.arrayFreeVoices[position])
        for (i in 0 until Constants.arrayFreeVoices.size) {
            Constants.arrayFreeVoices[i].select = position == i
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun sendDataToActivity(data: GenericModel) {
        val intent = Intent("PITCH_RATE_VIDEO")
        intent.putExtra("pitch", data.audioEffect.first)
        intent.putExtra("rate", data.audioEffect.second)
        requireActivity().sendBroadcast(intent)
    }
}