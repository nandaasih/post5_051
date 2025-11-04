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

class HomeFragment : Fragment() {

    private var _b: FragmentHomeBinding? = null
    private val b get() = _b!!
    private val vm: PostViewModel by viewModels({ requireActivity() })

    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _b = FragmentHomeBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // === STORIES (contoh statis) ===
        val stories = List(10) { "story_$it" }
        b.rvStories.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        b.rvStories.adapter = StoryAdapter(stories)

        // === POSTS ===
        postAdapter = PostAdapter(mutableListOf()) { post ->
            showPostMenu(post)
        }
        b.rvPosts.layoutManager = LinearLayoutManager(requireContext())
        b.rvPosts.adapter = postAdapter

        vm.posts.observe(viewLifecycleOwner) { list ->
            postAdapter.updateList(list)
        }

        // === FAB: tambah post baru ===
        b.fabAdd.setOnClickListener {
            val dlg = AddEditPostDialog(null) { _, pickedUri ->
                if (pickedUri != null) {
                    savePickedUriAsPermanent(pickedUri) { savedUriString ->
                        savedUriString?.let {
                            val post = Post(imageUri = it, caption = "")
                            vm.insert(post)
                        }
                    }
                }
            }
            dlg.show(childFragmentManager, "add")
        }
    }

    // === Popup Edit / Hapus ===
    private fun showPostMenu(post: Post) {
        val popup = PopupMenu(requireContext(), requireActivity().findViewById(android.R.id.content))
        popup.menu.add("Edit")
        popup.menu.add("Hapus")
        popup.setOnMenuItemClickListener { mi ->
            when (mi.title) {
                "Edit" -> {
                    val dlg = AddEditPostDialog(post) { updatedPost, newUri ->
                        lifecycleScope.launch {
                            if (newUri != null) {
                                savePickedUriAsPermanent(newUri) { savedUri ->
                                    savedUri?.let {
                                        val up = post.copy(
                                            imageUri = it,
                                            caption = updatedPost?.caption ?: post.caption
                                        )
                                        vm.update(up)
                                    }
                                }
                            } else {
                                val up = post.copy(caption = updatedPost?.caption ?: post.caption)
                                vm.update(up)
                            }
                        }
                    }
                    dlg.show(childFragmentManager, "edit")
                }

                "Hapus" -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Hapus post?")
                        .setMessage("Yakin menghapus post ini?")
                        .setPositiveButton("Ya") { _, _ -> vm.delete(post) }
                        .setNegativeButton("Batal", null)
                        .show()
                }
            }
            true
        }
        popup.show()
    }

    // === Simpan gambar ke MediaStore agar permanen ===
    private fun savePickedUriAsPermanent(pickedUri: Uri, callback: (String?) -> Unit) {
        lifecycleScope.launch {
            val resolver = requireContext().contentResolver
            val displayName = queryDisplayName(resolver, pickedUri) ?: "post_${System.currentTimeMillis()}.jpg"
            val input: InputStream? = resolver.openInputStream(pickedUri)
            val result = withContext(Dispatchers.IO) {
                if (input != null) {
                    val bm = BitmapFactory.decodeStream(input)
                    ImageUtils.saveBitmapToMediaStore(requireContext(), displayName, bm)
                } else null
            }
            callback(result)
        }
    }

    // === Ambil nama file dari URI ===
    private fun queryDisplayName(resolver: ContentResolver, uri: Uri): String? {
        var name: String? = null
        resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { c ->
            if (c.moveToFirst()) {
                name = c.getString(0)
            }
        }
        return name
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }
}
