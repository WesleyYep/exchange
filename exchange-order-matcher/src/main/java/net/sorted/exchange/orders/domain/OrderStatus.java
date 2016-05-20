package net.sorted.exchange.orders.domain;


public enum OrderStatus {
    UNSUBMITTED,
    OPEN,
    PARTIAL_FILL,
    FILLED,
    CANCELLED,
    REJECTED
}
