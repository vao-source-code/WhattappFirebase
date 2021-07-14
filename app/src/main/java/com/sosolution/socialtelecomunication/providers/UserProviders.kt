package com.sosolution.socialtelecomunication.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.sosolution.socialtelecomunication.models.User

class UserProviders {

    private val COLECTION_USERS = "Users"
    var mCollection: CollectionReference =
        FirebaseFirestore.getInstance().collection(COLECTION_USERS)


    public fun getUser(id: String): Task<DocumentSnapshot> {
        return mCollection.document(id).get()
    }

    public fun create(user : User): Task<Void> {
        return mCollection.document(user.id).set(user)
    }

}