package com.disparta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ChatsFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private List<Chats> chats;
    ListView rv;
    messageAdapter adapter;
    private ArrayList<Chats> messageList;
    ListView messageView;
    messageAdapter messageAdt;



    public static ChatsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ChatsFragment fragment = new ChatsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);

        //initializeData();
        chats = new ArrayList<>();


/*
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                initializeData();
            }
        }, 1000, 1000);
        */

    }

    private Runnable updateUI = new Runnable() {
        @Override
        public void run() {
            initializeData();
        }
    };


    public void initializeData() {

        try {
            convertData(getStringProperty("inbox"));
            //convertData(getStringProperty("inbox"));
        }
        catch(Exception e) {

        }

        AsyncHttpClient client = new AsyncHttpClient();
        String user_id;

        LoginActivity.sharedprefrences = getActivity().getSharedPreferences(LoginActivity.MyPREFRENCES, Context.MODE_PRIVATE);

        user_id = LoginActivity.sharedprefrences.getString("user_id", null);
        RequestParams params = new RequestParams();
        params.put("user_id", user_id);

        client.post("http://disparta.com/android/app/inbox.php", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http
            // response code '200'
            @Override
            public void onSuccess(String response) {
                // Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
               // convertData(response);
                try {
                    if (getStringProperty("inbox") != response) {
                        setStringProperty("inbox", response);
                    }
                }catch (Exception e){

                }

                if(getStringProperty("inbox") == response) {
                    convertData(getStringProperty("inbox"));
                }
            }

            // When the response returned by REST has Http
            // response code other than '200' such as '404',
            // '500' or '403' etc
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // Hide Progress Dialog

                convertData(getStringProperty("inbox"));

                // When Http response code is '404'
                if (statusCode == 404) {
                    Toast.makeText(getActivity(),
                            "Requested resource not found",
                            Toast.LENGTH_LONG).show();
                }
                // When Http response code is '500'
                else if (statusCode == 500) {
                    Toast.makeText(getActivity(),
                            "Something went wrong at server end",
                            Toast.LENGTH_LONG).show();
                }
                // When Http response code other than 404, 500
                else {
                    //Toast.makeText(getActivity(), "Error refreshing..", Toast.LENGTH_LONG).show();

                }
            }
        });




    }



    public String getStringProperty(String key) {
        LoginActivity.sharedprefrences = getActivity().getSharedPreferences(LoginActivity.MyPREFRENCES, Context.MODE_PRIVATE);

        String res = null;
        try {
            if (LoginActivity.sharedprefrences != null) {
                res = LoginActivity.sharedprefrences.getString(key, null);
            }
        }
        catch(Exception e) {

        }
        return res;
    }

    public void setStringProperty(String key, String value) {
        LoginActivity.sharedprefrences = getActivity().getSharedPreferences(LoginActivity.MyPREFRENCES, Context.MODE_PRIVATE);

        if (LoginActivity.sharedprefrences != null) {
            SharedPreferences.Editor editor = LoginActivity.sharedprefrences.edit();
            editor.putString(key, value);
            editor.commit();
            //    res = LoginActivity.sharedprefrences.getString(key, null);
        }

        convertData(value);

    }

    @Override
    public void onResume() {
        super.onResume();

        messageAdt = new messageAdapter(getActivity(), chats);
        messageView.setAdapter(messageAdt);

        initializeData();
    }

    @Override
    public void onStart() {
        super.onStart();

       // messageAdt = new messageAdapter(getActivity(), chats);
       // messageView.setAdapter(messageAdt);

        //initializeData();
    }

    @Override
    public void onPause() {
        super.onPause();
        //chats.clear();
    }

    public void convertData( String response ) {

        chats.clear();

        try {
            JSONArray jArray = new JSONArray(response);
          //  Toast.makeText(getActivity(), response , Toast.LENGTH_LONG).show();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                chats.add(new Chats(json_data.getString("name").trim(), json_data.getString("message").trim(), json_data.getString("message_id"), "http://disparta.com/images/" + json_data.getString("name").toLowerCase() + ".jpg", json_data.getString("stamp"), json_data.getString("new")));
                //Toast.makeText(getActivity(), json_data.getString("new"), Toast.LENGTH_LONG).show();


            }
            messageAdt.notifyDataSetChanged();


        } catch (JSONException e) {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            System.out.println("Error Parsing JSON: " + writer.toString());
            //Toast.makeText(this, writer.toString() , Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chats, container, false);

        messageView = (ListView)view.findViewById(R.id.listChats);

        messageAdt = new messageAdapter(getActivity(), chats);
        messageView.setAdapter(messageAdt);

        messageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatRoomActivity.ToUser = chats.get(position).name;
                ChatRoomActivity.messageID = chats.get(position).message_id;

                Intent i = new Intent(getActivity(), ChatRoomActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });

        return view;
    }




}