package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.orms.Cluster;
import com.github.aklakina.edmma.database.orms.Mission;
import com.github.aklakina.edmma.humanInterface.main_window;
import com.github.aklakina.edmma.logicalUnit.threading.CloserMethods;
import com.github.aklakina.edmma.logicalUnit.threading.RegisteredThread;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.XsiNilLoader;

import java.util.HashMap;
import java.util.List;

@Singleton
public class StatisticsCollector {
    /*
        this.slider1.setMaximum(cluster.getStackHeight());
        this.slider1.setMinimum(0);
        this.slider1.setValue(0);


     */

    public StatisticsCollector() {
        new RegisteredThread(this::collectMissionStatistics, CloserMethods.INTERRUPT).setNamed("MissionStatisticsThread").start();
        new RegisteredThread(this::collectGalaxyStatistics, CloserMethods.INTERRUPT).setNamed("GalaxyStatisticsThread").start();
        new RegisteredThread(this::collectCompletedStatistics, CloserMethods.INTERRUPT).setNamed("CompletedStatisticsThread").start();
        new RegisteredThread(this::collectTheoreticalStatistics, CloserMethods.INTERRUPT).setNamed("TheoreticalStatisticsThread").start();
    }

    private Cluster cluster;

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
        synchronized (StatisticsFlag.MISSIONS) {
            StatisticsFlag.MISSIONS.notify();
        }
        synchronized (StatisticsFlag.GALAXY) {
            StatisticsFlag.GALAXY.notify();
        }
        synchronized (StatisticsFlag.COMPLETED) {
            StatisticsFlag.COMPLETED.notify();
        }
        synchronized (StatisticsFlag.THEORETICAL) {
            StatisticsFlag.THEORETICAL.notify();
        }
        synchronized (this) {
            this.notifyAll();
        }
        SingletonFactory.getSingleton(main_window.class).resetSlider();
        SingletonFactory.getSingleton(main_window.class).setCluster(cluster.getTargetSystem().getName());
    }

    public Cluster getCluster() {
        return cluster;
    }

    private void collectMissionStatistics() {
        while (RegisteredThread.currentThread().shouldContinue()) {
            try {
                synchronized (StatisticsFlag.MISSIONS) {
                    StatisticsFlag.MISSIONS.wait();
                }
                synchronized (this) {
                    this.wait();
                }
            } catch (InterruptedException e) {
                continue;
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
        while (RegisteredThread.currentThread().shouldContinue()) {
            try {
                synchronized (StatisticsFlag.GALAXY) {
                    StatisticsFlag.GALAXY.wait();
                }
                synchronized (this) {
                    this.wait();
                }
            } catch (InterruptedException e) {
                continue;
            }
            SingletonFactory.getSingleton(main_window.class).constructTree(this.cluster);
            SingletonFactory.getSingleton(main_window.class).constructTable(this.cluster);
        }
    }

    private void collectCompletedStatistics() {
        while (RegisteredThread.currentThread().shouldContinue()) {
            try {
                synchronized (StatisticsFlag.COMPLETED) {
                    StatisticsFlag.COMPLETED.wait();
                }
                synchronized (this) {
                    this.wait();
                }
            } catch (InterruptedException e) {
                continue;
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
        while (RegisteredThread.currentThread().shouldContinue()) {
            try {
                synchronized (StatisticsFlag.THEORETICAL) {
                    StatisticsFlag.THEORETICAL.wait();
                }
            } catch (InterruptedException e) {
                continue;
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
