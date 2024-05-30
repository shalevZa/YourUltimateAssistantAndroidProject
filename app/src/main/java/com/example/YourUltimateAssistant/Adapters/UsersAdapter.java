package com.example.YourUltimateAssistant.Adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.YourUltimateAssistant.Models.UserModel;
import com.example.YourUltimateAssistant.Utils.FirebaseUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends ArrayAdapter<String> {

    public static List<String> list;
    public static ArrayAdapter<String> adapter;

    //constructive action
    public UsersAdapter(Context context, List<String> items) {
        super(context, 0, items);
    }


    //set list from firestore with all the app users
    public static void setRatingListView(Context context ,ListView listView) {

        list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        FirebaseUtils.getUserFromFirestore().get().addOnCompleteListener(task -> {

            UserModel user = task.getResult().toObject(UserModel.class);

        FirebaseFirestore.getInstance().collection("Users").whereNotEqualTo("nickname" ,user.getNickname() )
                .get()
                .addOnCompleteListener(task1 -> {
                    if (task.isSuccessful()) {

                        for (DocumentSnapshot document : task1.getResult()) {

                            String userName = document.getString("nickname");

                            list.add("@ " + userName);
                        }
                        if(adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                    }


                });

        });
    }
}
