package net.sorted.exchange.orders.dao;


public class TradeIdDaoInMemory implements TradeIdDao {

    private long currentId = 0;

    @Override
    public long getNextTradeId() {
        return currentId++;
    }
}
