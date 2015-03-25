package betsy.common.analytics.model;

import java.util.SortedMap;
import java.util.TreeMap;

public class Test implements Comparable<Test> {
    public String getFullName() {
        return name;
    }

    public Support getSupport() {

        if (engineToResult.values().stream().allMatch((it) -> Support.NONE.equals(it.getSupport()))) {
            return Support.NONE;
        } else if (engineToResult.values().stream().allMatch((it) -> Support.TOTAL.equals(it.getSupport()))) {
            return Support.TOTAL;
        } else {
            return Support.PARTIAL;
        }

    }

    public SortedMap<Engine, Result> getEngineToResult() {
        return engineToResult;
    }

    @Override
    public int compareTo(Test o) {
        return getFullName().compareTo(o.getFullName());
    }

    @Override
    public String toString() {
        return "Test{" + "name='" + name + "\'" + ", engineToResult=" + engineToResult + "}";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEngineToResult(SortedMap<Engine, Result> engineToResult) {
        this.engineToResult = engineToResult;
    }

    private String name;
    private SortedMap<Engine, Result> engineToResult = new TreeMap<>();
}
