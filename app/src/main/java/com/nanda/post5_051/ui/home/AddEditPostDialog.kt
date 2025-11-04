package com.nanda.post5_051.ui.home

import android.app.AlertDialog
import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.*
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nanda.post5_051.R
import com.nanda.post5_051.databinding.FragmentHomeBinding
import com.nanda.post5_051.ui.home.adapters.PostAdapter
import com.nanda.post5_051.ui.home.adapters.StoryAdapter
import com.nanda.post5_051.util.ImageUtils
import com.nanda.post5_051.viewmodel.PostViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import android.app.Dialog
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.nanda.post5_051.databinding.DialogAddEditPostBinding


class AddEditPostDialog(
    private val initial: Post?,
    private val listener: (Post?, Uri?) -> Unit // (postToUpdateOrNull, pickedUri)
) : DialogFragment() {

    private var _b: DialogAddEditPostBinding? = null
    private val b get() = _b!!

    private var selectedUri: Uri? = null

    private val pickLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            // persist permission
            requireContext().contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            selectedUri = it
            Glide.with(b.imgPreview).load(it).into(b.imgPreview)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _b = DialogAddEditPostBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(b.root)
        dialog.setCancelable(true)

        // fill if editing
        initial?.let {
            b.etCaption.setText(it.caption)
            Glide.with(b.imgPreview).load(it.imageUri).into(b.imgPreview)
        }

        b.btnPickImage.setOnClickListener {
            // pick images
            pickLauncher.launch(arrayOf("image/*"))
        }

        b.btnCancel.setOnClickListener { dialog.dismiss() }

        b.btnSave.setOnClickListener {
            val caption = b.etCaption.text.toString().trim()
            // If user selected new image -> return selectedUri
            listener(initial?.copy(caption = caption) , selectedUri)
            dialog.dismiss()
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
