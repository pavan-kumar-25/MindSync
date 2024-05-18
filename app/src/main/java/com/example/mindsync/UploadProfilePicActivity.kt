package com.example.mindsync

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class UploadProfilePicActivity : AppCompatActivity() {

    private lateinit var progressBar : ProgressBar
    private lateinit var imageViewUploadPic : ImageView
    private lateinit var authProfile : FirebaseAuth
    private lateinit var storageReference : StorageReference
    private lateinit var firebaseUser : FirebaseUser
    private var uriImage : Uri = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_profile_pic)

        val buttonUploadPicChoose = findViewById<Button>(R.id.upload_pic_choose_button)
        val buttonUploadPic = findViewById<Button>(R.id.upload_pic_button)
        progressBar = findViewById(R.id.progressBar)
        imageViewUploadPic = findViewById(R.id.imageView_profile_dp)

        authProfile = FirebaseAuth.getInstance()
        firebaseUser = authProfile.currentUser!!

        storageReference = FirebaseStorage.getInstance().getReference("DisplayPics")

        val uri: Uri? = firebaseUser.photoUrl

        //Set User's current DP in ImageView (if uploaded already. We will use Picasso
        Picasso.with(this@UploadProfilePicActivity).load(uri).into(imageViewUploadPic)

        buttonUploadPicChoose.setOnClickListener(){
            openFileChooser()
        }

        buttonUploadPic.setOnClickListener(){
            progressBar.visibility = View.VISIBLE
            uploadPic()
        }
    }

    private fun openFileChooser(){
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun uploadPic(){
        if (uriImage != Uri.EMPTY){
            //Save the image with uid of the currently logged user
            val fileReference : StorageReference = storageReference.child("${firebaseUser.uid}.${getFileExtension(uriImage)}")

            //Upload image to Storage
            fileReference.putFile(uriImage).addOnSuccessListener {
                fileReference.downloadUrl.addOnSuccessListener {uri ->
                    val downloadUri : Uri = uri

                    //Finally set the display image of the user after upload
                    val profileUpdates : UserProfileChangeRequest = UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build()
                    firebaseUser.updateProfile(profileUpdates).addOnCompleteListener{task ->
                        if(task.isSuccessful) {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this@UploadProfilePicActivity, "Upload Successful!", Toast.LENGTH_LONG).show()

                            val intentUserProfileActivity = Intent(this@UploadProfilePicActivity, ProfileActivity::class.java)
                            startActivity(intentUserProfileActivity)
                            finish()
                        }else{
                            Toast.makeText(this@UploadProfilePicActivity,  task.exception?.message ?: "Upload failed", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }else{
            progressBar.visibility = View.GONE
            Toast.makeText(this@UploadProfilePicActivity, "No File Selected!", Toast.LENGTH_LONG).show()
        }
    }

    //Obtain File Extension of the image
    private fun getFileExtension(uri: Uri) : String? {
        val cR : ContentResolver = contentResolver
        val mime : MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!=null && data.data !=null){
            uriImage = data.data!!
            imageViewUploadPic.setImageURI(uriImage)
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}