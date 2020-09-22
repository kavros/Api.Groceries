package application.controllers.dtos;

import java.util.ArrayList;

public class LabelsDTO {

    public ArrayList<Label> labels;


    public static class Label {
        public String name;
        public String origin;
        public String price;
        public String number;

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



        @Override
        public String toString() {
            return "Label{" +
                    "name='" + name + '\'' +
                    ", origin='" + origin + '\'' +
                    ", price='" + price + '\'' +
                    ", number='" + number + '\'' +
                    '}';
        }
    }
}
