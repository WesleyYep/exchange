package net.sorted;

public class Order {
//
//    public static char BID = 'B';
//    public static char ASK = 'A';



    private long id;
    private OrderType type;
    private double price;
    private Side side;
    private long size;
    private String sym;


    public Order(long orderid, double orderprice, Side orderside, long ordersize, String ordersym) {
        id=orderid;
        price=orderprice;
        size=ordersize;
        side=orderside;
        sym=ordersym;
    }


    public long getId() {
        return id;
    }

    public double getPrice(){
        return price;
    }

    public long getSize(){
        return size;
    }

    public String getSym(){
        return sym;
    }

    public Side getSide() {
        return side;
    }

}
