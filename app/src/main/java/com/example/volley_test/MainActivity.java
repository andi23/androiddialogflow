package com.example.volley_test;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
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

import android.media.*;

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
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject queryResult = jsonObject.getJSONObject("queryResult");
                            String queryText = queryResult.getString("queryText");
                            String fulfillmentText = queryResult.getString("fulfillmentText");
                            String audioencoded = jsonObject.getString("outputAudio");
                            Log.d("the audio", audioencoded);

                            mTextViewResult.append( queryText  + ", " + fulfillmentText + ", " + audioencoded + "\n\n");

                            int outputBufferSize = AudioTrack.getMinBufferSize(24000,
                                    AudioFormat.CHANNEL_IN_STEREO,
                                    AudioFormat.ENCODING_PCM_16BIT);

//                            AudioTrack voice = new AudioTrack(AudioManager.USE_DEFAULT_STREAM_TYPE,
//                                    16000, AudioFormat.CHANNEL_OUT_STEREO,
//                                    AudioFormat.ENCODING_PCM_16BIT, outputBufferSize,
//                                    AudioTrack.MODE_STREAM);
                            AudioTrack voice = new AudioTrack.Builder()
                                    .setAudioAttributes(new AudioAttributes.Builder()
                                            .setUsage(AudioAttributes.USAGE_ALARM)
                                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                            .build())
                                    .setAudioFormat(new AudioFormat.Builder()
                                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                            .setSampleRate(24000)
                                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                            .build())
                                    .setBufferSizeInBytes(outputBufferSize)
                                    .build();

//                            int minBuffSize = 44100;
//                            int maxJitter = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
//                            AudioTrack voice = new AudioTrack.Builder()
//                                    .setAudioAttributes(new AudioAttributes.Builder()
//                                            .setUsage(AudioAttributes.USAGE_ALARM)
//                                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                                            .build())
//                                    .setAudioFormat(new AudioFormat.Builder()
//                                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
//                                            .setSampleRate(44100)
//                                            .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
//                                            .build())
//                                    .setBufferSizeInBytes(minBuffSize)
//                                    .build();
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                voice.setVolume(100);
//                            }
                            voice.play();

                            byte[] data = Base64.decode(audioencoded, Base64.DEFAULT);

                            int iRes = voice.write(data, 0, data.length);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
                headers.put("Authorization","Bearer ya29.c.ElqGB7Z1asKlr-quxdTvmN0jpSIKpyTSHifZqvv6JkJXA_XUEiyOKYzmPWPTJlVKaC7x3yhPL-Efg62Vx8B1M12Kgl4NZxhY5sHWcHcWq2y2MxM0_hsQmctggf8");
                headers.put("Content-Type","application/json; charset=utf-8");
                return headers;
            }

        };

        mQueue.add(jsonObjRequest);
    }
}
