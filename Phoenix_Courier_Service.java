import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Phoenix_Courier_Service {

    // ===================== DATA (parallel ArrayLists) =====================
    static ArrayList<String>  trackingNos  = new ArrayList<>();
    static ArrayList<String>  receivers    = new ArrayList<>();
    static ArrayList<String>  destinations = new ArrayList<>();
    static ArrayList<String>  packageTypes = new ArrayList<>();
    static ArrayList<Double>  weights      = new ArrayList<>();   // kg
    static ArrayList<Integer> priorities   = new ArrayList<>();   // 1=Express 2=Standard 3=Economy
    static ArrayList<Integer> distances    = new ArrayList<>();   // km
    static ArrayList<String>  statuses     = new ArrayList<>();   // Pending / In Queue / Dispatched

    // Dispatch queue (FIFO) holds tracking numbers
    static Queue<String> dispatchQueue = new LinkedList<>();

    // Transaction history
    static ArrayList<String> history = new ArrayList<>();

    // Misc
    static Scanner sc = new Scanner(System.in);
    static int nextId = 1001;
    static long comparisons = 0;

    static final String[] TYPES = {"Document", "Parcel", "Fragile", "Perishable", "Electronics"};

    // Sort field codes
    static final int F_PRIORITY = 1, F_DISTANCE = 2, F_WEIGHT = 3, F_DEST = 4, F_TRACKING = 5;
    // Algorithm codes
    static final int A_MERGE = 1, A_INSERTION = 2;

    // ============================== MAIN ==============================
    public static void main(String[] args) {
        boolean running = true;
        System.out.println("=========================================================");
        System.out.println("""                                                                                                                                               
                     █████████▓███                                                  
                   ███▓▓▓▓▓▓▓▓▓░░▓██                                                
                 ██▓▓▓▓▓▓▓▓▓▓▓▓▒░▒▓██                                               
      ▒▒▒▒▒     █▒░░░▒▓▓████▓▓▓▓░▓▓███                                              
     ▒▒▒▒▒▒▒   █▒░▒▓▓▓██   ████▓▓▓▓▓███▓▒░░▒▓▓███                                   
  ▒▒▒▒▒▒▒▒▒▒▒▒▒▓▒▓▓▓▓██  ███▓▓██▓▓▓▓██▓░░░░░░███                 ██████             
 ▒▒▒▒▒░░░░░▒▒▒▒█▓▓▓▓▓██████▓▓██████████▒░░░██                ██▒░░░▓▓▓▒▒▓███        
 ▒▒▒▒░░░░░░░▒▒▒█▓▓▓▓█▓░░░░██████████████░░▓██▓██████       █▓░░░░▒▒░░░░░░░░▒██      
  ▒▒▒▒░░░░░▒▒▒▒██▓▓▓▒▓░░░░██▓▓██████████▓░░▒▓▓▒░░░▓█▓██  ██░░░░░░░░░░░░░░░░░░▓█     
   ▒▒▒▒▒▒▒▒▒▒▒▒▒██▓▒░▒█▓██▓▓████████▓▓▒░░░▒▒▒▓██▓░░░▒█▒███░░░░░░░░░░░░░░░░░░░░▓█    
   ░▒▒▒▒▒▒▒▒▒▒▒  ██▓▒▓██▓███████▓▓▓▓▒░░░░░░░▒▓░░▓█░░░░▓███▓▓▒░░▒▓▓█████▓▒░░░░░░██   
      ▒             ██▓███▓▓██▓▓▓▓▓░░░░░░░░░░░▓░░░▓░░░░▓█▓▓▓░░░░▓█     ██▓▒░░░░▓█   
                   ██▓██▓▓▓██▓▓▓▓▓▒░░░░░░░░░░░▒▓░░░▒░░░░▓█▓█▒░░▒█       █▓▓▒░░░▓█   
                 ██▓▓██▓▓▓██▓▓▓▓▓▓░▒░░░░░░░░░░░█░░░░░░░░░█▓▒▒██████    █▒▓▓▓▒░░██   
               ███▓███▓▓▓█████▓▓▓▓▓▒░░░░░░░░░░░█▒░░▒░░░░░░█▒▓███▓▓███▓▓░░▓▓▓▓░▒█    
              ███▓▓██▓▓▓▓██▓▓▓█▓▓▓▓░░░░░▒▓▓▓▓▓▒█▒░░▓░░░░░░▒█▓███▓██▒░░░░░▓▓▓▓▓█     
             ███▓▓████████▓▓▓▓▓▓▓▓▓░░░░░░░░░░░░█▓░▒▓░░░░░░░██▓▓██▓░░░░░░▒▓▓▓██      
            ███▓▓▓█▓██▒█▓▒░░░▒▒▒▒▒▓▓▓▓▒░░░░░▓▒▓▓▒▓▓█░░░░░░░▒█▓█▓░░░░░░░░▓███        
           ███▓▓▒▓▓████████▓░░░░░░░░░░░░▒▓█▓▒▓█░░▓██░░░░░░░░██▓▓▓████████           
          ███▓▓░▓▓▓▓██▒░░░██▓░░░░░░░░░░░░░░░██▒░░░▓▓░░░░░░░░▓█▓▓▒░░░░██             
         ███▓▓░░▓▓▓██▓░░░░░▓█▒░░░░░░░░░░▒██████▒░░▓▒░░░░░░░░▒█▒▓▓▒░░░░▒█ ▒▒ ▒▒▒▒    
         ██▓▓░░▒▓▓█▓░░░░░░░░▓▓░░░░░░░░▓██▓▓▓▓████▓█░░░░░▓░░░▒█░▒▓▓░░░░░▒▓▒▒▒▒▒▒▒▒   
           █▒░░▓▓██▒░░░░░░░░░░░░░░░░▒█▒░░░░░░░░▓██▓░░░░░▓▒░░░█▒░▒▒░░░░▒█▒▒░░░░░▒▒▒  
             ██▓▓▓█▒░░░░░░░░░░░░░░░░░░░░░░░░░░░░▒████▓░▓▓▓░░░█▓░░░░░░▓█▒▒░░░░░░░▒▒▒ 
       ▒▒▒▒    █████▒░░░░░░░░░░░░░░░░░░░░░░░░░░░▓▓░░░░░▓▓▓░░░▓▓░░░░░▓█ ▒▒▒░░░░░▒▒▒▒ 
       ▒▒▒░▒▒▒ █▒▒▒▓█▓░░░░░░░░░░░░░░░░░░░░░░░░░▒█░░░░░▒▓▓▓▒░░▓█░░░▒▓██ ▒▒▒▒▒▒▒▒▒    
     ▒▒▒░░░░▒▒ ██▒░░▒██▒░░░░░░░░░░▒▓░░░░░░░░░░░█▒░░░░░▒▓▓▓▓░▓█▒░░░░░░▒█     ▒▒▒▒    
     ▒▒▒▒░░░▒▒▒  █████▓██▓░░░░░▒▒▒▒▒▒░░░░░░░░░██░░░░░▒█████▓░░░░░░░░░░▓█            
       ▒▒▒▒▒▒▒  ██▓█ ██▓▓███▓▒░░▒▒▒▒▒░░░░░░▓▓▒▒▓▓████▓▒▒▒▒▓█▓░░░░░▓░░▒█             
                █▒▒▓███▓▓███ █████▓▓████████▒░░░░░▒▒▒█▒░░░▒███▒░▒▓▒░▓█              
               █▒▒▒▓▒▓▓▓▓▓█▒██▓▓███▓▓▓█████▓░█▓▓████▒█▓▒▒▒██  █░░░░░██              
               ▓▒▒▒▓▒██▒▒██▒▒█░░░░▒██▓▓▓██▒░░▒██████▓█████   █▒░░░░░░░▒██           
               █▓▓▓▓▓██▒▒██▓▓░▒▒▓▒▓░░███▓▒░░░░░█▓▓▓▓▓████▓▓███░░░░░░░░░░▓           
                  █▒▓█▓▓▓█▓▒██▓▓▓░░░▓███▓▓▓██▒█▓▓████   ▓▒▒▓██▓░░▓▒░░░░░▒█          
                   ███▓▓▓█▓█ ██▓▓▒░░█▓░░▓█▓▓▓▓███▓▓██   ▓▒▒▒▓  ██░░░░░░░▓█          
                    ██▓▓██   ██░░░░░▒▓▒▒░░░▒▓▒▓███████  █▓▓▓▓█ █▓▒▒▒▒▒▒▒██                                                                                                                                                                                                                  
         """);
        System.out.println("PHOENIX COURIER LOGISTICS HUB");
        System.out.println("=========================================================");
        while (running) {
            System.out.println("\n╔═══════════╗\n" + //
                                "│ Main Menu │\n" + //
                                "╚═══════════╝\n" + //
                                "");
            System.out.println(" 1. Add package");
            System.out.println(" 2. Display all packages");
            System.out.println(" 3. Search packages");
            System.out.println(" 4. Update package");
            System.out.println(" 5. Remove package");
            System.out.println(" 6. Sort packages");
            System.out.println(" 7. Route optimizer (sort + load dispatch queue)");
            System.out.println(" 8. Dispatch queue menu (enqueue/dequeue/peek/...)");
            System.out.println(" 9. Reports & transaction history");
            System.out.println("10. Load sample data");
            System.out.println("11. About the sorting algorithms");
            System.out.println(" 0. Exit");
            int choice = readInt("Enter choice: ", 0, 11);

            switch (choice) {
                case 1:  addPackage();        break;
                case 2:  printTable("ALL PACKAGES"); break;
                case 3:  searchMenu();        break;
                case 4:  updatePackage();     break;
                case 5:  removePackage();     break;
                case 6:  sortMenu();          break;
                case 7:  routeOptimizer();    break;
                case 8:  queueMenu();         break;
                case 9:  reportMenu();        break;
                case 10: loadSampleData();    break;
                case 11: explainAlgorithms(); break;
                case 0:
                    running = false;
                    System.out.println("\nThank you for using the Courier Logistics Hub. Goodbye!");
                    break;
            }
        }
        sc.close();
    }

    // ============================ INPUT HELPERS ============================
    static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v >= min && v <= max) return v;
                System.out.println("  Invalid: enter a number from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter a whole number.");
            }
        }
    }

    static double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                double v = Double.parseDouble(line);
                if (v >= min && v <= max) return v;
                System.out.println("  Invalid: enter a value from " + min + " to " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter a number.");
            }
        }
    }

    static String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.println("  Input cannot be empty.");
        }
    }

    static boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String line = sc.nextLine().trim().toLowerCase();
            if (line.equals("y") || line.equals("yes")) return true;
            if (line.equals("n") || line.equals("no")) return false;
            System.out.println("  Please answer y or n.");
        }
    }

    static int readType() {
        System.out.println("  Package types:");
        for (int i = 0; i < TYPES.length; i++) {
            System.out.println("    " + (i + 1) + ". " + TYPES[i]);
        }
        return readInt("  Select type: ", 1, TYPES.length) - 1;
    }

    static int readPriority() {
        System.out.println("  Priority: 1 = Express, 2 = Standard, 3 = Economy");
        return readInt("  Select priority: ", 1, 3);
    }

    // ============================ SMALL UTILITIES ============================
    static String priorityName(int p) {
        if (p == 1) return "Express";
        if (p == 2) return "Standard";
        return "Economy";
    }

    static int findIndex(String tracking) {
        for (int i = 0; i < trackingNos.size(); i++) {
            if (trackingNos.get(i).equalsIgnoreCase(tracking)) return i;
        }
        return -1;
    }

    static void log(String message) {
        history.add("#" + (history.size() + 1) + " " + message);
    }

    static void printHeader() {
        System.out.printf("%-4s %-8s %-14s %-14s %-12s %8s %-9s %7s %-10s%n",
                "No.", "Tracking", "Receiver", "Destination", "Type",
                "Wt(kg)", "Priority", "Dist(km)", "Status");
        System.out.println("-------------------------------------------------------------------------------------------");
    }

    static void printRow(int i) {
        System.out.printf("%-4d %-8s %-14.14s %-14.14s %-12s %8.2f %-9s %7d %-10s%n",
                i + 1, trackingNos.get(i), receivers.get(i), destinations.get(i),
                packageTypes.get(i), weights.get(i), priorityName(priorities.get(i)),
                distances.get(i), statuses.get(i));
    }

    static void printTable(String title) {
        System.out.println("\n--- " + title + " ---");
        if (trackingNos.isEmpty()) {
            System.out.println("No packages stored yet. Add a package or load sample data first.");
            return;
        }
        printHeader();
        for (int i = 0; i < trackingNos.size(); i++) printRow(i);
        System.out.println("Total records: " + trackingNos.size());
    }

    // ============================ RECORD OPERATIONS ============================
    // Adds one record to EVERY parallel list so indexes always stay aligned.
    static String addRecord(String receiver, String dest, String type,
                            double weight, int priority, int distance) {
        String tracking = "PKG" + nextId++;
        trackingNos.add(tracking);
        receivers.add(receiver);
        destinations.add(dest);
        packageTypes.add(type);
        weights.add(weight);
        priorities.add(priority);
        distances.add(distance);
        statuses.add("Pending");
        log("Added " + tracking + " -> " + dest + " (" + priorityName(priority) + ")");
        return tracking;
    }

    static void addPackage() {
        System.out.println("\n--- ADD PACKAGE ---");
        String receiver = readText("Receiver name: ");
        String dest = readText("Destination city: ");
        int t = readType();
        double weight = readDouble("Weight in kg (0.1 - 1000): ", 0.1, 1000);
        int priority = readPriority();
        int distance = readInt("Distance in km (1 - 5000): ", 1, 5000);
        String tracking = addRecord(receiver, dest, TYPES[t], weight, priority, distance);
        System.out.println("Package added successfully! Tracking number: " + tracking);
    }

    static void loadSampleData() {
        addRecord("Maria Santos",  "Batangas City", "Parcel",      2.5, 2, 110);
        addRecord("John Reyes",    "Manila",        "Document",    0.3, 1, 95);
        addRecord("Ana Cruz",      "Cebu",          "Fragile",     5.0, 1, 590);
        addRecord("Luis Garcia",   "Lipa",          "Electronics", 3.2, 3, 45);
        addRecord("Rosa Mendoza",  "Davao",         "Perishable",  8.0, 1, 970);
        addRecord("Carlo Dizon",   "Tagaytay",      "Parcel",      1.1, 2, 60);
        addRecord("Nina Bautista", "Baguio",        "Document",    0.5, 3, 250);
        System.out.println("7 sample packages loaded.");
    }

    static void searchMenu() {
        System.out.println("\n--- SEARCH PACKAGES ---");
        if (trackingNos.isEmpty()) {
            System.out.println("No packages to search. The list is empty.");
            return;
        }
        System.out.println("1. By tracking number (exact)");
        System.out.println("2. By receiver name (partial)");
        System.out.println("3. By destination (partial)");
        System.out.println("4. By status");
        System.out.println("0. Back");
        int c = readInt("Choose: ", 0, 4);
        if (c == 0) return;

        String keyword = "";
        if (c == 4) {
            System.out.println("1. Pending  2. In Queue  3. Dispatched");
            int s = readInt("Choose status: ", 1, 3);
            keyword = (s == 1) ? "Pending" : (s == 2) ? "In Queue" : "Dispatched";
        } else {
            keyword = readText("Enter search text: ").toLowerCase();
        }

        boolean found = false;
        for (int i = 0; i < trackingNos.size(); i++) {
            boolean match = false;
            if (c == 1) match = trackingNos.get(i).equalsIgnoreCase(keyword);
            else if (c == 2) match = receivers.get(i).toLowerCase().contains(keyword);
            else if (c == 3) match = destinations.get(i).toLowerCase().contains(keyword);
            else if (c == 4) match = statuses.get(i).equals(keyword);

            if (match) {
                if (!found) {
                    System.out.println();
                    printHeader();
                    found = true;
                }
                printRow(i);
            }
        }
        if (!found) System.out.println("No matching package found.");
    }

    static void updatePackage() {
        System.out.println("\n--- UPDATE PACKAGE ---");
        if (trackingNos.isEmpty()) {
            System.out.println("No packages to update. The list is empty.");
            return;
        }
        String t = readText("Enter tracking number: ");
        int i = findIndex(t);
        if (i == -1) {
            System.out.println("Package '" + t + "' not found. Update failed.");
            return;
        }
        System.out.println();
        printHeader();
        printRow(i);
        System.out.println("\n1. Receiver  2. Destination  3. Type  4. Weight  5. Priority  6. Distance  0. Cancel");
        int c = readInt("Which field to update? ", 0, 6);
        switch (c) {
            case 1: receivers.set(i, readText("New receiver: ")); break;
            case 2: destinations.set(i, readText("New destination: ")); break;
            case 3: packageTypes.set(i, TYPES[readType()]); break;
            case 4: weights.set(i, readDouble("New weight (0.1 - 1000): ", 0.1, 1000)); break;
            case 5: priorities.set(i, readPriority()); break;
            case 6: distances.set(i, readInt("New distance (1 - 5000): ", 1, 5000)); break;
            case 0: System.out.println("Update cancelled."); return;
        }
        log("Updated " + trackingNos.get(i));
        System.out.println("Package updated successfully.");
    }

    static void removePackage() {
        System.out.println("\n--- REMOVE PACKAGE ---");
        if (trackingNos.isEmpty()) {
            System.out.println("No packages to remove. The list is empty.");
            return;
        }
        String t = readText("Enter tracking number: ");
        int i = findIndex(t);
        if (i == -1) {
            System.out.println("Package '" + t + "' not found. Removal failed.");
            return;
        }
        printHeader();
        printRow(i);
        if (!readYesNo("Remove this package?")) {
            System.out.println("Removal cancelled.");
            return;
        }
        String tracking = trackingNos.get(i);
        // remove from EVERY parallel list at the same index
        trackingNos.remove(i);
        receivers.remove(i);
        destinations.remove(i);
        packageTypes.remove(i);
        weights.remove(i);
        priorities.remove(i);
        distances.remove(i);
        statuses.remove(i);
        // keep the queue consistent too
        if (dispatchQueue.remove(tracking)) {
            System.out.println("(Also removed from the dispatch queue.)");
        }
        log("Removed " + tracking);
        System.out.println("Package removed successfully.");
    }

    // ============================ SORTING ============================
    // Compare two records (by index) on the chosen field. Negative => a goes first.
    static int cmp(int a, int b, int field, boolean asc) {
        comparisons++;
        int r;
        switch (field) {
            case F_PRIORITY: r = Integer.compare(priorities.get(a), priorities.get(b)); break;
            case F_DISTANCE: r = Integer.compare(distances.get(a), distances.get(b)); break;
            case F_WEIGHT:   r = Double.compare(weights.get(a), weights.get(b)); break;
            case F_DEST:     r = destinations.get(a).compareToIgnoreCase(destinations.get(b)); break;
            default:         r = trackingNos.get(a).compareTo(trackingNos.get(b)); break;
        }
        return asc ? r : -r;
    }

    /*
     * Both algorithms sort an int[] of record INDEXES, not the lists directly.
     * After sorting, applyOrder() rearranges all 8 parallel lists at once, so
     * every record stays intact (index i still means the same package everywhere).
     */

    // ---- MERGE SORT (divide & conquer, stable, O(n log n)) ----
    static void mergeSort(int[] order, int left, int right, int field, boolean asc) {
        if (left >= right) return;
        int mid = (left + right) / 2;
        mergeSort(order, left, mid, field, asc);
        mergeSort(order, mid + 1, right, field, asc);
        merge(order, left, mid, right, field, asc);
    }

    static void merge(int[] order, int left, int mid, int right, int field, boolean asc) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;
        while (i <= mid && j <= right) {
            // <= keeps equal items in original order (stability)
            if (cmp(order[i], order[j], field, asc) <= 0) temp[k++] = order[i++];
            else temp[k++] = order[j++];
        }
        while (i <= mid)   temp[k++] = order[i++];
        while (j <= right) temp[k++] = order[j++];
        for (k = 0; k < temp.length; k++) order[left + k] = temp[k];
    }

    // ---- INSERTION SORT (simple, stable, great for small / nearly sorted data) ----
    static void insertionSort(int[] order, int field, boolean asc) {
        for (int i = 1; i < order.length; i++) {
            int key = order[i];
            int j = i - 1;
            while (j >= 0 && cmp(order[j], key, field, asc) > 0) {
                order[j + 1] = order[j];
                j--;
            }
            order[j + 1] = key;
        }
    }

    // Rearrange every parallel list according to the sorted index order
    static void applyOrder(int[] order) {
        int n = order.length;
        ArrayList<String>  t1 = new ArrayList<>(trackingNos);
        ArrayList<String>  t2 = new ArrayList<>(receivers);
        ArrayList<String>  t3 = new ArrayList<>(destinations);
        ArrayList<String>  t4 = new ArrayList<>(packageTypes);
        ArrayList<Double>  t5 = new ArrayList<>(weights);
        ArrayList<Integer> t6 = new ArrayList<>(priorities);
        ArrayList<Integer> t7 = new ArrayList<>(distances);
        ArrayList<String>  t8 = new ArrayList<>(statuses);
        for (int i = 0; i < n; i++) {
            trackingNos.set(i, t1.get(order[i]));
            receivers.set(i, t2.get(order[i]));
            destinations.set(i, t3.get(order[i]));
            packageTypes.set(i, t4.get(order[i]));
            weights.set(i, t5.get(order[i]));
            priorities.set(i, t6.get(order[i]));
            distances.set(i, t7.get(order[i]));
            statuses.set(i, t8.get(order[i]));
        }
    }

    // Runs one full sort pass with the chosen algorithm and field
    static void sortRecords(int algo, int field, boolean asc) {
        int n = trackingNos.size();
        int[] order = new int[n];
        for (int i = 0; i < n; i++) order[i] = i;
        comparisons = 0;
        if (algo == A_MERGE) mergeSort(order, 0, n - 1, field, asc);
        else insertionSort(order, field, asc);
        applyOrder(order);
    }

    static String fieldName(int f) {
        switch (f) {
            case F_PRIORITY: return "Priority";
            case F_DISTANCE: return "Distance";
            case F_WEIGHT:   return "Weight";
            case F_DEST:     return "Destination";
            default:         return "Tracking number";
        }
    }

    static void sortMenu() {
        System.out.println("\n--- SORT PACKAGES ---");
        if (trackingNos.size() < 2) {
            System.out.println("Need at least 2 packages to sort. Currently stored: " + trackingNos.size() + ".");
            return;
        }
        System.out.println("1. Merge Sort");
        System.out.println("2. Insertion Sort");
        System.out.println("3. Auto-select (Insertion if 10 or fewer records, otherwise Merge)");
        System.out.println("0. Back");
        int a = readInt("Choose algorithm: ", 0, 3);
        if (a == 0) return;
        if (a == 3) {
            a = (trackingNos.size() <= 10) ? A_INSERTION : A_MERGE;
            System.out.println("Auto-selected: " + (a == A_MERGE ? "Merge Sort" : "Insertion Sort")
                    + " for " + trackingNos.size() + " records.");
        }

        System.out.println("\nSort by: 1. Priority  2. Distance  3. Weight  4. Destination  5. Tracking number");
        int field = readInt("Choose field: ", 1, 5);
        System.out.println("Order: 1. Ascending  2. Descending");
        boolean asc = readInt("Choose order: ", 1, 2) == 1;

        printTable("BEFORE SORTING");
        sortRecords(a, field, asc);
        printTable("AFTER SORTING (" + (a == A_MERGE ? "Merge Sort" : "Insertion Sort")
                + " by " + fieldName(field) + (asc ? ", ascending" : ", descending") + ")");
        System.out.println("Comparisons made: " + comparisons);
        log("Sorted by " + fieldName(field) + " using " + (a == A_MERGE ? "Merge Sort" : "Insertion Sort"));
    }

    static void explainAlgorithms() {
        System.out.println("\n================ ABOUT THE SORTING ALGORITHMS ================");
        System.out.println("MERGE SORT");
        System.out.println("  Purpose : Splits the list in halves, sorts each half, then merges them.");
        System.out.println("  Speed   : O(n log n) in every case; stable (equal items keep their order).");
        System.out.println("  Why here: The hub's package list can grow large (hundreds of parcels per");
        System.out.println("            day). Merge Sort stays fast on big lists, and being stable lets us");
        System.out.println("            sort by distance first, then by priority, so same-priority");
        System.out.println("            parcels stay ordered by shortest distance (the route optimizer).");
        System.out.println();
        System.out.println("INSERTION SORT");
        System.out.println("  Purpose : Builds a sorted list by inserting each item into its correct spot.");
        System.out.println("  Speed   : O(n^2) worst case, but close to O(n) on small or nearly sorted data.");
        System.out.println("  Why here: Handy for small batches (e.g., a rider's few parcels) or when a");
        System.out.println("            few new packages are added to an already sorted list. Very low");
        System.out.println("            overhead and also stable.");
        System.out.println();
        System.out.println("QUEUE (FIFO)");
        System.out.println("  Sorted packages are loaded into the dispatch queue, which processes them");
        System.out.println("  strictly First-In, First-Out: the first package queued is dispatched first.");
    }

    // ============================ ROUTE OPTIMIZER ============================
    // Sort (priority first, then shortest distance) and load Pending packages into the queue.
    static void routeOptimizer() {
        System.out.println("\n--- ROUTE OPTIMIZER ---");
        if (trackingNos.isEmpty()) {
            System.out.println("No packages stored. Add packages or load sample data first.");
            return;
        }
        printTable("BEFORE OPTIMIZING");

        // Two stable Merge Sort passes: minor key (distance) first, major key (priority) last.
        long total = 0;
        sortRecords(A_MERGE, F_DISTANCE, true);
        total += comparisons;
        sortRecords(A_MERGE, F_PRIORITY, true);
        total += comparisons;

        printTable("AFTER OPTIMIZING (Priority first, then shortest distance)");
        System.out.println("Total comparisons (2 merge sort passes): " + total);

        int queued = 0;
        for (int i = 0; i < trackingNos.size(); i++) {
            if (statuses.get(i).equals("Pending")) {
                dispatchQueue.add(trackingNos.get(i));   // enqueue
                statuses.set(i, "In Queue");
                queued++;
            }
        }
        if (queued == 0) {
            System.out.println("No Pending packages to queue (all are already queued or dispatched).");
        } else {
            System.out.println(queued + " package(s) added to the dispatch queue in optimized order.");
            log("Route optimized; " + queued + " package(s) queued");
        }
    }

    // ============================ QUEUE OPERATIONS ============================
    static void queueMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n------------- DISPATCH QUEUE MENU -------------");
            System.out.println("1. Enqueue a package");
            System.out.println("2. Dequeue (dispatch next package)");
            System.out.println("3. Peek at next package");
            System.out.println("4. Display current queue");
            System.out.println("5. Check if queue is empty / size");
            System.out.println("6. FIFO demonstration");
            System.out.println("0. Back to main menu");
            int c = readInt("Choose: ", 0, 6);
            switch (c) {
                case 1: enqueuePackage(); break;
                case 2: dequeuePackage(); break;
                case 3: peekPackage();    break;
                case 4: displayQueue();   break;
                case 5:
                    if (dispatchQueue.isEmpty()) System.out.println("The dispatch queue is EMPTY.");
                    else System.out.println("The queue is NOT empty. Packages waiting: " + dispatchQueue.size());
                    break;
                case 6: fifoDemo(); break;
                case 0: back = true; break;
            }
        }
    }

    static void enqueuePackage() {
        if (trackingNos.isEmpty()) {
            System.out.println("Cannot enqueue: there are no packages stored.");
            return;
        }
        String t = readText("Enter tracking number to enqueue: ");
        int i = findIndex(t);
        if (i == -1) {
            System.out.println("Package '" + t + "' not found. Enqueue failed.");
            return;
        }
        if (!statuses.get(i).equals("Pending")) {
            System.out.println("Cannot enqueue " + trackingNos.get(i) + ": status is '"
                    + statuses.get(i) + "' (only Pending packages can be queued).");
            return;
        }
        dispatchQueue.add(trackingNos.get(i));
        statuses.set(i, "In Queue");
        log("Enqueued " + trackingNos.get(i));
        System.out.println(trackingNos.get(i) + " added to the back of the queue. Position: " + dispatchQueue.size());
    }

    static void dequeuePackage() {
        if (dispatchQueue.isEmpty()) {
            System.out.println("Queue is empty. No package to dispatch.");
            return;
        }
        String t = dispatchQueue.poll();          // removes the FRONT item
        int i = findIndex(t);
        if (i == -1) {
            System.out.println("Dequeued " + t + " but its record no longer exists.");
            return;
        }
        statuses.set(i, "Dispatched");
        log("Dispatched " + t + " to " + destinations.get(i) + " (" + priorityName(priorities.get(i)) + ")");
        System.out.println("Dispatched: " + t + " -> " + destinations.get(i)
                + " for " + receivers.get(i) + " (first in, first out).");
        System.out.println("Packages still waiting: " + dispatchQueue.size());
    }

    static void peekPackage() {
        if (dispatchQueue.isEmpty()) {
            System.out.println("Queue is empty. Nothing to peek at.");
            return;
        }
        String t = dispatchQueue.peek();          // looks at FRONT, does not remove
        int i = findIndex(t);
        System.out.println("Next to be dispatched (not removed):");
        printHeader();
        if (i != -1) printRow(i);
        else System.out.println(t + " (record not found)");
    }

    static void displayQueue() {
        System.out.println("\n--- CURRENT DISPATCH QUEUE ---");
        if (dispatchQueue.isEmpty()) {
            System.out.println("The queue is empty.");
            return;
        }
        System.out.println("FRONT -> next out");
        int pos = 1;
        for (String t : dispatchQueue) {
            int i = findIndex(t);
            if (i != -1) {
                System.out.printf("%2d. %-8s %-14.14s %-9s %6d km%n", pos, t,
                        destinations.get(i), priorityName(priorities.get(i)), distances.get(i));
            } else {
                System.out.printf("%2d. %-8s (record missing)%n", pos, t);
            }
            pos++;
        }
        System.out.println("BACK  <- newest in");
        System.out.println("Packages waiting: " + dispatchQueue.size());
    }

    static void fifoDemo() {
        System.out.println("\n--- FIFO DEMONSTRATION (separate demo queue) ---");
        Queue<String> demo = new ArrayDeque<>();
        String[] sample = {"Parcel A", "Parcel B", "Parcel C"};
        for (int i = 0; i < sample.length; i++) {
            demo.add(sample[i]);
            System.out.println("Enqueue: " + sample[i]);
        }
        System.out.println("Front of queue (peek): " + demo.peek());
        while (!demo.isEmpty()) {
            System.out.println("Dequeue: " + demo.poll());
        }
        System.out.println("Parcels left the queue in the same order they entered: A, B, C = FIFO.");
    }

    // ============================ REPORTS ============================
    static void reportMenu() {
        System.out.println("\n--- REPORTS & HISTORY ---");
        System.out.println("1. Summary report");
        System.out.println("2. Transaction history");
        System.out.println("0. Back");
        int c = readInt("Choose: ", 0, 2);
        if (c == 1) summaryReport();
        else if (c == 2) showHistory();
    }

    static void summaryReport() {
        if (trackingNos.isEmpty()) {
            System.out.println("No data to report. The package list is empty.");
            return;
        }
        int pending = 0, inQueue = 0, dispatched = 0;
        int[] byPriority = new int[4];
        double totalWeight = 0;
        long totalDistance = 0;
        int farthest = 0, heaviest = 0;

        for (int i = 0; i < trackingNos.size(); i++) {
            String s = statuses.get(i);
            if (s.equals("Pending")) pending++;
            else if (s.equals("In Queue")) inQueue++;
            else dispatched++;
            byPriority[priorities.get(i)]++;
            totalWeight += weights.get(i);
            totalDistance += distances.get(i);
            if (distances.get(i) > distances.get(farthest)) farthest = i;
            if (weights.get(i) > weights.get(heaviest)) heaviest = i;
        }
        int n = trackingNos.size();
        System.out.println("Total packages : " + n);
        System.out.println("Pending        : " + pending);
        System.out.println("In queue       : " + inQueue);
        System.out.println("Dispatched     : " + dispatched);
        System.out.println("Express/Standard/Economy: " + byPriority[1] + " / " + byPriority[2] + " / " + byPriority[3]);
        System.out.printf("Total weight   : %.2f kg%n", totalWeight);
        System.out.printf("Average distance: %.1f km%n", (double) totalDistance / n);
        System.out.println("Farthest       : " + trackingNos.get(farthest) + " -> " + destinations.get(farthest)
                + " (" + distances.get(farthest) + " km)");
        System.out.printf("Heaviest       : %s (%.2f kg)%n", trackingNos.get(heaviest), weights.get(heaviest));
    }

    static void showHistory() {
        if (history.isEmpty()) {
            System.out.println("No transactions recorded yet.");
            return;
        }
        System.out.println("\n--- TRANSACTION HISTORY ---");
        for (int i = 0; i < history.size(); i++) System.out.println(history.get(i));
    }
}