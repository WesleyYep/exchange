package net.sorted.exchange.orders.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="order_fill")
public class OrderFill {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="orders_id_seq")
    @SequenceGenerator(name="orders_id_seq", sequenceName="orders_id_seq", allocationSize=1)
    private long id;

    @Column(name = "quantity")
    private long quantity;

    @Column(name = "price")
    private double price;

    @Column(name="order_id", nullable=false)
    private long orderId;

    @Column(name="matched_order_id", nullable=false)
    private long matchedOrderId;


    public OrderFill(long id, long quantity, double price, long orderId, long matchedOrderId) {
        this.id = id;
        this.quantity = quantity;
        this.price = price;
        this.orderId = orderId;
        this.matchedOrderId = matchedOrderId;
    }

    protected OrderFill() {
        // For JPA
    }

    public long getId() {
        return id;
    }

    public long getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getMatchedOrderId() {
        return matchedOrderId;
    }

}
