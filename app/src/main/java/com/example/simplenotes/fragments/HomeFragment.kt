package com.example.simplenotes.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.simplenotes.R
import com.example.simplenotes.adapter.NoteAdapter
import com.example.simplenotes.databinding.FragmentHomeBinding
import com.example.simplenotes.ui.MainActivity
import com.example.simplenotes.viewmodel.NoteViewModel

class HomeFragment : Fragment(R.layout.fragment_home), SearchView.OnQueryTextListener {


    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: NoteAdapter
    private lateinit var notesViewModel: NoteViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notesViewModel = (activity as MainActivity).noteViewModel

        initRecyclerView()

        binding.fabComposeNewNote.setOnClickListener {
            val directions = HomeFragmentDirections.actionHomeFragmentToNewNoteFragment()
            it.findNavController().navigate(directions)
        }
    }

    private fun initRecyclerView() {
        val noteAdapter = NoteAdapter()

        binding.recyclerView.apply {

            layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )

            setHasFixedSize(true)
            adapter = noteAdapter
        }

        activity?.let {
            notesViewModel.getAllNote().observe(viewLifecycleOwner, { notes ->
                noteAdapter.differ.submitList(notes)

                if (notes.isNotEmpty()) {
                    binding.imageView.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                } else {
                    binding.imageView.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }

            })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.home_menu, menu)
        val searchView = menu.findItem(R.id.search_menu).actionView as SearchView
        searchView.isSubmitButtonEnabled = false
        searchView.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}