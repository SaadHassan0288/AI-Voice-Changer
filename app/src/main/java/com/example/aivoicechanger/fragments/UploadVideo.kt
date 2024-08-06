package com.example.aivoicechanger.fragments

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aivoicechanger.R
import com.example.aivoicechanger.activities.FreeVoicesForVideo
import com.example.aivoicechanger.databinding.FragmentUploadVideoBinding
import com.example.aivoicechanger.utils.Constants
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


class UploadVideo : Fragment() {

    val binding: FragmentUploadVideoBinding by lazy {
        FragmentUploadVideoBinding.inflate(layoutInflater)
    }

    var audioFileUri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding.generateBtn.isEnabled = false
        binding.uploadBtn.setOnClickListener {
            val intent: Intent
            intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "video/*"
            startActivityForResult(
                Intent.createChooser(
                    intent, "Select MP3 file "
                ), 1
            )
        }
        binding.uploadLayout.setOnClickListener {
            val intent: Intent
            intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "video/*"
            startActivityForResult(
                Intent.createChooser(
                    intent, "Select MP3 file "
                ), 1
            )
        }
        binding.generateBtn.setOnClickListener {
            val intent = Intent(requireContext(), FreeVoicesForVideo::class.java)
            intent.putExtra("videoFilePath", audioFileUri.toString())
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
            binding.uploadText.text = "Upload"
            binding.descrip.text = "Tap to Upload.\nSupports mp4.mkv.avi."
        }
        return binding.root
    }


    fun playingVideo() {

        binding.videoView.setVideoURI(audioFileUri)
        binding.videoView.start()
        binding.videoView.setOnPreparedListener { mediaPlayer ->
            binding.playBtn.visibility = View.GONE
            mediaPlayer.start()
        }

        binding.videoView.setOnCompletionListener {
            binding.playBtn.visibility = View.VISIBLE
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null && data.data != null) {
                audioFileUri = data.data

                binding.generateBtn.setCardBackgroundColor(requireContext().getColor(R.color.btnColor))
                binding.genText.setTextColor(requireContext().getColor(R.color.white))
                binding.generateBtn.isEnabled = true

                binding.simpleMic.visibility = View.GONE
                binding.audioFileLayout.visibility = View.VISIBLE

                binding.dateTime.text = Constants.getCurrentFormattedTime()
                binding.uploadText.text = "RE-Upload"
                binding.descrip.text = "Tap to Re-Upload.\nSupports mp4.mkv.avi."
            }
        }
    }


}