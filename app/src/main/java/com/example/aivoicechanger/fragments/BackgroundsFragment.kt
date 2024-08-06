package com.example.aivoicechanger.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.aivoicechanger.adapters.FreeBackgroundAdapter
import com.example.aivoicechanger.databinding.FragmentBackgroundsBinding
import com.example.aivoicechanger.interfaces.OnVoiceCallback
import com.example.aivoicechanger.models.BackgroundModel
import com.example.aivoicechanger.utils.Constants

class BackgroundsFragment : Fragment(), OnVoiceCallback {

    val binding: FragmentBackgroundsBinding by lazy {
        FragmentBackgroundsBinding.inflate(layoutInflater)
    }
    var adapter: FreeBackgroundAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        adapter = FreeBackgroundAdapter(Constants.arrayFreeBG, this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 4)
        return binding.root
    }

    override fun onItemClick(position: Int) {
        sendDataToActivity(Constants.arrayFreeBG[position])
        for (i in 0 until Constants.arrayFreeBG.size) {
            Constants.arrayFreeBG[i].select = position == i
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun sendDataToActivity(data: BackgroundModel) {
        val intent = Intent("BACKGROUND")
        intent.putExtra("file", data.audioEffect)
        requireActivity().sendBroadcast(intent)
    }

}