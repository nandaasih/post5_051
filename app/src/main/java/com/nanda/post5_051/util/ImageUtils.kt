package com.nanda.post5_051.util

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.OutputStream

object ImageUtils {
    // Simpan bitmap ke MediaStore dan kembalikan Uri string
    fun saveBitmapToMediaStore(context: Context, filename: String, bitmap: Bitmap): String? {
        val resolver: ContentResolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            else
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        val itemUri: Uri? = resolver.insert(collection, contentValues)
        itemUri?.let { uri ->
            var out: OutputStream? = null
            try {
                out = resolver.openOutputStream(uri)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            } finally {
                out?.close()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
            return uri.toString()
        }
        return null
    }
}
