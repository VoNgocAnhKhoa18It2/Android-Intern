package com.vnakhoa.midtest_intern.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vnakhoa.midtest_intern.R;
import com.vnakhoa.midtest_intern.model.Magic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AdapterMagic extends RecyclerView.Adapter<AdapterMagic.Holder> {

    private Activity context;
    private ArrayList<Magic> list;

    public AdapterMagic(Activity context, ArrayList<Magic> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_magic,parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        Magic magic = list.get(position);
        if (magic.isOpen()) {
            Picasso.get().load(magic.getUrlMagic()).into(holder.imgMagic);
        } else {
            holder.imgMagic.setImageResource(R.drawable.bb);
        }
        holder.imgMagic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Picasso.get().load(magic.getUrlMagic()).into(holder.imgMagic);
                magic.setOpen(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for  (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else {
            for  (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition,toPosition);
    }

    public void randomMagic() {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setOpen(false);
            int j = new Random().nextInt(list.size()-1);
            int k = new Random().nextInt(list.size()-1);
            Collections.swap(list, j, k);
        }
        notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder{
        ImageView imgMagic;
        public Holder(@NonNull View itemView) {
            super(itemView);
            imgMagic = itemView.findViewById(R.id.imgMagic);

        }
    }
}
