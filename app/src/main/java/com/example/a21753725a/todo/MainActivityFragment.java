package com.example.a21753725a.todo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class MainActivityFragment extends Fragment {

    String mCurrentPhotoPath;
    ArrayAdapter adapter;
    View view;
    ListView lv;
    FloatingActionButton fab;
    ImageView preview;
    EditText todoText;
    ArrayList<TODO> todos;
    FirebaseDatabase database;
    DatabaseReference myRef;

    static final int REQUEST_TAKE_PHOTO = 1;


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("TODO");
        preview = (ImageView) view.findViewById(R.id.preview);
        preview.setVisibility(View.INVISIBLE);
        todoText = (EditText) view.findViewById(R.id.todoText);
        todoText.setVisibility(View.INVISIBLE);

        lv = (ListView) view.findViewById(R.id.todolst);
        loadList();

        todoText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String currentText = todoText.getText().toString();
                    if (currentText != null) {
                        TODO todo = new TODO(currentText, mCurrentPhotoPath);
                        myRef.push().setValue(todo);
                        preview.setVisibility(View.INVISIBLE);
                        todoText.setVisibility(View.INVISIBLE);
                        lv.setVisibility(View.VISIBLE);
                        fab.setVisibility(View.VISIBLE);
                        hideInputMethod();
                    }
                    return true;
                }
                return false;
            }
        });


        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        return view;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                System.out.println(ex);
            }
            // Continue only if the File was successfully created
            if (photoFile.exists()) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }

        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    lv.setVisibility(View.INVISIBLE);
                    fab.setVisibility(View.INVISIBLE);
                    Glide.with(getActivity()).load(mCurrentPhotoPath).into(preview);
                    preview.setVisibility(View.VISIBLE);
                    todoText.setVisibility(View.VISIBLE);
                    todoText.requestFocus();
                }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void hideInputMethod() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(todoText.getWindowToken(), 0);
    }


    public void loadList() {
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                todos = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    TODO todo = postSnapshot.getValue(TODO.class);
                    todos.add(todo);

                }
                adapter = new TODOAdapter(getContext(), R.layout.list_view_adapter, todos);
                lv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                error.toException().printStackTrace();
            }
        });
    }
}
