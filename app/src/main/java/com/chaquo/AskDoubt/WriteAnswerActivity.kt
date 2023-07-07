package com.chaquo.AskDoubt

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.PyException
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import java.text.DateFormat
import java.util.*

class WriteAnswerActivity : AppCompatActivity() {
    private var editText: EditText? = null
    private var activityResultLauncher: ActivityResultLauncher<String>? = null
    private var imageView: ImageView? = null
    private var imageUri: Uri? = null
    private var post: Button? = null
    private var storageReference: StorageReference? = null
    private var uploadTask: StorageTask<*>? = null
    private var pd: ProgressDialog? = null
    private var dialog: ProgressDialog? =null

    private var ansBy: String? = null
    private var myURL: String? = null
    private var onlineUser: String? = null
    private var ansByRef: DatabaseReference? = null
    private var postid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            imageView!!.setImageURI(uri)
            imageUri = uri
        }
        setContentView(R.layout.activity_write_answer)
        imageView = findViewById(R.id.answerImage)
        editText = findViewById(R.id.editTextTextMultiLine)
        post = findViewById(R.id.postAns)
        pd = ProgressDialog(this)
dialog= ProgressDialog(this)
        onlineUser= FirebaseAuth.getInstance().currentUser?.uid
storageReference=FirebaseStorage.getInstance().getReference().child("questions")
        ansByRef = FirebaseDatabase.getInstance().getReference("users").child(onlineUser!!)
        postid = intent.getStringExtra("postId")
        ansByRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ansBy = snapshot.child("fullname").getValue(String::class.java)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        imageView!!.setOnClickListener { view: View? ->
            activityResultLauncher!!.launch(
                "image/*"
            )
        }
        post!!.setOnClickListener { view: View? ->
            run {

                performValidations()



            }
        }
    }

    var date = DateFormat.getDateInstance().format(Date())
    var ref = FirebaseDatabase.getInstance().getReference("answers")
    private fun performValidations() {
        val ans = editText!!.text.toString().trim { it <= ' ' }
        if (ans.isEmpty()) {
            editText!!.error = "Answer Required "
           // pd!!.dismiss()
        }
        if (!ans.isEmpty()) {
            startProgress()
            extractKeywords(ans)
            if (imageUri == null) uploadDataNoImage() else uploadData()
        }
    }

    private fun extractKeywords(text: String) {


        if (! Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        val py = Python.getInstance()
        val module = py.getModule("plot")

        try {

            val txt=module.callAttr("plot",text)




            fun stringToWords(mnemonic: String) = mnemonic.replace("\\s+".toRegex(), " ").trim().split("~")
           var tagList= stringToWords(txt.toString())
            Log.d("Tagss",txt.toString())


            val ref = FirebaseDatabase.getInstance().getReference("tags")

            for (tg in tagList){

                ref.child(tg).child(postid!!).setValue(true)

            }


            currentFocus?.let {
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(it.windowToken, 0)}

            pd?.dismiss()

        } catch (e: PyException) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            pd?.dismiss()
        }



    }
    fun startProgress() {
        pd!!.setMessage("Extracting keywords")
        pd!!.setCanceledOnTouchOutside(false)
        pd!!.show()
    }

    private fun getFileExtension(uri: Uri?): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    private fun uploadData() {
        startProgress()
        val fileReference = storageReference!!.child(
            System.currentTimeMillis().toString() + "." + getFileExtension(imageUri)
        )
        uploadTask = fileReference.putFile(imageUri!!)
        (uploadTask as UploadTask).continueWithTask { task: Task<*> ->
            if (!task.isComplete) {
                throw task.exception!!
            }
            fileReference.downloadUrl
        }.addOnCompleteListener { task: Task<*> ->
            if (task.isSuccessful) {
                val downloadUri = task.result as Uri
                myURL = downloadUri.toString()
                val hashMap = HashMap<String, Any?>()
                hashMap["postId"] = postid
                hashMap["answer"] = editText!!.text.toString().trim { it <= ' ' }
                hashMap["publisher"] = onlineUser
                hashMap["answerImage"] = myURL
                hashMap["ansby"] = ansBy
                hashMap["date"] = date
                assert(postid != null)
                var abc=postid!!
                var key=ref.child(postid!!).push().key
                ref.child(postid!!).child(key!!).setValue(hashMap).addOnCompleteListener { task1: Task<Void?> ->
                    if (task1.isSuccessful) {
                        Toast.makeText(
                            this@WriteAnswerActivity,
                            "Answer Posted Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        pd!!.dismiss()
                        startActivity(Intent(this@WriteAnswerActivity, HomeActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@WriteAnswerActivity,
                            "could not upload",
                            Toast.LENGTH_SHORT
                        ).show()
                        pd!!.dismiss()
                    }
                }
            }
        }.addOnFailureListener { e: Exception? ->
            Toast.makeText(
                this@WriteAnswerActivity,
                "Failed to upload answer",
                Toast.LENGTH_SHORT

            ).show()
            pd!!.dismiss()
        }
    }

    private fun uploadDataNoImage() {
       // startProgress()

        val hashMap = HashMap<String, Any?>()
        hashMap["postId"] = postid
        hashMap["answer"] = editText!!.text.toString().trim { it <= ' ' }
        hashMap["publisher"] = onlineUser
        hashMap["ansby"] = ansBy
        hashMap["date"] = date
        assert(postid != null)
        var pkey=ref.child(postid!!).push().key;
        ref.child(postid!!).child(pkey!!).setValue(hashMap).addOnCompleteListener { task: Task<Void?> ->
            if (task.isSuccessful) {
                Toast.makeText(
                    this@WriteAnswerActivity,
                    "Answer Posted Successfully",
                    Toast.LENGTH_SHORT
                ).show()
                pd!!.dismiss()
                startActivity(Intent(this@WriteAnswerActivity, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this@WriteAnswerActivity, "could not upload", Toast.LENGTH_SHORT)
                    .show()
                pd!!.dismiss()
            }
        }
    }
}


