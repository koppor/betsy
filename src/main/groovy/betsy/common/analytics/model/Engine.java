package betsy.common.analytics.model;

public class Engine implements Comparable<Engine> {

    public Engine(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Engine o) {
        return name.compareTo(o.name);
    }

    @Override
    public String toString() {
        return "Engine{" + "name='" + name + "\'" + "}";
    }

    public String getName() {
        return name;
    }

    private final String name;
}
