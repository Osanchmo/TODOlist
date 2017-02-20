package com.example.a21753725a.todo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by 21753725a on 20/02/17.
 */

public class TODOAdapter extends ArrayAdapter<TODO>{

    public TODOAdapter(Context context, int resource, List<TODO> objects) {
    super(context, resource, objects);
}
    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {


        TODO item = getItem(pos);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_view_adapter, parent, false);
        }

        TextView tvTodo = (TextView) convertView.findViewById(R.id.listViewText);
        ImageView imTodo = (ImageView) convertView.findViewById(R.id.mediaView);

        tvTodo.setText(item.getText());
        Glide.with(getContext()).load(item.getMediapath()).into(imTodo);
        return convertView;
    }
}
