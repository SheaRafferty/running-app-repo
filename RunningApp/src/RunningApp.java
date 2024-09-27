import java.io.*;
import java.nio.file.*;
import java.util.Scanner;

public class RunningApp {
    
    static double weight;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Prompt user for their weight in kg
        System.out.print("Enter your weight in kg: ");
        while (true) {
            try {
                weight = Double.parseDouble(scanner.nextLine());
                if (weight <= 0) {  // Validate user input
                    System.out.print("Please enter a positive weight: ");
                } else {
                    break;
                }
            } catch (NumberFormatException e) { // Handle exceptions
                System.out.print("Invalid input. Please enter your weight as a number: ");
            }
        }

        while (true) {
            // Display a menu with the 3 options ai requested
            System.out.println("\nMenu:");
            System.out.println("1. Process run data from file or directory");
            System.out.println("2. View saved statistics");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    processRunData(scanner);
                    break;
                case "2":
                    viewSavedStatistics(scanner);
                    break;
                case "3":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }

    private static void processRunData(Scanner scanner) {
        System.out.println("Current working directory: " + Paths.get("").toAbsolutePath().toString());

        // Get file or directory name from user
        System.out.print("Enter the file or directory name: ");
        String inputPath = scanner.nextLine();
        File file = new File(inputPath);

        // Validate the user's input to make sure the file exists
        while (!file.exists()) {
            System.out.println("The file or directory does not exist. Try again.");
            System.out.print("Enter the file or directory name: ");
            inputPath = scanner.nextLine();
            file = new File(inputPath);
        }

        // Check if it's a file or directory
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isFile()) {
                        processFile(f);
                    }
                }
            }
        } else {
            processFile(file);
        }

        System.out.println("Processing complete. Returning to main menu.");
    }
    
    // Add a private method for processing/reading the users file

    private static void processFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            double totalDistance = 0;
            double totalTime = 0;
            String line;

            // Prevent errors trying to read the header
            if ((line = br.readLine()) != null) {     
            }

            // Read the rest of the lines containing actual data
            while ((line = br.readLine()) != null) {
                // Split on the tab character
                String[] data = line.split("\\t"); // Use "\\t" to split on tabs

             // Parse the distance and time
                try {
                    double distance = Double.parseDouble(data[0].trim()); 
                    double time = Double.parseDouble(data[1].trim());      

                    totalDistance += distance;
                    totalTime += time;
                } catch (NumberFormatException e) {
                	
                    // Handle problems with missing data or wrong formatting in the users file
                    System.out.println("Invalid data format in file: " + file.getName() + ", skipping line: " + line);
                } catch (ArrayIndexOutOfBoundsException e) {
                    // Handle missing data
                    System.out.println("Missing data in line: " + line + ", skipping.");
                }
            }

            // Convert time to minutes
            totalTime = totalTime / 60;
            
            // Calculate average speed in mph
            double avgSpeed = (totalDistance / 1.609) / (totalTime / 60);
            int roundedSpeed = (int) Math.round(avgSpeed);

            // Get MET value based on speed
            double met = getMet(roundedSpeed);

            // Calculate calories burned
            double caloriesBurned = (3.5 * (totalTime * met * weight)) / 200;

            // Save results to file
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdir();
            }

            File outputFile = new File(outputDir, file.getName());
            try (PrintWriter pw = new PrintWriter(new FileWriter(outputFile))) {
                pw.printf("Calories: %.3f%n", caloriesBurned);
                pw.printf("Speed: %.3f%n", avgSpeed);
                pw.printf("Distance: %.3f%n", totalDistance);
                pw.printf("Duration: %.3f%n", totalTime);
            }
              // Handle exceptions when trying to read the file
        } catch (IOException e) {
            System.out.println("Error reading the file.");
        }
    }
    
    // Add a private method containing the MET table's data

    private static double getMet(int speed) {
        switch (speed) {
            case 5: return 8.3;
            case 6: return 9.8;
            case 7: return 11;
            case 8: return 11.8;
            case 9: return 12.8;
            case 10: return 14.5;
            case 11: return 16;
            case 12: return 19;
            case 13: return 19.8;
            case 14: return 23;
            default: return 8.3; 
        }
    }
    
    // Add a private method for the users saved statistics

    private static void viewSavedStatistics(Scanner scanner) {
        File outputDir = new File("output");
        if (!outputDir.exists() || outputDir.listFiles() == null || outputDir.listFiles().length == 0) {
            System.out.println("No saved statistics. Please process a run first."); // Tell user if there aren't any saved statistics
            return;
        }

        // List files in output directory
        File[] files = outputDir.listFiles();
        System.out.println("Saved statistics:");
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                System.out.println((i + 1) + ". " + files[i].getName());
            }

            // Prompt user to pick a file
            System.out.print("Choose a file (enter the number): ");
            int fileIndex = -1;
            
            // Validate users input and handle any exceptions
            while (true) {
                try {
                    fileIndex = Integer.parseInt(scanner.nextLine()) - 1;
                    if (fileIndex >= 0 && fileIndex < files.length) {
                        break;
                    } else {
                        System.out.print("Invalid number. Choose a valid file number: ");
                    }
                } catch (NumberFormatException e) {
                    System.out.print("Invalid input. Enter a number: ");
                }
            }

            // Display selected file and handle exceptions
            File chosenFile = files[fileIndex];
            try (BufferedReader br = new BufferedReader(new FileReader(chosenFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println("Error reading the file.");
            }
        }
    }
}
