package com.retrytech.ledgeapp.activity

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.retrytech.ledgeapp.R
import com.retrytech.ledgeapp.adapter.StatusItem
import com.retrytech.ledgeapp.adapter.StatusSaverAdapter
import java.io.File

class StatusSaverActivity : BaseActivity() {

    private lateinit var rvStatuses: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: StatusSaverAdapter

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val granted = result.values.all { it }
            if (granted) {
                loadStatusesFromKnownFolders()
            } else {
                showEmpty(true)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_status_saver)

        rvStatuses = findViewById(R.id.rv_statuses)
        tvEmpty = findViewById(R.id.tv_empty)
        findViewById<ImageView>(R.id.btn_back).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        adapter = StatusSaverAdapter { item ->
            val intent = Intent(this, StatusPreviewActivity::class.java)
            intent.putExtra("status_uri", item.uri.toString())
            intent.putExtra("status_name", item.name)
            intent.putExtra("status_mime", item.mimeType)
            intent.putExtra("status_is_video", item.isVideo)
            startActivity(intent)
        }
        rvStatuses.layoutManager = GridLayoutManager(this, 2)
        rvStatuses.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        if (hasRequiredPermission()) {
            loadStatusesFromKnownFolders()
        } else {
            requestRequiredPermission()
        }
    }

    private fun hasRequiredPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasImages = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
            val hasVideo = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_VIDEO
            ) == PackageManager.PERMISSION_GRANTED
            hasImages && hasVideo
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestRequiredPermission() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO
            )
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        requestPermissionLauncher.launch(permissions)
    }

    private fun loadStatusesFromKnownFolders() {
        val knownFolders = listOf(
            File("/storage/emulated/0/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"),
            File("/storage/emulated/0/Android/media/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses"),
            File("/storage/emulated/0/WhatsApp/Media/.Statuses"),
            File("/storage/emulated/0/WhatsApp Business/Media/.Statuses")
        )

        val fileStatuses = knownFolders
            .filter { it.exists() && it.isDirectory }
            .flatMap { folder -> folder.listFiles()?.toList().orEmpty() }
            .filter { it.isFile && !it.name.startsWith(".") }
            .mapNotNull { file ->
                val name = file.name
                val ext = name.substringAfterLast('.', "").lowercase()
                val isVideo = ext in setOf("mp4", "mov", "3gp", "mkv", "webm")
                val isImage = ext in setOf("jpg", "jpeg", "png", "webp")
                if (!isVideo && !isImage) return@mapNotNull null
                StatusItem(
                    uri = android.net.Uri.fromFile(file),
                    name = name,
                    mimeType = if (isVideo) "video/mp4" else "image/jpeg",
                    isVideo = isVideo,
                    lastModified = file.lastModified()
                )
            }
        val mediaStoreStatuses = loadStatusesFromMediaStore()

        val statuses = (fileStatuses + mediaStoreStatuses)
            .distinctBy { "${it.name}_${it.lastModified}" }
            .sortedByDescending { it.lastModified }

        adapter.submitData(statuses)
        showEmpty(statuses.isEmpty())
    }

    private fun loadStatusesFromMediaStore(): List<StatusItem> {
        val result = ArrayList<StatusItem>()
        val collection = android.provider.MediaStore.Files.getContentUri("external")
        val projection = arrayOf(
            android.provider.MediaStore.Files.FileColumns._ID,
            android.provider.MediaStore.Files.FileColumns.DISPLAY_NAME,
            android.provider.MediaStore.Files.FileColumns.MIME_TYPE,
            android.provider.MediaStore.Files.FileColumns.DATE_MODIFIED,
            android.provider.MediaStore.Files.FileColumns.RELATIVE_PATH
        )
        val selection = "(" +
            "${android.provider.MediaStore.Files.FileColumns.RELATIVE_PATH} LIKE ? OR " +
            "${android.provider.MediaStore.Files.FileColumns.RELATIVE_PATH} LIKE ? OR " +
            "${android.provider.MediaStore.Files.FileColumns.RELATIVE_PATH} LIKE ? OR " +
            "${android.provider.MediaStore.Files.FileColumns.RELATIVE_PATH} LIKE ?" +
            ") AND (" +
            "${android.provider.MediaStore.Files.FileColumns.MIME_TYPE} LIKE 'image/%' OR " +
            "${android.provider.MediaStore.Files.FileColumns.MIME_TYPE} LIKE 'video/%'" +
            ")"
        val args = arrayOf(
            "Android/media/com.whatsapp/WhatsApp/Media/.Statuses/%",
            "Android/media/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses/%",
            "WhatsApp/Media/.Statuses/%",
            "WhatsApp Business/Media/.Statuses/%"
        )
        val sort = "${android.provider.MediaStore.Files.FileColumns.DATE_MODIFIED} DESC"

        contentResolver.query(collection, projection, selection, args, sort)?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Files.FileColumns._ID)
            val nameCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Files.FileColumns.DISPLAY_NAME)
            val mimeCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Files.FileColumns.MIME_TYPE)
            val dateCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Files.FileColumns.DATE_MODIFIED)
            val relCol = cursor.getColumnIndexOrThrow(android.provider.MediaStore.Files.FileColumns.RELATIVE_PATH)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val name = cursor.getString(nameCol) ?: continue
                if (name.startsWith(".")) continue
                val mime = cursor.getString(mimeCol) ?: continue
                val relative = cursor.getString(relCol) ?: continue
                if (!relative.contains("/.Statuses/")) continue
                val lastModifiedMillis = cursor.getLong(dateCol) * 1000L
                val isVideo = mime.startsWith("video/")
                val uri: Uri = ContentUris.withAppendedId(collection, id)
                result.add(
                    StatusItem(
                        uri = uri,
                        name = name,
                        mimeType = mime,
                        isVideo = isVideo,
                        lastModified = lastModifiedMillis
                    )
                )
            }
        }
        return result
    }
    private fun showEmpty(show: Boolean) {
        tvEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }
}
