package com.example.aivoicechanger.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.*
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.NoiseSuppressor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.aivoicechanger.R
import com.example.aivoicechanger.adapters.TabLayoutAdapter
import com.example.aivoicechanger.databinding.ActivityFreeVoicesBinding
import com.example.aivoicechanger.utils.Constants
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.sql.Types.NULL


class FreeVoices : AppCompatActivity() {
    val binding: ActivityFreeVoicesBinding by lazy {
        ActivityFreeVoicesBinding.inflate(layoutInflater)
    }

    private lateinit var dataReceiver: DataReceiver
    private lateinit var dataReceiver2: DataReceiver2
    var audioFilePath: String? = null
    var audioPlayer: MediaPlayer? = null
    var audioPlayerBG: MediaPlayer? = null
    var totalDuration = 0
    var rateFinal: Float = 1f
    var pitchFinal: Float = 1f
    var bgFile: Int = NULL
    private val handler = Handler()
    private var audioRecord: AudioRecord? = null
    private val sampleRate = 44100
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    private var mRecorder: MediaRecorder? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        audioFilePath = intent.getStringExtra("audioFilePath")

        dataReceiver = DataReceiver()
        val filter = IntentFilter("PITCH_RATE")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(dataReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(dataReceiver, filter)
        }

        dataReceiver2 = DataReceiver2()
        val filter2 = IntentFilter("BACKGROUND")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(dataReceiver2, filter2, RECEIVER_EXPORTED)
        } else {
            registerReceiver(dataReceiver2, filter2)
        }
        binding.tablayout.addTab(binding.tablayout.newTab().setText("Change Voice"))
        binding.tablayout.addTab(binding.tablayout.newTab().setText("Voice Effects"))
        binding.tablayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = TabLayoutAdapter(this, supportFragmentManager, binding.tablayout.tabCount)
        binding.viewPager.adapter = adapter
        binding.viewPager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                binding.tablayout
            )
        )

        binding.tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        binding.playBtn.setOnClickListener {
            if (audioPlayerBG != null || audioPlayer != null) {
                var isPlaying = false
                if (audioPlayer != null) {
                    if (audioPlayer!!.isPlaying) {
                        isPlaying = true
                        audioPlayer!!.stop()
                    }
                }
                if (audioPlayerBG != null) {
                    if (audioPlayerBG!!.isPlaying) {
                        isPlaying = true
                        audioPlayerBG!!.stop()
                    }
                }
                if (isPlaying) {
                    binding.progressBar.progress = 0
                    binding.runningTime.text = "00:00"
                    binding.playImg.setImageResource(R.drawable.play_audio_icon)
                    handler.removeCallbacks(updateSeekBarRunnable)
                } else {

                    if (checkReadPermissions()) {
                        playingAudio(pitchFinal, rateFinal)
                        if (bgFile != NULL) {
                            playingBackground(bgFile)
                        }
                    } else {
                        requestPermissions()
                    }
                }
            } else {
                if (checkReadPermissions()) {
                    playingAudio(pitchFinal, rateFinal)
                    if (bgFile != NULL) {
                        playingBackground(bgFile)
                    }
                } else {
                    requestPermissions()
                }
            }
        }
        binding.downloadBtn.setOnClickListener {

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun playingAudio(pitch: Float, rate: Float) {
        if (audioPlayer != null) {
            if (audioPlayer!!.isPlaying) {
                audioPlayer!!.stop()
                if (audioRecord != null) {
                    stopRecording()
                }
            }
        }

        audioPlayer = MediaPlayer.create(this, Uri.parse(audioFilePath))
        if (audioPlayer != null) {
            audioPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            val params = PlaybackParams()
            params.pitch = pitch
            params.speed = rate
            audioPlayer!!.playbackParams = params

            audioPlayer!!.setOnPreparedListener {
                totalDuration = audioPlayer!!.duration
                binding.totalTime.text = Constants.formatMilliseconds(totalDuration.toLong())
                binding.playImg.setImageResource(R.drawable.pause_audio_icon)
                updateSeekBar()
                val outputFile123 =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                        .toString() + "/AI Voice Changer/Test/${Constants.getAudioFileName(this)}"
                val outputFile = File(outputFile123)
                outputFile.parentFile?.mkdirs()
                CoroutineScope(Dispatchers.IO).launch {
                    startPlaybackAndRecording(outputFile)
                    //  startRecording(outputFile123)
                }

            }

            audioPlayer!!.setOnCompletionListener {
                if (audioPlayerBG != null) {
                    if (audioPlayerBG!!.isPlaying) {
                        audioPlayerBG!!.stop()
                    }
                }
                //pauseRecording()
                binding.progressBar.progress = 0
                binding.runningTime.text = "00:00"
                binding.playImg.setImageResource(R.drawable.play_audio_icon)
                handler.removeCallbacks(updateSeekBarRunnable)
            }
        }

    }

    private val updateSeekBarRunnable = object : Runnable {
        override fun run() {
            val progress: Float = (audioPlayer!!.currentPosition / totalDuration.toFloat()) * 100f
            binding.runningTime.text =
                Constants.formatMilliseconds(audioPlayer!!.currentPosition.toLong())
            binding.progressBar.progress = progress.toInt()
            handler.postDelayed(this, 50)
        }
    }

    private fun updateSeekBar() {
        handler.postDelayed(updateSeekBarRunnable, 50)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    fun startPlaybackAndRecording(outputFile: File) {
        audioPlayer?.start()
        audioPlayerBG?.start()

        audioRecord = AudioRecord.Builder()
            .setAudioSource(MediaRecorder.AudioSource.MIC)
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .build()
        if (audioRecord != null) {
            audioRecord!!.startRecording()

            val buffer = ByteArray(bufferSize)
            val outputStream = FileOutputStream(outputFile)
            var totalAudioLen: Long = 0

            // Check for and apply NoiseSuppressor if available
            if (mRecorder != null && NoiseSuppressor.isAvailable()) {
                val noiseSuppressor = NoiseSuppressor.create(audioRecord!!.audioSessionId)
                if (noiseSuppressor != null) {
                    Log.i("TAG", "NoiseSuppressor applied")
                } else {
                    Log.e("TAG", "Failed to apply NoiseSuppressor")
                }
            }

            // Check for and apply AcousticEchoCanceler if available
            if (mRecorder != null && AcousticEchoCanceler.isAvailable()) {
                val echoCanceler = AcousticEchoCanceler.create(audioRecord!!.audioSessionId)
                if (echoCanceler != null) {
                    Log.i("TAG", "AcousticEchoCanceler applied")
                } else {
                    Log.e("TAG", "Failed to apply AcousticEchoCanceler")
                }
            }
            // Write placeholder header
            writeWavHeader(outputStream, 0)

            try {
                while (audioPlayer?.isPlaying == true) {
                    val read = audioRecord!!.read(buffer, 0, buffer.size)
                    if (read > 0) {
                        outputStream.write(buffer, 0, read)
                        totalAudioLen += read
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    outputStream.close()

                    // Update WAV header with correct sizes
                    RandomAccessFile(outputFile, "rw").use { raf ->
                        raf.seek(0)
                        writeWavHeader(FileOutputStream(raf.fd), totalAudioLen)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                stopRecording()
            }
        }
    }


    private fun writeWavHeader(outputStream: FileOutputStream, totalAudioLen: Long) {
        val channels = 1
        val byteRate = 16 * sampleRate * channels / 8

        val header = ByteArray(44)
        val totalDataLen = totalAudioLen + 36

        ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("RIFF".toByteArray())
            putInt(totalDataLen.toInt())
            put("WAVE".toByteArray())
            put("fmt ".toByteArray())
            putInt(16) // Subchunk1Size for PCM
            putShort(1.toShort()) // AudioFormat for PCM
            putShort(channels.toShort())
            putInt(sampleRate)
            putInt(byteRate)
            putShort((channels * 2).toShort()) // BlockAlign
            putShort(16.toShort()) // BitsPerSample
            put("data".toByteArray())
            putInt(totalAudioLen.toInt())
        }

        outputStream.write(header, 0, 44)
    }

    private fun stopRecording() {
        if (audioRecord != null && audioRecord!!.state == AudioRecord.STATE_INITIALIZED) {
            try {
                audioRecord!!.stop()
                audioRecord!!.release()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
            audioRecord = null
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(dataReceiver)
        handler.removeCallbacks(updateSeekBarRunnable)
        audioPlayer!!.release()
    }


    fun checkReadPermissions(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.RECORD_AUDIO
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
            ),
            1
        )
    }

    inner class DataReceiver : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context, intent: Intent) {
            val pitch = intent.getFloatExtra("pitch", 0f)
            val rate = intent.getFloatExtra("rate", 0f)
            rateFinal = rate
            pitchFinal = pitch

            if (checkReadPermissions()) {
                playingAudio(pitch, rate)
                if (bgFile != NULL) {
                    playingBackground(bgFile)
                }
            } else {
                requestPermissions()
            }
        }
    }

    inner class DataReceiver2 : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context, intent: Intent) {
            val file = intent.getIntExtra("file", R.raw.sea_noise)
            bgFile = file

            if (checkReadPermissions()) {
                playingAudio(pitchFinal, rateFinal)
                if (file != NULL) {
                    playingBackground(file)
                }
            } else {
                requestPermissions()
            }

        }
    }


    fun playingBackground(file: Int) {
        audioPlayerBG = MediaPlayer.create(this, file)
        audioPlayerBG!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        audioPlayerBG!!.isLooping = true
    }
}