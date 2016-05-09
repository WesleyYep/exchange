package net.sorted.exchange.orders.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

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

    @Column(name="symbol")
    private String symbol;

    @Column(name="client_id")
    private String clientId;

    @Column(name="order_type")
    private OrderType type;

    @Column(name="status")
    private OrderStatus status;


    public Order(long id, double price, Side side, long quantity, String symbol, String clientId, OrderType type, OrderStatus status) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.side = side;
        this.symbol = symbol;
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

    public String getSymbol(){
        return symbol;
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
}
