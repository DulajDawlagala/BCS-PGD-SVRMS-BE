// FILE: dashboard/domain/CustomerDashboard.java
package com.svrmslk.customer.dashboard.domain;

public class CustomerDashboard {

    private final String customerId;
    private final int totalBookings;
    private final int activeBookings;
    private final int completedBookings;
    private final int cancelledBookings;

    public CustomerDashboard(String customerId, int totalBookings,
                             int activeBookings, int completedBookings,
                             int cancelledBookings) {
        this.customerId = customerId;
        this.totalBookings = totalBookings;
        this.activeBookings = activeBookings;
        this.completedBookings = completedBookings;
        this.cancelledBookings = cancelledBookings;
    }

    public String getCustomerId() { return customerId; }
    public int getTotalBookings() { return totalBookings; }
    public int getActiveBookings() { return activeBookings; }
    public int getCompletedBookings() { return completedBookings; }
    public int getCancelledBookings() { return cancelledBookings; }
}