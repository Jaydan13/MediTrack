package com.example.meditrack;

public class StockItem {

    String id;
    private String name;
    private int quantity;
    private String expDate;

    public StockItem() {

    }
    public StockItem(String id, String name, int quantity, String expDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.expDate = expDate;
    }

    public String getId() {return id;}
    public String getName() {return name;}
    public int getQuantity() {return quantity;}
    public String getExpDate() {return expDate;}
}
