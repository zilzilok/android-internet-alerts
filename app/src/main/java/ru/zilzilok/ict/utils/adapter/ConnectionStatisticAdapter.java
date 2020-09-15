package ru.zilzilok.ict.utils.adapter;

import android.content.Context;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import ru.zilzilok.ict.R;
import ru.zilzilok.ict.utils.connection.ConnectionTypeConverter;
import ru.zilzilok.ict.utils.database.ConnectionInfoDBHelper;

public class ConnectionStatisticAdapter extends BaseAdapter {
    private Context context;
    private ConnectionInfoDBHelper db;
    private int firstVisible = 1;

    private Integer[] imageIds;
    private String[] names = {ConnectionTypeConverter.TYPES._2G, ConnectionTypeConverter.TYPES._3G,
            ConnectionTypeConverter.TYPES._4G, ConnectionTypeConverter.TYPES.WIFI,
            ConnectionTypeConverter.TYPES.NO_INTERNET};

    public ConnectionStatisticAdapter(@NonNull Context context) {
        this.context = context;
        db = new ConnectionInfoDBHelper(context);
        boolean isRus = context.getResources().getBoolean(R.bool.stat_no_internet_type_rus);
        imageIds = new Integer[]{R.drawable.ic_statgroup_2g, R.drawable.ic_statgroup_3g,
                R.drawable.ic_statgroup_4g, R.drawable.ic_statgroup_wifi,
                isRus ? R.drawable.ic_statgroup_no_internet_rus : R.drawable.ic_statgroup_no_internet};
    }

    @Override
    public int getCount() {
        return imageIds.length;
    }

    @Override
    public Object getItem(int position) {
        return imageIds[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;

        if (convertView == null) {
            grid = new View(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            grid = inflater.inflate(R.layout.connection_stat_gridcell, parent, false);
        } else {
            grid = convertView;
        }

        ImageView imageView = grid.findViewById(R.id.imagepart);
        TextView textViewAppeared = grid.findViewById(R.id.textpartAppeared);
        TextView textViewSelected = grid.findViewById(R.id.textpartSelected);
        if (firstVisible == 1) {
            textViewAppeared.setVisibility(View.VISIBLE);
            textViewSelected.setVisibility(View.GONE);
        } else {
            textViewAppeared.setVisibility(View.GONE);
            textViewSelected.setVisibility(View.VISIBLE);
        }
        imageView.setImageResource(imageIds[position]);
        textViewAppeared.setText(String.valueOf(db.getInt(names[position], false)));
        textViewSelected.setText(String.valueOf(db.getInt(names[position], true)));
        return grid;
    }

    public void updateView(int firstVisible) {
        this.firstVisible = firstVisible;
        notifyDataSetChanged();
    }
}
