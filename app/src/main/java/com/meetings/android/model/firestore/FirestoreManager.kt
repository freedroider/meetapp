package com.meetings.android.model.firestore

import android.content.Context
import android.location.Location
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.*
import com.meetings.android.entity.Meeting
import com.meetings.android.entity.Request
import com.meetings.android.entity.User
import com.meetings.android.utils.*
import io.reactivex.Single
import org.imperiumlabs.geofirestore.GeoFirestore
import org.imperiumlabs.geofirestore.GeoQueryDataEventListener
import java.lang.Exception
import javax.inject.Inject


private const val GEO_COLLECTION = "geo"
private const val GEO_RADIUS = 0.1

class FirestoreManager @Inject constructor(context: Context) : Firestore {
    private val firestore: FirebaseFirestore
    private val geoFirestore: GeoFirestore

    init {
        FirebaseApp.initializeApp(context)
        firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
        firestore.firestoreSettings = settings
        geoFirestore = GeoFirestore(firestore.collection(GEO_COLLECTION))
    }

    override fun queryUser(user: User): Single<User> {
        return Single.create { emitter ->
            firestore.collection(user.collectionName)
                    .whereEqualTo(user.fieldFullName, user.fullName)
                    .limit(1)
                    .get()
                    .addOnSuccessListener {
                        it.rxFirstOrThrow(user, emitter)
                    }
                    .addOnFailureListener(emitter::onError)
        }
    }

    override fun queryUser(userId: String): Single<User> {
        return Single.create { emitter ->
            val user: User? = null
            firestore.collection(user.collectionName)
                    .document(userId)
                    .get()
                    .addOnSuccessListener {
                        if (it != null && it.exists()) {
                            emitter.onSuccess(it.parse(user))
                        } else {
                            emitter.onError(Exception("User with id: $userId " +
                                    "doesn't exist in Firestore"))
                        }
                    }
                    .addOnFailureListener(emitter::onError)
        }
    }

    override fun insertUser(user: User): Single<User> {
        return Single.create { emitter ->
            firestore.collection(user.collectionName)
                    .add(user)
                    .addOnSuccessListener {
                        user.id = it.id
                        emitter.onSuccess(user)
                    }
                    .addOnFailureListener(emitter::onError)
        }
    }

    override fun insertMeeting(meeting: Meeting): Single<Meeting> {
        val single: Single<Meeting> = Single.create { emitter ->
            firestore.collection(meeting.collectionName)
                    .add(meeting)
                    .addOnSuccessListener {
                        meeting.id = it.id
                        emitter.onSuccess(meeting)
                    }
                    .addOnFailureListener(emitter::onError)
        }
        return single.flatMap {
            insertGeo(it)
        }
    }

    override fun insertRequest(request: Request): Single<Request> {
        return Single.create { emitter ->
            firestore.collection(request.collectionName)
                    .add(request)
                    .addOnSuccessListener {
                        request.id = it.id
                        emitter.onSuccess(request)
                    }
                    .addOnFailureListener(emitter::onError)
        }
    }

