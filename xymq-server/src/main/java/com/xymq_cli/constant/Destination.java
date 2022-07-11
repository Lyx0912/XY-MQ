package com.xymq_cli.constant;

/**
 * @author 黎勇炫
 * @date 2022年07月10日 13:37
 */
public enum Destination{
    QUEUE(0),
    TOPIC(1);

    private int destination;

    Destination(int destination) {
        this.destination = destination;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }
}