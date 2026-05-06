package com.nearprop.service.franchisee.impl;

import com.nearprop.dto.franchisee.*;
import com.nearprop.entity.FranchiseeDistrict;
import com.nearprop.entity.Property;
import com.nearprop.entity.PropertyStatus;
import com.nearprop.entity.Subscription;
import com.nearprop.entity.User;
import com.nearprop.repository.PropertyRepository;
import com.nearprop.repository.SubscriptionRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.repository.franchisee.FranchiseeDistrictRepository;
import com.nearprop.service.franchisee.FranchiseeDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.nearprop.dto.PropertyDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FranchiseeDashboardServiceImpl implements FranchiseeDashboardService {

    private final UserRepository userRepository;
    private final FranchiseeDistrictRepository franchiseeDistrictRepository;
    private final PropertyRepository propertyRepository;
    private final SubscriptionRepository subscriptionRepository;

    private final Random random = new Random();

    @Override
    public FranchiseeDashboardDto getDashboardData(Long franchiseeId, LocalDate startDate, LocalDate endDate) {
        log.info("Getting dashboard data for franchisee: {} from {} to {}", franchiseeId, startDate, endDate);

        // Get user
        User franchisee = userRepository.findById(franchiseeId)
                .orElseThrow(() -> new IllegalArgumentException("Franchisee not found with ID: " + franchiseeId));

        // Get districts for this franchisee
        List<FranchiseeDistrict> districts = franchiseeDistrictRepository.findByUserId(franchiseeId);
        if (districts.isEmpty()) {
            throw new IllegalStateException("No districts found for franchisee: " + franchiseeId);
        }

        // Get district IDs
        List<Long> districtIds = districts.stream()
                .map(FranchiseeDistrict::getDistrictId)
                .collect(Collectors.toList());

        // Get all properties in these districts
        List<Property> properties = new ArrayList<>();
        for (Long districtId : districtIds) {
            // Since there's no direct findByDistrictId method, we'll use a mock approach
            // In a real implementation, you would query properties by district ID
            List<Property> districtProperties = getMockPropertiesForDistrict(districtId);
            properties.addAll(districtProperties);
        }

        // Calculate property statistics
        int totalListings = properties.size();
        int activeListings = (int) properties.stream().filter(p -> p.isActive()).count();
        int soldListings = (int) properties.stream().filter(p -> PropertyStatus.SOLD.equals(p.getStatus())).count();
        int pendingListings = totalListings - activeListings - soldListings;

        // Get subscriptions
        List<Subscription> subscriptions = new ArrayList<>();
        for (Property property : properties) {
            if (property.getSubscriptionId() != null) {
                subscriptionRepository.findById(property.getSubscriptionId())
                        .ifPresent(subscriptions::add);
            }
        }

        // Calculate subscription statistics
        int totalSubscriptions = subscriptions.size();
        int activeSubscriptions = (int) subscriptions.stream().filter(Subscription::isActive).count();
        int expiredSubscriptions = totalSubscriptions - activeSubscriptions;

        // Calculate revenue
        BigDecimal totalRevenue = subscriptions.stream()
                .map(s -> s.getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal franchiseeRevenue = totalRevenue.multiply(BigDecimal.valueOf(0.7));
        BigDecimal platformRevenue = totalRevenue.subtract(franchiseeRevenue);
        BigDecimal walletBalance = BigDecimal.valueOf(randomRange(10000, 50000));
        BigDecimal pendingWithdrawal = BigDecimal.valueOf(randomRange(1000, 10000));

        // Get district performance
        List<DistrictPerformanceDto> districtPerformance = getDistrictPerformance(franchiseeId, startDate, endDate);

        // Generate time series data
        List<DashboardDtos.TimeSeriesData> listingTimeSeriesData = generateListingTimeSeriesData();
        List<DashboardDtos.TimeSeriesData> subscriptionTimeSeriesData = generateSubscriptionTimeSeriesData();

        // New: User registration stats for the franchisee's districts
        DashboardDtos.UserRegistrationStats userRegistrationStats = generateUserRegistrationStats(districtIds);

        // Generate active subscription users
        List<SubscriptionUserDto> activeSubscriptionUsers = generateActiveSubscriptionUsers(10);

        // Generate additional metrics
        DashboardDtos.ListingData listingData = generateListingMetrics();
        DashboardDtos.RevenueData revenueData = generateRevenueMetrics();
        DashboardDtos.SubscriptionData subscriptionData = generateSubscriptionMetrics();

        return FranchiseeDashboardDto.builder()
                .franchiseeId(franchiseeId)
                .franchiseeName(franchisee.getName())
                .businessName("Business " + franchiseeId)
                .generatedAt(LocalDateTime.now())
                .districtIds(districtIds)
                .totalListings(totalListings)
                .activeListings(activeListings)
                .soldListings(soldListings)
                .pendingListings(pendingListings)
                .totalRevenue(totalRevenue)
                .franchiseeRevenue(franchiseeRevenue)
                .platformRevenue(platformRevenue)
                .walletBalance(walletBalance)
                .pendingWithdrawal(pendingWithdrawal)
                .totalSubscriptions(totalSubscriptions)
                .activeSubscriptions(activeSubscriptions)
                .expiredSubscriptions(expiredSubscriptions)
                .listingTimeSeriesData(listingTimeSeriesData)
                .subscriptionTimeSeriesData(subscriptionTimeSeriesData)
                .userRegistrationStats(userRegistrationStats)
                .activeSubscriptionUsers(activeSubscriptionUsers)
                .listingData(listingData)
                .revenueData(revenueData)
                .subscriptionData(subscriptionData)
                .build();
    }

    @Override
    public List<DistrictPerformanceDto> getDistrictPerformance(Long franchiseeId, LocalDate startDate, LocalDate endDate) {
        log.info("Getting district performance for franchisee: {} from {} to {}", franchiseeId, startDate, endDate);

        List<FranchiseeDistrict> districts = franchiseeDistrictRepository.findByUserId(franchiseeId);

        return districts.stream()
                .map(district -> {
                    Long districtId = district.getDistrictId();

                    // Get properties for this district
                    List<Property> properties = getMockPropertiesForDistrict(districtId);

                    // Calculate property statistics
                    int totalListings = properties.size();
                    int activeListings = (int) properties.stream().filter(p -> p.isActive()).count();
                    int soldListings = (int) properties.stream().filter(p -> PropertyStatus.SOLD.equals(p.getStatus())).count();
                    int pendingListings = totalListings - activeListings - soldListings;
                    int featuredListings = (int) (totalListings * 0.2); // 20% are featured

                    // Get subscriptions
                    List<Subscription> subscriptions = new ArrayList<>();
                    for (Property property : properties) {
                        if (property.getSubscriptionId() != null) {
                            subscriptionRepository.findById(property.getSubscriptionId())
                                    .ifPresent(subscriptions::add);
                        }
                    }

                    // Calculate subscription statistics
                    int totalSubscriptions = subscriptions.size();
                    int activeSubscriptions = (int) subscriptions.stream().filter(Subscription::isActive).count();
                    int expiredSubscriptions = totalSubscriptions - activeSubscriptions;

                    // Calculate revenue
                    BigDecimal totalRevenue = subscriptions.stream()
                            .map(s -> s.getPrice())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    BigDecimal franchiseeRevenue = totalRevenue.multiply(BigDecimal.valueOf(0.7));
                    BigDecimal platformRevenue = totalRevenue.subtract(franchiseeRevenue);

                    // Generate growth rates (mock data)
                    double revenueGrowth = randomRange(-10.0, 30.0);
                    double listingsGrowth = randomRange(-5.0, 40.0);
                    double subscriptionsGrowth = randomRange(-8.0, 25.0);

                    // Generate time series data
                    List<DashboardDtos.TimeSeriesData> listingTimeSeriesData = generateListingTimeSeriesData();
                    List<DashboardDtos.TimeSeriesData> revenueTimeSeriesData = generateRevenueTimeSeriesData();
                    List<DashboardDtos.TimeSeriesData> subscriptionTimeSeriesData = generateSubscriptionTimeSeriesData();

                    return DistrictPerformanceDto.builder()
                            .districtId(districtId)
                            .districtName(district.getDistrictName())
                            .state(district.getState())
                            .totalListings(totalListings)
                            .activeListings(activeListings)
                            .soldListings(soldListings)
                            .pendingListings(pendingListings)
                            .featuredListings(featuredListings)
                            .totalRevenue(totalRevenue)
                            .franchiseeRevenue(franchiseeRevenue)
                            .platformRevenue(platformRevenue)
                            .totalSubscriptions(totalSubscriptions)
                            .activeSubscriptions(activeSubscriptions)
                            .expiredSubscriptions(expiredSubscriptions)
                            .listingTimeSeriesData(listingTimeSeriesData)
                            .revenueTimeSeriesData(revenueTimeSeriesData)
                            .subscriptionTimeSeriesData(subscriptionTimeSeriesData)
                            .totalUsers(randomRange(50, 200))
                            .activeUsers(randomRange(30, 150))
                            .newUsersThisMonth(randomRange(5, 30))
                            .revenueRank(randomRange(1, 5))
                            .listingsRank(randomRange(1, 5))
                            .subscriptionsRank(randomRange(1, 5))
                            .revenueGrowth(revenueGrowth)
                            .listingsGrowth(listingsGrowth)
                            .subscriptionsGrowth(subscriptionsGrowth)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public DistrictPerformanceDto getSingleDistrictPerformance(Long districtId, LocalDate startDate, LocalDate endDate) {
        log.info("Getting district performance for district: {} from {} to {}", districtId, startDate, endDate);

        FranchiseeDistrict district = franchiseeDistrictRepository.findByDistrictId(districtId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Franchisee district not found with district ID: " + districtId));

        // Get properties for this district
        List<Property> properties = getMockPropertiesForDistrict(districtId);

        // Calculate property statistics
        int totalListings = properties.size();
        int activeListings = (int) properties.stream().filter(p -> p.isActive()).count();
        int soldListings = (int) properties.stream().filter(p -> PropertyStatus.SOLD.equals(p.getStatus())).count();
        int pendingListings = totalListings - activeListings - soldListings;
        int featuredListings = (int) (totalListings * 0.2); // 20% are featured

        // Get subscriptions
        List<Subscription> subscriptions = new ArrayList<>();
        for (Property property : properties) {
            if (property.getSubscriptionId() != null) {
                subscriptionRepository.findById(property.getSubscriptionId())
                        .ifPresent(subscriptions::add);
            }
        }

        // Calculate subscription statistics
        int totalSubscriptions = subscriptions.size();
        int activeSubscriptions = (int) subscriptions.stream().filter(Subscription::isActive).count();
        int expiredSubscriptions = totalSubscriptions - activeSubscriptions;

        // Calculate revenue
        BigDecimal totalRevenue = subscriptions.stream()
                .map(s -> s.getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal franchiseeRevenue = totalRevenue.multiply(BigDecimal.valueOf(0.7));
        BigDecimal platformRevenue = totalRevenue.subtract(franchiseeRevenue);

        // Generate growth rates (mock data)
        double revenueGrowth = randomRange(-10.0, 30.0);
        double listingsGrowth = randomRange(-5.0, 40.0);
        double subscriptionsGrowth = randomRange(-8.0, 25.0);

        // Generate time series data
        List<DashboardDtos.TimeSeriesData> listingTimeSeriesData = generateListingTimeSeriesData();
        List<DashboardDtos.TimeSeriesData> revenueTimeSeriesData = generateRevenueTimeSeriesData();
        List<DashboardDtos.TimeSeriesData> subscriptionTimeSeriesData = generateSubscriptionTimeSeriesData();

        return DistrictPerformanceDto.builder()
                .districtId(districtId)
                .districtName(district.getDistrictName())
                .state(district.getState())
                .totalListings(totalListings)
                .activeListings(activeListings)
                .soldListings(soldListings)
                .pendingListings(pendingListings)
                .featuredListings(featuredListings)
                .totalRevenue(totalRevenue)
                .franchiseeRevenue(franchiseeRevenue)
                .platformRevenue(platformRevenue)
                .totalSubscriptions(totalSubscriptions)
                .activeSubscriptions(activeSubscriptions)
                .expiredSubscriptions(expiredSubscriptions)
                .listingTimeSeriesData(listingTimeSeriesData)
                .revenueTimeSeriesData(revenueTimeSeriesData)
                .subscriptionTimeSeriesData(subscriptionTimeSeriesData)
                .totalUsers(randomRange(50, 200))
                .activeUsers(randomRange(30, 150))
                .newUsersThisMonth(randomRange(5, 30))
                .revenueRank(randomRange(1, 5))
                .listingsRank(randomRange(1, 5))
                .subscriptionsRank(randomRange(1, 5))
                .revenueGrowth(revenueGrowth)
                .listingsGrowth(listingsGrowth)
                .subscriptionsGrowth(subscriptionsGrowth)
                .build();
    }

    // Helper method to generate mock properties for a district
    private List<Property> getMockPropertiesForDistrict(Long districtId) {
        // Generate between 10 and 50 mock properties
        int count = randomRange(10, 50);
        List<Property> properties = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Property property = Property.builder()
                    .id((long) (districtId * 1000 + i))
                    .title("Property " + i + " in District " + districtId)
                    .description("Mock property for testing")
                    .price(BigDecimal.valueOf(randomRange(1000000, 10000000)))
                    .area(randomRange(500.0, 5000.0))
                    .address("Mock Address " + i)
                    .bedrooms(randomRange(1, 5))
                    .bathrooms(randomRange(1, 4))
                    .active(random.nextDouble() > 0.2)
                    .status(random.nextDouble() > 0.7 ? PropertyStatus.SOLD : PropertyStatus.ACTIVE)
                    .subscriptionId(random.nextDouble() > 0.3 ? (long) randomRange(1, 100) : null)
                    .build();

            properties.add(property);
        }

        return properties;
    }

    // Generate time series data for listings
    private List<DashboardDtos.TimeSeriesData> generateListingTimeSeriesData() {
        List<DashboardDtos.TimeSeriesData> result = new ArrayList<>();

        // Daily data
        result.add(createTimeSeriesData("daily", 30,
                Map.of("active", generateRandomValues(30, 50, 100),
                        "sold", generateRandomValues(30, 10, 30),
                        "pending", generateRandomValues(30, 5, 20))));

        // Weekly data
        result.add(createTimeSeriesData("weekly", 12,
                Map.of("active", generateRandomValues(12, 50, 100),
                        "sold", generateRandomValues(12, 10, 30),
                        "pending", generateRandomValues(12, 5, 20))));

        // Monthly data
        result.add(createTimeSeriesData("monthly", 12,
                Map.of("active", generateRandomValues(12, 50, 100),
                        "sold", generateRandomValues(12, 10, 30),
                        "pending", generateRandomValues(12, 5, 20))));

        return result;
    }

    // Generate time series data for revenue
    private List<DashboardDtos.TimeSeriesData> generateRevenueTimeSeriesData() {
        List<DashboardDtos.TimeSeriesData> result = new ArrayList<>();

        // Daily data
        result.add(createTimeSeriesData("daily", 30,
                Map.of("total", generateRandomValues(30, 5000, 20000),
                        "franchisee", generateRandomValues(30, 3500, 14000),
                        "platform", generateRandomValues(30, 1500, 6000))));

        // Weekly data
        result.add(createTimeSeriesData("weekly", 12,
                Map.of("total", generateRandomValues(12, 35000, 140000),
                        "franchisee", generateRandomValues(12, 24500, 98000),
                        "platform", generateRandomValues(12, 10500, 42000))));

        // Monthly data
        result.add(createTimeSeriesData("monthly", 12,
                Map.of("total", generateRandomValues(12, 150000, 600000),
                        "franchisee", generateRandomValues(12, 105000, 420000),
                        "platform", generateRandomValues(12, 45000, 180000))));

        return result;
    }

    // New method: Generate revenue time series for the current user only
    private List<DashboardDtos.TimeSeriesData> generateRevenueTimeSeriesDataForUser(List<Subscription> subscriptions, LocalDate startDate, LocalDate endDate) {
        // Default to last 30 days if not provided
        if (endDate == null) endDate = LocalDate.now();
        if (startDate == null) startDate = endDate.minusDays(29);
        int days = (int) (endDate.toEpochDay() - startDate.toEpochDay()) + 1;
        List<String> labels = new ArrayList<>();
        List<Object> totalList = new ArrayList<>();
        List<Object> franchiseeList = new ArrayList<>();
        List<Object> platformList = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate day = startDate.plusDays(i);
            labels.add(day.toString());
            // Sum revenue for this day
            BigDecimal total = subscriptions.stream()
                    .filter(s -> s.getCreatedAt() != null && s.getCreatedAt().toLocalDate().equals(day))
                    .map(Subscription::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal franchisee = total.multiply(BigDecimal.valueOf(0.7));
            BigDecimal platform = total.subtract(franchisee);
            totalList.add(total);
            franchiseeList.add(franchisee);
            platformList.add(platform);
        }
        Map<String, List<Object>> series = new LinkedHashMap<>();
        series.put("total", totalList);
        series.put("franchisee", franchiseeList);
        series.put("platform", platformList);
        List<DashboardDtos.TimeSeriesData> result = new ArrayList<>();
        result.add(DashboardDtos.TimeSeriesData.builder()
                .timeUnit("daily")
                .labels(labels)
                .series(series)
                .build());
        return result;
    }

    // Generate time series data for subscriptions
    private List<DashboardDtos.TimeSeriesData> generateSubscriptionTimeSeriesData() {
        List<DashboardDtos.TimeSeriesData> result = new ArrayList<>();

        // Daily data
        result.add(createTimeSeriesData("daily", 30,
                Map.of("active", generateRandomValues(30, 30, 100),
                        "new", generateRandomValues(30, 1, 10),
                        "expired", generateRandomValues(30, 0, 5))));

        // Weekly data
        result.add(createTimeSeriesData("weekly", 12,
                Map.of("active", generateRandomValues(12, 30, 100),
                        "new", generateRandomValues(12, 5, 30),
                        "expired", generateRandomValues(12, 2, 15))));

        // Monthly data
        result.add(createTimeSeriesData("monthly", 12,
                Map.of("active", generateRandomValues(12, 30, 100),
                        "new", generateRandomValues(12, 10, 50),
                        "expired", generateRandomValues(12, 5, 30))));

        return result;
    }

    // Helper method to create a TimeSeriesData object
    private DashboardDtos.TimeSeriesData createTimeSeriesData(String timeUnit, int count, Map<String, List<Object>> series) {
        List<String> labels = generateTimeLabels(timeUnit, count);

        return DashboardDtos.TimeSeriesData.builder()
                .timeUnit(timeUnit)
                .labels(labels)
                .series(series)
                .build();
    }

    // Helper method to generate time labels
    private List<String> generateTimeLabels(String timeUnit, int count) {
        List<String> labels = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter;

        switch (timeUnit) {
            case "hourly":
                formatter = DateTimeFormatter.ofPattern("HH:mm");
                for (int i = 0; i < count; i++) {
                    labels.add(now.minusHours(count - i - 1).format(formatter));
                }
                break;
            case "daily":
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                for (int i = 0; i < count; i++) {
                    labels.add(now.minusDays(count - i - 1).format(formatter));
                }
                break;
            case "weekly":
                formatter = DateTimeFormatter.ofPattern("yyyy-'W'ww");
                for (int i = 0; i < count; i++) {
                    labels.add(now.minusWeeks(count - i - 1).format(formatter));
                }
                break;
            case "monthly":
                formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                for (int i = 0; i < count; i++) {
                    labels.add(now.minusMonths(count - i - 1).format(formatter));
                }
                break;
            case "yearly":
                formatter = DateTimeFormatter.ofPattern("yyyy");
                for (int i = 0; i < count; i++) {
                    labels.add(now.minusYears(count - i - 1).format(formatter));
                }
                break;
            default:
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                for (int i = 0; i < count; i++) {
                    labels.add(now.minusDays(count - i - 1).format(formatter));
                }
        }

        return labels;
    }

    // Helper method to generate random values for time series
    private List<Object> generateRandomValues(int count, int min, int max) {
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            values.add(randomRange(min, max));
        }
        return values;
    }

    // Generate mock listing metrics
    private DashboardDtos.ListingData generateListingMetrics() {
        return DashboardDtos.ListingData.builder()
                .totalListings(randomRange(100, 300))
                .activeListings(randomRange(70, 200))
                .soldListings(randomRange(20, 70))
                .pendingListings(randomRange(10, 30))
                .featuredListings(randomRange(15, 50))
                .premiumListings(randomRange(25, 80))
                .newListingsThisMonth(randomRange(10, 40))
                .soldListingsThisMonth(randomRange(5, 25))
                .build();
    }

    // Generate mock revenue metrics
    private DashboardDtos.RevenueData generateRevenueMetrics() {
        BigDecimal totalRevenue = BigDecimal.valueOf(randomRange(50000, 200000));
        BigDecimal franchiseeRevenue = totalRevenue.multiply(BigDecimal.valueOf(0.7));
        BigDecimal platformRevenue = totalRevenue.subtract(franchiseeRevenue);

        return DashboardDtos.RevenueData.builder()
                .totalRevenue(totalRevenue)
                .franchiseeRevenue(franchiseeRevenue)
                .platformRevenue(platformRevenue)
                .walletBalance(BigDecimal.valueOf(randomRange(20000, 100000)))
                .pendingWithdrawal(BigDecimal.valueOf(randomRange(5000, 20000)))
                .lastWithdrawalDate(LocalDate.now().minusDays(randomRange(1, 30)))
                .lastWithdrawalAmount(BigDecimal.valueOf(randomRange(10000, 50000)))
                .build();
    }

    // Generate mock subscription metrics
    private DashboardDtos.SubscriptionData generateSubscriptionMetrics() {
        int totalSubscriptions = randomRange(50, 200);
        int activeSubscriptions = randomRange(40, totalSubscriptions - 5);
        int expiredSubscriptions = totalSubscriptions - activeSubscriptions;

        return DashboardDtos.SubscriptionData.builder()
                .totalSubscriptions(totalSubscriptions)
                .activeSubscriptions(activeSubscriptions)
                .expiredSubscriptions(expiredSubscriptions)
                .newSubscriptionsThisMonth(randomRange(10, 30))
                .renewalsThisMonth(randomRange(5, 20))
                .averageSubscriptionValue(BigDecimal.valueOf(randomRange(1500, 3000)))
                .totalSubscriptionRevenue(BigDecimal.valueOf(randomRange(75000, 300000)))
                .build();
    }

    // Generate mock recent activity
    private List<DashboardDtos.UserActivityData> generateRecentActivity(int count) {
        List<DashboardDtos.UserActivityData> activities = new ArrayList<>();
        String[] actions = {"viewed", "contacted", "subscribed", "reviewed", "favorited"};

        for (int i = 0; i < count; i++) {
            activities.add(DashboardDtos.UserActivityData.builder()
                    .userId((long) randomRange(100, 999))
                    .userName("User " + randomRange(100, 999))
                    .action(actions[randomRange(0, actions.length - 1)])
                    .timestamp(LocalDateTime.now().minusMinutes(randomRange(1, 1440))) // Up to 24 hours ago
                    .propertyId("PROP" + randomRange(1000, 9999))
                    .propertyTitle("Property " + randomRange(1, 100))
                    .build());
        }

        return activities;
    }

    // Generate mock active subscription users
    private List<SubscriptionUserDto> generateActiveSubscriptionUsers(int count) {
        List<SubscriptionUserDto> users = new ArrayList<>();
        String[] plans = {"Basic", "Standard", "Premium", "Pro"};

        for (int i = 0; i < count; i++) {
            String plan = plans[randomRange(0, plans.length - 1)];
            BigDecimal amount = BigDecimal.valueOf(
                    plan.equals("Basic") ? randomRange(999, 1999) :
                            plan.equals("Standard") ? randomRange(1999, 2999) :
                                    plan.equals("Premium") ? randomRange(2999, 3999) :
                                            randomRange(3999, 4999));

            LocalDate startDate = LocalDate.now().minusDays(randomRange(1, 180));
            LocalDate endDate = startDate.plusMonths(randomRange(3, 12));

            users.add(SubscriptionUserDto.builder()
                    .userId((long) randomRange(100, 999))
                    .name("User " + randomRange(100, 999))
                    .email("user" + randomRange(100, 999) + "@example.com")
                    .phone("999" + randomRange(1000000, 9999999))
                    .profileImageUrl(null)
                    .subscriptionId((long) randomRange(10000, 99999))
                    .subscriptionPlan(plan)
                    .startDate(startDate)
                    .endDate(endDate)
                    .status("ACTIVE")
                    .amount(amount)
                    .paymentMethod(randomRange(0, 1) == 0 ? "Credit Card" : "UPI")
                    .propertyId((long) randomRange(1000, 9999))
                    .propertyTitle("Property " + randomRange(1, 100))
                    .propertyType("Apartment")
                    .propertyLocation("Location " + randomRange(1, 20))
                    .lastLoginAt(LocalDateTime.now().minusHours(randomRange(1, 168)))
                    .totalLogins(randomRange(5, 50))
                    .totalPropertyViews(randomRange(10, 200))
                    .totalEnquiries(randomRange(0, 10))
                    .autoRenewal(randomRange(0, 1) == 1)
                    .renewalCount(randomRange(0, 3))
                    .nextRenewalDate(endDate)
                    .build());
        }

        return users;
    }

    // New method: Generate user registration stats for the franchisee's districts
    private DashboardDtos.UserRegistrationStats generateUserRegistrationStats(List<Long> districtIds) {
        // Get all users in these districts
        List<User> users = userRepository.findByDistrictIdIn(districtIds);
        // Daily (last 30 days)
        LocalDate today = LocalDate.now();
        List<String> dailyLabels = new ArrayList<>();
        List<Integer> dailyCounts = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            int count = (int) users.stream()
                    .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().toLocalDate().equals(day))
                    .count();
            dailyLabels.add(day.toString());
            dailyCounts.add(count);
        }
        // Weekly (last 12 weeks)
        List<String> weeklyLabels = new ArrayList<>();
        List<Integer> weeklyCounts = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            LocalDate weekStart = today.minusWeeks(i).with(java.time.DayOfWeek.MONDAY);
            LocalDate weekEnd = weekStart.plusDays(6);
            int count = (int) users.stream()
                    .filter(u -> u.getCreatedAt() != null &&
                            !u.getCreatedAt().toLocalDate().isBefore(weekStart) &&
                            !u.getCreatedAt().toLocalDate().isAfter(weekEnd))
                    .count();
            weeklyLabels.add(weekStart.toString() + " to " + weekEnd.toString());
            weeklyCounts.add(count);
        }
        // Monthly (last 12 months)
        List<String> monthlyLabels = new ArrayList<>();
        List<Integer> monthlyCounts = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            LocalDate month = today.minusMonths(i).withDayOfMonth(1);
            int count = (int) users.stream()
                    .filter(u -> u.getCreatedAt() != null &&
                            u.getCreatedAt().getYear() == month.getYear() &&
                            u.getCreatedAt().getMonthValue() == month.getMonthValue())
                    .count();
            monthlyLabels.add(month.getYear() + "-" + String.format("%02d", month.getMonthValue()));
            monthlyCounts.add(count);
        }
        return DashboardDtos.UserRegistrationStats.builder()
                .dailyLabels(dailyLabels)
                .dailyCounts(dailyCounts)
                .weeklyLabels(weeklyLabels)
                .weeklyCounts(weeklyCounts)
                .monthlyLabels(monthlyLabels)
                .monthlyCounts(monthlyCounts)
                .build();
    }

    @Override
    public List<PropertyDto> getAllDistrictProperties(Long franchiseeId) {
        log.info("Getting all properties for franchisee: {}", franchiseeId);

        // 1. Get all districts assigned to the franchisee
        List<FranchiseeDistrict> districts = franchiseeDistrictRepository.findByUserId(franchiseeId);
        if (districts.isEmpty()) {
            throw new IllegalStateException("No districts found for this franchisee");
        }

        // 2. Extract district IDs
        List<Long> districtIds = districts.stream()
                .map(FranchiseeDistrict::getDistrictId)
                .collect(Collectors.toList());

        // 3. Fetch properties for these districts
        List<Property> properties = propertyRepository.findByDistrictIdIn(districtIds);

        // 4. Map entity -> DTO
//        return properties.stream()
//                .map(p -> {
//                    PropertyDto.PropertyDtoBuilder builder = PropertyDto.builder()
//                            .id(p.getId())
//                            .title(p.getTitle())
//                            .description(p.getDescription())
//                            .price(p.getPrice())
//                            .area(p.getArea())
//                            .address(p.getAddress())
//                            .status(p.getStatus())
//                            .type(p.getType())
//                            .createdAt(p.getCreatedAt())
//                            .updatedAt(p.getUpdatedAt());
//
//                    // Optional fields — include them only if present in your entity
//                    try { builder.active(p.isActive()); } catch (Exception ignored) {}
//                    try { builder.approved(p.isApproved()); } catch (Exception ignored) {}
//                    //try { builder.featured(p.getFeatured()); } catch (Exception ignored) {}
//                    //try { builder.districtId(p.getDistrictId()); } catch (Exception ignored) {}
//                    try { builder.subscriptionId(p.getSubscriptionId()); } catch (Exception ignored) {}
//                    try { builder.subscriptionExpiry(p.getSubscriptionExpiry()); } catch (Exception ignored) {}
//
//                    return builder.build();
//                })
//                .collect(Collectors.toList());

        return properties.stream()
                .map((Property p) -> {
                    Long districtId = null;
                    String districtName = null;

                    if (p.getDistrict() != null) {
                        districtId = p.getDistrict().getId();
                        districtName = p.getDistrict().getName();
                    }

                    return PropertyDto.builder()
                            .id(p.getId())
                            .title(p.getTitle())
                            .description(p.getDescription())
                            .type(p.getType())
                            .status(p.getStatus())
                            .price(p.getPrice())
                            .area(p.getArea())
                            .address(p.getAddress())
                            .pincode(p.getPincode())
                            .city(p.getCity())
                            .state(p.getState())
                            .bedrooms(p.getBedrooms())
                            .bathrooms(p.getBathrooms())
                            .garages(p.getGarages())
                            .garageSize(p.getGarageSize())
                            .yearBuilt(p.getYearBuilt())
                            // .featured(p.getFeatured())
                            .approved(p.isApproved())
                            .active(p.isActive())
                            .videoUrl(p.getVideoUrl())
                            .youtubeUrl(p.getYoutubeUrl())
                            .latitude(p.getLatitude())
                            .longitude(p.getLongitude())
                            .unitCount(p.getUnitCount())
                            //.favoriteCount(p.getFavoritedBy())
                            .subscriptionId(p.getSubscriptionId())
                            .subscriptionExpiry(p.getSubscriptionExpiry())
                            .districtId(districtId)
                            .districtName(districtName)
                            .createdAt(p.getCreatedAt())
                            .updatedAt(p.getUpdatedAt())
                            .build();
                })
                .collect(Collectors.toList());


    }

    @Override
    public Map<String, Object> getDistrictSummary(Long franchiseeId) {
        log.info("Getting district summary for franchisee: {}", franchiseeId);

        User franchisee = userRepository.findById(franchiseeId)
                .orElseThrow(() -> new IllegalArgumentException("Franchisee not found with ID: " + franchiseeId));

        // 1️ a Get all franchisee districts
        List<FranchiseeDistrict> districts = franchiseeDistrictRepository.findByUserId(franchiseeId);
        if (districts.isEmpty()) {
            throw new IllegalStateException("No districts found for this franchisee");
        }

        // 1 b district Id get
        List<Long> districtIds = districts.stream()
                .map(FranchiseeDistrict::getDistrictId)
                .collect(Collectors.toList());

        // 2️ Get all users from these districts
        List<User> usersInDistricts = userRepository.findByDistrictIdIn(districtIds);
        List<Long> userIds = usersInDistricts.stream()
                .map(User::getId)
                .collect(Collectors.toList());

        // 3️ Fetch subscriptions of these users
        List<Subscription> subscriptions = subscriptionRepository.findByUserIdIn(userIds);

        long totalSubscriptions = subscriptions.size();
        long activeSubscriptions = subscriptions.stream().filter(Subscription::isActive).count();

        // 4️ Count properties in these districts
        long totalProperties = propertyRepository.countByDistrictIdIn(districtIds);

        Map<String, Object> result = new HashMap<>();
        result.put("districtIds", districtIds);
        result.put("totalUsers", usersInDistricts.size());
        result.put("totalProperties", totalProperties);
        result.put("totalSubscriptions", totalSubscriptions);
        result.put("activeSubscriptions", activeSubscriptions);

        return result;
    }


    // Utility methods for generating random values
    private int randomRange(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    private long randomRange(long min, long max) {
        return min + (long) (random.nextDouble() * (max - min + 1));
    }

    private double randomRange(double min, double max) {
        return min + (random.nextDouble() * (max - min));
    }
} 
