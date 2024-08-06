package com.example.aivoicechanger.activities

import android.Manifest
import android.Manifest.permission.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.aivoicechanger.MainActivity
import com.example.aivoicechanger.adapters.VideoTabAdapter
import com.example.aivoicechanger.databinding.ActivityVideoToAudioBinding
import com.google.android.material.tabs.TabLayout

class VideoToAudio : AppCompatActivity() {
    val binding: ActivityVideoToAudioBinding by lazy {
        ActivityVideoToAudioBinding.inflate(layoutInflater)
    }
    val REQUEST_CODE_PERMISSIONS = 1001

    val REQUEST_PERMISSION_CODE = 1
    val permissions = arrayOf(
        CAMERA,
        RECORD_AUDIO,
        WRITE_EXTERNAL_STORAGE
    )

    var audioPath: String = "null"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if (!checkPermissions()) {
                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA
                    )
                )
            }
        } else {
            if (!checkReadPermissions() || !checkWritePermissions() || !checkRecordPermissions() || !checkCameraPermissions()) {
                requestPermissions()
            }
        }
        binding.tablayout.addTab(binding.tablayout.newTab().setText("Record Video"))
        binding.tablayout.addTab(binding.tablayout.newTab().setText("Upload Video"))
        binding.tablayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = VideoTabAdapter(this, supportFragmentManager, binding.tablayout.tabCount)
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

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }


    override fun onBackPressed() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun checkCameraPermissions(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.CAMERA
        )
        return result == PackageManager.PERMISSION_GRANTED
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
        val cameraMediaAudio =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)

        return readMediaVideo == PackageManager.PERMISSION_GRANTED &&
                readMediaAudio == PackageManager.PERMISSION_GRANTED &&
                recordMediaAudio == PackageManager.PERMISSION_GRANTED &&
                cameraMediaAudio == PackageManager.PERMISSION_GRANTED
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
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
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

    override fun onResume() {
        super.onResume()
        audioPath = intent.extras!!.getString("videoPath") ?: audioPath

        Handler(Looper.getMainLooper()).postDelayed({
            if (audioPath != "null") {
                val intent = Intent("VIDEO_PATH")
                intent.putExtra("videoPath", audioPath)
                sendBroadcast(intent)
            }
        }, 1000)
    }

}