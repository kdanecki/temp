package org.example;

import java.util.Objects;

public class Mage {
    private String name;
    private int level;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Mage mage = (Mage) o;

        if (level != mage.level) return false;
        return Objects.equals(name, mage.name);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + level;
        return result;
    }

    public Mage(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }
}
