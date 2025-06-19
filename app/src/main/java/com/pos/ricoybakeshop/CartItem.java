package com.pos.ricoybakeshop;

public class CartItem {
    public Product product;
    public int quantity;

    public double getSubTotal(){
        return product.price * quantity;
    }
}
