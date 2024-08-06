package com.example.aivoicechanger.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.fragment.app.Fragment
import com.example.aivoicechanger.R
import com.example.aivoicechanger.activities.CameraActivity
import com.example.aivoicechanger.activities.FreeVoicesForVideo
import com.example.aivoicechanger.databinding.FragmentRecordVideoBinding
import com.example.aivoicechanger.utils.Constants


class RecordVideo : Fragment() {

    val binding: FragmentRecordVideoBinding by lazy {
        FragmentRecordVideoBinding.inflate(layoutInflater)
    }


    private lateinit var dataReceiver: DataReceiver
    var videoPath = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.generateBtn.isEnabled = false
        dataReceiver = DataReceiver()
        val filter = IntentFilter("VIDEO_PATH")
        registerReceiver(requireContext(), dataReceiver, filter, ContextCompat.RECEIVER_EXPORTED)
        binding.cameraBtn.setOnClickListener {
            startActivity(Intent(requireContext(), CameraActivity::class.java))
        }
        binding.generateBtn.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("videoFilePath", videoPath)
            val intent = (Intent(requireContext(), FreeVoicesForVideo::class.java))
            intent.putExtras(bundle)
            startActivity(intent)
        }
        binding.playBtn.setOnClickListener {
            playingVideo()
        }

        binding.deletBtn.setOnClickListener {
            binding.simpleMic.visibility = View.VISIBLE
            binding.audioFileLayout.visibility = View.GONE
            binding.generateBtn.setCardBackgroundColor(requireContext().getColor(R.color.boardColor))
            binding.genText.setTextColor(requireContext().getColor(R.color.btnUnselect))
            binding.generateBtn.isEnabled = false
        }
        return binding.root
    }

    fun playingVideo() {

        binding.videoView.setVideoPath(videoPath)
        binding.videoView.start()
        binding.videoView.setOnPreparedListener { mediaPlayer ->
            binding.playBtn.visibility = View.GONE
            mediaPlayer.start()
        }

        binding.videoView.setOnCompletionListener {
            binding.playBtn.visibility = View.VISIBLE
        }

    }

    inner class DataReceiver : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context, intent: Intent) {
            videoPath = intent.getStringExtra("videoPath") ?: videoPath
            binding.generateBtn.setCardBackgroundColor(requireContext().getColor(R.color.btnColor))
            binding.genText.setTextColor(requireContext().getColor(R.color.white))
            binding.generateBtn.isEnabled = true

            binding.simpleMic.visibility = View.GONE
            binding.audioFileLayout.visibility = View.VISIBLE

            binding.dateTime.text = Constants.getCurrentFormattedTime()
        }
    }
}