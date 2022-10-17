package com.example.chattingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.chattingapp.Model.NoData;
import com.example.chattingapp.R;
import com.example.chattingapp.databinding.NodataBinding;

import java.util.ArrayList;

public class NodataAdapter extends RecyclerView.Adapter<NodataAdapter.NoDataViewHolder> {

    Context context;
    ArrayList<NoData> noDatalist;

    public NodataAdapter(Context context, ArrayList<NoData> noDatalist) {
        this.context = context;
        this.noDatalist = noDatalist;
    }


    @NonNull
    @Override
    public NoDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.nodata,parent,false);

        return new NoDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoDataViewHolder holder, int position) {

        NoData noData = noDatalist.get(position);
        holder.binding.textView3.setText(noData.getHeading());
        holder.binding.nodataimage.setImageResource(R.drawable.usernotfound);
    }

    @Override
    public int getItemCount() {
        return noDatalist.size();
    }

    public class NoDataViewHolder extends RecyclerView.ViewHolder{
        NodataBinding binding;

        public NoDataViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=NodataBinding.bind(itemView);
        }
    }
}
