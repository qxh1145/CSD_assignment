package csd_assightment.bst;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

class Book {
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

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public String getPublisher() {
        return publisher;
    }

    @Override
    public String toString() {
        return "Book{isbn='" + isbn + "', title='" + title + "', author='" + author + "', year=" + year + "}";
    }
}

public class Main {
    class Node {
        Book book;
        Node left, right;

        Node(Book book) {
            this.book = book;
            left = right = null;
        }
    }

    private Node root;

    public Main() {
        root = null;
    }

    public void loadFromCsv(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine(); // Bỏ qua dòng tiêu đề
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String isbn = data[0];
                String title = data[1];
                String author = data[2];
                int year = Integer.parseInt(data[3]);
                String publisher = data[4];
                insert(new Book(isbn, title, author, year, publisher));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save books back to CSV file (in-order traversal)
    public void saveToCsv(String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write("ISBN,Book-Title,Book-Author,Year-Of-Publication,Publisher\n");
            saveToCsvRec(root, bw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveToCsvRec(Node node, BufferedWriter bw) throws IOException {
        if (node == null) return;
        saveToCsvRec(node.left, bw);
        bw.write(String.format("%s,%s,%s,%d,%s\n",
                node.book.getIsbn(), node.book.getTitle(), node.book.getAuthor(),
                node.book.getYear(), node.book.getPublisher()));
        saveToCsvRec(node.right, bw);
    }

    private void insert(Book book) {
        root = insertRec(root, book);
    }

    private Node insertRec(Node root, Book book) {
        if (root == null) return new Node(book);
        if (book.getIsbn().compareTo(root.book.getIsbn()) < 0) {
            root.left = insertRec(root.left, book);
        } else if (book.getIsbn().compareTo(root.book.getIsbn()) > 0) {
            root.right = insertRec(root.right, book);
        }
        return root;
    }

    public Book search(String isbn) {
        return searchRec(root, isbn);
    }

    private Book searchRec(Node root, String isbn) {
        if (root == null || root.book.getIsbn().equals(isbn)) {
            return root != null ? root.book : null;
        }
        if (isbn.compareTo(root.book.getIsbn()) < 0) {
            return searchRec(root.left, isbn);
        }
        return searchRec(root.right, isbn);
    }

    // Delete the first element (smallest ISBN)
    public void deleteFirst() {
        root = deleteFirstRec(root);
    }

    private Node deleteFirstRec(Node root) {
        if (root == null) return null;
        if (root.left == null) return root.right;
        root.left = deleteFirstRec(root.left);
        return root;
    }

    public static void main(String[] args) {
        Main bst = new Main();
        long start = System.nanoTime();
        bst.loadFromCsv("src/csd_assightment/bst/books.csv");
        long end = System.nanoTime();
        System.out.println("Load time: " + (end - start) + " ns");

        start = System.nanoTime();
        System.out.println("Search 195153448: " + bst.search("195153448"));
        end = System.nanoTime();
        System.out.println("Search time: " + (end - start) + " ns");

        start = System.nanoTime();
        bst.deleteFirst();
        end = System.nanoTime();
        System.out.println("Delete time: " + (end - start) + " ns");
        System.out.println("After deleting first: " + bst.search("2005018"));

        bst.saveToCsv("book1_updated.csv");
    }
}