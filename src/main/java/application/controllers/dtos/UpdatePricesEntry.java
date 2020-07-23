package application.controllers.dtos;

public class UpdatePricesEntry {
    private String name;
    private float newPrice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(float newPrice) {
        this.newPrice = newPrice;
    }

    @Override
    public String toString() {
        return "UpdatePricesEntry{" +
                "name='" + name + '\'' +
                ", newPrice=" + newPrice +
                '}';
    }
}
