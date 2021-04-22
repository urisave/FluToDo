package com.tst.flutodo.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tst.flutodo.R;
import com.tst.flutodo.model.TodoItem;

import java.util.List;

public class CustomAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List taskItems;

    public CustomAdapter(Activity activity, List taskItems) {
        this.activity = activity;
        this.taskItems = taskItems;
    }

    @Override
    public int getCount() {
        return taskItems.size();
    }

    @Override
    public Object getItem(int location) {
        return taskItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_row, null);


        TextView name = (TextView) convertView.findViewById(R.id.title);
        CheckBox isCompleted =(CheckBox) convertView.findViewById(R.id.checkbox_isCompleted);

        // getting model data for the row
        TodoItem task = (TodoItem) taskItems.get(position);

        //Task name
        name.setText(String.valueOf(task.getName()));

        //Task state
        isCompleted.setChecked(task.getCompleted());


        return convertView;
    }
}
