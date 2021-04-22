package com.tst.flutodo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;
import com.tst.flutodo.controller.RestController;
import com.tst.flutodo.model.TodoItem;
import com.tst.flutodo.view.CustomAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private List taskList = new ArrayList();
    private ListView listView;
    private CustomAdapter adapter;
    private Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        btn = (Button) findViewById(R.id.btn);

        //Init Adapter to show items
        adapter = new CustomAdapter(this, taskList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);

        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        loadTasks();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateActivity.class));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //get Item to delete
                TodoItem taskItem = (TodoItem) adapter.getItem(position);
                //execute HTTP delete
                deleteTask(taskItem);
                //remove task from listview
                taskList.remove(taskItem);
                //notify change
                adapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this,"Task deleted: " + taskItem.getName(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    //GET HTTP to retrieve API items
    public void loadTasks(){
        // Creating volley request obj
        JsonArrayRequest jsonObjectRequestGET = new JsonArrayRequest(Request.Method.GET, Constants.URL_API_LOCAL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Gson gson = new Gson();
                for (int i = 0; i < response.length(); i++) {
                    String jsonTask = null;
                    try {
                        jsonTask = response.getJSONObject(i).toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.i("ResponseJson: ", jsonTask);
                    //Create TodoItem from response
                    TodoItem tdTask = gson.fromJson(jsonTask, TodoItem.class);
                    //Add item to the list
                    taskList.add(tdTask);
                    hidePDialog();
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error http", error.getMessage());
                hidePDialog();
            }
        });

        // Adding GET request to request queue
        RestController.getInstance().addToRequestQueue(jsonObjectRequestGET);
    };

    public void deleteTask(TodoItem taskItem){

        //Add id of delete item
        String urlDelete = Constants.URL_API_LOCAL + taskItem.getKey();
        JsonArrayRequest jsonArrayRequestDelete = new JsonArrayRequest(Request.Method.DELETE, urlDelete, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                Toast.makeText(MainActivity.this,"Deleted", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RestController.getInstance().addToRequestQueue(jsonArrayRequestDelete);
        adapter.notifyDataSetChanged();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}