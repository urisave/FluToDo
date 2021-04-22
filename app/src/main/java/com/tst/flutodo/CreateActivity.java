package com.tst.flutodo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tst.flutodo.controller.RestController;
import com.tst.flutodo.model.TodoItem;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateActivity extends AppCompatActivity {

    EditText editText;
    Button btnCreate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_layout);

        getSupportActionBar().setTitle("Add new task");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText = (EditText) findViewById(R.id.ediText);
        btnCreate = (Button) findViewById(R.id.btnCreate);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskName = editText.getText().toString();
                if (taskName.isEmpty()){
                    Toast.makeText(CreateActivity.this,Constants.MSG_EMPTY_TASK,Toast.LENGTH_LONG).show();
                }else{
                    TodoItem itemCreated = new TodoItem(taskName);

                    try {
                        JSONObject itemJson = convertObjToJsonObject(itemCreated);
                        createTask(itemJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Clear task name
                    editText.getText().clear();

                }
            }
        });
    }

    //Http post request with Volley
    /*
    PARAM: JSONObject as TodoItem to be added
     */
    public void createTask(JSONObject itemJson) {

        // Creating volley request obj
        JsonObjectRequest jsonObjectRequestPost = new JsonObjectRequest(Request.Method.POST, Constants.URL_API_LOCAL, itemJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    Toast.makeText(getBaseContext(), "Taskname "+ itemJson.getString("Name")+" sent.", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error http post", error.getMessage());
            }
        });

        // Adding request to request queue
        RestController.getInstance().addToRequestQueue(jsonObjectRequestPost);
    };

    //Method to convert TodoItem to JSONObject with gson 3rd party lib.
    public JSONObject convertObjToJsonObject(TodoItem tdItem) throws JSONException {
        Gson gson = new Gson();
        String convertJson = gson.toJson(tdItem);
        JSONObject itemJsonObj = new JSONObject(convertJson);

        return itemJsonObj;
    }


}
