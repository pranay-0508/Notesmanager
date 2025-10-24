import java.io.*;
import java.util.Scanner;
public class NotesManager {
    private static final String FILE_NAME = System.getProperty("user.home")
            + File.separator + "Documents" + File.separator + "notes.txt";
    private static final int MAX_NOTES = 100;
    private static String[] titles = new String[MAX_NOTES];
    private static String[] contents = new String[MAX_NOTES];
    private static int noteCount = 0;
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        loadNotes();
        while (true) {
            printMenu();
            System.out.print("Enter choice: ");
            int choice;
            if (!scanner.hasNextInt()) {
                System.out.println(" Please enter a valid number.");
                scanner.nextLine();
                continue;
            }
            choice = scanner.nextInt();
            scanner.nextLine(); 
            switch (choice) {
                case 1 -> addNote(scanner);
                case 2 -> viewTitles();
                case 3 -> openNote(scanner);
                case 4 -> deleteNote(scanner);
                case 5 -> {
                    saveNotes();
                    System.out.println(" Notes saved manually.");
                }
                case 6 -> {
                    System.out.println(" Auto-saving before exit...");
                    saveNotes();
                    System.out.println(" Exiting program. Bye!");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice! Try again.");
            }
        }
    }
    private static void printMenu() {
        System.out.println("\n===== NOTES MANAGER =====");
        System.out.println("1. Add New Note");
        System.out.println("2. View Note Titles");
        System.out.println("3. Open a Note");
        System.out.println("4. Delete a Note");
        System.out.println("5. Save Notes");
        System.out.println("6. Exit");
    }
    private static void addNote(Scanner scanner) {
        if (noteCount >= MAX_NOTES) {
            System.out.println(" Maximum note limit reached!");
            return;
        }
        System.out.print("Enter note title: ");
        String title = scanner.nextLine();
        System.out.println("Enter note content (end with a single '.' on a new line):");
        StringBuilder content = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).equals(".")) {
            content.append(line).append(System.lineSeparator());
        }
        titles[noteCount] = title;
        contents[noteCount] = content.toString().trim();
        noteCount++;
        System.out.println(" Note added successfully!");
        autoSave();
    }
    private static void viewTitles() {
        if (noteCount == 0) {
            System.out.println(" No notes found.");
            return;
        }
        System.out.println("\n--- Saved Notes ---");
        for (int i = 0; i < noteCount; i++) {
            System.out.println((i + 1) + ". " + titles[i]);
        }
        System.out.println("-------------------");
    }
    private static void openNote(Scanner scanner) {
        if (noteCount == 0) {
            System.out.println(" No notes available.");
            return;
        }
        viewTitles();
        System.out.print("Enter note number to open: ");
        int index = scanner.nextInt();
        scanner.nextLine(); // consume newline
        if (index < 1 || index > noteCount) {
            System.out.println(" Invalid note number!");
            return;
        }
        int i = index - 1;
        System.out.println("\n===== " + titles[i] + " =====");
        System.out.println(contents[i]);
        System.out.println("============================");
    }
    private static void deleteNote(Scanner scanner) {
        if (noteCount == 0) {
            System.out.println(" No notes to delete.");
            return;
        }
        viewTitles();
        System.out.print("Enter note number to delete: ");
        int index = scanner.nextInt();
        scanner.nextLine();
        if (index < 1 || index > noteCount) {
            System.out.println("Invalid note number!");
            return;
        }
        for (int i = index - 1; i < noteCount - 1; i++) {
            titles[i] = titles[i + 1];
            contents[i] = contents[i + 1];
        }
        titles[--noteCount] = null;
        contents[noteCount] = null;
        System.out.println(" Note deleted!");
        autoSave();
    }
    private static void saveNotes() {
        try {
            File dir = new File(FILE_NAME).getParentFile();
            if (!dir.exists()) dir.mkdirs(); 
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                for (int i = 0; i < noteCount; i++) {
                    writer.write("TITLE:" + titles[i]);
                    writer.newLine();
                    writer.write("CONTENT:" + contents[i].replace("\n", "\\n"));
                    writer.newLine();
                    writer.write("----");
                    writer.newLine();
                }
            }
            System.out.println(" Notes saved to " + FILE_NAME);
        } catch (IOException e) {
            System.out.println(" Error saving notes: " + e.getMessage());
        }
    }
    private static void loadNotes() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line, title = null, content = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("TITLE:")) {
                    title = line.substring(6).trim();
                } else if (line.startsWith("CONTENT:")) {
                    content = line.substring(8).replace("\\n", "\n");
                } else if (line.equals("----") && title != null && content != null) {
                    titles[noteCount] = title;
                    contents[noteCount] = content;
                    noteCount++;
                    title = null;
                    content = null;
                }
            }
            if (noteCount > 0)
                System.out.println(" Loaded " + noteCount + " notes from " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("Error loading notes: " + e.getMessage());
        }
    }
    private static void autoSave() {
        saveNotes();
    }
}