package net.sorted.exchange.orders.domain;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="orders")
public class Order {

    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="orders_id_seq")
    @SequenceGenerator(name="orders_id_seq", sequenceName="orders_id_seq", allocationSize=1)
    private long id;

    @Column(name="price")
    private double price;

    @Column(name="side")
    private Side side;

    @Column(name="quantity")
    private long quantity;

    @Column(name="instrument_id")
    private String instrumentId;

    @Column(name="client_id")
    private long clientId;

    @Column(name="order_type")
    private OrderType type;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name="order_submitter")
    private String orderSubmitter;

    @Column(name="submitted_timestamp")
    private long submittedMs;

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name="order_id")
    private Set<OrderFill> fills;

    @Transient
    private long unfilledQuantity;

    @PostLoad
    private void onLoad() {
        long filled = fills.stream().mapToLong(f -> f.getQuantity()).sum();
        setUnfilledQuantity(quantity - filled);
    }

    public Order(long id, double price, Side side, long quantity, String instrumentId, long clientId, OrderType type, OrderStatus status, String orderSubmitter, long submittedMs) {
        this(id, price, side, quantity, quantity, instrumentId, clientId, type, status, orderSubmitter, submittedMs);
    }

    public Order(long id, double price, Side side, long quantity, long unfilledQuantity, String instrumentId, long clientId, OrderType type, OrderStatus status, String orderSubmitter, long submittedMs) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.side = side;
        this.instrumentId = instrumentId;
        this.clientId = clientId;
        this.type = type;
        this.status = status;
        this.orderSubmitter = orderSubmitter;
        this.submittedMs = submittedMs;
        this.unfilledQuantity = unfilledQuantity;
    }

    protected Order() {

    }

    public long getId() {
        return id;
    }

    public double getPrice(){
        return price;
    }

    public long getQuantity(){
        return quantity;
    }

    public String getInstrumentId(){
        return instrumentId;
    }

    public Side getSide() {
        return side;
    }

    public long getClientId() {
        return clientId;
    }

    public OrderType getType() {
        return type;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getOrderSubmitter() {
        return orderSubmitter;
    }

    public long getUnfilledQuantity() { return unfilledQuantity; }

    public void setUnfilledQuantity(long unfilledQuantity) {
        this.unfilledQuantity = unfilledQuantity;
        if (status == OrderStatus.OPEN) {
            if (unfilledQuantity == 0 ) {
                status = OrderStatus.FILLED;
            } else if (unfilledQuantity < quantity) {
                status = OrderStatus.PARTIAL_FILL;
            }
        }
    }

    public long getSubmittedMs() {
        return submittedMs;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Order order = (Order) o;

        return id == order.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", price=" + price +
                ", side=" + side +
                ", quantity=" + quantity +
                ", instrumentId='" + instrumentId + '\'' +
                ", clientId=" + clientId +
                ", type=" + type +
                ", status=" + status +
                ", orderSubmitter='" + orderSubmitter + '\'' +
                ", submittedMs=" + submittedMs +
                ", unfilledQuantity=" + unfilledQuantity +
                '}';
    }
}
