package com.abdullah.shelf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    List<Book> books;

    public BookAdapter(List<Book> bookList)
    {
        this.books = bookList;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder{
        TextView title, author, isbn, pubYear;

        public BookViewHolder(View itemview)
        {
            super(itemview);
            title = itemview.findViewById(R.id.bookTitle);
            author = itemview.findViewById(R.id.bookAuthor);
            isbn = itemview.findViewById(R.id.bookISBN);
            pubYear = itemview.findViewById(R.id.bookPubYear);
        }
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item_layout, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.title.setText(book.getName());
        holder.isbn.setText(book.getISBN());
        holder.author.setText(book.getAuthor());
        holder.pubYear.setText(book.getPubYear());
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
