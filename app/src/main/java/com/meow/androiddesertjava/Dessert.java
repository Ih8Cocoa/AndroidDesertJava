package com.meow.androiddesertjava;

public final class Dessert {
    private final int imageId, price, startProductionAmount;

    public Dessert(int imageId, int price, int startProductionAmount) {
        this.imageId = imageId;
        this.price = price;
        this.startProductionAmount = startProductionAmount;
    }

    public int getImageId() {
        return imageId;
    }

    public int getPrice() {
        return price;
    }

    public int getStartProductionAmount() {
        return startProductionAmount;
    }
}
