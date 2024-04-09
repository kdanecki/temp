package org.example;

import org.hibernate.event.spi.ClearEventListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static EntityManagerFactory entityManagerFactory;

    public static void main(String[] args) {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        entityManagerFactory = Persistence.createEntityManagerFactory("MyPersistenceUnit");

        addTowerWithMages("Tower of Fire", 100, Arrays.asList("Gandalf", "Merlin"));
        addTowerWithMages("Tower of Ice", 80, Arrays.asList("Iceman", "Frosty"));
        addEmptyTower("Tower of Void", 15100900);
        addMage("boo", 123, "");
        addMage("Radagast", 7, "Tower of Fire");

        printTowersAndMages();

        List<Mage> mages = getAllMagesWithLevel(10);
        for (Mage m :
                mages) {
            System.out.println(m.getName() + " has big level = " + m.getLevel());
        }
        List<Tower> towers = getAllShortTowers(90);
        for (Tower t :
                towers) {
            System.out.println(t.getName() + " is short = " + t.getHeight());
        }
        mages = getAllMagesWithLevelFromTower(10, "Tower of Fire");
        for (Mage m :
                mages) {
            System.out.println(m.getName() + " has big level = " + m.getLevel() + " and is from " + m.getTower().getName());
        }

        removeTower("Tower of Fire");
        removeMage("Iceman");

        System.out.println("\nafter removal\n");

        printTowersAndMages();

        entityManagerFactory.close();
    }

    private static void printTowersAndMages() {
        List<Tower> towers = getAllTowers();
        for (Tower tower : towers) {
            System.out.println("Tower: " + tower.getName() + ", Height: " + tower.getHeight());
            for (Mage mage : tower.getMages()) {
                System.out.println("  Mage: " + mage.getName() + ", Level: " + mage.getLevel());
            }
        }
        List<Mage> mages = getAllMages();
        for (Mage m: mages) {
            if (m.getTower() == null) {
                System.out.println("Mage: " + m.getName() + " no tower");
            }
        }
    }

    private static void addMage(String name, int level, String towerName) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Tower tower = entityManager.find(Tower.class, towerName);
        Mage mage = new Mage(name, level, tower);

        entityManager.persist(mage);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private static void addEmptyTower(String name, int height) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Tower tower = new Tower(name, height, new ArrayList<>());
        tower.setName(name);
        tower.setHeight(height);

        entityManager.persist(tower);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private static void removeTower(String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Tower tower = entityManager.find(Tower.class, name);
        entityManager.remove(tower);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private static void removeMage(String name) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Mage mage = entityManager.find(Mage.class, name);
        entityManager.remove(mage);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private static void addTowerWithMages(String towerName, int towerHeight, List<String> mageNames) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();

        Tower tower = new Tower(towerName, towerHeight, new ArrayList<>());
        tower.setName(towerName);
        tower.setHeight(towerHeight);

        for (String name : mageNames) {
            Mage mage = new Mage(name, 50, tower);
            tower.getMages().add(mage);
            entityManager.persist(mage);
        }

        entityManager.persist(tower);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    private static List<Tower> getAllTowers() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<Tower> towers = entityManager.createQuery("SELECT t FROM Tower t", Tower.class).getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();

        return towers;
    }

    private static List<Mage> getAllMages() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        List<Mage> mages = entityManager.createQuery("SELECT m FROM Mage m", Mage.class).getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();

        return mages;
    }

    private static List<Mage> getAllMagesWithLevel(int min_level) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("SELECT m FROM Mage m WHERE level > :min_level", Mage.class);
        query.setParameter("min_level", min_level);
        List<Mage> mages = query.getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();

        return mages;
    }

    private static List<Tower> getAllShortTowers(int max_height) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("SELECT t FROM Tower t WHERE height < :max_height", Tower.class);
        query.setParameter("max_height", max_height);
        List<Tower> towers = query.getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();

        return towers;
    }

    private static List<Mage> getAllMagesWithLevelFromTower(int min_level, String towerName) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("SELECT m FROM Mage m WHERE level > :min_level AND tower.name = :tower_name", Mage.class);
        query.setParameter("min_level", min_level);
        query.setParameter("tower_name", towerName);
        List<Mage> mages = query.getResultList();
        entityManager.getTransaction().commit();
        entityManager.close();

        return mages;
    }
}

