package dev.west.byteverse.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object IdentityCardExporter {

    suspend fun saveCardToGallery(
        context: Context,
        bitmap: Bitmap,
        username: String
    ): Result<Uri> = withContext(Dispatchers.IO) {
        runCatching {
            val safeUser = username.replace(Regex("[^a-zA-Z0-9_-]"), "_")
            val fileName = "BYTEVERSE_${safeUser}_${System.currentTimeMillis()}.png"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    put(
                        MediaStore.Images.Media.RELATIVE_PATH,
                        "${Environment.DIRECTORY_PICTURES}/BYTEVERSE"
                    )
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }

                val imageUri = resolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                ) ?: throw IOException("Unable to create MediaStore entry for identity card.")

                try {
                    resolver.openOutputStream(imageUri)?.use { out ->
                        if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                            throw IOException("Failed to encode identity card PNG.")
                        }
                    } ?: throw IOException("Unable to open output stream for identity card.")

                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    resolver.update(imageUri, contentValues, null, null)
                    imageUri
                } catch (e: Exception) {
                    resolver.delete(imageUri, null, null)
                    throw e
                }
            } else {
                @Suppress("DEPRECATION")
                val picturesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                )
                val appDir = File(picturesDir, "BYTEVERSE").apply {
                    if (!exists()) mkdirs()
                }
                val file = File(appDir, fileName)
                FileOutputStream(file).use { out ->
                    if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                        throw IOException("Failed to encode identity card PNG.")
                    }
                }
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(file.absolutePath),
                    arrayOf("image/png"),
                    null
                )
                val legacyValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    @Suppress("DEPRECATION")
                    put(MediaStore.Images.Media.DATA, file.absolutePath)
                }
                context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    legacyValues
                ) ?: throw IOException("Unable to register identity card in MediaStore.")
            }
        }
    }

    fun shareSavedCard(context: Context, imageUri: Uri, username: String) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            putExtra(
                Intent.EXTRA_TEXT,
                "BYTEVERSE Developer Identity Card for @$username — Explore Your Developer Universe (WEST.DEV)"
            )
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(shareIntent, "Share BYTEVERSE Identity Card")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
