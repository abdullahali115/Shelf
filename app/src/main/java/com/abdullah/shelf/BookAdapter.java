package com.abdullah.shelf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {
    List<Book> books;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
        void onEditClick(int position);
    }
    private OnItemClickListener listener;

    public BookAdapter(List<Book> bookList, OnItemClickListener listener)
    {
        this.books = bookList;
        this.listener = listener;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder{
        TextView title, author, isbn, pubYear;
        MaterialCardView delButton, editButton;

        public BookViewHolder(View itemview)
        {
            super(itemview);
            title = itemview.findViewById(R.id.bookTitle);
            author = itemview.findViewById(R.id.bookAuthor);
            isbn = itemview.findViewById(R.id.bookISBN);
            pubYear = itemview.findViewById(R.id.bookPubYear);
            delButton = itemview.findViewById(R.id.delBookButton);
            editButton = itemview.findViewById(R.id.editBookButton);
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
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEditClick(holder.getBindingAdapterPosition());
            }
        });
        holder.delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.widget.Toast.makeText(v.getContext(), "Long Press to Delete", Toast.LENGTH_SHORT).show();
            }
        });
        holder.delButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(listener != null)
                {
                    listener.onDeleteClick(holder.getBindingAdapterPosition());
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
