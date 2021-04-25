package com.tst.flutodo.controller;

import android.app.Application;
import android.net.NetworkRequest;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.tst.flutodo.Constants;
import com.tst.flutodo.MainActivity;
import com.tst.flutodo.model.TodoItem;
import com.tst.flutodo.view.CustomAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class RestController extends Application {

    public static final String TAG = RestController.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static RestController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static synchronized RestController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }


    public  void addToRequestQueue(Request req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /*
    HTTP REQUESTS
     */

    //GET HTTP to retrieve API items
    public void loadTasks(List taskList, CustomAdapter adapter){
        // Creating volley request obj
        JsonArrayRequest jsonObjectRequestGET = new JsonArrayRequest(Request.Method.GET, Constants.URL_API_LOCAL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                taskList.clear();
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
                   // hidePDialog();
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error http", error.getMessage());
                handleExceptionHttp(error);
            }
        });

        // Adding GET request to request queue
        RestController.getInstance().addToRequestQueue(jsonObjectRequestGET);
    };

    //Http post request with Volley
    public void createTask(JSONObject itemJson) {

        // Creating volley request obj
        VolleyJsonRequest jsonObjectRequestPost = new VolleyJsonRequest(Request.Method.POST, Constants.URL_API_LOCAL, itemJson, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Toast.makeText(RestController.getInstance().getBaseContext(), "Taskname "+ itemJson.getString("Name")+" created.", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleExceptionHttp(error);
                Log.e("error http post", error.getMessage());
            }
        });

        // Adding request to request queue
        RestController.getInstance().addToRequestQueue(jsonObjectRequestPost);
    };

    public void deleteTask(String keyItem, CustomAdapter adapter){

        //Add id of delete item
        String urlDelete = Constants.URL_API_LOCAL + keyItem;
        StringRequest str = new StringRequest(Request.Method.DELETE, urlDelete, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Toast.makeText(RestController.getInstance().getBaseContext(), "Task deleted", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleExceptionHttp(error);
            }
        });

        RestController.getInstance().addToRequestQueue(str);
        adapter.notifyDataSetChanged();
    }

    public void changeStatus(TodoItem taskItem) throws JSONException {

        Gson gson = new Gson();
        String convertJson = gson.toJson(taskItem);
        JSONObject itemJsonObj = new JSONObject(convertJson);

        //Add id of delete item
        String urlPut = Constants.URL_API_LOCAL + taskItem.getKey();
        VolleyJsonRequest jsonObjectRequestPost = new VolleyJsonRequest(Request.Method.PUT, urlPut, itemJsonObj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(RestController.getInstance().getBaseContext(), "Task status changed", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handleExceptionHttp(error);
            }
        });

        // Adding request to request queue
        RestController.getInstance().addToRequestQueue(jsonObjectRequestPost);
    }

    public void handleExceptionHttp(VolleyError volleyError){
        String message = null;

        if (volleyError instanceof NetworkError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof ServerError) {
            message = "The server could not be found. Please try again after some time!!";
        } else if (volleyError instanceof AuthFailureError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof ParseError) {
            message = "Parsing error! Please try again after some time!!";
        } else if (volleyError instanceof NoConnectionError) {
            message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof TimeoutError) {
            message = "Connection TimeOut! Please check your internet connection.";
        }

        Toast.makeText(RestController.getInstance().getBaseContext(),message, Toast.LENGTH_SHORT).show();
    }
}
