package application.domain.labels_generator;

class Position {
    float x;
    float y;

    public float getStartX() {
        return x;
    }

    public float getStartY() {
        return y;
    }

    Position(float x, float y){
        this.x=x;
        this.y=y;
    }
}


public class Label {

    private String name;
    private String origin;
    private String price;
    String number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    Label(String name, String origin,
          String number, String price)
    {
        this.name = name;
        this.origin = origin;
        this.number = number;
        this.price = price;
    }
}