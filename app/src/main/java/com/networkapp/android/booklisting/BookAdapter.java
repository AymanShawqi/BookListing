package com.networkapp.android.booklisting;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {
    Context mContext;

    public BookAdapter(@NonNull Context context, @NonNull List<Book> books) {
        super(context, 0, books);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
            holder.titleTextView = convertView.findViewById(R.id.title);
            holder.authorsTextView = convertView.findViewById(R.id.authors);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Book currentBook = getItem(position);
        holder.titleTextView.setText(currentBook.getTitle());
        holder.authorsTextView.setText(currentBook.getAuthors());
        return convertView;
    }

    private static class ViewHolder {
        TextView titleTextView;
        TextView authorsTextView;
    }
}
