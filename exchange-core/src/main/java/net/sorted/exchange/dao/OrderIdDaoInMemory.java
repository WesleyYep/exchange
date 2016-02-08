package net.sorted.exchange.dao;


public class OrderIdDaoInMemory implements OrderIdDao {

    private int currentId = 0;

    @Override
    public String getNextOrderId() {
        return ""+currentId++;
    }
}
