package de.dhbw.database;

/**
 * Created by MarkundFlo on 14.06.2014.
 */
public class Admin {

    private int id;
    private String name;
    private String image;
    private String rank;
    private String detailedRank;
    private long membershipDate;
    private String location;
    private int age;
    private String gender;
    private int postCount;
    private int likeCount;
    private int points;

    public Admin() {
    }

    public Admin(String name, String image, String rank, String detailedRank, long membershipDate,
                 String location, String gender, int age, int postCount, int likeCount, int points)
    {
        setName(name);
        setImage(image);
        setRank(rank);
        setDetailedRank(detailedRank);
        setMembershipDate(membershipDate);
        setLocation(location);
        setGender(gender);
        setAge(age);
        setPostCount(postCount);
        setLikeCount(likeCount);
        setPoints(points);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getDetailedRank() {
        return detailedRank;
    }

    public void setDetailedRank(String detailedRank) {
        this.detailedRank = detailedRank;
    }

    public long getMembershipDate() {
        return membershipDate;
    }

    public void setMembershipDate(long membershipDate) {
        this.membershipDate = membershipDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
