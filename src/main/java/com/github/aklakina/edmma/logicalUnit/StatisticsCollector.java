package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.orms.Cluster;
import com.github.aklakina.edmma.database.orms.Mission;
import com.github.aklakina.edmma.humanInterface.main_window;

import java.util.HashMap;
import java.util.List;

@Singleton
public class StatisticsCollector {
    /*
        this.slider1.setMaximum(cluster.getStackHeight());
        this.slider1.setMinimum(0);
        this.slider1.setValue(0);


     */
    private List<Thread> threads = new java.util.ArrayList<>();

    public StatisticsCollector() {
        threads.add(new Thread(this::collectMissionStatistics));
        threads.add(new Thread(this::collectGalaxyStatistics));
        threads.add(new Thread(this::collectCompletedStatistics));
        threads.add(new Thread(this::collectTheoreticalStatistics));
        for (Thread thread : threads) {
            thread.start();
        }
    }

    public void Shutdown() {
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    private Cluster cluster;

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
        StatisticsFlag.MISSIONS.notify();
        StatisticsFlag.GALAXY.notify();
        StatisticsFlag.COMPLETED.notify();
        StatisticsFlag.THEORETICAL.notify();
        this.notifyAll();
        SingletonFactory.getSingleton(main_window.class).resetSlider();
    }

    public Cluster getCluster() {
        return cluster;
    }

    private void collectMissionStatistics() {
        for (;;) {
            try {
                StatisticsFlag.MISSIONS.wait();
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HashMap<String, String> statistics = new HashMap<>();
            statistics.put("killCounter", String.valueOf(cluster.getMissions().stream().mapToInt(m -> m.getProgress()).sum()));
            statistics.put("killsLeft", String.valueOf(cluster.getNotCompletedMissions().stream().mapToInt(m -> m.getKillsLeft()).sum()));
            statistics.put("missionsLeft", String.valueOf(cluster.getNotCompletedMissions().size()));
            statistics.put("killEfficiency", String.valueOf(cluster.getNotCompletedMissions().stream().mapToDouble(m -> m.getKillsLeft()).sum() / cluster.getStackHeight()));
            statistics.put("maxKills", String.valueOf(cluster.getStackHeight()));
            SingletonFactory.getSingleton(main_window.class).displayStatistics(statistics);
        }
    }

    private void collectGalaxyStatistics() {
        for (;;) {
            try {
                StatisticsFlag.GALAXY.wait();
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            SingletonFactory.getSingleton(main_window.class).constructTree(this.cluster);
            SingletonFactory.getSingleton(main_window.class).constructTable(this.cluster);
        }
    }

    private void collectCompletedStatistics() {
        for (;;) {
            try {
                StatisticsFlag.COMPLETED.wait();
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HashMap<String, String> statistics = new HashMap<>();
            statistics.put("completedCounter", String.valueOf(cluster.getCompletedMissions().size()));
            statistics.put("PaymentCounter", String.valueOf(cluster.getCompletedMissions().stream().mapToDouble(m -> m.getReward()).sum()));
            statistics.put("paymentRatio", String.valueOf(cluster.getNotCompletedMissions().stream().mapToDouble(m -> m.getReward()).sum() / cluster.getNotCompletedMissions().stream().mapToDouble(m -> m.getKillsLeft()).sum()));
            statistics.put("paymentLeft", String.valueOf(cluster.getNotCompletedMissions().stream().mapToDouble(m -> m.getReward()).sum()));
            SingletonFactory.getSingleton(main_window.class).displayStatistics(statistics);
        }
    }

    private void collectTheoreticalStatistics() {
        for (;;) {
            try {
                StatisticsFlag.THEORETICAL.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int theorKills = SingletonFactory.getSingleton(main_window.class).getTheorKills();
            HashMap<String, String> statistics = new HashMap<>();
            statistics.put("theorKills", String.valueOf(theorKills));
            statistics.put("theorMissions", String.valueOf(
                    cluster.getCompletedMissions().size() + cluster.getNotCompletedMissions().stream().filter(m ->
                            m.getKillsLeft() <= theorKills).count()));
            statistics.put("theorReward", String.valueOf(
                    cluster.getCompletedMissions().stream().mapToDouble(Mission::getReward).sum() +
                            cluster.getNotCompletedMissions().stream().filter(m ->
                                    m.getKillsLeft() <= theorKills).mapToDouble(Mission::getReward).sum()));
            SingletonFactory.getSingleton(main_window.class).displayStatistics(statistics);
        }
    }

    public static enum StatisticsFlag {
        MISSIONS,
        GALAXY,
        COMPLETED,
        THEORETICAL
    }

}
