package model;

public class Coordinator extends User {
    private String area;

    public Coordinator(String id, String name, String email, String area) {
        super(id, name, email);
        this.area = area;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    @Override
    public String getRole() {
        return "Coordinator";
    }

    @Override
    public String toString() {
        return super.toString() + " | Area: " + area;
    }
}
