package com.abdullah.shelf;

public class Book {
    private String name;
    private String author;
    private String isbn;
    private String pubYear;

    public Book(String name, String author, String isbn, String pubYear) {
        this.name = name;
        this.author = author;
        this.isbn = isbn;
        this.pubYear = pubYear;
    }

    // Getters
    public String getName() { return name; }
    public String getAuthor() { return author; }
    public String getISBN() { return isbn; }
    public String getPubYear() { return pubYear; }

}