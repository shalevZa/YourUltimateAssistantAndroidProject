package com.example.YourUltimateAssistant.History;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.YourUltimateAssistant.R;
import com.example.YourUltimateAssistant.Utils.FirebaseUtils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class WriteWithVoiceHistoryFragment extends Fragment {

    // Declaration of variables
    ListView listView;
    List<String> list = new ArrayList<>();
    ArrayAdapter<String> adapter;
    RelativeLayout layout;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflating the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_write_with_voice_history, container, false);

        // Initializing views
        listView = view.findViewById(R.id.list);
        layout = view.findViewById(R.id.relative);

        // Setting up swipe refresh layout
        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.container);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadData();
            swipeRefreshLayout.setRefreshing(false);
        });

        // Fetching data from Firestore and populating the list view
        FirebaseUtils.getUserFromFirestore().collection("writeWithYourVoice").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        list.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String text = document.getString("text");
                            list.add(text);
                        }
                    }

                    // Creating and setting adapter
                    if (getActivity() != null) {
                        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
                        listView.setAdapter(adapter);
                    }
                });

        // Setting item click listener to create popup window for copy/delete actions
        listView.setOnItemClickListener((parent, view1, position, id) -> createPopUpWindow(parent , position));

        return view;
    }

    // Method to create and display the popup window for copy/delete actions
    @SuppressLint("MissingInflatedId")
    public void createPopUpWindow(AdapterView<?> parent , int position) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.history_copy);
        dialog.show();

        // Setting up buttons in the dialog
        Button copy = dialog.findViewById(R.id.copyBtn);
        Button delete = dialog.findViewById(R.id.deleteBtn);

        // Delete action
        delete.setOnClickListener(v -> {
            Query query = FirebaseUtils.getUserFromFirestore().collection("writeWithYourVoice").whereEqualTo("text",  (String) parent.getItemAtPosition(position));
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String documentId = document.getId();
                        DocumentReference documentReference = FirebaseUtils.getUserFromFirestore().collection("writeWithYourVoice").document(documentId);
                        documentReference.delete()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Deleted!!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Error!!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }
            });
        });

        // Copy action
        copy.setOnClickListener(v -> {
            ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("EditText" , (String) parent.getItemAtPosition(position) );
            clipboardManager.setPrimaryClip(clip);
            Toast.makeText(getActivity() , "Copied" , Toast.LENGTH_SHORT).show();
        });
    }

    // Method to load data from Firestore and populate the list view
    private void loadData() {
        FirebaseUtils.getUserFromFirestore().collection("writeWithYourVoice").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        list.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String text = document.getString("text");
                            list.add(text);
                        }
                    }

                    // Creating and setting adapter
                    adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
                    listView.setAdapter(adapter);
                });
    }
}
