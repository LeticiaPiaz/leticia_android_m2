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
import com.example.unccrudapp.model.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ItemActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RequestQueue requestQueue;
    private SwipeRefreshLayout refreshLayout;
    private ArrayList<Item> item = new ArrayList<>();
    private JsonArrayRequest arrayRequest;
    private RecyclerView recyclerView; // para carregar e atualizar
    private Dialog dialog; // a teinha de diagolo
    private ItemAdapter itemAdapter; // comunica com o recyclerView para atualizar
    private String url = "http://10.0.2.2:3000/products/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeDown);
        recyclerView = (RecyclerView) findViewById(R.id.item);

        dialog = new Dialog(this);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                item.clear();
                getData();
            }
        });
    }

    private void getData() {
        refreshLayout.setRefreshing(true);
        arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        Item it = new Item();
                        it.setId(jsonObject.getString("_id"));
                        it.setNome(jsonObject.getString("name"));
                        it.setTipo(jsonObject.getString("tipo"));
                        it.setMarca(jsonObject.getString("marca"));
                        it.setPreco(jsonObject.getInt("preco"));
                        item.add(it);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapterPush(item);
                refreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue = Volley.newRequestQueue(ItemActivity.this);
        requestQueue.add(arrayRequest);
    }

    private void adapterPush(ArrayList<Item> item) {
        itemAdapter = new ItemAdapter(this, item);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(itemAdapter);
    }

    public void addItem(View v) {
        TextView txtItem, txtClose;
        EditText edtName, edtTipo, edtMarca, edtPreco;
        Button btnSaveItem;

        dialog.setContentView(R.layout.activity_moduser);

        txtClose = (TextView) dialog.findViewById(R.id.txtClose);
        txtItem = (TextView) dialog.findViewById(R.id.txtUser);

        txtItem.setText("Novo item");

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        edtName = (EditText) dialog.findViewById(R.id.edtName);
        edtTipo = (EditText) dialog.findViewById(R.id.edtTipo);
        edtMarca = (EditText) dialog.findViewById(R.id.edtMarca);
        edtPreco = (EditText) dialog.findViewById(R.id.edtPreco);

        btnSaveItem = (Button) dialog.findViewById(R.id.btnSaveStore);

        btnSaveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    object.put("name", edtName.getText());
                    object.put("tipo", edtTipo.getText());
                    object.put("marca", edtMarca.getText());
                    object.put("preco", edtPreco.getText());
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
        String url = "http://10.0.2.2:3000/products/create";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                refreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        item.clear();
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
        item.clear();
        getData();
    }
}