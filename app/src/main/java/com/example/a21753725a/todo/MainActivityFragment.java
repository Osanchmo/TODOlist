package com.example.a21753725a.todo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    ArrayAdapter adapter;
    View view;
    ArrayList todo;
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        todo = new ArrayList<String>();

        Button btn = (Button) view.findViewById(R.id.button);

        final EditText editText = (EditText) view.findViewById(R.id.editText);
        ListView lv = (ListView) view.findViewById(R.id.todolst);
        adapter = new ArrayAdapter<String>(this.getContext(),R.layout.list_view_adapter,R.id.listViewText,todo);

        lv.setAdapter(adapter);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                adapter.add(String.valueOf(editText.getText()));
                editText.setText(null);
            }
        });
        // Write a message to the database
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("message");
        //myRef.setValue("Hello, World!");

        return view;
    }
}
