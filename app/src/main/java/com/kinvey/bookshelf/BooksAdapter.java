package com.kinvey.bookshelf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Prots on 3/16/16.
 */
public class BooksAdapter extends BaseAdapter {

    private final List<Book> books;
    private final Context context;

    public BooksAdapter(List<Book> books, Context context){
        this.books = books;
        this.context = context;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Book getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false);
        }
        TextView tv = (TextView) convertView;
        Book book = books.get(position);
        tv.setText(book.getName());
        return tv;
    }
}
