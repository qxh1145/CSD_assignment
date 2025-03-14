package AVLTree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author ASUS PC
 */
public class main {

    // Book class remains unchanged
    public class Book {

        private String yearOfPublication, isbn, bookTitle, bookAuthor, publisher, imageUrlS, imageUrlM, imageUrlL;
        private int id;

        public Book(String isbn, String bookTitle, String bookAuthor, String publisher, String imageUrlS, String imageUrlM, String imageUrlL, String yearOfPublication, int id) {
            this.isbn = isbn;
            this.bookTitle = bookTitle;
            this.bookAuthor = bookAuthor;
            this.publisher = publisher;
            this.imageUrlS = imageUrlS;
            this.imageUrlM = imageUrlM;
            this.imageUrlL = imageUrlL;
            this.yearOfPublication = yearOfPublication;
            this.id = id;
        }

        public String getIsbn() {
            return isbn;
        }

        public String getBookTitle() {
            return bookTitle;
        }

        public String getBookAuthor() {
            return bookAuthor;
        }

        public String getPublisher() {
            return publisher;
        }

        public String getImageUrlS() {
            return imageUrlS;
        }

        public String getImageUrlM() {
            return imageUrlM;
        }

        public String getImageUrlL() {
            return imageUrlL;
        }

        public String getYearOfPublication() {
            return yearOfPublication;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "Book{" + "isbn=" + isbn + ", bookTitle=" + bookTitle + ", bookAuthor=" + bookAuthor + ", publisher=" + publisher + ", imageUrlS=" + imageUrlS + ", imageUrlM=" + imageUrlM + ", imageUrlL=" + imageUrlL + ", yearOfPublication=" + yearOfPublication + ", id=" + id + '}';
        }
    }

    // Optimized AVL Tree implementation
    public class BalanceBinSearchTree {

        class Node {

            Book data;
            Node right, left;
            // Add height field for faster balance calculations
            int height;

            public Node(Book x, Node left, Node right) {
                this.data = x;
                this.left = left;
                this.right = right;
                this.height = 1; // New node is initially at height 1
            }

            public Node(Book x) {
                this(x, null, null);
            }
        }

        Node root;
        int idx = -1;
        Node[] arrayNodes = new Node[20];

        public BalanceBinSearchTree() {
            root = null;
        }

        boolean isEmpty() {
            return root == null;
        }

        // Optimized height method using stored height
        int height(Node node) {
            if (node == null) {
                return 0;
            }
            return node.height;
        }

        // Update height of a node
        void updateHeight(Node node) {
            if (node != null) {
                node.height = 1 + Math.max(height(node.left), height(node.right));
            }
        }

        int maxInt(int a, int b) {
            return a > b ? a : b;
        }

        boolean isBalanced(Node node) {
            if (node == null) {
                return true;
            }
            return Math.abs(height(node.left) - height(node.right)) <= 1;
        }

        int getBalance(Node node) {
            if (node == null) {
                return 0;
            }
            return height(node.left) - height(node.right);
        }

        Node insertRec(Book x, Node node) {
            if (node == null) {
                return new Node(x);
            }
            if (x.getId() < node.data.getId()) {
                node.left = insertRec(x, node.left);
            } else if (x.getId() > node.data.getId()) {
                node.right = insertRec(x, node.right);
            } else {
                // Duplicate ID, do nothing or update as needed
                return node;
            }

            // Update height
            updateHeight(node);
            return node;
        }

        void insertRec(Book x) {
            root = insertRec(x, root);
        }

        // Optimized balance method
        void balance(int fIdx, int lIdx, Node[] nodes) {
            if (fIdx > lIdx) {
                return;
            }
            int mid = (fIdx + lIdx) / 2;
            insertRec(nodes[mid].data);
            balance(fIdx, mid - 1, nodes);
            balance(mid + 1, lIdx, nodes);
        }

