package net.sorted.exchange;


public enum Side {
    BUY('B'),
    SELL('S');

    private char charVal;

    Side(char charVal) {
        this.charVal = charVal;
    }

    public char toChar() {
        return charVal;
    }

    public Side other() { return (this == BUY) ? SELL: BUY; }
}
