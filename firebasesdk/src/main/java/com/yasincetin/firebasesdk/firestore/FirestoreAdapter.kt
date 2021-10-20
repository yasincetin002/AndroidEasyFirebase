package com.yasincetin.firebasesdk.firestore

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*

abstract class FirestoreAdapter<VH : RecyclerView.ViewHolder, M : FirestoreModel>(private val query: Query?) :
    RecyclerView.Adapter<VH>(), EventListener<QuerySnapshot> {

    abstract fun getModelClass():Class<M>

    private val snapshots = arrayListOf<DocumentSnapshot>()

    override fun getItemCount() = snapshots.size

    init {
        query?.let {
            addQuery(query)
        }
    }

    private fun getSnapshot(position: Int) = snapshots[position]

    fun getItem(position: Int) = getSnapshot(position).toObject(getModelClass())

    override fun onEvent(querySnapshot: QuerySnapshot?, error: FirebaseFirestoreException?) {
        querySnapshot?.documentChanges?.forEach { document ->
            when (document.type) {
                DocumentChange.Type.ADDED -> onDocumentAdded(document)
            }
        }

        onDataChange()
    }

    fun onDataChange() {

    }

    private fun onDocumentAdded(documentChange: DocumentChange?) {
        documentChange?.let {
            snapshots.add(documentChange.newIndex, documentChange.document)
        }
    }

    private fun addQuery(query: Query) {
        query.get().addOnSuccessListener { querySnapshot ->
            snapshots.addAll(querySnapshot)
            notifyDataSetChanged()
        }
    }
}