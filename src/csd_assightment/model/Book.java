package csd_assightment.model;

public class Book {
    private String isbn;
    private String title;
    private String author;
    private int year;
    private String publisher;

    public Book(String isbn, String title, String author, int year, String publisher) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.year = year;
        this.publisher = publisher;
    }

    public String getIsbn() {
        return isbn;
    }

    @Override
    public String toString() {
        return "Book{isbn='" + isbn + "', title='" + title + "', author='" + author + "', year=" + year + "}";
    }
}