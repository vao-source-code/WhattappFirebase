package com.sosolution.socialtelecomunication.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.sosolution.socialtelecomunication.models.Post

class PostProvider {

     var mCollection : CollectionReference = FirebaseFirestore.getInstance().collection("Posts")

    fun save (post :Post): Task<Void> {
        return mCollection.document().set(post)
    }


}