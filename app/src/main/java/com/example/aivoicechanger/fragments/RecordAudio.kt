package com.example.aivoicechanger.fragments

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.aivoicechanger.R
import com.example.aivoicechanger.activities.FreeVoices
import com.example.aivoicechanger.databinding.FragmentRecordAudioBinding
import com.example.aivoicechanger.utils.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException


class RecordAudio : Fragment() {

    val binding: FragmentRecordAudioBinding by lazy {
        FragmentRecordAudioBinding.inflate(layoutInflater)
    }
    private var mRecorder: MediaRecorder? = null
    var audioPlayer: MediaPlayer? = null

    val outputFile = " "

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding.generateBtn.isEnabled = false
        binding.micBtn.setOnClickListener {
            if (checkReadPermissions()) {
                showRecordDialog()
            } else {
                requestPermissions()
            }
        }

        binding.generateBtn.setOnClickListener {
            val audioUrirecord: Uri = Uri.fromFile(File(outputFile))
            val intent = Intent(requireContext(), FreeVoices::class.java)
            intent.putExtra("audioFilePath", audioUrirecord.toString())
            startActivity(intent)
        }

        binding.playBtn.setOnClickListener {
            playingAudio()
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

    @RequiresApi(Build.VERSION_CODES.M)
    fun playingAudio() {

        audioPlayer = MediaPlayer.create(requireContext(), Uri.parse(outputFile))
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

    fun checkReadPermissions(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
            ),
            1
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showRecordDialog() {
        try {
            val dialog = Dialog(requireContext(), R.style.DialogStyle)
            dialog.setContentView(R.layout.record_audio_layout)
            dialog.setCancelable(false)

            val stopIcon: ImageView = dialog.findViewById(R.id.stopIcon)
            val stopText: TextView = dialog.findViewById(R.id.stopText)

            stopIcon.setOnClickListener {
                pauseRecording()
                dialog.dismiss()
                binding.generateBtn.setCardBackgroundColor(requireContext().getColor(R.color.btnColor))
                binding.genText.setTextColor(requireContext().getColor(R.color.white))
                binding.generateBtn.isEnabled = true

                binding.simpleMic.visibility = View.GONE
                binding.audioFileLayout.visibility = View.VISIBLE

                binding.dateTime.text = Constants.getCurrentFormattedTime()
            }
            stopText.setOnClickListener {
                pauseRecording()
                dialog.dismiss()

                binding.generateBtn.setCardBackgroundColor(requireContext().getColor(R.color.btnColor))
                binding.genText.setTextColor(requireContext().getColor(R.color.white))
                binding.generateBtn.isEnabled = true
                binding.simpleMic.visibility = View.GONE
                binding.audioFileLayout.visibility = View.VISIBLE
                binding.dateTime.text = Constants.getCurrentFormattedTime()
            }

            dialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                    .toString() + "/AI Voice Changer/Test/${
                    Constants.getAudioFileName(
                        requireContext()
                    )
                }"
                startRecording()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun startRecording() {
        mRecorder = MediaRecorder()
        mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        mRecorder!!.setOutputFile(outputFile)
        try {

            mRecorder!!.prepare()
        } catch (e: IOException) {
            Log.e("TAG", "prepare() failed")
        }
        mRecorder!!.start()
    }

    fun pauseRecording() {
        mRecorder!!.stop()
        mRecorder!!.release()
        mRecorder = null
    }
}