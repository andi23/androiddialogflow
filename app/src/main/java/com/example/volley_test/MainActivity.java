package com.example.volley_test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView mTextViewResult;
    private RequestQueue mQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewResult = findViewById(R.id.text_view_result);
        Button buttonParse = findViewById(R.id.button_parse);

        mQueue = Volley.newRequestQueue(this);

        buttonParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jsonPostParse();
            }
        });
    }

    private void jsonParse() {
        String url = "https://api.myjson.com/bins/1bankd";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("Response", response.toString());
                    JSONObject jsonObject = response.getJSONObject("queryResult");
                    String queryText = jsonObject.getString("queryText");
                    String fulfillmentText = jsonObject.getString("fulfillmentText");

                    mTextViewResult.append( queryText  + ", " + fulfillmentText + "\n\n");

                    Log.d("Query ", queryText);
                    Log.d(" fulfillment", fulfillmentText);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }

    private void jsonPostParse() {
        String URL = "https://dialogflow.googleapis.com/v2/projects/oceanic-student-233500" +
                "/agent/sessions/49534bcd-d1a7-7d9e-20c5-c58800d9c1a0:detectIntent";
        JSONObject text1 = new JSONObject();
        JSONObject queryParamsTZ = new JSONObject();
        JSONObject postObject = new JSONObject();
        try {
            text1.put("text", "hi rolo");
            text1.put("languageCode", "en");

            JSONObject text2 = new JSONObject().put("text", text1);
            queryParamsTZ.put("timeZone" ,"Australia/Melbourne");
            postObject.put("queryInput", text2);
            postObject.put("queryParams", queryParamsTZ);
            Log.d("this is the json", postObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String requestBody = postObject.toString();

        StringRequest jsonObjRequest = new StringRequest(Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {


            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers=new HashMap<String,String>();
                headers.put("Authorization","Bearer ya29.c.Elp_B-8H7ZC6hqPJYDAJS5OED4tMoQktUqHjSCNKoyutYmSNai-_h9gMpSPXjscwpMvBPwT6x2uFnHyQ_V9JpUrwXP4FSQ3WV3ujeZIl83xXZ0u1SJJerNxTrL4");
                headers.put("Content-Type","application/json; charset=utf-8");
                return headers;
            }

        };

        mQueue.add(jsonObjRequest);
    }
}