        void balance() {
            // Resize array if needed
            if (idx >= arrayNodes.length - 1) {
                Node[] newArray = new Node[arrayNodes.length * 2];
                System.arraycopy(arrayNodes, 0, newArray, 0, arrayNodes.length);
                arrayNodes = newArray;
            }

            // Save tree to array
            idx = -1;
            saveOrder(root, arrayNodes);

            root = null; // Delete tree
            balance(0, idx, arrayNodes);
        }

        Node balanceRot(Node node) {
            if (node == null) {
                return null;
            }

            // Update height
            updateHeight(node);

            int balance = getBalance(node);

            // Right heavy
            if (balance < -1) {
                if (getBalance(node.right) > 0) { // Right-Left case
                    node.right = rightRotate(node.right);
                }
                return leftRotate(node); // Right-Right case
            }

            // Left heavy
            if (balance > 1) {
                if (getBalance(node.left) < 0) { // Left-Right case
                    node.left = leftRotate(node.left);
                }
                return rightRotate(node); // Left-Left case
            }

            // No rotation needed
            return node;
        }

        void balanceRot() {
            root = balanceRot(root);
        }

        Node leftRotate(Node x) {
            Node y = x.right;
            Node T2 = y.left;

            // Perform rotation
            y.left = x;
            x.right = T2;

            // Update heights
            updateHeight(x);
            updateHeight(y);

            return y;
        }

        Node rightRotate(Node y) {
            Node x = y.left;
            Node T2 = x.right;

            // Perform rotation
            x.right = y;
            y.left = T2;

            // Update heights
            updateHeight(y);
            updateHeight(x);

            return x;
        }

        // Optimized insert with balance
        Node insertBalance(Book x, Node node) {
            // Normal BST insertion
            if (node == null) {
                return new Node(x);
            }

            if (x.getId() < node.data.getId()) {
                node.left = insertBalance(x, node.left);
            } else if (x.getId() > node.data.getId()) {
                node.right = insertBalance(x, node.right);
            } else {
                // Duplicate ID, do nothing or update as needed
                return node;
            }

            // Update height
            updateHeight(node);

            // Get balance factor
            int balance = getBalance(node);

            // Balance if needed
            // Left heavy
            if (balance > 1) {
                if (x.getId() > node.left.data.getId()) { // Left-Right case
                    node.left = leftRotate(node.left);
                    return rightRotate(node);
                } else { // Left-Left case
                    return rightRotate(node);
                }
            }

            // Right heavy
            if (balance < -1) {
                if (x.getId() < node.right.data.getId()) { // Right-Left case
                    node.right = rightRotate(node.right);
                    return leftRotate(node);
                } else { // Right-Right case
                    return leftRotate(node);
                }
            }

            return node;
        }

        void insertBalance(Book x) {
            root = insertBalance(x, root);
        }

        // Batch insertion method for better performance
        void insertBatch(List<Book> books) {
            // Sort books by ID for more balanced insertion
            books.sort(Comparator.comparing(Book::getId));

            // Insert middle element first, then recursively insert left and right halves
            insertBatchRecursive(books, 0, books.size() - 1);
        }

        void insertBatchRecursive(List<Book> books, int start, int end) {
            if (start > end) {
                return;
            }

            int mid = (start + end) / 2;
            insertBalance(books.get(mid));

            // Process left half
            insertBatchRecursive(books, start, mid - 1);

            // Process right half
            insertBatchRecursive(books, mid + 1, end);
        }

        void saveOrder(Node root, Node[] arr) {
            if (root == null) {
                return;
            }

            if (root.left != null) {
                saveOrder(root.left, arr);
            }

            // Check if array needs resizing
            if (idx + 1 >= arr.length) {
                Node[] newArray = new Node[arr.length * 2];
                System.arraycopy(arr, 0, newArray, 0, arr.length);
                arrayNodes = newArray;
                arr = newArray;
            }

            arr[++idx] = root;

            if (root.right != null) {
                saveOrder(root.right, arr);
            }
        }

