package com.disparta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class FriendsFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private FloatingActionButton fab;
    private List<Chats> chats;
    ListView rv;
    messageAdapter adapter;
    private ArrayList<Chats> messageList;
    ListView messageView;
    messageAdapter messageAdt;


    public static FriendsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        FriendsFragment fragment = new FriendsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        chats = new ArrayList<>();

        initializeData();

    }

    private void initializeData() {

        AsyncHttpClient client = new AsyncHttpClient();
        String user_id;

        LoginActivity.sharedprefrences = getActivity().getSharedPreferences(LoginActivity.MyPREFRENCES, Context.MODE_PRIVATE);

        user_id = LoginActivity.sharedprefrences.getString("user_id", null);
        RequestParams params = new RequestParams();
        params.put("user_id", user_id);

        client.post("http://disparta.com/android/app/friends.php", params, new AsyncHttpResponseHandler() {
            // When the response returned by REST has Http
            // response code '200'
            @Override
            public void onSuccess(String response) {
                try {
                    if (getStringProperty("friends") != response) {
                        setStringProperty("friends", response);
                    }
                }catch (Exception e){

                }

                if(getStringProperty("friends") == response) {
                    convertData(getStringProperty("friends"));

                }
                //convertData(response);
            }

            // When the response returned by REST has Http
            // response code other than '200' such as '404',
            // '500' or '403' etc
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                // Hide Progress Dialog
                //prgDialog.hide();
                convertData(getStringProperty("friends"));
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
                    //Toast.makeText(getActivity(), "Error Occured \n Most Common Error: \n1. Device not connected to Internet\n2. Web App is not deployed in App server\n3. App server is not running\n HTTP Status code : " + statusCode, Toast.LENGTH_LONG).show();
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
        try {
            if (LoginActivity.sharedprefrences != null) {
                SharedPreferences.Editor editor = LoginActivity.sharedprefrences.edit();
                editor.putString(key, value);
                editor.commit();
                //    res = LoginActivity.sharedprefrences.getString(key, null);
            }

            convertData(value);
        }catch(Exception e){}

    }


    public void convertData( String response ) {
        chats.clear();
        try {
            JSONArray jArray = new JSONArray(response);
            //  Toast.makeText(getActivity(), response , Toast.LENGTH_LONG).show();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                chats.add(new Chats(json_data.getString("username"), json_data.getString("status"), json_data.getString("user_id"), "http://disparta.com/images/"+json_data.getString("username").toLowerCase()+".jpg", "", ""));
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
        View view = inflater.inflate(R.layout.friends, container, false);

        fab = (FloatingActionButton) view.findViewById(R.id.fabFriends);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This will refresh friends", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        messageView = (ListView)view.findViewById(R.id.listFriends);

        messageAdt = new messageAdapter(getActivity(), chats);
        messageView.setAdapter(messageAdt);

        messageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getContext(), " Clicked - "+chats.get(position).message_id,Toast.LENGTH_LONG).show();
                ChatRoomActivity.ToUser = chats.get(position).name;
                //ChatRoomActivity.messageID = chats.get(position).message_id;

                Intent i = new Intent(getActivity(), ChatRoomActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });

        return view;
    }
}
