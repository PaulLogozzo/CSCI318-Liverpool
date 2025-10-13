package com.riderecs.car_listings_service.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.riderecs.car_listings_service.entity.AuditAction;
import com.riderecs.car_listings_service.entity.AuditLog;
import com.riderecs.car_listings_service.entity.Car;
import com.riderecs.car_listings_service.entity.CarCondition;
import com.riderecs.car_listings_service.entity.CarStatus;
import com.riderecs.car_listings_service.entity.CarView;
import com.riderecs.car_listings_service.entity.Favourite;
import com.riderecs.car_listings_service.entity.Inspection;
import com.riderecs.car_listings_service.entity.InspectionMessage;
import com.riderecs.car_listings_service.entity.InspectionStatus;
import com.riderecs.car_listings_service.entity.MarketAverage;
import com.riderecs.car_listings_service.entity.Notification;
import com.riderecs.car_listings_service.entity.NotificationType;
import com.riderecs.car_listings_service.entity.SavedSearch;
import com.riderecs.car_listings_service.entity.Transaction;
import com.riderecs.car_listings_service.entity.TransactionStatus;
import com.riderecs.car_listings_service.entity.User;
import com.riderecs.car_listings_service.entity.UserRole;
import com.riderecs.car_listings_service.repository.AuditLogRepository;
import com.riderecs.car_listings_service.repository.CarRepository;
import com.riderecs.car_listings_service.repository.CarViewRepository;
import com.riderecs.car_listings_service.repository.FavouriteRepository;
import com.riderecs.car_listings_service.repository.InspectionMessageRepository;
import com.riderecs.car_listings_service.repository.InspectionRepository;
import com.riderecs.car_listings_service.repository.MarketAverageRepository;
import com.riderecs.car_listings_service.repository.NotificationRepository;
import com.riderecs.car_listings_service.repository.SavedSearchRepository;
import com.riderecs.car_listings_service.repository.TransactionRepository;
import com.riderecs.car_listings_service.repository.UserRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TestSetupController {

    @Autowired private UserRepository userRepository;
    @Autowired private CarRepository carRepository;
    @Autowired private FavouriteRepository favouriteRepository;
    @Autowired private SavedSearchRepository savedSearchRepository;
    @Autowired private TransactionRepository transactionRepository;
    @Autowired private InspectionRepository inspectionRepository;
    @Autowired private InspectionMessageRepository inspectionMessageRepository;
    @Autowired private CarViewRepository carViewRepository;
    @Autowired private MarketAverageRepository marketAverageRepository;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private NotificationRepository notificationRepository;

    @PostMapping("/test-setup")
    public ResponseEntity<Map<String, Object>> setupTestData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Clear existing data
            clearAllData();
            
            // Create test data in dependency order
            List<User> users = createUsers();
            result.put("users_created", users.size());
            
            List<Car> cars = createCars(users);
            result.put("cars_created", cars.size());
            
            List<Favourite> favourites = createFavourites(users, cars);
            result.put("favourites_created", favourites.size());
            
            List<SavedSearch> savedSearches = createSavedSearches(users);
            result.put("saved_searches_created", savedSearches.size());
            
            List<Transaction> transactions = createTransactions(users, cars);
            result.put("transactions_created", transactions.size());
            
            List<Inspection> inspections = createInspections(users, cars);
            result.put("inspections_created", inspections.size());
            
            List<InspectionMessage> messages = createInspectionMessages(users, inspections);
            result.put("inspection_messages_created", messages.size());
            
            List<CarView> carViews = createCarViews(users, cars);
            result.put("car_views_created", carViews.size());
            
            List<MarketAverage> marketAverages = createMarketAverages();
            result.put("market_averages_created", marketAverages.size());
            
            List<AuditLog> auditLogs = createAuditLogs(users);
            result.put("audit_logs_created", auditLogs.size());
            
            List<Notification> notifications = createNotifications(users);
            result.put("notifications_created", notifications.size());
            
            result.put("status", "SUCCESS");
            result.put("message", "All test data created successfully!");
            result.put("total_records", getTotalRecordCount());
            
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Error creating test data: " + e.getMessage());
            e.printStackTrace();
        }
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/test-setup/status")
    public ResponseEntity<Map<String, Object>> getDataStatus() {
        Map<String, Object> status = new HashMap<>();
        
        status.put("users", userRepository.count());
        status.put("cars", carRepository.count());
        status.put("favourites", favouriteRepository.count());
        status.put("saved_searches", savedSearchRepository.count());
        status.put("transactions", transactionRepository.count());
        status.put("inspections", inspectionRepository.count());
        status.put("inspection_messages", inspectionMessageRepository.count());
        status.put("car_views", carViewRepository.count());
        status.put("market_averages", marketAverageRepository.count());
        status.put("audit_logs", auditLogRepository.count());
        status.put("notifications", notificationRepository.count());
        status.put("total_records", getTotalRecordCount());
        
        return ResponseEntity.ok(status);
    }

    @DeleteMapping("/test-setup/clear")
    public ResponseEntity<Map<String, Object>> clearTestData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            long totalBefore = getTotalRecordCount();
            clearAllData();
            result.put("status", "SUCCESS");
            result.put("message", "All test data cleared successfully!");
            result.put("records_deleted", totalBefore);
            result.put("remaining_records", getTotalRecordCount());
        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", "Error clearing test data: " + e.getMessage());
        }
        
        return ResponseEntity.ok(result);
    }

    private void clearAllData() {
        // Clear in reverse dependency order
        notificationRepository.deleteAll();
        auditLogRepository.deleteAll();
        carViewRepository.deleteAll();
        inspectionMessageRepository.deleteAll();
        inspectionRepository.deleteAll();
        transactionRepository.deleteAll();
        marketAverageRepository.deleteAll();
        favouriteRepository.deleteAll();
        savedSearchRepository.deleteAll();
        carRepository.deleteAll();
        userRepository.deleteAll();
    }

    private long getTotalRecordCount() {
        return userRepository.count() +
               carRepository.count() +
               favouriteRepository.count() +
               savedSearchRepository.count() +
               transactionRepository.count() +
               inspectionRepository.count() +
               inspectionMessageRepository.count() +
               carViewRepository.count() +
               marketAverageRepository.count() +
               auditLogRepository.count() +
               notificationRepository.count();
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<>();
        
        // Admin users
        users.add(createUser("admin", "admin@example.com", "Admin", "User", "+61400000000", UserRole.BOTH));
        users.add(createUser("superadmin", "superadmin@example.com", "Super", "Admin", "+61400000001", UserRole.BOTH));
        
        // Sellers
        users.add(createUser("john_seller", "john@example.com", "John", "Doe", "+61400123456", UserRole.BOTH));
        users.add(createUser("jane_seller", "jane@example.com", "Jane", "Smith", "+61400987654", UserRole.BOTH));
        users.add(createUser("mike_seller", "mike@example.com", "Mike", "Johnson", "+61400555777", UserRole.SELLER));
        users.add(createUser("sarah_seller", "sarah@example.com", "Sarah", "Williams", "+61400111222", UserRole.BOTH));
        
        // Buyers
        users.add(createUser("bob_buyer", "bob@example.com", "Bob", "Brown", "+61400333444", UserRole.BUYER));
        users.add(createUser("alice_buyer", "alice@example.com", "Alice", "Davis", "+61400666777", UserRole.BUYER));
        users.add(createUser("charlie_buyer", "charlie@example.com", "Charlie", "Wilson", "+61400888999", UserRole.BOTH));
        users.add(createUser("diana_buyer", "diana@example.com", "Diana", "Garcia", "+61400777888", UserRole.BUYER));
        
        // Both buyers and sellers
        users.add(createUser("alex_both", "alex@example.com", "Alex", "Martinez", "+61400999000", UserRole.BOTH));
        users.add(createUser("taylor_both", "taylor@example.com", "Taylor", "Anderson", "+61400222333", UserRole.BOTH));
        users.add(createUser("jordan_both", "jordan@example.com", "Jordan", "Taylor", "+61400444555", UserRole.BOTH));
        users.add(createUser("casey_both", "casey@example.com", "Casey", "Thomas", "+61400666888", UserRole.BOTH));
        users.add(createUser("morgan_both", "morgan@example.com", "Morgan", "Jackson", "+61400111333", UserRole.BOTH));

        return userRepository.saveAll(users);
    }

    private User createUser(String username, String email, String firstName, String lastName, 
                           String phone, UserRole role) {
        User user = new User(username, email, "password123", firstName, lastName, role);
        user.setPhoneNumber(phone);
        user.setActive(true);
        return user;
    }

    private List<Car> createCars(List<User> users) {
        List<Car> cars = new ArrayList<>();
        List<User> sellers = users.stream()
                .filter(u -> u.getRole() == UserRole.SELLER || u.getRole() == UserRole.BOTH)
                .toList();

        // Car data arrays for variety
        String[] makes = {"Toyota", "Honda", "Ford", "BMW", "Audi", "Mercedes-Benz", "Volkswagen", "Nissan", "Hyundai", "Mazda", "Chevrolet", "Subaru"};
        String[] toyotaModels = {"Camry", "Corolla", "Prius", "RAV4", "Highlander"};
        String[] hondaModels = {"Civic", "Accord", "CR-V", "Pilot", "Fit"};
        String[] fordModels = {"Focus", "Mustang", "F-150", "Escape", "Explorer"};
        String[] bmwModels = {"3 Series", "5 Series", "X3", "X5", "Z4"};
        String[] colors = {"Black", "White", "Silver", "Blue", "Red", "Gray", "Green", "Yellow"};
        String[] locations = {"Sydney, NSW", "Melbourne, VIC", "Brisbane, QLD", "Perth, WA", "Adelaide, SA", "Canberra, ACT"};
        CarCondition[] conditions = CarCondition.values();
        
        Random random = new Random();
        
        for (int i = 0; i < 25; i++) { // Create 25 cars
            String make = makes[random.nextInt(makes.length)];
            String model = getModelForMake(make, random);
            
            Car car = new Car();
            car.setMake(make);
            car.setModel(model);
            car.setYear(2018 + random.nextInt(6)); // 2018-2023
            car.setMileage(5000 + random.nextInt(95000)); // 5,000 - 100,000
            car.setCondition(conditions[random.nextInt(conditions.length)]);
            car.setAskingPrice(BigDecimal.valueOf(15000 + random.nextInt(85000))); // $15,000 - $100,000
            car.setDescription("Well-maintained " + make + " " + model + " in " + car.getCondition().toString().toLowerCase() + " condition.");
            car.setColor(colors[random.nextInt(colors.length)]);
            car.setFuelType(random.nextBoolean() ? "Gasoline" : "Hybrid");
            car.setTransmission(random.nextBoolean() ? "Automatic" : "Manual");
            car.setNumberOfDoors(random.nextBoolean() ? 4 : (random.nextBoolean() ? 2 : 5));
            car.setEngineSize(getEngineSize(make, random));
            car.setLocation(locations[random.nextInt(locations.length)]);
            car.setSeller(sellers.get(random.nextInt(sellers.size())));
            car.setContactPhone(car.getSeller().getPhoneNumber());
            car.setContactEmail(car.getSeller().getEmail());
            car.setStatus(i < 3 ? CarStatus.SOLD : (i < 5 ? CarStatus.INACTIVE : CarStatus.ACTIVE)); // Mix of statuses
            
            cars.add(car);
        }

        return carRepository.saveAll(cars);
    }

    private String getModelForMake(String make, Random random) {
        return switch (make) {
            case "Toyota" -> new String[]{"Camry", "Corolla", "Prius", "RAV4", "Highlander"}[random.nextInt(5)];
            case "Honda" -> new String[]{"Civic", "Accord", "CR-V", "Pilot", "Fit"}[random.nextInt(5)];
            case "Ford" -> new String[]{"Focus", "Mustang", "F-150", "Escape", "Explorer"}[random.nextInt(5)];
            case "BMW" -> new String[]{"3 Series", "5 Series", "X3", "X5", "Z4"}[random.nextInt(5)];
            default -> new String[]{"Sedan", "Hatchback", "SUV", "Coupe", "Wagon"}[random.nextInt(5)];
        };
    }

    private String getEngineSize(String make, Random random) {
        String[] sizes = {"1.5L", "1.8L", "2.0L", "2.5L", "3.0L", "3.5L"};
        return sizes[random.nextInt(sizes.length)];
    }

    private List<Favourite> createFavourites(List<User> users, List<Car> cars) {
        List<Favourite> favourites = new ArrayList<>();
        List<User> buyers = users.stream()
                .filter(u -> u.getRole() == UserRole.BUYER || u.getRole() == UserRole.BOTH)
                .toList();
        
        Random random = new Random();
        Set<String> uniquePairs = new HashSet<>();
        
        // Create 30 favourites with unique buyer-car pairs
        while (favourites.size() < 30) {
            User buyer = buyers.get(random.nextInt(buyers.size()));
            Car car = cars.get(random.nextInt(cars.size()));
            String pair = buyer.getId() + "-" + car.getId();
            
            if (!uniquePairs.contains(pair) && !buyer.equals(car.getSeller())) {
                favourites.add(new Favourite(buyer, car));
                uniquePairs.add(pair);
            }
        }
        
        return favouriteRepository.saveAll(favourites);
    }

    private List<SavedSearch> createSavedSearches(List<User> users) {
        List<SavedSearch> searches = new ArrayList<>();
        List<User> buyers = users.stream()
                .filter(u -> u.getRole() == UserRole.BUYER || u.getRole() == UserRole.BOTH)
                .toList();
        
        String[] searchNames = {
            "Affordable Toyota Cars", "Luxury BMW Sedans", "Fuel-Efficient Hybrids", 
            "SUVs Under 30k", "Sports Cars", "Family Sedans", "Compact Cars",
            "Electric Vehicles", "Trucks and Pickups", "Convertibles",
            "Low Mileage Cars", "Recent Models", "Budget Friendly", "Premium Cars"
        };
        
        String[] makes = {"Toyota", "Honda", "Ford", "BMW", "Audi", null};
        String[] locations = {"Sydney", "Melbourne", "Brisbane", null};
        CarCondition[] conditions = {null, CarCondition.EXCELLENT, CarCondition.GOOD};
        
        Random random = new Random();
        
        for (int i = 0; i < 20; i++) { // Create 20 saved searches
            User buyer = buyers.get(random.nextInt(buyers.size()));
            SavedSearch search = new SavedSearch(searchNames[i % searchNames.length], buyer);
            
            if (random.nextBoolean()) search.setMake(makes[random.nextInt(makes.length)]);
            if (random.nextBoolean()) search.setMinPrice(BigDecimal.valueOf(10000 + random.nextInt(40000)));
            if (random.nextBoolean()) search.setMaxPrice(BigDecimal.valueOf(20000 + random.nextInt(80000)));
            if (random.nextBoolean()) search.setMinYear(2015 + random.nextInt(5));
            if (random.nextBoolean()) search.setMaxYear(2020 + random.nextInt(4));
            if (random.nextBoolean()) search.setCondition(conditions[random.nextInt(conditions.length)]);
            if (random.nextBoolean()) search.setLocation(locations[random.nextInt(locations.length)]);
            search.setEmailNotifications(random.nextBoolean());
            
            searches.add(search);
        }
        
        return savedSearchRepository.saveAll(searches);
    }

    private List<Transaction> createTransactions(List<User> users, List<Car> cars) {
        List<Transaction> transactions = new ArrayList<>();
        List<User> buyers = users.stream()
                .filter(u -> u.getRole() == UserRole.BUYER || u.getRole() == UserRole.BOTH)
                .toList();
        
        List<Car> soldCars = cars.stream()
                .filter(c -> c.getStatus() == CarStatus.SOLD || c.getStatus() == CarStatus.INACTIVE)
                .toList();
        
        TransactionStatus[] statuses = TransactionStatus.values();
        String[] paymentMethods = {"Credit Card", "Bank Transfer", "Cash", "Financing", "Crypto"};
        Random random = new Random();
        
        for (int i = 0; i < Math.min(15, soldCars.size()); i++) { // Create up to 15 transactions
            Car car = soldCars.get(i);
            User buyer = buyers.get(random.nextInt(buyers.size()));
            
            if (!buyer.equals(car.getSeller())) {
                Transaction transaction = new Transaction(
                    car, car.getSeller(), buyer, 
                    car.getAskingPrice().subtract(BigDecimal.valueOf(random.nextInt(5000))), // Sale price slightly lower
                    car.getAskingPrice()
                );
                
                transaction.setStatus(statuses[random.nextInt(statuses.length)]);
                transaction.setPaymentMethod(paymentMethods[random.nextInt(paymentMethods.length)]);
                transaction.setNotes("Transaction for " + car.getMake() + " " + car.getModel());
                transaction.setTransactionDate(LocalDateTime.now().minusDays(random.nextInt(90)));
                
                transactions.add(transaction);
            }
        }
        
        return transactionRepository.saveAll(transactions);
    }

    private List<Inspection> createInspections(List<User> users, List<Car> cars) {
        List<Inspection> inspections = new ArrayList<>();
        List<User> buyers = users.stream()
                .filter(u -> u.getRole() == UserRole.BUYER || u.getRole() == UserRole.BOTH)
                .toList();
        
        InspectionStatus[] statuses = InspectionStatus.values();
        Random random = new Random();
        
        for (int i = 0; i < 18; i++) { // Create 18 inspections
            Car car = cars.get(random.nextInt(cars.size()));
            User buyer = buyers.get(random.nextInt(buyers.size()));
            
            if (!buyer.equals(car.getSeller())) {
                Inspection inspection = new Inspection(
                    car, buyer, car.getSeller(),
                    LocalDateTime.now().plusDays(1 + random.nextInt(30))
                );
                
                inspection.setStatus(statuses[random.nextInt(statuses.length)]);
                inspection.setDurationMinutes(60 + random.nextInt(60)); // 60-120 minutes
                inspection.setLocation(car.getLocation());
                inspection.setNotes("Inspection requested for " + car.getMake() + " " + car.getModel());
                
                inspections.add(inspection);
            }
        }
        
        return inspectionRepository.saveAll(inspections);
    }

    private List<InspectionMessage> createInspectionMessages(List<User> users, List<Inspection> inspections) {
        List<InspectionMessage> messages = new ArrayList<>();
        
        String[] messageTemplates = {
            "Hi, I'm interested in scheduling an inspection for this car.",
            "What time works best for you this week?",
            "I can meet on weekends if that's more convenient.",
            "The inspection went well, thank you!",
            "Could we reschedule for next week?",
            "I have some questions about the car's history.",
            "Is the car still available for viewing?",
            "Thank you for your time during the inspection.",
            "I noticed some minor issues during the inspection.",
            "The car looks great! Very well maintained."
        };
        
        Random random = new Random();
        
        for (Inspection inspection : inspections) {
            int numMessages = 2 + random.nextInt(6); // 2-7 messages per inspection
            
            for (int i = 0; i < numMessages; i++) {
                User sender = random.nextBoolean() ? inspection.getBuyer() : inspection.getSeller();
                String message = messageTemplates[random.nextInt(messageTemplates.length)];
                
                InspectionMessage inspMsg = new InspectionMessage(inspection, sender, message);
                inspMsg.setIsRead(random.nextBoolean());
                
                messages.add(inspMsg);
            }
        }
        
        return inspectionMessageRepository.saveAll(messages);
    }

    private List<CarView> createCarViews(List<User> users, List<Car> cars) {
        List<CarView> carViews = new ArrayList<>();
        Random random = new Random();
        
        String[] ipAddresses = {
            "192.168.1.1", "10.0.0.1", "172.16.0.1", "203.1.1.1", 
            "134.195.1.1", "8.8.8.8", "1.1.1.1"
        };
        
        String[] userAgents = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 14_7_1 like Mac OS X) AppleWebKit/605.1.15"
        };
        
        // Create 50 car views
        for (int i = 0; i < 50; i++) {
            Car car = cars.get(random.nextInt(cars.size()));
            
            CarView view = new CarView();
            view.setCar(car);
            view.setIpAddress(ipAddresses[random.nextInt(ipAddresses.length)]);
            view.setUserAgent(userAgents[random.nextInt(userAgents.length)]);
            view.setSessionId("session-" + UUID.randomUUID().toString().substring(0, 8));
            
            // 70% chance of being a logged-in user view
            if (random.nextDouble() < 0.7) {
                User viewer = users.get(random.nextInt(users.size()));
                view.setViewer(viewer);
            }
            
            carViews.add(view);
        }
        
        return carViewRepository.saveAll(carViews);
    }

    private List<MarketAverage> createMarketAverages() {
        List<MarketAverage> averages = new ArrayList<>();
        
        String[][] makeModels = {
            {"Toyota", "Camry"}, {"Toyota", "Corolla"}, {"Toyota", "RAV4"},
            {"Honda", "Civic"}, {"Honda", "Accord"}, {"Honda", "CR-V"},
            {"Ford", "Focus"}, {"Ford", "Mustang"}, {"Ford", "F-150"},
            {"BMW", "3 Series"}, {"BMW", "5 Series"}, {"BMW", "X3"}
        };
        
        Random random = new Random();
        
        for (String[] makeModel : makeModels) {
            for (int year = 2019; year <= 2023; year++) {
                MarketAverage avg = new MarketAverage(makeModel[0], makeModel[1], year);
                
                // Generate realistic market data
                double basePrice = 20000 + random.nextInt(40000);
                avg.setAveragePrice(BigDecimal.valueOf(basePrice));
                avg.setMedianPrice(BigDecimal.valueOf(basePrice * 0.95));
                avg.setMinPrice(BigDecimal.valueOf(basePrice * 0.7));
                avg.setMaxPrice(BigDecimal.valueOf(basePrice * 1.5));
                
                avg.setAverageMileage(15000.0 + random.nextInt(50000));
                avg.setMedianMileage((int) (avg.getAverageMileage() * 0.9));
                avg.setAverageDaysOnMarket(30.0 + random.nextInt(60));
                
                avg.setTotalListings(10 + random.nextInt(50));
                avg.setSoldListings(5 + random.nextInt(avg.getTotalListings() - 5));
                
                averages.add(avg);
            }
        }
        
        return marketAverageRepository.saveAll(averages);
    }

    private List<AuditLog> createAuditLogs(List<User> users) {
        List<AuditLog> auditLogs = new ArrayList<>();
        
        AuditAction[] actions = AuditAction.values();
        String[] entityTypes = {"USER", "CAR", "TRANSACTION", "INSPECTION", "FAVOURITE"};
        String[] ipAddresses = {"192.168.1.1", "10.0.0.1", "172.16.0.1"};
        
        Random random = new Random();
        
        for (int i = 0; i < 40; i++) { // Create 40 audit logs
            User user = users.get(random.nextInt(users.size()));
            AuditAction action = actions[random.nextInt(actions.length)];
            String entityType = entityTypes[random.nextInt(entityTypes.length)];
            
            AuditLog log = new AuditLog(user, action, entityType, (long)(1 + random.nextInt(100)));
            log.setDetails(action.toString() + " performed on " + entityType);
            log.setIpAddress(ipAddresses[random.nextInt(ipAddresses.length)]);
            log.setUserAgent("TestAgent/1.0");
            
            if (random.nextDouble() < 0.1) { // 10% chance of being flagged
                log.flagForReview("Suspicious activity pattern detected");
            }
            
            auditLogs.add(log);
        }
        
        return auditLogRepository.saveAll(auditLogs);
    }

    private List<Notification> createNotifications(List<User> users) {
        List<Notification> notifications = new ArrayList<>();
        
        NotificationType[] types = NotificationType.values();
        String[] titles = {
            "Price Updated", "New Message", "Inspection Confirmed", "Transaction Complete",
            "Listing Approved", "New Match Found", "Payment Received", "Schedule Reminder"
        };
        String[] messages = {
            "The price for your saved search item has been updated.",
            "You have received a new message regarding your listing.",
            "Your inspection has been confirmed for tomorrow.",
            "Your transaction has been completed successfully.",
            "Your listing has been approved and is now live.",
            "A new listing matches your saved search criteria.",
            "Payment has been received for your transaction.",
            "Reminder: You have an upcoming inspection scheduled."
        };
        
        Random random = new Random();
        
        for (int i = 0; i < 35; i++) { // Create 35 notifications
            User user = users.get(random.nextInt(users.size()));
            NotificationType type = types[random.nextInt(types.length)];
            String title = titles[random.nextInt(titles.length)];
            String message = messages[random.nextInt(messages.length)];
            
            Notification notification = new Notification(user, type, title, message);
            notification.setRelatedEntityId((long)(1 + random.nextInt(50)));
            notification.setRelatedEntityType("CAR");
            
            // 60% chance of being read
            if (random.nextDouble() < 0.6) {
                notification.markAsRead();
            }
            
            // 30% chance email was sent
            notification.setIsEmailSent(random.nextDouble() < 0.3);
            
            notifications.add(notification);
        }
        
        return notificationRepository.saveAll(notifications);
    }
}