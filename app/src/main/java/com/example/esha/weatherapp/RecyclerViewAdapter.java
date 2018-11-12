package com.example.esha.weatherapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.TemperatureViewHolder> {

    private ArrayList<Weather> weatherList;

    public RecyclerViewAdapter(ArrayList<Weather> weatherList) {
        this.weatherList = weatherList;
    }

    @NonNull
    @Override
    public TemperatureViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.daily_activity, viewGroup, false);
        TemperatureViewHolder tempHolder=new TemperatureViewHolder(view);
        return tempHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TemperatureViewHolder holder, int i) {
        int id=IconActivity.getIcon(weatherList.get(0).getSummary());
        holder.mDay.setImageResource(id);
        holder.mTemp.setText(String.valueOf(weatherList.get(i).getTemperature()));
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    public class TemperatureViewHolder extends RecyclerView.ViewHolder{

        private ImageView mDay;
        private TextView mTemp;

        public TemperatureViewHolder(@NonNull View itemView) {
            super(itemView);
            mDay=itemView.findViewById(R.id.day);
            mTemp=itemView.findViewById(R.id.daily_temp);
        }
    }
}
