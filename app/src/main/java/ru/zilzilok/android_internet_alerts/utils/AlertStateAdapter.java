package ru.zilzilok.android_internet_alerts.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import ru.zilzilok.android_internet_alerts.R;

public class AlertStateAdapter extends ArrayAdapter<AlertState> implements ListAdapter {
    private List<AlertState> states;
    private int layout;
    private LayoutInflater inflater;

    public AlertStateAdapter(@NonNull Context context, int resource, @NonNull List<AlertState> objects) {
        super(context, resource, objects);
        states = objects;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(this.layout, parent, false);
        }
        TextView stateText = convertView.findViewById(R.id.textViewConType);
        stateText.setText(states.get(position).getConnectionType());
        ImageView exitButton = convertView.findViewById(R.id.imageViewPoop);
        exitButton.setOnClickListener(v -> {
            states.remove(position);
            notifyDataSetChanged();
        });

        return convertView;
    }
}
