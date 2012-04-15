package fr.free.jnizet.shivadep;

public class Option {
    private String value;
    private String label;

    public Option(String value, String label) {
        super();
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
