package application.domain.docs_generator;

class Position {
    float x;
    float y;
    Position(float x, float y){
        this.x=x;
        this.y=y;
    }
}


public class Label {

    private Position p;
    private String name;
    private String origin;
    private String price;
    String number;

    public float getStartX() {
        return p.x;
    }
    public float getStartY() {
        return p.y;
    }

    public void setP(Position p) {
        this.p = p;
    }

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

    Label(Position p, String name, String origin,
          String number, String price)
    {
        this.p = p;
        this.name = name;
        this.origin = origin;
        this.number = number;
        this.price = price;
    }
}