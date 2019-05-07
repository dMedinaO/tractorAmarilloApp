package com.example.tractoramarilloapp.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tractoramarilloapp.R;
import com.example.tractoramarilloapp.handlers.InformationDetailSession;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<String> names;
    private List<InformationDetailSession> informationDetailSessions;
    private int layout;
    private OnItemClickListener listener;


    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewName,textViewMaqImple,textViewFaena;
        public String idInforme;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewName = (TextView) itemView.findViewById(R.id.textUsuario);
            this.textViewMaqImple = (TextView) itemView.findViewById(R.id.textMaquinariaImplemento);
            this.textViewFaena = (TextView) itemView.findViewById(R.id.textFaena);
        }

        public void bind(final String name, final String line2, final String line3, final String idInforme, final OnItemClickListener listener){
            this.textViewName.setText(name);
            this.textViewMaqImple.setText(line2);
            this.textViewFaena.setText(line3);
            this.idInforme = idInforme;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(idInforme, getAdapterPosition());
                }
            });
        }
    }

    public RecyclerAdapter(List<InformationDetailSession> detailesInformationValues, int layout, OnItemClickListener listener){

        this.informationDetailSessions = detailesInformationValues;//monton de info de la wea XD
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
        String line2 = this.informationDetailSessions.get(i).getMaquinaria().getNameMachine()+" / "+ this.informationDetailSessions.get(i).getImplemento().getNameImplement();

        viewHolder.bind(this.informationDetailSessions.get(i).getUserSession().getNameUser(),line2, this.informationDetailSessions.get(i).getFaena().getNameFaena(), this.informationDetailSessions.get(i).getInformeID(), listener);
    }

    @Override
    public int getItemCount() {
        return informationDetailSessions.size();
    }



}
