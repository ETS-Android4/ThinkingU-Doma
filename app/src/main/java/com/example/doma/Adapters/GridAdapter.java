package com.example.doma.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.doma.R;
import com.example.doma.Model.UserData;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class GridAdapter extends BaseAdapter {

    Context context;
    List<UserData> userDataList;
    List<Drawable> drawableList;
    LayoutInflater inflater;

    public GridAdapter(Context context, List<UserData> userDataList, List<Drawable> drawableList) {
        this.context = context;
        this.userDataList = userDataList;
        this.drawableList = drawableList;
    }

    @Override
    public int getCount() {
        return userDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.grid_item, null);

        }

        CircleImageView profileImage = convertView.findViewById(R.id.grid_contact_profile_image);
        TextView name = convertView.findViewById(R.id.grid_contact_name);
        TextView energyPercentage = convertView.findViewById(R.id.grid_energy_percentage);
        energyPercentage.setText(userDataList.get(position).getEnergyPercentage() + " %");
        ProgressBar progressBar = convertView.findViewById(R.id.progress_bar_grid);
        progressBar.setProgressDrawable(drawableList.get(new Random().nextInt(drawableList.size() - 1) + 0));
        progressBar.setProgress(Integer.valueOf(userDataList.get(position).getEnergyPercentage()));
        Picasso.get().load(userDataList.get(position).getImageUrl()).fit().centerCrop().into(profileImage);
        name.setText(userDataList.get(position).getFullName());


        return convertView;
    }
}
