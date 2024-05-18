package com.example.mindsync

import Therapist
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TherapistFragment : Fragment() {
    private lateinit var editTextSearch: EditText
    private lateinit var recyclerViewTherapists: RecyclerView
    private lateinit var therapistAdapter: TherapistAdapter
    private lateinit var therapistList: List<Therapist>
    private lateinit var noCounselorsFoundTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_therapists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextSearch = view.findViewById(R.id.editTextSearch)
        recyclerViewTherapists = view.findViewById(R.id.recyclerViewTherapists)

        // Set up RecyclerView
        recyclerViewTherapists.layoutManager = LinearLayoutManager(requireContext())
        noCounselorsFoundTextView = view.findViewById(R.id.NoCounselorsFound)

        // Assume you have a list of therapists retrieved from Firebase
//        val therapistList: List<Therapist> = getTherapistListFromFirebase()
        // Get list of therapists from Firebase
        therapistList = getTherapistListFromFirebase()
        Log.d("TherapistFragment", "Therapist List Size: ${therapistList.size}")

        therapistAdapter = TherapistAdapter(therapistList)
        recyclerViewTherapists.adapter = therapistAdapter

        // Add text change listener to the search EditText
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterTherapists(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No implementation needed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No implementation needed
            }
        })
    }

    // Function to filter therapists based on search query
    private fun filterTherapists(query: String) {
        val filteredList = therapistList.filter { therapist ->
            therapist.name.contains(query, ignoreCase = true) || therapist.designation.contains(query, ignoreCase = true)
        }

        // Update adapter with filtered list
        therapistAdapter.updateDataset(filteredList)

        // Update visibility of "No Counselors Found" TextView
        if (filteredList.isEmpty()) {
            noCounselorsFoundTextView.visibility = View.VISIBLE
        } else {
            noCounselorsFoundTextView.visibility = View.GONE
        }
    }

    // Function to get therapist list from Firebase
    private fun getTherapistListFromFirebase(): List<Therapist> {
        // Implement your logic to retrieve therapists from Firebase
        // Return a list of Therapist objects
        // For now, let's create a dummy list for testing
        return listOf(
            Therapist("Niharika S.", "Counseling Psychologist"),
            Therapist("Dr.Shakti K.", "Clinical Psychologist"),
            Therapist("Gaurav K.", "Life Coach"),
            Therapist("Sanya O.", "Peer Listener"),
            Therapist("Nandani A", "Counseling Psychologist"),
            Therapist("Nirmay C.", "Clinical Psychologist"),
            Therapist("Ritika B.", "Relationship Coach"),
            Therapist("Pooja S.", "Clinical Psychologist"),
            Therapist("Kavya S.", "Counseling Psychologist"),
            Therapist("Naina G.", "Clinical Psychologist"),
        )
    }
}
