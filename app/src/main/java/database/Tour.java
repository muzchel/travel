package database;

public class Tour {
    private int id;
    private String name;
    private String description;
    private String location;
    private double price;
    private double rating;
    private String contactInfo;
    private String images;
   // private int isVerified;
    private String createdAt;

    public Tour(int id, String name, String description, String location, double price, double rating, String contactInfo, String images, String imagePath, boolean isVerified) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.price = price;
        this.rating = rating;
        this.contactInfo = contactInfo;
        this.images = images;
        //this.isVerified = isVerified;
        this.createdAt = createdAt;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public double getPrice() { return price; }
    public double getRating() { return rating; }
    public String getContactInfo() { return contactInfo; }
    public String getImages() { return images; }
   // public int getIsVerified() { return isVerified; }
    public String getCreatedAt() { return createdAt; }
}