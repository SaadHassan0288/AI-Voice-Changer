package com.example.aivoicechanger

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.aivoicechanger.activities.AudioToAudioActivity
import com.example.aivoicechanger.activities.TextToAudio
import com.example.aivoicechanger.activities.VideoToAudio
import com.example.aivoicechanger.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    val REQUEST_PERMISSION_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if (!checkPermissions()) {
                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.RECORD_AUDIO
                    )
                )
            }
        } else {
            if (!checkReadPermissions() || !checkWritePermissions() || !checkRecordPermissions()) {
                requestPermissions()
            }
        }

        binding.btnTextToAudio.setOnClickListener {
            startActivity(Intent(this, TextToAudio::class.java))
        }
        binding.btnAudioToAudio.setOnClickListener {
            startActivity(Intent(this, AudioToAudioActivity::class.java))
        }
        binding.btnVideoToAudio.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("videoPath", "null")
            val intent = Intent(this, VideoToAudio::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_PERMISSION_CODE -> if (grantResults.size > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(applicationContext, "Permission granted", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value
            if (isGranted) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val readMediaVideo =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO)
        val readMediaAudio =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
        val recordMediaAudio =
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)

        return readMediaVideo == PackageManager.PERMISSION_GRANTED &&
                readMediaAudio == PackageManager.PERMISSION_GRANTED &&
                recordMediaAudio == PackageManager.PERMISSION_GRANTED
    }

    fun checkWritePermissions(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    fun checkReadPermissions(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
            ),
            REQUEST_PERMISSION_CODE
        )
    }

    fun checkRecordPermissions(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.RECORD_AUDIO
        )
        return result == PackageManager.PERMISSION_GRANTED
    }

}