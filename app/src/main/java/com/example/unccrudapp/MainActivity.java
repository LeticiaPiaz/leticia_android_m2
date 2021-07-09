package com.example.unccrudapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.unccrudapp.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RequestQueue requestQueue;
    private SwipeRefreshLayout refreshLayout;
    private ArrayList<User> user = new ArrayList<>();
    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView; // para carregar e atualizar
    private Dialog dialog; // a caixinha de diagolo
    private UserAdapter userAdapter; // comunica com o recyclerView para atualizar
    private String url = "http://10.0.2.2:3000/users/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeDown);
        recyclerView = (RecyclerView) findViewById(R.id.user);

        dialog = new Dialog(this);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                user.clear();
                getData();
            }
        });
    }

    public void getData() {
        refreshLayout.setRefreshing(true);
        arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        User u = new User();
                        u.setId(jsonObject.getString("_id"));
                        u.setName(jsonObject.getString("name"));
                        u.setUserName(jsonObject.getString("userName"));
                        u.setEmail(jsonObject.getString("email"));
                        u.setPhone(jsonObject.getString("phone"));
                        user.add(u);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterPush(user);
                refreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(arrayRequest);
    }

    private void adapterPush(ArrayList<User> user) {
        userAdapter = new UserAdapter(this, user);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(userAdapter);
    }

    public void addUser(View v) {
        TextView txtUser, txtClose;
        EditText edtName, edtUserName, edtEmail, edtPhone, edtPassword;
        Button btnSave;

        dialog.setContentView(R.layout.activity_moduser);

        txtClose = (TextView) dialog.findViewById(R.id.txtClose);
        txtUser = (TextView) dialog.findViewById(R.id.txtUser);

        txtUser.setText("Novo Usuário");

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        edtName = (EditText) dialog.findViewById(R.id.edtName);
        edtUserName = (EditText) dialog.findViewById(R.id.edtuserName);
        edtEmail = (EditText) dialog.findViewById(R.id.edtEmail);
        edtPhone = (EditText) dialog.findViewById(R.id.edtPhone);
        edtPassword = (EditText) dialog.findViewById(R.id.edtPass);

        btnSave = (Button) dialog.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    object.put("name", edtName.getText());
                    object.put("userName", edtUserName.getText());
                    object.put("email", edtEmail.getText());
                    object.put("phone", edtPhone.getText());
                    object.put("password", edtPassword.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                submit(object);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void submit(JSONObject object) {
        String url = "http://10.0.2.2:3000/users/create";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                refreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        user.clear();
                        getData();
                    }
                });
                Toast.makeText(getApplicationContext(), "Dados gravados com sucesso!", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Erro ao gravar dados!", Toast.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }) { };

        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    @Override
    public void onRefresh() {
        user.clear();
        getData();
    }
}