    override fun updateMeeting(meeting: Meeting): Single<Meeting> {
        return Single.create { emitter ->
            firestore.collection(meeting.collectionName)
                    .document(meeting.id!!)
                    .set(meeting)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            emitter.onSuccess(meeting)
                        } else {
                            emitter.onError(Exception("something went wrong"))
                        }
                    }
                    .addOnFailureListener(emitter::onError)
        }
    }

    override fun updateUser(user: User): Single<User> {
        return Single.create { emitter ->
            firestore.collection(user.collectionName)
                    .document(user.id!!)
                    .set(user)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            emitter.onSuccess(user)
                        } else {
                            emitter.onError(Exception("something went wrong"))
                        }
                    }
                    .addOnFailureListener(emitter::onError)
        }
    }

    override fun queryCreatedMeetings(user: User): Single<List<Meeting>> {
        return Single.create { emitter ->
            val meeting: Meeting? = null
            firestore.collection(meeting.collectionName)
                    .whereEqualTo(meeting.fieldOwnerId, user.id)
                    .get()
                    .addOnSuccessListener {
                        val meetingList = arrayListOf<Meeting>()
                        it?.documents?.forEach { documentSnapshot ->
                            meetingList.add(documentSnapshot.parse(meeting))
                        }
                        emitter.onSuccess(meetingList)
                    }
                    .addOnFailureListener(emitter::onError)
        }
    }

    override fun queryNearbyMeetings(user: User, location: Location): Single<List<Meeting>> {
        val meeting: Meeting? = null
        val single: Single<List<String>> = queryGeo(location)
        return single.flatMap { meetingIds ->
            queryMeetings(meetingIds, meeting, user)
        }
    }

    override fun queryJoinedMeetings(user: User): Single<List<Meeting>> {
        return Single.create<List<Meeting>> { emitter ->
            val meeting: Meeting? = null
            firestore.collection(meeting.collectionName)
                    .whereArrayContains(meeting.fieldParticipantsIds, user.id!!)
                    .get()
                    .addOnSuccessListener {
                        val meetingList = arrayListOf<Meeting>()
                        it?.documents?.forEach { documentSnapshot ->
                            val meetingFirestore = documentSnapshot.parse(meeting)
                            if (meetingFirestore.ownerId == user.id) return@forEach
                            meetingList.add(meetingFirestore)
                        }
                        emitter.onSuccess(meetingList)
                    }
                    .addOnFailureListener(emitter::onError)
        }
    }

    override fun queryMeeting(meetingId: String): Single<Meeting> {
        return Single.create { emitter ->
            val meeting: Meeting? = null
            firestore.collection(meeting.collectionName)
                    .document(meetingId)
                    .get()
                    .addOnSuccessListener {
                        if (it != null && it.exists()) {
                            emitter.onSuccess(it.parse(meeting))
                        } else {
                            emitter.onError(Exception("Meeting with id: $meetingId " +
                                    "doesn't exist in Firestore"))
                        }
                    }
                    .addOnFailureListener(emitter::onError)
        }
    }

    override fun queryRequest(meeting: Meeting, userId: String): Single<Request> {
        return Single.create { emitter ->
            val request: Request? = null
            firestore.collection(request.collectionName)
                    .whereEqualTo(request.fieldOwnerId, meeting.ownerId)
                    .whereEqualTo(request.fieldParticipantId, userId)
                    .whereEqualTo(request.fieldMeetingId, meeting.id)
                    .limit(1)
                    .get()
                    .addOnSuccessListener {
                        it.rxFirstOrThrow(request, emitter)
                    }
                    .addOnFailureListener(emitter::onError)
        }
    }

    override fun queryRequests(ownerId: String): Single<List<Request>> {
        return Single.create { emitter ->
            val request: Request? = null
            firestore.collection(request.collectionName)
                    .whereEqualTo(request.fieldOwnerId, ownerId)
                    .get()
                    .addOnSuccessListener {
                        val meetingList = arrayListOf<Request>()
                        it?.documents?.forEach { documentSnapshot ->
                            meetingList.add(documentSnapshot.parse(request))
                        }
                        emitter.onSuccess(meetingList)
                    }
                    .addOnFailureListener(emitter::onError)
        }
    }

    override fun deleteRequest(request: Request): Single<Request> {
        return Single.create { emitter ->
            firestore.collection(request.collectionName)
                    .document(request.id!!)
                    .delete()
                    .addOnSuccessListener {
                        request.id = null
                        emitter.onSuccess(request)
                    }
                    .addOnFailureListener(emitter::onError)
        }
    }

    private fun queryMeetings(meetingIds: List<String>, meeting: Meeting?, user: User):
            Single<List<Meeting>>? {
        return Single.create<List<Meeting>> { emitter ->
            if (meetingIds.isEmpty()) {
                emitter.onSuccess(arrayListOf())
                return@create
            }
            val tasks = ArrayList<Task<DocumentSnapshot>>()
            meetingIds.forEach {
                tasks.add(firestore.collection(meeting.collectionName).document(it).get())
            }
            firestore.collection(meeting.collectionName)
                    .get()
                    .continueWithTask(Continuation<QuerySnapshot,
                            Task<List<DocumentSnapshot>>> {
                        Tasks.whenAllSuccess(tasks)
                    })
                    .addOnCompleteListener {
                        emitter.onSuccess(it.parse(meeting, user))
                    }
                    .addOnFailureListener(emitter::onError)
        }
    }

    private fun insertGeo(meeting: Meeting): Single<Meeting> {
        return Single.create { emitter ->
            geoFirestore.setLocation(meeting.id, meeting.location) {
                if (it != null) {
                    emitter.onError(it)
                } else {
                    emitter.onSuccess(meeting)
                }
            }
        }
    }

    private fun queryGeo(location: Location): Single<List<String>> =
            queryGeo(GeoPoint(location.latitude, location.longitude))

    private fun queryGeo(geoPoint: GeoPoint): Single<List<String>> {
        return Single.create { emitter ->
            val documentIds = arrayListOf<String>()
            val geoQuery = geoFirestore.queryAtLocation(geoPoint, GEO_RADIUS)
            val queryDataEventListener: GeoQueryDataEventListener = object
                : GeoQueryDataEventListener {
                override fun onDocumentExited(documentSnapshot: DocumentSnapshot?) {
                }

                override fun onDocumentChanged(documentSnapshot: DocumentSnapshot?, p1: GeoPoint?) {
                }

                override fun onDocumentEntered(documentSnapshot: DocumentSnapshot?, p1: GeoPoint?) {
                    documentSnapshot ?: return
                    documentIds.add(documentSnapshot.id)
                }

                override fun onDocumentMoved(documentSnapshot: DocumentSnapshot?, p1: GeoPoint?) {
                }

                override fun onGeoQueryError(exception: Exception?) {
                    emitter.onError(exception ?: IllegalStateException())
                    geoQuery.removeGeoQueryEventListener(this)
                }

                override fun onGeoQueryReady() {
                    emitter.onSuccess(documentIds)
                    geoQuery.removeGeoQueryEventListener(this)
                }
            }
            geoQuery.addGeoQueryDataEventListener(queryDataEventListener)
        }
    }
}
