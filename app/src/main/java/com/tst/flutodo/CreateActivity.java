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
    private  RestController restController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_layout);

        restController = new RestController();


        getSupportActionBar().setTitle("Add new task");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText = (EditText) findViewById(R.id.ediText);
        btnCreate = (Button) findViewById(R.id.btnCreate);

        //Create button listener
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get text from edit text
                String taskName = editText.getText().toString();

                //If text is empty don't create task
                if (taskName.isEmpty()){
                    Toast.makeText(CreateActivity.this,Constants.MSG_EMPTY_TASK,Toast.LENGTH_LONG).show();
                }else{
                    //Create item object with status not completed and name entered at edit text
                    TodoItem itemCreated = new TodoItem(taskName);

                    try {
                        //Create JSONObject to add on POST request
                        JSONObject itemJson = convertObjToJsonObject(itemCreated);
                        //HTTP Post call to create item task on API
                        restController.createTask(itemJson);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    //Clear task name
                    editText.getText().clear();

                }
            }
        });
    }



    //Method to convert TodoItem to JSONObject using gson 3rd party lib.
    public JSONObject convertObjToJsonObject(TodoItem tdItem) throws JSONException {
        Gson gson = new Gson();
        String convertJson = gson.toJson(tdItem);
        JSONObject itemJsonObj = new JSONObject(convertJson);

        return itemJsonObj;
    }


}
