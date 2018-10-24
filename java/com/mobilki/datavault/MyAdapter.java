package com.mobilki.datavault;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;


class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private File[] files;

    MyAdapter(File[] files){
        this.files = files;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView mTextView;
        ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.filename);
        }
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.explorer_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(files[position].getName());
    }

    @Override
    public int getItemCount() {
        return files.length;
    }


}
