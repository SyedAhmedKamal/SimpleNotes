package com.example.simplenotes.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.simplenotes.R
import com.example.simplenotes.databinding.FragmentUpdateNoteBinding
import com.example.simplenotes.model.Note
import com.example.simplenotes.ui.MainActivity
import com.example.simplenotes.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*


class UpdateNoteFragment : Fragment() {

    private var _binding: FragmentUpdateNoteBinding? = null
    private val binding get() = _binding!!

    private val args: UpdateNoteFragmentArgs by navArgs()
    private lateinit var currentNote: Note
    private lateinit var notesViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
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

        binding.updateNoteTitle.setText(currentNote.noteTitle)
        binding.updateNoteDescription.setText(currentNote.noteDescription)
        binding.updateNoteImageView.load(currentNote.imageFilePath)

        binding.displayTimeStamp.text = currentNote.timeStamp

        binding.fabDoneUpdate.setOnClickListener {

            val title = binding.updateNoteTitle.text.toString().trim()
            val description = binding.updateNoteDescription.text.toString().trim()
            val imagePath = currentNote.imageFilePath
            val sdf = SimpleDateFormat("dd MMM, yyyy - HH:mm")
            val updatedTimeStamp: String = "Edited: " + sdf.format(Date())

            if (title.isNotEmpty()) {

                val note = Note(currentNote.id, title, description, imagePath, updatedTimeStamp)
                notesViewModel.updateNote(note)
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
                deleteNote()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteNote() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}