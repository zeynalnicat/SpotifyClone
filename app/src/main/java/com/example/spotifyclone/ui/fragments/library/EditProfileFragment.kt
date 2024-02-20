package com.example.spotifyclone.ui.fragments.library

import android.R.attr
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.spotifyclone.R
import com.example.spotifyclone.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject


@AndroidEntryPoint
class EditProfileFragment : Fragment() {
    private lateinit var binding: FragmentEditProfileBinding

    private var image: ByteArray? = null

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    @Inject
    lateinit var firebaseFirestore: FirebaseFirestore

    @Inject
    lateinit var storage: FirebaseStorage

    private lateinit var gender: Any
    private lateinit var name: Any

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater)
        setNavigation()
        setLayout()
        openGallery()
        updateProfile()
        return binding.root
    }

    private fun setNavigation() {
        binding.navBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setLayout() {
        val userRef = firebaseFirestore.collection("users")
        val query = userRef.whereEqualTo("userId", firebaseAuth.currentUser?.uid)

        query.get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot !== null && !querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    gender = document.getString("gender") ?: ""
                    name = document.getString("username") ?: ""
                    val img = document.getString("img")
                    binding.edtName.setText(name.toString())

                    if (img.toString().isEmpty()) {
                        binding.imgProfile.setImageResource(if (gender == "Men") R.drawable.man_icon else R.drawable.woman_icon)
                    } else {
                        Glide.with(binding.root)
                            .load(img.toString())
                            .into(binding.imgProfile)
                    }


                }

            }
    }


    private fun updateProfile() {
        binding.txtSave.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val name = binding.edtName.text.toString()
            val storageRef = storage.reference
            val uuidImg = UUID.randomUUID()
            val imageRef = storageRef.child("profiles/$uuidImg.jpg")
            val userRef = firebaseFirestore.collection("users")
            val query = userRef.whereEqualTo("userId", firebaseAuth.currentUser?.uid)

            image?.let { img ->
                val uploadImg = imageRef.putBytes(img)
                try {
                    uploadImg.addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl
                            .addOnSuccessListener { uri ->
                                val imgUri = uri.toString()
                                query.get()
                                    .addOnSuccessListener { querySnapshot ->
                                        if (!querySnapshot.isEmpty && querySnapshot != null) {
                                            val document = querySnapshot.documents[0]
                                            val user = hashMapOf(
                                                "gender" to gender,
                                                "img" to imgUri,
                                                "userId" to firebaseAuth.currentUser?.uid,
                                                "username" to name
                                            )
                                            document.reference.update(user)
                                                .addOnSuccessListener {
                                                    binding.progressBar.visibility = View.GONE
                                                    findNavController().popBackStack()
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(
                                                        requireContext(),
                                                        e.message,
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    binding.progressBar.visibility = View.GONE
                                                }
                                        }

                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            requireContext(),
                                            e.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        binding.progressBar.visibility = View.GONE
                                    }
                            }
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun openGallery() {
        binding.viewUpdate.setOnClickListener {
            val intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.setType("image/*")
            startActivityForResult(intent, 1)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === 1 && resultCode == RESULT_OK) {
            binding.imgProfile.setImageURI(data?.data)
            val selectedImageUri: Uri? = data?.data
            val bitmap: Bitmap? = selectedImageUri?.let { uri ->
                try {
                    val inputStream = requireActivity().contentResolver.openInputStream(uri)
                    BitmapFactory.decodeStream(inputStream)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
            bitmap?.let { selectedBitmap ->
                val baos = ByteArrayOutputStream()
                selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val byteArray: ByteArray = baos.toByteArray()
                image = byteArray

            }

        }
    }
}


