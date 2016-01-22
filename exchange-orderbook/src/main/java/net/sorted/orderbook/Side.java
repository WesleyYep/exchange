package net.sorted.orderbook;


public enum Side {
    BUY('B'),
    SELL('S');

    private char charVal;
    Side(char charVal) {
        this.charVal = charVal;
    }

    char toChar() {
        return charVal;
    }

    Side other() { return (this == BUY) ? SELL: BUY; }
}
