package com.tst.flutodo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        listView.setClickable(true);
        adapter = new CustomAdapter(this, taskList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        loadTasks();


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
                    TodoItem tdTask = gson.fromJson(jsonTask, TodoItem.class);
                    taskList.add(tdTask);
                    hidePDialog();
                }
                Toast.makeText(RestController.getInstance(),"TOAST",Toast.LENGTH_LONG);
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error http", error.getMessage());
                hidePDialog();
            }
        });

        // Adding request to request queue
        RestController.getInstance().addToRequestQueue(jsonObjectRequestGET);
    };

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