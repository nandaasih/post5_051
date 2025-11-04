package com.nanda.post5_051.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.igclone.data.db.Post
import com.example.igclone.data.db.PostDatabase
import com.example.igclone.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: PostRepository

    init {
        val db = PostDatabase.getInstance(application)
        repo = PostRepository(db.postDao())
    }

    val posts: LiveData<List<Post>> = repo.allPosts().asLiveData()

    fun insert(post: Post, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.insert(post)
            onDone?.invoke()
        }
    }

    fun update(post: Post, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.update(post)
            onDone?.invoke()
        }
    }

    fun delete(post: Post, onDone: (() -> Unit)? = null) {
        viewModelScope.launch {
            repo.delete(post)
            onDone?.invoke()
        }
    }
}
