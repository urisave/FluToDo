package com.tst.flutodo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.tst.flutodo.controller.RestController;
import com.tst.flutodo.model.TodoItem;
import com.tst.flutodo.view.CustomAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List taskList = new ArrayList();
    private ListView listView;
    private CustomAdapter adapter;
    private Button btn;
    private CheckBox taskState;

    private  RestController restController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create controller for Rest calls
        restController = new RestController();


        listView = (ListView) findViewById(R.id.list);
        btn = (Button) findViewById(R.id.btn);
        taskState = (CheckBox) findViewById(R.id.checkbox_isCompleted);

        //Init Adapter to show items
        adapter = new CustomAdapter(this, taskList, getApplicationContext());
        listView.setAdapter(adapter);

        //Load tasks from API
        restController.loadTasks(taskList,adapter);

        //Button create new task to new activity screen
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateActivity.class));
            }
        });

        //On long click for delete task
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //get Item to delete
                TodoItem taskItem = (TodoItem) adapter.getItem(position);
                //execute HTTP delete
                restController.deleteTask(taskItem.getKey(), adapter);
                //remove task from listview
                taskList.remove(taskItem);
                //notify change
                adapter.notifyDataSetChanged();
                return true;
            }
        });




    }

    //Checkbox on click listener method to change task status
    public void onCheckboxClicked(View v) throws JSONException {
        //get Checkbox position
        int position = (int) v.getTag();
        //retrieve Task data
       TodoItem itemChanged = (TodoItem) taskList.get(position);
       //Toggle value status task
       itemChanged.setCompleted(!(itemChanged.getCompleted()));
       //HTTP Put call to refresh new value on API
       restController.changeStatus(itemChanged);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}