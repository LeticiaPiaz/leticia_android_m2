package com.example.unccrudapp;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.example.unccrudapp.model.User;
import com.example.unccrudapp.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private SwipeRefreshLayout refreshLayout;
    private RequestQueue requestQueue;
    private JsonArrayRequest arrayRequest;
    private Context context;
    private ArrayList<User> user;
    private String url = "";
    private UserAdapter userAdapter;
    private RecyclerView recyclerView;

    public UserAdapter(Context context, ArrayList<User> user) {
        this.context = context;
        this.user = user;
    }

    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.user_list, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.MyViewHolder holder, int position) {
        holder.txtUserName.setText(user.get(position).getName());
        holder.txtNumber.setText(String.valueOf(position + 1));
        holder.edtUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = user.get(position).getId();
                String name = user.get(position).getName();
                String userName = user.get(position).getUserName();
                String email = user.get(position).getEmail();
                String phone = user.get(position).getPhone();
                String password = user.get(position).getPassword();
                JSONObject object = new JSONObject();
                try {
                    object.put("_id", id);
                    object.put("name", name);
                    object.put("userName", userName);
                    object.put("email", email);
                    object.put("phone", phone);
                    object.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                editUser(id, object);
            }
        });
        holder.deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = user.get(position).getId();
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
        String url = "http://10.0.2.2:3000/users/delete/" + userId;
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

        txtUser.setText("Alterar Usuário");

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
            edtUserName.setText(object.getString("userName"));
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
                    object.put("name", edtName.getText());
                    object.put("userName", edtUserName.getText());
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
        String url = "http://10.0.2.2:3000/users/update/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                dialog.dismiss();
//                getData();

                dialog.dismiss();
//                Toast.makeText(context, "Dados alterados com sucesso!", Toast.LENGTH_LONG).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
            }
        }) { };

        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    private void getData() {
        MainActivity mainActivity = new MainActivity();
        mainActivity.onRefresh();
        mainActivity.getData();
//        refreshLayout.setRefreshing(true);
//
//        arrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                JSONObject jsonObject = null;
//                for (int i = 0; i < response.length(); i++) {
//                    try {
//                        jsonObject = response.getJSONObject(i);
//                        User u = new User();
//                        u.setId(jsonObject.getString("_id"));
//                        u.setName(jsonObject.getString("name"));
//                        u.setUserName(jsonObject.getString("userName"));
//                        u.setEmail(jsonObject.getString("email"));
//                        u.setPhone(jsonObject.getString("phone"));
//                        user.add(u);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//                adapterPush(user);
//                refreshLayout.setRefreshing(false);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
//            }
//        });

    }

    private void adapterPush(ArrayList<User> user) {
        //userAdapter = new UserAdapter(this, user);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView.setAdapter(userAdapter);
    }

    @Override
    public int getItemCount() {
        return user.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtUserName, txtNumber;
        private ImageView edtUser, deleteUser;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNumber = (TextView) itemView.findViewById(R.id.idNumber);
            txtUserName = (TextView) itemView.findViewById(R.id.nameUser);
            edtUser = (ImageView) itemView.findViewById(R.id.editUser);
            deleteUser = (ImageView) itemView.findViewById(R.id.deleteUser);
        }
    }
}
