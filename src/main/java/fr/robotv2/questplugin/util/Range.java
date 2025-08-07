package fr.robotv2.questplugin.util;

public class Range {

    private final double min;
    private final double max;

    public Range(double value) {
        this.min = value;
        this.max = value;
    }

    public Range(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public Range(int value) {
        this.min = value;
        this.max = value;
    }

    public Range(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public Range(String literal) {
        final String[] split = literal.split("-");
        if(split.length == 1) {
            this.min = Double.parseDouble(split[0]);
            this.max = Double.parseDouble(split[0]);
        } else {
            this.min = Double.parseDouble(split[0]);
            this.max = Double.parseDouble(split[1]);
        }
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double random() {
        return Math.random() * (max - min) + min;
    }

    public int randomInt() {
        return (int) Math.round(random());
    }
}
