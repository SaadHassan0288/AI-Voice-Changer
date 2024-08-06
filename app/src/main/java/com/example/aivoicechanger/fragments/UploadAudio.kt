package com.example.aivoicechanger.fragments

import android.app.Activity
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.aivoicechanger.R
import com.example.aivoicechanger.activities.FreeVoices
import com.example.aivoicechanger.databinding.FragmentUploadAudioBinding
import com.example.aivoicechanger.utils.Constants


class UploadAudio : Fragment() {

    val binding: FragmentUploadAudioBinding by lazy {
        FragmentUploadAudioBinding.inflate(layoutInflater)
    }

    var audioPlayer: MediaPlayer? = null
    var audioFileUri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding.generateBtn.isEnabled = false
        binding.uploadBtn.setOnClickListener {
            val intent: Intent
            intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "audio/mpeg"
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
            intent.type = "audio/mpeg"
            startActivityForResult(
                Intent.createChooser(
                    intent, "Select MP3 file "
                ), 1
            )
        }
        binding.deletBtn.setOnClickListener {
            binding.simpleMic.visibility = View.VISIBLE
            binding.audioFileLayout.visibility = View.GONE
            binding.generateBtn.setCardBackgroundColor(requireContext().getColor(R.color.boardColor))
            binding.genText.setTextColor(requireContext().getColor(R.color.btnUnselect))
            binding.generateBtn.isEnabled = false
            binding.uploadText.text = "Upload"
            binding.descrip.text = "Tap to Upload.\\nSupports mp3.wav."
        }

        binding.generateBtn.setOnClickListener {
            val intent = Intent(requireContext(), FreeVoices::class.java)
            intent.putExtra("audioFilePath", audioFileUri.toString())
            startActivity(intent)
        }

        binding.playBtn.setOnClickListener {
            playingAudio()
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
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
                binding.descrip.text = "Tap to Re-Upload.\\nSupports mp3.wav."
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun playingAudio() {

        audioPlayer = MediaPlayer.create(requireContext(), audioFileUri)
        if (audioPlayer != null) {
            audioPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            val params = PlaybackParams()
            params.pitch = 1f
            params.speed = 1f
            audioPlayer!!.playbackParams = params

            audioPlayer!!.setOnPreparedListener {
                binding.playBtn.setImageResource(R.drawable.pause_audio_icon)

            }

            audioPlayer!!.setOnCompletionListener {
                binding.playBtn.setImageResource(R.drawable.play_audio_icon)
            }
        }

    }
}