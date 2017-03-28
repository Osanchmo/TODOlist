package com.example.a21753725a.todo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MainActivityFragment extends Fragment {

    String mCurrentPhotoPath;
    ArrayAdapter adapter;
    View view;
    ArrayList todo;
    ListView lv;
    FloatingActionButton fab;
    ImageView preview;
    EditText todoText;

    static final int REQUEST_TAKE_PHOTO = 1;


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container);

        preview = (ImageView) view.findViewById(R.id.preview);
        preview.setVisibility(View.INVISIBLE);
        todoText = (EditText) view.findViewById(R.id.todoText);
        todoText.setVisibility(View.INVISIBLE);

        todoText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        String currentText = todoText.getText().toString();
                        if (currentText != null){
                            TODO todo = new TODO(currentText,mCurrentPhotoPath);
                            firebaseUpload(todo);
                            preview.setVisibility(View.INVISIBLE);
                            todoText.setVisibility(View.INVISIBLE);
                            lv.setVisibility(View.VISIBLE);
                            fab.setVisibility(View.VISIBLE);

                        }
                    return true;
                }
                return false;
            }
        });


        todo = new ArrayList<TODO>();

        fab = (FloatingActionButton) view.findViewById(R.id.fab);

       lv = (ListView) view.findViewById(R.id.todolst);
        adapter = new TODOAdapter(getContext(),R.layout.list_view_adapter, todo);

        lv.setAdapter(adapter);

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
                    showInputMethod();
                }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
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

    public void showInputMethod() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void firebaseUpload(TODO todo) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("TODO");

        myRef.push().setValue(todo);
    }
}