        // Tree traversal methods remain mostly unchanged
        void preOrder(Node root) {
            if (root == null) {
                return;
            }

            System.out.print(root.data + "     ");
            if (root.left != null) {
                preOrder(root.left);
            }
            if (root.right != null) {
                preOrder(root.right);
            }
        }

        void preOrder() {
            if (isEmpty()) {
                System.out.println("The tree is empty");
                return;
            }
            preOrder(root);
        }

        void postOrder(Node root) {
            if (root == null) {
                return;
            }

            if (root.left != null) {
                postOrder(root.left);
            }
            if (root.right != null) {
                postOrder(root.right);
            }
            System.out.print(root.data + "     ");
        }

        void postOrder() {
            if (isEmpty()) {
                System.out.println("The tree is empty");
                return;
            }
            postOrder(root);
        }

        void inOrder(Node root) {
            if (root == null) {
                return;
            }

            if (root.left != null) {
                inOrder(root.left);
            }
            System.out.println(root.data + "     ");
            if (root.right != null) {
                inOrder(root.right);
            }
        }

        void inOrder() {
            if (isEmpty()) {
                System.out.println("The tree is empty");
                return;
            }
            inOrder(root);
        }

        // Optimized search method
        public Node search(int id, Node node) {
            if (node == null) {
                return null;
            }
            if (node.data.getId() == id) {
                return node;
            }

            if (id < node.data.getId()) {
                return search(id, node.left);
            } else {
                return search(id, node.right);
            }
        }

        // Improved wrapper method with better error handling
        public Book searchBook(int id) {
            Node result = search(id, root);
            if (result == null) {
                System.out.println("Book with ID " + id + " not found.");
                return null;
            }
            return result.data;
        }

        public Node deleteMin(Node node) {
            if (node == null) {
                return null;
            }
            if (node.left == null) {
                return node.right;
            }
            node.left = deleteMin(node.left);

            // Update height and balance
            updateHeight(node);
            return balanceRot(node);
        }

        public void deleteFirst() {
            if (root == null) {
                System.out.println("Tree is empty, nothing to delete.");
                return;
            }
            root = deleteMin(root);
        }
    }

