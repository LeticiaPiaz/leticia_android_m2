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
import com.example.unccrudapp.model.Store;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {
    private SwipeRefreshLayout refreshLayout;
    private RequestQueue requestQueue;
    private JsonArrayRequest arrayRequest;
    private Context context;
    private ArrayList<Store> store;
    private String url = "";
    private StoreAdapter storeAdapter;
    private RecyclerView recyclerView;

    public StoreAdapter(Context context, ArrayList<Store> store) {
        this.context = context;
        this.store = store;
    }

    @NonNull
    @Override
    public StoreAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.user_list, parent, false);

        return new StoreAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreAdapter.MyViewHolder holder, int position) {
        holder.nameStore.setText(store.get(position).getNome());
        holder.numberStore.setText(String.valueOf(position +1));
        holder.edtStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = store.get(position).getId();
                String nome = store.get(position).getNome();
                String site = store.get(position).getSite();
                String tipo = store.get(position).getTipo();
                String cidade = store.get(position).getCidade();
                String estado = store.get(position).getEstado();
                JSONObject object = new JSONObject();
                try {
                    object.put("_id", id);
                    object.put("nome", nome);
                    object.put("site", site);
                    object.put("tipo", tipo);
                    object.put("cidade", cidade);
                    object.put("estado", estado);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editStore(id, object);
            }
        });
        holder.deleteStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = store.get(position).getId();
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

        txtUser.setText("Excluir loja");

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

    private void delete(Dialog dialog, String storeId) {
        String url = "http://10.0.2.2:3000/stores/delete/" + storeId;
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

    private void editStore(final String id, JSONObject object) {
        TextView txtStore, txtClose;
        EditText edtName, edtSite, edtTipoStore, edtCidade, edtEstado;
        Button btnSave;
        final Dialog dialog;

        dialog = new Dialog(context);

        dialog.setContentView(R.layout.activity_modstore);

        txtClose = (TextView) dialog.findViewById(R.id.txtClose);
        txtStore = (TextView) dialog.findViewById(R.id.txtStore);

        txtStore.setText("Alterar lojas");

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        edtName = (EditText) dialog.findViewById(R.id.edtName);
        edtSite = (EditText) dialog.findViewById(R.id.edtSite);
        edtTipoStore = (EditText) dialog.findViewById(R.id.edtTipoStore);
        edtCidade = (EditText) dialog.findViewById(R.id.edtCidade);
        edtEstado = (EditText) dialog.findViewById(R.id.edtEstado);

        btnSave = (Button) dialog.findViewById(R.id.btnSave);
        String userId = null;
        try {
            userId = object.getString("_id");
            edtName.setText(object.getString("nome"));
            edtSite.setText(object.getString("site"));
            edtTipoStore.setText(object.getString("tipo"));
            edtCidade.setText(object.getString("cidade"));
            edtEstado.setText(object.getString("estado"));
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
                    object.put("site", edtSite.getText());
                    object.put("tipo", edtTipoStore.getText());
                    object.put("cidade", edtCidade.getText());
                    object.put("estado", edtEstado.getText());
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
        String url = "http://10.0.2.2:3000/stores/update/" + id;
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
                        Store s = new Store();
                        s.setId(jsonObject.getString("_id"));
                        s.setNome(jsonObject.getString("nome"));
                        s.setSite(jsonObject.getString("site"));
                        s.setTipo(jsonObject.getString("tipo"));
                        s.setCidade(jsonObject.getString("cidade"));
                        s.setEstado(jsonObject.getString("estado"));
                        store.add(s);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                refreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return store.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView numberStore, nameStore;
        private ImageView edtStore, deleteStore;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            numberStore = (TextView) itemView.findViewById(R.id.idNumberStore);
            nameStore = (TextView) itemView.findViewById(R.id.nameStore);
            edtStore = (ImageView) itemView.findViewById(R.id.editStore);
            deleteStore = (ImageView) itemView.findViewById(R.id.deleteStore);
        }
    }
}
