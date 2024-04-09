package org.example;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
class Tower {
    public Tower() {
        this.name = "Tower of NULL";
        this.height = -1;
        this.mages = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Id
    private String name;
    private int height;

    public List<Mage> getMages() {
        return mages;
    }

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "tower", cascade = CascadeType.REMOVE)
    private List<Mage> mages = new ArrayList<>();

    public Tower(String name, int height, List<Mage> mages) {
        this.name = name;
        this.height = height;
        this.mages = mages;
    }
}
