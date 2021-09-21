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
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.simplenotes.R
import com.example.simplenotes.databinding.FragmentUpdateNoteBinding
import com.example.simplenotes.model.Note
import com.example.simplenotes.ui.MainActivity
import com.example.simplenotes.viewmodel.NoteViewModel
import dev.shreyaspatil.MaterialDialog.AbstractDialog
import java.text.SimpleDateFormat
import java.util.*
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class UpdateNoteFragment : Fragment() {

    private var _binding: FragmentUpdateNoteBinding? = null
    private val binding get() = _binding!!

    private val args: UpdateNoteFragmentArgs by navArgs()
    private lateinit var currentNote: Note
    private lateinit var notesViewModel: NoteViewModel

    private lateinit var activityResultLauncher: ActivityResultLauncher<String>
    private var updateFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
        selectUpdatedImageFromDevice()
    }

    private fun selectUpdatedImageFromDevice() {
        pickUpdatedImage()
    }

    private fun pickUpdatedImage() {


        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->

                if (imageUri != null) {

                    updateFile = saveToFileUpdated(imageUri)

                    updateFile?.let {

                        binding.updateNoteImageView.visibility = View.VISIBLE
                        binding.updateNoteImageView.setImageBitmap(BitmapFactory.decodeFile(updateFile?.absolutePath))
                        Log.d("MyTag", "fileJob: ${updateFile?.absolutePath}")

                    }

                }
            }

    }

    private fun saveToFileUpdated(imageUri: Uri): File? {

        val imageStream = context?.applicationContext
            ?.contentResolver?.openInputStream(imageUri)

        val bitmap = BitmapFactory.decodeStream(imageStream)

        try {

            updateFile = File(
                context?.applicationContext
                    ?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString() + File.separator + "${System.currentTimeMillis()}.jpg"
            )

            updateFile?.createNewFile()

            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            val bitmapData = bos.toByteArray()

            val fos = FileOutputStream(updateFile)
            fos.write(bitmapData)
            fos.flush()
            fos.close()

        } catch (e: Exception) {
            Log.d("MyTag", "selectImageFromDevice: ${e.printStackTrace()}")
        }

        return updateFile

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notesViewModel = (activity as MainActivity).noteViewModel
        currentNote = args.note!!

        binding.updateImageFromDevice.setOnClickListener {

            activityResultLauncher.launch("image/*")

        }

        binding.updateNoteTitle.setText(currentNote.noteTitle)
        binding.updateNoteDescription.setText(currentNote.noteDescription)
        binding.updateNoteImageView.load(currentNote.imageFilePath)
        binding.displayTimeStamp.text = currentNote.timeStamp

        // Existing image file path
        currentNote.imageFilePath?.let { nonNullPath ->

            Log.d("MyTag", "Update path: ${currentNote.imageFilePath}")
            binding.updateNoteImageView.visibility = View.VISIBLE
            binding.updateNoteImageView.setImageBitmap(BitmapFactory.decodeFile(nonNullPath))

        }

        if (updateFile?.absolutePath!=null){
            Log.d("MyTag", "Update path: ${updateFile?.absolutePath}")
            binding.updateNoteImageView.visibility = View.VISIBLE
            binding.updateNoteImageView.setImageBitmap(BitmapFactory.decodeFile(updateFile?.absolutePath))
        }

        binding.fabDoneUpdate.setOnClickListener {

            val title = binding.updateNoteTitle.text.toString().trim()
            val description = binding.updateNoteDescription.text.toString().trim()
            val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm")
            val updatedTimeStamp: String = "Edited: " + sdf.format(Date())

            if (title.isNotEmpty()) {

                val note = Note(currentNote.id, title, description, updateFile?.absolutePath, updatedTimeStamp)
                notesViewModel.updateNote(note)

                val directions =
                    UpdateNoteFragmentDirections.actionUpdateNoteFragmentToHomeFragment()
                it.findNavController().navigate(directions)

            } else {
                Toast.makeText(context?.applicationContext, "Empty note", Toast.LENGTH_LONG).show()
            }

        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        menu.clear()
        inflater.inflate(R.menu.update_note_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.delete_note -> {


                val mDialog = MaterialDialog.Builder(requireActivity())
                    .setTitle("Delete Note?")
                    .setMessage("Are you sure want to delete this note?")
                    .setCancelable(false)
                    .setPositiveButton("Delete") { dialogInterface, which ->
                        deleteNote()
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialogInterface, which ->
                        dialogInterface.dismiss()
                    }
                    .build()
                mDialog.show()

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteNote() {

        val directions = UpdateNoteFragmentDirections.actionUpdateNoteFragmentToHomeFragment()
        findNavController().navigate(directions)
        notesViewModel.deleteNote(currentNote)
        Toast.makeText(context?.applicationContext, "Note Deleted", Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}