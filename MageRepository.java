package org.example;

import org.example.Mage;

import java.util.*;

public class MageRepository {
    public MageRepository() {
        collection = new ArrayList<>();
    }

    private Collection<Mage> collection;

    public Optional<Mage> find(String name) {
        for (Mage m :
                collection) {
            if (m.getName().equals(name))
                return Optional.of(m);
        }
        return Optional.empty();
    }

    public void delete(String name) {
        if (find(name).isEmpty()) {
            throw new IllegalArgumentException("Mage with name " + name + " not found");
        }
        collection.remove(find(name).get());
    }

    public void save(Mage mage) {
        if (collection.contains(mage)) {
            throw new IllegalArgumentException("Mage with name " + mage.getName() + " already exists");
        }
        collection.add(mage);
    }
}
