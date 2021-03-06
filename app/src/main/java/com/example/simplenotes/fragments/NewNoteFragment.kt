package com.example.simplenotes.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.simplenotes.R
import com.example.simplenotes.databinding.FragmentNewNoteBinding
import com.example.simplenotes.model.Note
import com.example.simplenotes.ui.MainActivity
import com.example.simplenotes.viewmodel.NoteViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class NewNoteFragment : Fragment(R.layout.fragment_new_note) {

    private var _binding: FragmentNewNoteBinding? = null
    private val binding get() = _binding!!
    private lateinit var notesViewModel: NoteViewModel
    private lateinit var mView: View
    private lateinit var activityResultLauncher: ActivityResultLauncher<String>
    private var file: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        selectImageFromDevice()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel = (activity as MainActivity).noteViewModel
        mView = view



        binding.newImageFromDevice.setOnClickListener {

            activityResultLauncher.launch("image/*")

        }
    }

    private fun selectImageFromDevice() {

        pickImage()

    }

    private fun pickImage() {

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->

                if (imageUri != null) {

                    file = saveToFile(imageUri)

                    file?.let {

                        binding.newNoteImageView.visibility = View.VISIBLE
                        binding.newNoteImageView.setImageBitmap(BitmapFactory.decodeFile(file?.absolutePath))
                        Log.d("MyTag", "fileJob: ${file?.absolutePath}")

                    }

                }
            }

    }

    private fun saveToFile(imageUri: Uri): File? {

        val imageStream = context?.applicationContext
            ?.contentResolver?.openInputStream(imageUri)

        val bitmap = BitmapFactory.decodeStream(imageStream)

        try {

            file = File(
                context?.applicationContext
                    ?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString() + File.separator + "${System.currentTimeMillis()}.jpg"
            )

            file?.createNewFile()

            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            val bitmapData = bos.toByteArray()

            val fos = FileOutputStream(file)
            fos.write(bitmapData)
            fos.flush()
            fos.close()

        } catch (e: Exception) {
            Log.d("MyTag", "selectImageFromDevice: ${e.printStackTrace()}")
        }

        return file
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.new_note_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.save_note -> {
                saveNote(mView)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveNote(mView: View) {

        val title = binding.newNoteTitle.text.toString().trim()
        val description = binding.newNoteDescription.text.toString().trim()
        val imagePath = file?.absolutePath
        val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm")
        val timeStamp: String = "Edited: " + sdf.format(Date())
        Log.d("MyTag", "saveNote: $timeStamp")

        if (title.isNotEmpty()) {
            val note = Note(0, title, description, imagePath, timeStamp)
            notesViewModel.addNote(note)
            Toast.makeText(context?.applicationContext, "Note saved", Toast.LENGTH_LONG).show()
            val directions = NewNoteFragmentDirections.actionNewNoteFragmentToHomeFragment()
            findNavController().navigate(directions)
        } else {
            Toast.makeText(
                context?.applicationContext,
                "Empty note is not saved",
                Toast.LENGTH_LONG
            ).show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}