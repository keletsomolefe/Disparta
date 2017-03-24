package com.disparta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class FeedsFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private  ListView feedView;
    private List<Chats> feeds;
    private EditText updateText;
    private  feedAdapter feedAdapter;

    public static FeedsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        FeedsFragment fragment = new FeedsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);

        feeds = new ArrayList<>();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.feeds, container, false);

        feedView = (ListView)view.findViewById(R.id.feedsArea);

        feedAdapter = new feedAdapter(getActivity(), feeds);
        feedView.setAdapter(feedAdapter);
        updateText = (EditText)view.findViewById(R.id.statusText);


        feedView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // ChatRoomActivity.ToUser = feeds.get(position).name;
               // ChatRoomActivity.messageID = feeds.get(position).message_id;

               // Intent i = new Intent(getActivity(), ChatRoomActivity.class);
                //i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                //startActivity(i);
            }
        });

        FloatingActionButton btnUpdate = (FloatingActionButton)view.findViewById(R.id.sendUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendPost();
            }
        });



        return view;
    }

    private void initializeData() {

        try {
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

        client.post("http://disparta.com/android/app/post.php", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http
            // response code '200'
            @Override
            public void onSuccess(String response) {
                // Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                // convertData(response);
                try {
                    if (getStringProperty("feeds") != response) {
                        setStringProperty("feeds", response);
                    }
                } catch (Exception e) {

                }

                if (getStringProperty("feeds") == response) {
                    convertData(getStringProperty("feeds"));

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
        }

        convertData(value);

    }

    @Override
    public void onResume() {
        super.onResume();

        feedAdapter = new feedAdapter(getActivity(), feeds);
        feedView.setAdapter(feedAdapter);

        initializeData();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        feeds.clear();
    }

    public void convertData( String response ) {

        feeds.clear();

        try {
            JSONArray jArray = new JSONArray(response);
            //  Toast.makeText(getActivity(), response , Toast.LENGTH_LONG).show();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                feeds.add(new Chats(json_data.getString("name"), json_data.getString("body"), json_data.getString("id"), "http://disparta.com/images/" + json_data.getString("name").toLowerCase() + ".jpg", json_data.getString("stamp"), ""));
            }
            feedAdapter.notifyDataSetChanged();


        } catch (JSONException e) {
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            System.out.println("Error Parsing JSON: " + writer.toString());
            //Toast.makeText(this, writer.toString() , Toast.LENGTH_LONG).show();
        }
    }


    public void sendPost() {

        final String theMessage;


        theMessage = updateText.getText().toString().trim();

        if (theMessage.isEmpty()) {
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        final String user_id, usernames;

        LoginActivity.sharedprefrences = getActivity().getSharedPreferences(LoginActivity.MyPREFRENCES, Context.MODE_PRIVATE);

        user_id = LoginActivity.sharedprefrences.getString("user_id", null);

        RequestParams params = new RequestParams();
        params.put("user_id", user_id);
        params.put("body", theMessage);

        client.post("http://disparta.com/android/app/sendPost.php", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http
            // response code '200'
            @Override
            public void onSuccess(String response) {
                // Hide Progress Dialog
                //prgDialog.hide();

                if (response.contains("Successfully")) {
                    updateText.setText("");
                    initializeData();

                    //messageService.sendMessage(ToUser, theMessage);
                    //messageAdapter.addMessage(theMessage, MessageChatAdapter.DIRECTION_INCOMING);
                    //messageAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(getActivity(),
                            response,
                            Toast.LENGTH_SHORT).show();
                }


            }

            // When the response returned by REST has Http
            // response code other than '200' such as '404',
            // '500' or '403' etc
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // Hide Progress Dialog
                //prgDialog.hide();
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
                    Toast.makeText(
                            getActivity(),
                            "Error Occured \n Most Common Error: \n1. Device not connected to Internet\n2. Web App is not deployed in App server\n3. App server is not running\n HTTP Status code : "
                                    + statusCode, Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }


}
