package com.example.aivoicechanger.activities

import android.content.Intent
import android.hardware.Camera
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.view.SurfaceHolder
import androidx.appcompat.app.AppCompatActivity
import com.example.aivoicechanger.R
import com.example.aivoicechanger.databinding.ActivityCameraBinding
import java.io.IOException

class CameraActivity : AppCompatActivity(), SurfaceHolder.Callback {
    val binding: ActivityCameraBinding by lazy {
        ActivityCameraBinding.inflate(layoutInflater)
    }

    private lateinit var camera: Camera
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var surfaceHolder: SurfaceHolder
    val outputFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        .toString() + "/AI Voice Changer/MyVideo.mp4"
    var isStart = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        surfaceHolder = binding.surfaceView.holder
        surfaceHolder.addCallback(this)
        binding.playBtn.setOnClickListener {
            if (!isStart) {
                isStart = true
                binding.playBtn.setImageResource(R.drawable.pause_audio_icon)
                startRecording()
            } else {
                isStart = false
                binding.playBtn.setImageResource(R.drawable.play_audio_icon)
                stopRecording()
            }
        }
    }

    private fun setupMediaRecorder(): Boolean {
        mediaRecorder = MediaRecorder()

        camera.unlock()
        mediaRecorder.setCamera(camera)
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mediaRecorder.setOutputFile(outputFile)
        mediaRecorder.setPreviewDisplay(binding.surfaceView.holder.surface)
        mediaRecorder.setVideoSize(1920, 1080)
        mediaRecorder.setVideoFrameRate(30)
        mediaRecorder.setVideoEncodingBitRate(10000000)

        try {
            mediaRecorder.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

        return true
    }


    private fun startRecording() {
        if (setupMediaRecorder()) {
            mediaRecorder.start()
            // Recording started
        } else {
            // Failed to prepare MediaRecorder
        }
    }

    private fun stopRecording() {
        mediaRecorder.stop()
        mediaRecorder.reset()
        camera.lock()
        val bundle = Bundle()
        bundle.putString("videoPath", outputFile)
        val intent = Intent(this, VideoToAudio::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
        // Video file is saved at `outputFilePath`
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaRecorder()
        releaseCamera()
    }

    private fun releaseMediaRecorder() {
        if (::mediaRecorder.isInitialized) {
            mediaRecorder.reset()
            mediaRecorder.release()
        }
    }

    private fun releaseCamera() {
        if (::camera.isInitialized) {
            camera.release()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        camera = Camera.open()
        camera.setDisplayOrientation(90)
        try {
            camera.setPreviewDisplay(holder)
            camera.startPreview()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {
        releaseMediaRecorder()
        releaseCamera()
    }

}