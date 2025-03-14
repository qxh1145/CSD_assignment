
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Book {
    String isbn;
    String title;
    String author;
    int yearOfPublication;
    String publisher;

    public Book(String isbn, String title, String author, int yearOfPublication, String publisher) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.yearOfPublication = yearOfPublication;
        this.publisher = publisher;
    }

    @Override
    public String toString() {
        return "ISBN: " + isbn + ", Title: " + title + ", Author: " + author +
                ", Year: " + yearOfPublication + ", Publisher: " + publisher;
    }
}

class Node {
    Book book;
    Node left, right;

    public Node(Book book) {
        this.book = book;
        this.left = this.right = null;
    }
}

class BinarySearchTree {
    Node root;

    public BinarySearchTree() {
        root = null;
    }

    private String[] parseCSVLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(field.toString().trim());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }
        values.add(field.toString().trim());
        return values.toArray(new String[0]);
    }


    public void readAndInsertFromCSV(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] values = parseCSVLine(line);
                if (values.length >= 5) {
                    try {
                        String isbn = values[0].trim();
                        String title = values[1].trim();
                        String author = values[2].trim();
                        int year = Integer.parseInt(values[3].trim());
                        String publisher = values[4].trim();

                        Book book = new Book(isbn, title, author, year, publisher);
                        insert(book);
                    } catch (NumberFormatException e) {
                        System.err.println(e);
                    }
                }
            }
        }
    }

    public void insert(Book book) {
        root = insertRec(root, book);
    }

    private Node insertRec(Node root, Book book) {
        if (root == null) {
            return new Node(book);
        }

        if (book.isbn.compareTo(root.book.isbn) < 0) {
            root.left = insertRec(root.left, book);
        } else if (book.isbn.compareTo(root.book.isbn) > 0) {
            root.right = insertRec(root.right, book);
        }

        return root;
    }

    public Book searchFirst() {
        Node firstNode = findFirst(root);
        return (firstNode != null) ? firstNode.book : null;
    }

    private Node findFirst(Node root) {
        Node current = root;
        while (current != null && current.left != null) {
            current = current.left;
        }
        return current;
    }

    public void deleteFirst() {
        root = deleteFirstRec(root);
    }

    private Node deleteFirstRec(Node root) {
        if (root == null) {
            return null;
        }
        if (root.left == null) {
            Node temp = root.right;
            return temp;
        }

        Node parent = root;
        Node current = root.left;
        while (current.left != null) {
            parent = current;
            current = current.left;
        }

        parent.left = current.right;
        return root;
    }

    public void inOrder() {
        inOrderRec(root);
    }

    private void inOrderRec(Node root) {
        if (root != null) {
            inOrderRec(root.left);
            System.out.println(root.book);
            inOrderRec(root.right);
        }
    }
}

public class BookManagement {
    public static void main(String[] args) {
        BinarySearchTree bst = new BinarySearchTree();
        String filePath = "books.csv";

        try {
            long startTime = System.nanoTime();
            bst.readAndInsertFromCSV(filePath);
            long endTime = System.nanoTime();
            long insertionTime = endTime - startTime;

            System.out.println("BST after insertion from CSV:");
            bst.inOrder();
            System.out.println("Time to store data in BST: " + insertionTime + " nanoseconds");

            startTime = System.nanoTime();
            Book firstBook = bst.searchFirst();
            endTime = System.nanoTime();
            long searchTime = endTime - startTime;

            // Gộp tất cả thành một System.out.println
            System.out.println(
                    "\nFirst element in BST: " + (firstBook != null ? firstBook : "Tree is empty") + "\n" +
                            "Time to search the first element: " + searchTime + " nanoseconds\n" +
                            "\nBST after deleting the first element:\n" + bstToString(bst, true) + // Gọi hàm phụ với delete
                            "\nTime Complexity Evaluation:\n" +
                            "1. Storing Data in BST:\n" +
                            "   - Average Case: O(n log n), where n is the number of books.\n" +
                            "   - Worst Case: O(n^2) if the tree becomes unbalanced (e.g., sorted input).\n" +
                            "   - For this dataset, assuming random ISBN order, it's closer to O(n log n).\n" +
                            "2. Searching the First Element in BST:\n" +
                            "   - Average Case: O(log n) for a balanced BST.\n" +
                            "   - Worst Case: O(n) if the tree is unbalanced.\n" +
                            "   - For this dataset, likely O(log n) due to random insertion order.\n" +
                            "3. Deleting the First Element in BST:\n" +
                            "   - Average Case: O(log n) for a balanced BST.\n" +
                            "   - Worst Case: O(n) if the tree is unbalanced.\n" +
                            "   - Similar to search, likely O(log n) for this dataset.\n" +
                            "\nWhy I Chose the First Element for Evaluation:\n" +
                            "- The first element (smallest ISBN) is a natural choice for ordered data structures like BSTs.\n" +
                            "- It tests the tree's ability to traverse the leftmost path, which is critical for performance.\n" +
                            "- Deletion of the minimum element is a standard operation in BSTs, often used in priority queues.\n" +
                            "- This choice highlights the BST's strengths and weaknesses, especially regarding balance."
            );

        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }

    // Hàm phụ để lấy chuỗi inOrder và thực hiện delete nếu cần
    private static String bstToString(BinarySearchTree bst, boolean deleteFirst) {
        if (deleteFirst) {
            bst.deleteFirst();
        }
        StringBuilder sb = new StringBuilder();
        inOrderToString(bst.root, sb);
        return sb.toString();
    }

    private static void inOrderToString(Node root, StringBuilder sb) {
        if (root != null) {
            inOrderToString(root.left, sb);
            sb.append(root.book).append("\n");
            inOrderToString(root.right, sb);
        }
    }
}