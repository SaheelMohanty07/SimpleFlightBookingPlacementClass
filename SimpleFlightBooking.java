import java.util.ArrayList;
import java.util.Scanner;

public class SimpleFlightBooking {

    static class Flight {
        String flightNo;
        String from;
        String to;
        double price;
        boolean[] booked = new boolean[20]; // seat 1 is booked[0]

        Flight(String flightNo, String from, String to, double price) {
            this.flightNo = flightNo;
            this.from = from;
            this.to = to;
            this.price = price;
        }

        int seatsLeft() {
            int count = 0;
            for (boolean b : booked) {
                if (!b) count++;
            }
            return count;
        }

        void showSeatPlan() {
            System.out.println("\nSeat plan for " + flightNo + " (XX = booked)");
            for (int i = 1; i <= 20; i++) {
                String label = booked[i - 1] ? "XX" : String.valueOf(i);
                System.out.printf("%-4s", label);
                if (i % 4 == 2) System.out.print("  "); // aisle
                if (i % 4 == 0) System.out.println();
            }
        }
    }

    static class Booking {
        int id;
        String name;
        Flight flight;
        ArrayList<Integer> seats;

        Booking(int id, String name, Flight flight, ArrayList<Integer> seats) {
            this.id = id;
            this.name = name;
            this.flight = flight;
            this.seats = seats;
        }
    }

    static Scanner sc = new Scanner(System.in);
    static ArrayList<Flight> flights = new ArrayList<>();
    static ArrayList<Booking> bookings = new ArrayList<>();
    static int nextId = 1;

    public static void main(String[] args) {
        flights.add(new Flight("AI101", "Delhi", "Mumbai", 5500));
        flights.add(new Flight("6E202", "Mumbai", "Chennai", 4200));
        flights.add(new Flight("SG303", "Chennai", "Bengaluru", 2800));

        System.out.println("==============================");
        System.out.println("   WELCOME TO FLIGHT BOOKING");
        System.out.println("==============================");

        while (true) {
            printMenu();
            int choice = readInt("Enter your choice (0-5): ");

            if (choice == 1) {
                showFlights();
            } else if (choice == 2) {
                Flight f = chooseFlight();
                if (f != null) f.showSeatPlan();
            } else if (choice == 3) {
                bookTickets();
            } else if (choice == 4) {
                showBookings();
            } else if (choice == 5) {
                cancelBooking();
            } else if (choice == 0) {
                System.out.println("Visit Us Again!!!");
                break;
            } else {
                System.out.println("Invalid choice.");
            }

            pause(); // let the user read the result before the menu shows again
        }
    }

    static void printMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("  1. View flights");
        System.out.println("  2. View seat plan");
        System.out.println("  3. Book tickets");
        System.out.println("  4. View bookings");
        System.out.println("  5. Cancel booking");
        System.out.println("  0. Exit");
        System.out.println("===============================");
    }

    static void pause() {
        System.out.print("\nPress Enter to return to the menu...");
        sc.nextLine();
    }

    static void showFlights() {
        System.out.println("\nFlight  Route                    Price     Seats left");
        for (Flight f : flights) {
            System.out.printf("%-7s %-24s Rs.%-6.0f %d%n",
                    f.flightNo, f.from + " -> " + f.to, f.price, f.seatsLeft());
        }
    }

    static Flight chooseFlight() {
        showFlights();
        System.out.print("Enter flight number: ");
        String no = sc.nextLine().trim();
        for (Flight f : flights) {
            if (f.flightNo.equalsIgnoreCase(no)) {
                return f;
            }
        }
        System.out.println("Flight not found.");
        return null;
    }

    static void bookTickets() {
        Flight flight = chooseFlight();
        if (flight == null) return;

        flight.showSeatPlan();
        System.out.print("Your name: ");
        String name = sc.nextLine().trim();
        System.out.print("Seat numbers (e.g. 3 4): ");
        String[] parts = sc.nextLine().trim().split("\\s+");

        // Check every seat first, so either all get booked or none
        ArrayList<Integer> seats = new ArrayList<>();
        for (String p : parts) {
            int seat;
            try {
                seat = Integer.parseInt(p);
            } catch (NumberFormatException e) {
                System.out.println("Invalid seat: " + p);
                return;
            }
            if (seat < 1 || seat > 20) {
                System.out.println("Seat " + seat + " does not exist.");
                return;
            }
            if (flight.booked[seat - 1]) {
                System.out.println("Seat " + seat + " is already booked.");
                return;
            }
            if (!seats.contains(seat)) {
                seats.add(seat);
            }
        }

        for (int seat : seats) {
            flight.booked[seat - 1] = true;
        }
        Booking b = new Booking(nextId++, name, flight, seats);
        bookings.add(b);
        System.out.println("\nBooked! Booking ID: " + b.id + " | Seats: " + seats
                + " | Total: Rs." + (seats.size() * flight.price));
    }

    static void showBookings() {
        if (bookings.isEmpty()) {
            System.out.println("\nNo bookings yet.");
            return;
        }
        System.out.println();
        for (Booking b : bookings) {
            System.out.println("ID " + b.id + " | " + b.name + " | " + b.flight.flightNo
                    + " (" + b.flight.from + " -> " + b.flight.to + ") | Seats: " + b.seats
                    + " | Rs." + (b.seats.size() * b.flight.price));
        }
    }

    static void cancelBooking() {
        if (bookings.isEmpty()) {
            System.out.println("\nNo bookings to cancel.");
            return;
        }
        showBookings();
        int id = readInt("Enter booking ID to cancel: ");
        for (Booking b : bookings) {
            if (b.id == id) {
                for (int seat : b.seats) {
                    b.flight.booked[seat - 1] = false; // free the seat
                }
                bookings.remove(b);
                System.out.println("Booking " + id + " cancelled. Seats are free again.");
                return;
            }
        }
        System.out.println("Booking not found.");
    }

    static int readInt(String message) {
        System.out.print(message);
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
