package net.sorted.exchange.orders.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    private String clientId;

    @Column(name="order_type")
    private OrderType type;

    @Column(name="status")
    private OrderStatus status;

//    @OneToMany(cascade = {CascadeType.ALL})
//    @JoinColumn(name="order_id")
//    private Set<OrderFill> fills;

    @Transient
    private long unfilledQuantity;

    public Order(long id, double price, Side side, long quantity, String instrumentId, String clientId, OrderType type, OrderStatus status) {
        this(id, price, side, quantity, quantity, instrumentId, clientId, type, status);
    }

    public Order(long id, double price, Side side, long quantity, long unfilledQuantity, String instrumentId, String clientId, OrderType type, OrderStatus status) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.unfilledQuantity = unfilledQuantity;
        this.side = side;
        this.instrumentId = instrumentId;
        this.clientId = clientId;
        this.type = type;
        this.status = status;
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

    public String getClientId() {
        return clientId;
    }

    public OrderType getType() {
        return type;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public long getUnfilledQuantity() { return unfilledQuantity; }

    public void setUnfilledQuantity(long unfilledQuantity) {
        this.unfilledQuantity = unfilledQuantity;
        if (unfilledQuantity == 0 && status == OrderStatus.OPEN) {
            status = OrderStatus.FILLED;
        }
    }
}
