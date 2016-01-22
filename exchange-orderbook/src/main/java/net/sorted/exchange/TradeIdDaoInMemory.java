package net.sorted.exchange;


public class TradeIdDaoInMemory implements TradeIdDao {

    private int currentId = 0;

    @Override
    public String getNextTradeId() {
        return ""+currentId++;
    }
}
