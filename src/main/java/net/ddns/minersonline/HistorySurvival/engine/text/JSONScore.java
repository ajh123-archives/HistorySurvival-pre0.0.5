package net.ddns.minersonline.HistorySurvival.engine.text;

public class JSONScore {
    private String name;
    private String objective;
    private String value;

    public JSONScore(String name, String objective, String value) {
        this.name = name;
        this.objective = objective;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
