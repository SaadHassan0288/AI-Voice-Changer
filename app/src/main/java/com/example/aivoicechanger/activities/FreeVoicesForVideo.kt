package com.example.aivoicechanger.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
import android.content.pm.PackageManager
import android.media.*
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.NoiseSuppressor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.FFmpegExecution
import com.example.aivoicechanger.R
import com.example.aivoicechanger.adapters.VideoVoicesTabAdapter
import com.example.aivoicechanger.databinding.ActivityFreeVoicesForVideoBinding
import com.example.aivoicechanger.utils.Constants
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.sql.Types


class FreeVoicesForVideo : AppCompatActivity() {

    val binding: ActivityFreeVoicesForVideoBinding by lazy {
        ActivityFreeVoicesForVideoBinding.inflate(layoutInflater)
    }
    var videoFilePath = " "

    var outputFile123 = " "
    var audioPlayer: MediaPlayer? = null
    var audioPlayerBG: MediaPlayer? = null
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    private lateinit var dataReceiver: DataReceiverVideo
    private lateinit var dataReceiver2: DataReceiverVideo2
    var totalDuration = 0
    var rateFinal: Float = 1f
    var pitchFinal: Float = 1f
    var bgFile: Int = Types.NULL
    private var audioRecord: AudioRecord? = null
    private val sampleRate = 44100
    private val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    private var mRecorder: MediaRecorder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        videoFilePath = intent.getStringExtra("videoFilePath") ?: videoFilePath
        binding.videoView.setVideoPath(videoFilePath)
        handler = Handler()
        dataReceiver = DataReceiverVideo()
        val filter = IntentFilter("PITCH_RATE_VIDEO")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(dataReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(dataReceiver, filter)
        }

