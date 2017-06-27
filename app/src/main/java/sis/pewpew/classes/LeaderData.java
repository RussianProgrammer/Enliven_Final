package sis.pewpew.classes;

@SuppressWarnings("unused")
public class LeaderData {

    public String name;
    public String key;
    public long points;

    public LeaderData() {
    }

    public LeaderData(String name, String key, long points) {
        this.name = name;
        this.key = key;
        this.points = points;
    }

}