    // Helper method to parse CSV lines properly (handles quoted fields)
    private static String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(field.toString());
                field = new StringBuilder();
            } else {
                field.append(c);
            }
        }

        result.add(field.toString());
        return result.toArray(new String[0]);
    }

    // Main method with optimized file reading and insertion
    public static void main(String[] args) {
        System.out.println("Hello World!");
        String filePath = "src/books.csv";
        main assignment = new main();
        BalanceBinSearchTree tree = assignment.new BalanceBinSearchTree();

        // Determine optimal batch size and thread count
        final int BATCH_SIZE = 1000;
        final int THREAD_COUNT = Runtime.getRuntime().availableProcessors();

        System.out.println("Using " + THREAD_COUNT + " threads for processing");
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<?>> futures = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath), 8192)) { // Larger buffer for faster reading
            String line;
            int i = 1;

            // Skip header if present
            if ((line = br.readLine()) != null && line.startsWith("ISBN")) {
                // This is a header, skip it
                System.out.println("Skipping header: " + line);
            } else {
                // Not a header, process this line
                processLine(assignment, tree, line, i++);
            }

            // Process in batches for better performance
            List<Book> batch = new ArrayList<>(BATCH_SIZE);

            while ((line = br.readLine()) != null) {
                final int bookId = i;
                final String currentLine = line;

                // Parse and create book
                String[] values = parseCsvLine(currentLine);

                if (values.length < 8) {
                    i++;
                    continue;
                }

                Book book = assignment.new Book(
                        values[0], values[1], values[2], values[4],
                        values[5], values[6], values[7], values[3], bookId
                );

                batch.add(book);

                // When batch is full, submit for processing
                if (batch.size() >= BATCH_SIZE) {
                    final List<Book> batchToProcess = new ArrayList<>(batch);
                    futures.add(executor.submit(() -> {
                        System.out.println("Processing batch up to ID: " + batchToProcess.get(batchToProcess.size() - 1).getId());
                        synchronized (tree) {
                            tree.insertBatch(batchToProcess);
                        }
                        return null;
                    }));

                    batch.clear();
                }

                i++;
            }

            // Process any remaining books
            if (!batch.isEmpty()) {
                final List<Book> batchToProcess = new ArrayList<>(batch);
                futures.add(executor.submit(() -> {
                    System.out.println("Processing final batch up to ID: " + batchToProcess.get(batchToProcess.size() - 1).getId());
                    synchronized (tree) {
                        tree.insertBatch(batchToProcess);
                    }
                    return null;
                }));
            }

            // Wait for all tasks to complete
            for (Future<?> future : futures) {
                future.get();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        // Test the search
        System.out.println("==============================      READY    =========================");

        // Perform 3 different searches with time measurements
        System.out.println("\\n==============================  SEARCH OPERATIONS  =========================");

        // Search 1: ID near the beginning
        int searchId1 = 1000;
        long startSearch1 = System.nanoTime();
        Book found1 = tree.searchBook(searchId1);
        long endSearch1 = System.nanoTime();

        if (found1 != null) {
            System.out.println("Found book with ID " + searchId1 + ": " + found1.getBookTitle());
        }
        System.out.println("Search time for ID " + searchId1 + ": " + ((endSearch1 - startSearch1) / 1000000.0) + " ms");

        // Search 2: ID in the middle
        int searchId2 = 100000;
        long startSearch2 = System.nanoTime();
        Book found2 = tree.searchBook(searchId2);
        long endSearch2 = System.nanoTime();

        if (found2 != null) {
            System.out.println("Found book with ID " + searchId2 + ": " + found2.getBookTitle());
        }
        System.out.println("Search time for ID " + searchId2 + ": " + ((endSearch2 - startSearch2) / 1000000.0) + " ms");

        // Search 3: ID near the end or non-existent
        int searchId3 = 270000;
        long startSearch3 = System.nanoTime();
        Book found3 = tree.searchBook(searchId3);
        long endSearch3 = System.nanoTime();

        if (found3 != null) {
            System.out.println("Found book with ID " + searchId3 + ": " + found3.getBookTitle());
        }
        System.out.println("Search time for ID " + searchId3 + ": " + ((endSearch3 - startSearch3) / 1000000.0) + " ms");

        // Perform 3 delete operations with time measurements
        System.out.println("\\n==============================  DELETE OPERATIONS  =========================");

        // Delete 1
        long startDelete1 = System.nanoTime();
        tree.deleteFirst();
        long endDelete1 = System.nanoTime();
        System.out.println("First delete operation time: " + ((endDelete1 - startDelete1) / 1000000.0) + " ms");

        // Delete 2
        long startDelete2 = System.nanoTime();
        tree.deleteFirst();
        long endDelete2 = System.nanoTime();
        System.out.println("Second delete operation time: " + ((endDelete2 - startDelete2) / 1000000.0) + " ms");

        // Delete 3
        long startDelete3 = System.nanoTime();
        tree.deleteFirst();
        long endDelete3 = System.nanoTime();
        System.out.println("Third delete operation time: " + ((endDelete3 - startDelete3) / 1000000.0) + " ms");
    }

    // Helper method to process a single line
    private static void processLine(main assignment, BalanceBinSearchTree tree, String line, int bookId) {
        if (line == null) {
            return;
        }

        String[] values = parseCsvLine(line);

        if (values.length < 8) {
            return;
        }

        Book book = assignment.new Book(
                values[0], values[1], values[2], values[4],
                values[5], values[6], values[7], values[3], bookId
        );

        tree.insertBalance(book);
    }
}
