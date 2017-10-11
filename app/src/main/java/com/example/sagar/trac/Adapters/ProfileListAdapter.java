package com.example.sagar.trac.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;


import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sagar.trac.Models.ProfileDataSettingModel;
import com.example.sagar.trac.Models.ProfileSettings;
import com.example.sagar.trac.R;
import com.example.sagar.trac.Utils.Constants;
import com.example.sagar.trac.Utils.Trac;
import com.example.sagar.trac.Views.HomePage;
import com.example.sagar.trac.Views.ProfileSetupPage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.RealmResults;


/**
 * Created by Sagar on 1/26/2017.
 */

public class ProfileListAdapter extends RecyclerView.Adapter<ProfileListAdapter.MyProfileViewHolder> {
    Context context;
    List<ProfileDataSettingModel> results;
    Trac trac;

    public ProfileListAdapter(Context context, ArrayList<ProfileDataSettingModel> results) {
        this.context = context;
        this.results = results;
    }

    @Override
    public MyProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profile_row, parent, false);

        return new MyProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyProfileViewHolder holder, final int position) {

        HashMap<String,String> map=results.get(position).getProfile_content();

        holder.profile_name.setText(results.get(position).getProfile_name());

        if (results.get(position).getStatus().equals("1")){
            holder.profile_active_swich.setChecked(true);
        }else {
            holder.profile_active_swich.setChecked(false);
        }

        holder.profile_active_swich.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){
                    BackgroundMonitorData(results.get(position).getProfile_id(),"1");
                }else{
                    BackgroundMonitorData(results.get(position).getProfile_id(),"0");
                }

            }
        });
        holder.delete_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteProfile(results.get(position).getProfile_id(),position);
            }
        });


    }

    private void DeleteProfile(final String profile_id, final int position) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST,Trac.getInstance().getIPAddress()+Constants.PROFILE_DELETE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.contains("Profile Deleted Successfully")){
                            Toast.makeText(context,"Profile deleted Successfully..",Toast.LENGTH_LONG).show();
                            results.remove(position);
                            notifyDataSetChanged();

                        }else {
                            Toast.makeText(context,"OOp..something wents wrong",Toast.LENGTH_LONG).show();
                        }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("profile_id",profile_id);


                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    private void BackgroundMonitorData(final String profile_id, final String status) {



        StringRequest stringRequest = new StringRequest(Request.Method.POST,Trac.getInstance().getIPAddress()+Constants.PROFILE_STATUS_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(context,"Profile updated Successfully..",Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("profile_id",profile_id);
                params.put("status",status);

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);



    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class MyProfileViewHolder extends RecyclerView.ViewHolder {
        public TextView profile_name, emailid, phone;
        ImageView delete_profile;
        public Switch profile_active_swich;

        public MyProfileViewHolder(View view) {
            super(view);

            profile_active_swich = (Switch) view.findViewById(R.id.profile_active_swich);
            profile_name = (TextView) view.findViewById(R.id.profile_name);
            delete_profile=(ImageView) view.findViewById(R.id.delete_profile);


        }
    }
}