        dataReceiver2 = DataReceiverVideo2()
        val filter2 = IntentFilter("BACKGROUND_VIDEO")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(dataReceiver2, filter2, RECEIVER_EXPORTED)
        } else {
            registerReceiver(dataReceiver2, filter2)
        }

        binding.tablayout.addTab(binding.tablayout.newTab().setText("Change Voice"))
        binding.tablayout.addTab(binding.tablayout.newTab().setText("Voice Effects"))
        binding.tablayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter =
            VideoVoicesTabAdapter(this, supportFragmentManager, binding.tablayout.tabCount)
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
                    handler.removeCallbacks(runnable)
                } else {

                    if (checkReadPermissions()) {
                        playingAudio(pitchFinal, rateFinal)
                        if (bgFile != Types.NULL) {
                            playingBackground(bgFile)
                        }
                    } else {
                        requestPermissions()
                    }
                }
            } else {
                if (checkReadPermissions()) {
                    playingAudio(pitchFinal, rateFinal)
                    if (bgFile != Types.NULL) {
                        playingBackground(bgFile)
                    }
                } else {
                    requestPermissions()
                }
            }
            playingVideo()
        }
        binding.downloadBtn.setOnClickListener {
            if (audioPlayer != null) {
                if (!audioPlayer!!.isPlaying) {
                    val outputFinal =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                            .toString() + "/AI Voice Changer/Videos/${
                            Constants.getVideoFileName(
                                this
                            )
                        }"


                    val file = File(outputFinal)
                    file.parentFile?.mkdirs()
                    Log.d(
                        "TAG_DOWN",
                        "onCreate: Video Path: ${getFileFromContentUri(Uri.parse(videoFilePath))}"
                    )
                    Log.d("TAG_DOWN", "onCreate: Audio Path: ${File(outputFile123).absolutePath}")
                    Log.d("TAG_DOWN", "onCreate: Final Video Path: ${file.absolutePath}")
                    val c = arrayOf(
                        "-i",
                        getFileFromContentUri(Uri.parse(videoFilePath))!!.absolutePath,
                        "-i",
                        File(outputFile123).absolutePath,
                        "-c:v",
                        "copy",
                        "-c:a",
                        "aac",
                        "-map",
                        "0:v:0",
                        "-map",
                        "1:a:0",
                        "-shortest",
                        file.absolutePath
                    )
                    MergeVideo(c, file.absolutePath)
                } else {
                    Toast.makeText(
                        this,
                        "Video is playing please wait for a while",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        binding.shareBtn.setOnClickListener {
            if (audioPlayer != null) {
                if (!audioPlayer!!.isPlaying) {
                    val outputFinal =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                            .toString() + "/AI Voice Changer/Videos/${
                            Constants.getVideoFileName(
                                this
                            )
                        }"


                    val file = File(outputFinal)
                    file.parentFile?.mkdirs()
                    Log.d(
                        "TAG_DOWN",
                        "onCreate: Video Path: ${getFileFromContentUri(Uri.parse(videoFilePath))}"
                    )
                    Log.d("TAG_DOWN", "onCreate: Audio Path: ${File(outputFile123).absolutePath}")
                    Log.d("TAG_DOWN", "onCreate: Final Video Path: ${file.absolutePath}")
                    val c = arrayOf(
                        "-i",
                        getFileFromContentUri(Uri.parse(videoFilePath))!!.absolutePath,
                        "-i",
                        File(outputFile123).absolutePath,
                        "-c:v",
                        "copy",
                        "-c:a",
                        "aac",
                        "-map",
                        "0:v:0",
                        "-map",
                        "1:a:0",
                        "-shortest",
                        file.absolutePath
                    )
                    ShareVideo(c, file.absolutePath)
                } else {
                    Toast.makeText(
                        this,
                        "Video is playing please wait for a while",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun MergeVideo(co: Array<String>, outputFile: String) {
        FFmpeg.executeAsync(co) { executionId, returnCode ->
            Log.d("hello", "return  $returnCode")
            Log.d("hello", "executionID  $executionId")
            Log.d("hello", "FFMPEG  " + FFmpegExecution(executionId, co))
            if (returnCode == 0) {
                Toast.makeText(this, "Vide Saved in $outputFile", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun ShareVideo(co: Array<String>, outputFile: String) {
        FFmpeg.executeAsync(co) { executionId, returnCode ->
            Log.d("hello", "return  $returnCode")
            Log.d("hello", "executionID  $executionId")
            Log.d("hello", "FFMPEG  " + FFmpegExecution(executionId, co))
            if (returnCode == 0) {
                Toast.makeText(this, "Vide Saved in $outputFile", Toast.LENGTH_SHORT).show()
                val videoFile = File(outputFile)
                val videoURI =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) FileProvider.getUriForFile(
                        this,
                        "${packageName}.fileprovider",
                        videoFile
                    ) else Uri.fromFile(videoFile)
                ShareCompat.IntentBuilder.from(this)
                    .setStream(videoURI)
                    .setType("video/mp4")
                    .setChooserTitle("Share video...")
                    .startChooser()
            }
        }
    }

    fun playingVideo() {
        if (!binding.videoView.isPlaying) {
            binding.videoView.setVideoPath(videoFilePath)
            binding.videoView.start()
            binding.videoView.setOnPreparedListener { mediaPlayer ->
                mediaPlayer.setVolume(0f, 0f)
                totalDuration = mediaPlayer.duration
                binding.totalTime.text = Constants.formatMilliseconds(totalDuration.toLong())
                binding.playImg.setImageResource(R.drawable.pause_audio_icon)
                mediaPlayer.start()
                updateSeekBar()
            }

            binding.videoView.setOnCompletionListener {
                binding.progressBar.progress = 0
                binding.runningTime.text = "00:00"
                binding.playImg.setImageResource(R.drawable.play_audio_icon)
                handler.removeCallbacks(runnable)
            }
        } else {
            binding.videoView.stopPlayback()
            binding.progressBar.progress = 0
            binding.runningTime.text = "00:00"
            binding.playImg.setImageResource(R.drawable.play_audio_icon)
            handler.removeCallbacks(runnable)
        }
    }

    private fun updateSeekBar() {
        runnable = Runnable {
            val progress: Float =
                (binding.videoView.currentPosition / totalDuration.toFloat()) * 100f
            binding.runningTime.text =
                Constants.formatMilliseconds(binding.videoView.currentPosition.toLong())
            binding.progressBar.progress = progress.toInt()
            handler.postDelayed(runnable, 50)
        }
        handler.postDelayed(runnable, 0)
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

        audioPlayer = MediaPlayer.create(this, Uri.parse(videoFilePath))
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
                outputFile123 =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
                        .toString() + "/AI Voice Changer/Test/${Constants.getAudioFileName(this)}"
                val outputFile = File(outputFile123)

                outputFile.parentFile?.mkdirs()
                CoroutineScope(Dispatchers.IO).launch {
                    if (outputFile.exists()) {
                        outputFile.delete()
                    }
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
                binding.progressBar.progress = 0
                binding.runningTime.text = "00:00"
                binding.playImg.setImageResource(R.drawable.play_audio_icon)
                handler.removeCallbacks(runnable)
            }
        }

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
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(dataReceiver)
        if (runnable != null) {
            handler.removeCallbacks(runnable)
        }
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

    inner class DataReceiverVideo : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context, intent: Intent) {
            val pitch = intent.getFloatExtra("pitch", 0f)
            val rate = intent.getFloatExtra("rate", 0f)
            Log.d("TAG-REC", "onReceive: $pitch , $rate")
            rateFinal = rate
            pitchFinal = pitch

            if (checkReadPermissions()) {
                playingAudio(pitch, rate)
                if (bgFile != Types.NULL) {
                    playingBackground(bgFile)
                }
            } else {
                requestPermissions()
            }

        }
    }

    inner class DataReceiverVideo2 : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context, intent: Intent) {
            val file = intent.getIntExtra("file", R.raw.sea_noise)

            bgFile = file

            if (checkReadPermissions()) {
                playingAudio(pitchFinal, rateFinal)
                if (file != Types.NULL) {
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

    fun getFileFromContentUri(uri: Uri): File? {
        val contentResolver: ContentResolver = contentResolver
        val filePathColumn = arrayOf(MediaStore.MediaColumns.DISPLAY_NAME)
        val cursor = contentResolver.query(uri, filePathColumn, null, null, null)

        if (cursor != null && cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)
            val fileName = cursor.getString(nameIndex)
            cursor.close()

            val file = File(cacheDir, fileName)
            try {
                val inputStream: InputStream? = contentResolver.openInputStream(uri)
                val outputStream: OutputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                return file
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }


}