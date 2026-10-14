package railsystem;


	import java.util.*;

	public class RailSystem {

	    // ---------- STATION GRAPH ----------
	    static class Edge {
	        String destination;
	        int distance;

	        Edge(String destination, int distance) {
	            this.destination = destination;
	            this.distance = distance;
	        }
	    }

	    static class Graph {
	        Map<String, List<Edge>> adj = new LinkedHashMap<>();

	        void addStation(String station) {
	            adj.putIfAbsent(station, new ArrayList<>());
	        }

	        void addEdge(String a, String b, int distance) {
	            addStation(a);
	            addStation(b);
	            adj.get(a).add(new Edge(b, distance));
	            adj.get(b).add(new Edge(a, distance));
	        }

	        boolean contains(String station) {
	            return adj.containsKey(station);
	        }

	        // BFS: route with the fewest edges
	        List<String> bfsPath(String source, String destination) {
	            if (!contains(source) || !contains(destination))
	                return Collections.emptyList();

	            Queue<String> queue = new LinkedList<>();
	            Map<String, String> parent = new HashMap<>();
	            Set<String> visited = new HashSet<>();

	            queue.offer(source);
	            visited.add(source);

	            while (!queue.isEmpty()) {
	                String current = queue.poll();

	                if (current.equals(destination))
	                    break;

	                for (Edge edge : adj.get(current)) {
	                    if (visited.add(edge.destination)) {
	                        parent.put(edge.destination, current);
	                        queue.offer(edge.destination);
	                    }
	                }
	            }

	            if (!visited.contains(destination))
	                return Collections.emptyList();

	            LinkedList<String> path = new LinkedList<>();
	            for (String at = destination; at != null;
	                 at = parent.get(at)) {
	                path.addFirst(at);
	                if (at.equals(source)) break;
	            }
	            return path;
	        }

	        // DFS: traverse reachable stations
	        void dfs(String start) {
	            if (!contains(start)) {
	                System.out.println("Station not found.");
	                return;
	            }

	            Set<String> visited = new LinkedHashSet<>();
	            dfsVisit(start, visited);
	            System.out.println();
	        }

	        void dfsVisit(String station, Set<String> visited) {
	            visited.add(station);
	            System.out.print(station + " ");

	            for (Edge edge : adj.get(station)) {
	                if (!visited.contains(edge.destination))
	                    dfsVisit(edge.destination, visited);
	            }
	        }

	        // Dijkstra: shortest distance using a greedy priority queue
	        Map<String, Integer> dijkstra(String source,
	                                      Map<String, String> parent) {
	            Map<String, Integer> dist = new HashMap<>();
	            for (String station : adj.keySet())
	                dist.put(station, Integer.MAX_VALUE);

	            if (!contains(source))
	                return dist;

	            PriorityQueue<String> pq = new PriorityQueue<>(
	                Comparator.comparingInt(dist::get)
	            );

	            dist.put(source, 0);
	            pq.offer(source);
	            Set<String> processed = new HashSet<>();

	            while (!pq.isEmpty()) {
	                String current = pq.poll();

	                if (!processed.add(current))
	                    continue;

	                for (Edge edge : adj.get(current)) {
	                    if (dist.get(current) == Integer.MAX_VALUE)
	                        continue;

	                    int newDistance =
	                        dist.get(current) + edge.distance;

	                    if (newDistance < dist.get(edge.destination)) {
	                        dist.put(edge.destination, newDistance);
	                        parent.put(edge.destination, current);
	                        pq.offer(edge.destination);
	                    }
	                }
	            }

	            return dist;
	        }

	        void shortestRoute(String source, String destination) {
	            if (!contains(source) || !contains(destination)) {
	                System.out.println("Invalid station.");
	                return;
	            }

	            Map<String, String> parent = new HashMap<>();
	            Map<String, Integer> dist = dijkstra(source, parent);

	            if (dist.get(destination) == Integer.MAX_VALUE) {
	                System.out.println("No route exists.");
	                return;
	            }

	            LinkedList<String> path = new LinkedList<>();
	            String at = destination;

	            while (at != null) {
	                path.addFirst(at);
	                if (at.equals(source)) break;
	                at = parent.get(at);
	            }

	            System.out.println("Shortest route: " +
	                               String.join(" -> ", path));
	            System.out.println("Total distance: " +
	                               dist.get(destination) + " km");
	        }

	        // Greedy: choose the nearest unvisited next station.
	        // This is a heuristic, not guaranteed to find the global shortest path.
	        void greedyRoute(String source, String destination) {
	            if (!contains(source) || !contains(destination)) {
	                System.out.println("Invalid station.");
	                return;
	            }

	            String current = source;
	            Set<String> visited = new HashSet<>();
	            List<String> path = new ArrayList<>();
	            int total = 0;
	            path.add(current);
	            visited.add(current);

	            while (!current.equals(destination)) {
	                Edge best = null;

	                for (Edge edge : adj.get(current)) {
	                    if (!visited.contains(edge.destination) &&
	                        (best == null || edge.distance < best.distance)) {
	                        best = edge;
	                    }
	                }

	                if (best == null) {
	                    System.out.println(
	                        "Greedy route got stuck; no route found by this heuristic."
	                    );
	                    return;
	                }

	                current = best.destination;
	                total += best.distance;
	                visited.add(current);
	                path.add(current);
	            }

	            System.out.println("Greedy route: " +
	                               String.join(" -> ", path));
	            System.out.println("Distance: " + total + " km");
	        }

	        // Distance between directly connected stations
	        int directDistance(String a, String b) {
	            if (!contains(a)) return -1;
	            for (Edge edge : adj.get(a)) {
	                if (edge.destination.equals(b))
	                    return edge.distance;
	            }
	            return -1;
	        }

	        void showStations() {
	            System.out.println("Available stations:");
	            for (String station : adj.keySet())
	                System.out.println("- " + station);
	        }
	    }

	    // ---------- TRAIN ----------
	    static class Train {
	        int number;
	        String name, source, destination, departure;
	        int distance, capacity, availableSeats;

	        Train(int number, String name, String source,
	              String destination, String departure,
	              int distance, int capacity) {
	            this.number = number;
	            this.name = name;
	            this.source = source;
	            this.destination = destination;
	            this.departure = departure;
	            this.distance = distance;
	            this.capacity = capacity;
	            this.availableSeats = capacity;
	        }

	        @Override
	        public String toString() {
	            return number + " | " + name + " | " + source +
	                " -> " + destination + " | Departure: " +
	                departure + " | " + distance + " km | Seats: " +
	                availableSeats + "/" + capacity;
	        }
	    }

	    // ---------- BOOKING ----------
	    static class Booking {
	        String pnr, passenger;
	        int trainNumber;
	        String status;

	        Booking(String pnr, String passenger,
	                int trainNumber, String status) {
	            this.pnr = pnr;
	            this.passenger = passenger;
	            this.trainNumber = trainNumber;
	            this.status = status;
	        }

	        @Override
	        public String toString() {
	            return "PNR: " + pnr +
	                " | Passenger: " + passenger +
	                " | Train: " + trainNumber +
	                " | Status: " + status;
	        }
	    }

	    static Graph graph = new Graph();
	    static Map<Integer, Train> trains = new LinkedHashMap<>();
	    static Map<String, Booking> bookings = new HashMap<>();
	    static Map<Integer, Queue<Booking>> waitingLists = new HashMap<>();
	    static int nextPnr = 1001;

	    // ---------- SAMPLE DATA ----------
	    static void loadData() {
	        String[] stations = {
	            "Vijayawada", "Guntur", "Tenali", "Eluru", "Rajahmundry",
	            "Visakhapatnam", "Hyderabad", "Chennai", "Bengaluru",
	            "Tirupati"
	        };

	        for (String station : stations)
	            graph.addStation(station);

	        graph.addEdge("Vijayawada", "Guntur", 35);
	        graph.addEdge("Vijayawada", "Eluru", 60);
	        graph.addEdge("Vijayawada", "Tenali", 30);
	        graph.addEdge("Eluru", "Rajahmundry", 100);
	        graph.addEdge("Rajahmundry", "Visakhapatnam", 190);
	        graph.addEdge("Guntur", "Hyderabad", 270);
	        graph.addEdge("Tenali", "Chennai", 300);
	        graph.addEdge("Chennai", "Bengaluru", 350);
	        graph.addEdge("Chennai", "Tirupati", 150);
	        graph.addEdge("Hyderabad", "Bengaluru", 570);
	        graph.addEdge("Vijayawada", "Hyderabad", 275);

	        addTrain(new Train(12701, "AP Express", "Vijayawada",
	            "Visakhapatnam", "08:00", 380, 2));
	        addTrain(new Train(12702, "South Express", "Vijayawada",
	            "Hyderabad", "09:30", 275, 3));
	        addTrain(new Train(12703, "Coastal Express", "Eluru",
	            "Visakhapatnam", "10:00", 290, 2));
	        addTrain(new Train(12704, "Chennai Mail", "Tenali",
	            "Chennai", "11:00", 300, 2));
	        addTrain(new Train(12705, "City Express", "Guntur",
	            "Hyderabad", "12:30", 270, 3));
	        addTrain(new Train(12706, "Southern Express", "Chennai",
	            "Bengaluru", "14:00", 350, 2));
	        addTrain(new Train(12707, "Tirupati Express", "Chennai",
	            "Tirupati", "15:30", 150, 2));
	        addTrain(new Train(12708, "Godavari Express", "Rajahmundry",
	            "Visakhapatnam", "17:00", 190, 3));
	    }

	    static void addTrain(Train train) {
	        trains.put(train.number, train);
	        waitingLists.put(train.number, new LinkedList<>());
	    }

	    // ---------- TRAIN SEARCH ----------
	    static void searchByRoute(Scanner sc) {
	        System.out.print("Enter source station: ");
	        String source = sc.nextLine().trim();
	        System.out.print("Enter destination station: ");
	        String destination = sc.nextLine().trim();

	        boolean found = false;
	        for (Train train : trains.values()) {
	            if (train.source.equalsIgnoreCase(source) &&
	                train.destination.equalsIgnoreCase(destination)) {
	                System.out.println(train);
	                found = true;
	            }
	        }

	        if (!found)
	            System.out.println("No direct train found for this route.");
	    }

	    // Merge Sort by distance
	    static List<Train> mergeSort(List<Train> list) {
	        if (list.size() <= 1)
	            return list;

	        int mid = list.size() / 2;
	        List<Train> left =
	            mergeSort(new ArrayList<>(list.subList(0, mid)));
	        List<Train> right =
	            mergeSort(new ArrayList<>(list.subList(mid, list.size())));

	        List<Train> result = new ArrayList<>();
	        int i = 0, j = 0;

	        while (i < left.size() && j < right.size()) {
	            if (left.get(i).distance <= right.get(j).distance)
	                result.add(left.get(i++));
	            else
	                result.add(right.get(j++));
	        }

	        while (i < left.size()) result.add(left.get(i++));
	        while (j < right.size()) result.add(right.get(j++));
	        return result;
	    }

	    static void sortTrains() {
	        List<Train> list = mergeSort(
	            new ArrayList<>(trains.values()));

	        System.out.println("Trains sorted by distance:");
	        for (Train train : list)
	            System.out.println(train);
	    }

	    // Binary Search by train number; list must be sorted.
	    static void binarySearchTrain(Scanner sc) {
	        List<Train> list = new ArrayList<>(trains.values());
	        list.sort(Comparator.comparingInt(t -> t.number));

	        System.out.print("Enter train number: ");
	        int target = readInt(sc);

	        int low = 0, high = list.size() - 1;

	        while (low <= high) {
	            int mid = low + (high - low) / 2;
	            Train train = list.get(mid);

	            if (train.number == target) {
	                System.out.println(train);
	                return;
	            } else if (train.number < target) {
	                low = mid + 1;
	            } else {
	                high = mid - 1;
	            }
	        }

	        System.out.println("Train not found.");
	    }

	    // ---------- TICKETING ----------
	    static void bookTicket(Scanner sc) {
	        System.out.print("Enter train number: ");
	        int number = readInt(sc);

	        Train train = trains.get(number);
	        if (train == null) {
	            System.out.println("Train not found.");
	            return;
	        }

	        System.out.print("Enter passenger name: ");
	        String name = sc.nextLine().trim();

	        if (name.isEmpty()) {
	            System.out.println("Passenger name cannot be empty.");
	            return;
	        }

	        String pnr = "RS" + nextPnr++;
	        String status;

	        if (train.availableSeats > 0) {
	            train.availableSeats--;
	            status = "CONFIRMED";
	        } else {
	            status = "WAITLISTED";
	        }

	        Booking booking = new Booking(pnr, name, number, status);
	        bookings.put(pnr, booking);

	        if (status.equals("WAITLISTED"))
	            waitingLists.get(number).offer(booking);

	        System.out.println("Booking created:");
	        System.out.println(booking);
	    }

	    static void cancelTicket(Scanner sc) {
	        System.out.print("Enter PNR: ");
	        String pnr = sc.nextLine().trim().toUpperCase();

	        Booking booking = bookings.get(pnr);
	        if (booking == null) {
	            System.out.println("PNR not found.");
	            return;
	        }

	        if (booking.status.equals("CANCELLED")) {
	            System.out.println("Ticket is already cancelled.");
	            return;
	        }

	        Train train = trains.get(booking.trainNumber);

	        if (booking.status.equals("CONFIRMED")) {
	            train.availableSeats++;
	            booking.status = "CANCELLED";

	            Queue<Booking> queue =
	                waitingLists.get(booking.trainNumber);

	            // Promote the first passenger still on the waiting list.
	            while (!queue.isEmpty()) {
	                Booking next = queue.poll();
	                if (next.status.equals("WAITLISTED")) {
	                    next.status = "CONFIRMED";
	                    train.availableSeats--;
	                    System.out.println(
	                        "Waiting-list passenger promoted: " + next);
	                    break;
	                }
	            }
	        } else {
	            booking.status = "CANCELLED";
	            // Lazy removal: cancelled entries are skipped during promotion.
	        }

	        System.out.println("Ticket cancelled: " + pnr);
	    }

	    static void checkPNR(Scanner sc) {
	        System.out.print("Enter PNR: ");
	        String pnr = sc.nextLine().trim().toUpperCase();

	        Booking booking = bookings.get(pnr);
	        if (booking == null)
	            System.out.println("PNR not found.");
	        else
	            System.out.println(booking);
	    }

	    static void showBookings() {
	        if (bookings.isEmpty()) {
	            System.out.println("No bookings yet.");
	            return;
	        }

	        System.out.println("All bookings:");
	        for (Booking booking : bookings.values())
	            System.out.println(booking);
	    }

	    static void showWaitingList(Scanner sc) {
	        System.out.print("Enter train number: ");
	        int number = readInt(sc);

	        if (!trains.containsKey(number)) {
	            System.out.println("Train not found.");
	            return;
	        }

	        Queue<Booking> queue = waitingLists.get(number);
	        boolean found = false;
	        System.out.println("Active waiting-list passengers:");

	        for (Booking booking : queue) {
	            if (booking.status.equals("WAITLISTED")) {
	                System.out.println(booking);
	                found = true;
	            }
	        }

	        if (!found)
	            System.out.println("Waiting list is empty.");
	    }

	    static int readInt(Scanner sc) {
	        while (true) {
	            String input = sc.nextLine().trim();
	            try {
	                return Integer.parseInt(input);
	            } catch (NumberFormatException e) {
	                System.out.print("Enter a valid integer: ");
	            }
	        }
	    }

	    // ---------- MAIN MENU ----------
	    public static void main(String[] args) {
	        loadData();
	        Scanner sc = new Scanner(System.in);

	        while (true) {
	            System.out.println("\n========== RAILSYSTEM ==========");
	            System.out.println("1. Display all trains");
	            System.out.println("2. Search train by source/destination");
	            System.out.println("3. BFS: route with fewest edges");
	            System.out.println("4. DFS: traverse railway network");
	            System.out.println("5. Dijkstra: shortest-distance route");
	            System.out.println("6. Greedy route demonstration");
	            System.out.println("7. Sort trains using Merge Sort");
	            System.out.println("8. Search train using Binary Search");
	            System.out.println("9. Book ticket");
	            System.out.println("10. Cancel ticket");
	            System.out.println("11. Check PNR status");
	            System.out.println("12. Display all bookings");
	            System.out.println("13. Display waiting list");
	            System.out.println("14. Display all stations");
	            System.out.println("0. Exit");
	            System.out.print("Enter choice: ");

	            int choice = readInt(sc);

	            switch (choice) {
	                case 1:
	                    for (Train train : trains.values())
	                        System.out.println(train);
	                    break;

	                case 2:
	                    searchByRoute(sc);
	                    break;

	                case 3: {
	                    System.out.print("Source station: ");
	                    String source = sc.nextLine().trim();
	                    System.out.print("Destination station: ");
	                    String destination = sc.nextLine().trim();

	                    List<String> path =
	                        graph.bfsPath(source, destination);

	                    if (path.isEmpty())
	                        System.out.println("No route found.");
	                    else
	                        System.out.println("BFS route: " +
	                            String.join(" -> ", path));
	                    break;
	                }

	                case 4:
	                    System.out.print("Starting station: ");
	                    graph.dfs(sc.nextLine().trim());
	                    break;

	                case 5:
	                    System.out.print("Source station: ");
	                    String source = sc.nextLine().trim();
	                    System.out.print("Destination station: ");
	                    String destination = sc.nextLine().trim();
	                    graph.shortestRoute(source, destination);
	                    break;

	                case 6:
	                    System.out.print("Source station: ");
	                    String gs = sc.nextLine().trim();
	                    System.out.print("Destination station: ");
	                    String gd = sc.nextLine().trim();
	                    graph.greedyRoute(gs, gd);
	                    break;

	                case 7:
	                    sortTrains();
	                    break;

	                case 8:
	                    binarySearchTrain(sc);
	                    break;

	                case 9:
	                    bookTicket(sc);
	                    break;

	                case 10:
	                    cancelTicket(sc);
	                    break;

	                case 11:
	                    checkPNR(sc);
	                    break;

	                case 12:
	                    showBookings();
	                    break;

	                case 13:
	                    showWaitingList(sc);
	                    break;

	                case 14:
	                    graph.showStations();
	                    break;

	                case 0:
	                    System.out.println("Thank you for using RailSystem.");
	                    sc.close();
	                    return;

	                default:
	                    System.out.println("Invalid choice. Try again.");
	            }
	        }
	    }
	}

