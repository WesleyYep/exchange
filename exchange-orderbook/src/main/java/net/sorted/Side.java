package net.sorted;


public enum Side {
    BID('B'),
    ASK('A');

    private char charVal;
    Side(char charVal) {
        this.charVal = charVal;
    }

    char toChar() {
        return charVal;
    }
}
