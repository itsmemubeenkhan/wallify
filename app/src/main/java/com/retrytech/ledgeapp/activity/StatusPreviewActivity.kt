package com.retrytech.ledgeapp.activity

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.retrytech.ledgeapp.R
import java.io.File
import java.io.FileOutputStream

class StatusPreviewActivity : BaseActivity() {

    private lateinit var imgStatus: ImageView
    private lateinit var videoStatus: VideoView
    private lateinit var btnDownload: TextView
    private var statusUri: Uri? = null
    private var statusName: String = ""
    private var statusMime: String = ""
    private var isVideo: Boolean = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                saveStatus()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_preview)

        imgStatus = findViewById(R.id.img_status)
        videoStatus = findViewById(R.id.video_status)
        btnDownload = findViewById(R.id.btn_download)
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        statusUri = intent.getStringExtra("status_uri")?.let { Uri.parse(it) }
        statusName = intent.getStringExtra("status_name").orEmpty()
        statusMime = intent.getStringExtra("status_mime").orEmpty()
        isVideo = intent.getBooleanExtra("status_is_video", false)

        if (statusUri == null) {
            finish()
            return
        }

        if (isVideo) {
            imgStatus.visibility = View.GONE
            videoStatus.visibility = View.VISIBLE
            videoStatus.setVideoURI(statusUri)
            videoStatus.setOnPreparedListener { mp ->
                mp.isLooping = true
                videoStatus.start()
            }
        } else {
            videoStatus.visibility = View.GONE
            imgStatus.visibility = View.VISIBLE
            Glide.with(this).load(statusUri).into(imgStatus)
        }

        btnDownload.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                saveStatus()
            }
        }
    }

    private fun saveStatus() {
        val uri = statusUri ?: return
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val collection = if (isVideo) {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                val relativePath = if (isVideo) {
                    "${Environment.DIRECTORY_MOVIES}/StatusSaver"
                } else {
                    "${Environment.DIRECTORY_PICTURES}/StatusSaver"
                }
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, statusName)
                    put(MediaStore.MediaColumns.MIME_TYPE, statusMime.ifEmpty { if (isVideo) "video/mp4" else "image/jpeg" })
                    put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
                }
                val outUri = contentResolver.insert(collection, values)
                    ?: throw IllegalStateException("Failed to create media uri")
                contentResolver.openInputStream(uri).use { input ->
                    contentResolver.openOutputStream(outUri).use { output ->
                        requireNotNull(input)
                        requireNotNull(output)
                        input.copyTo(output)
                    }
                }
            } else {
                val parentDir = if (isVideo) {
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
                } else {
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                }
                val outDir = File(parentDir, "StatusSaver")
                if (!outDir.exists()) outDir.mkdirs()
                val outFile = File(outDir, statusName.ifEmpty { "status_${System.currentTimeMillis()}" })
                contentResolver.openInputStream(uri).use { input ->
                    FileOutputStream(outFile).use { output ->
                        requireNotNull(input)
                        input.copyTo(output)
                    }
                }
            }
        }.onSuccess {
            Toast.makeText(this, getString(R.string.status_saved), Toast.LENGTH_SHORT).show()
        }.onFailure {
            Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        if (isVideo) {
            videoStatus.pause()
        }
    }
}
