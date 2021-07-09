package com.example.unccrudapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.unccrudapp.model.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {
    private SwipeRefreshLayout refreshLayout;
    private RequestQueue requestQueue;
    private JsonArrayRequest arrayRequest;
    private Context context;
    private ArrayList<Item> item;
    private String url = "";
    private ItemAdapter itemAdapter;
    private RecyclerView recyclerView;

    public ItemAdapter(Context context, ArrayList<Item> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public ItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.user_list, parent, false);

        return new ItemAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapter.MyViewHolder holder, int position) {
        holder.nameItem.setText(item.get(position).getNome());
        holder.numberItem.setText(String.valueOf(position +1));
        holder.edtItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = item.get(position).getId();
                String nome = item.get(position).getNome();
                String tipo = item.get(position).getTipo();
                String marca = item.get(position).getMarca();
                Integer preco = (Integer) item.get(position).getPreco();
                JSONObject object = new JSONObject();
                try {
                    object.put("_id", id);
                    object.put("nome", nome);
                    object.put("site", tipo);
                    object.put("tipo", marca);
                    object.put("cidade", preco);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editUser(id, object);
            }
        });
        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = item.get(position).getId();
                deleteUser(id);
            }
        });
    }

    private void deleteUser(final String id) {
        TextView txtUser, txtClose;
        Button btnSave;
        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.user_delete);

        txtClose = (TextView) dialog.findViewById(R.id.txtClose);
        txtUser = (TextView) dialog.findViewById(R.id.txtUser);

        txtUser.setText("Excluir Usuário");

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnSave = (Button) dialog.findViewById(R.id.btnDelete);
        String userId = id;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(dialog, userId);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void delete(Dialog dialog, String userId) {
        String url = "http://10.0.2.2:3000/products/delete/" + userId;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Toast.makeText(context, "Dados excluídos com sucesso!", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) { };

        Volley.newRequestQueue(context).add(stringRequest);
    }

    private void editUser(final String id, JSONObject object) {
        TextView txtUser, txtClose;
        EditText edtName, edtUserName, edtEmail, edtPhone, edtPassword;
        Button btnSave;
        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_moduser);

        txtClose = (TextView) dialog.findViewById(R.id.txtClose);
        txtUser = (TextView) dialog.findViewById(R.id.txtUser);

        txtUser.setText("Alterar item");

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
        String userId = null;
        try {
            userId = object.getString("_id");
            edtName.setText(object.getString("name"));
            edtUserName.setText(object.getString("username"));
            edtEmail.setText(object.getString("email"));
            edtPhone.setText(object.getString("phone"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String finalUserId = userId;
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    object.put("_id", finalUserId);
                    object.put("name", edtName.getText());
                    object.put("username", edtUserName.getText());
                    object.put("email", edtEmail.getText());
                    object.put("phone", edtPhone.getText());
                    object.put("password", edtPassword.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                submit(object, dialog, finalUserId);
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void submit(final JSONObject object, final Dialog dialog, String id) {
        String url = "http://10.0.2.2:3000/products/update/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
                /*
                refreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        user.clear();
                        getData();
                    }
                });
                 */
                Toast.makeText(context, "Dados alterados com sucesso!", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "Erro ao alterar dados!", Toast.LENGTH_LONG).show();
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) { };

        Volley.newRequestQueue(context).add(jsonObjectRequest);
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
                        it.setNome(jsonObject.getString("nome"));
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
                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        //requestQueue = Volley.newRequestQueue(MainActivity.this);
        //requestQueue.add(arrayRequest);
    }

    private void adapterPush(ArrayList<Item> item) {
//        itemAdapter = new UserAdapter(this, item);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(itemAdapter);
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView numberItem, nameItem;
        private ImageView edtItem, deleteItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            numberItem = (TextView) itemView.findViewById(R.id.idNumberItem);
            nameItem = (TextView) itemView.findViewById(R.id.nameItem);
            edtItem = (ImageView) itemView.findViewById(R.id.editItem);
            deleteItem = (ImageView) itemView.findViewById(R.id.deleteItem);
        }
    }
}
