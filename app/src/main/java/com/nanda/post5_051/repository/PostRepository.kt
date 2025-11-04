package com.nanda.post5_051.repository

import com.example.igclone.data.db.Post
import com.example.igclone.data.db.PostDao
import kotlinx.coroutines.flow.Flow

class PostRepository(private val dao: PostDao) {
    fun allPosts(): Flow<List<Post>> = dao.getAll()
    suspend fun insert(post: Post) = dao.insert(post)
    suspend fun update(post: Post) = dao.update(post)
    suspend fun delete(post: Post) = dao.delete(post)
}