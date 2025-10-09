public class Organizer {
    private String name;
    private String phone;

    public Organizer(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }

    @Override
    public String toString() {
        return name + " (" + phone + ")";
    }
}
