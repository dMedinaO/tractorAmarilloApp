package com.example.tractoramarilloapp.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tractoramarilloapp.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<String> names;
    private int layout;
    private OnItemClickListener listener;


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewName,textViewMaqImple,textViewFaena;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textUsuario);
            this.textViewMaqImple = (TextView) itemView.findViewById(R.id.textMaquinariaImplemento);
            this.textViewFaena = (TextView) itemView.findViewById(R.id.textFaena);
        }

        public void bind(final String name, final OnItemClickListener listener){
            this.textViewName.setText(name);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(name, getAdapterPosition());
                }
            });
        }
    }

    public RecyclerAdapter(List<String> names, int layout, OnItemClickListener listener){

        this.names = names;
        this.layout = layout;
        this.listener = listener;

    }

    public interface OnItemClickListener{
        void onItemClick(String name, int position);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(names.get(i), listener);
    }

    @Override
    public int getItemCount() {
        return names.size();
    }



}
