package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.orms.Cluster;
import com.github.aklakina.edmma.database.orms.Mission;
import com.github.aklakina.edmma.humanInterface.main_window;
import com.github.aklakina.edmma.logicalUnit.threading.CloserMethods;
import com.github.aklakina.edmma.logicalUnit.threading.RegisteredThread;

import java.util.HashMap;

@Singleton
public class StatisticsCollector {
    /*
        this.slider1.setMaximum(cluster.getStackHeight());
        this.slider1.setMinimum(0);
        this.slider1.setValue(0);


     */

    private Cluster cluster;

    public StatisticsCollector() {
        new RegisteredThread(this::collectMissionStatistics, CloserMethods.INTERRUPT).setNamed("MissionStatisticsThread").start();
        new RegisteredThread(this::collectGalaxyStatistics, CloserMethods.INTERRUPT).setNamed("GalaxyStatisticsThread").start();
        new RegisteredThread(this::collectCompletedStatistics, CloserMethods.INTERRUPT).setNamed("CompletedStatisticsThread").start();
        new RegisteredThread(this::collectTheoreticalStatistics, CloserMethods.INTERRUPT).setNamed("TheoreticalStatisticsThread").start();
    }

    public Cluster getCluster() {
        return cluster;
    }

    /**
     * Method to set the cluster.
     * This method will notify all threads when it is done setting the cluster.
     * This method will also notify the main window to reset the slider and set the cluster name.
     *
     * @see main_window#resetSlider()
     * @see main_window#setCluster(String)
     *
     * @see Cluster
     *
     * @param cluster the cluster to be set
     */
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

    /**
     * Method to collect mission statistics.
     * This method will wait for thread notification and then collect mission statistics while the current thread should continue.
     * This method will notify all threads when it is done collecting mission statistics.
     * This method will also notify the main window to display the statistics.
     *
     * @see main_window#displayStatistics(HashMap)
     * @see StatisticsFlag#MISSIONS
     *
     * @see Cluster
     * @see Cluster#getMissions()
     * @see Cluster#getNotCompletedMissions()
     * @see Mission#getKillsLeft()
     * @see Mission#getProgress()
     * @see Mission#getReward()
     */
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

    /**
     * Method to collect galaxy statistics.
     * This method will wait for thread notification and then collect galaxy statistics while the current thread should continue.
     * This method will notify all threads when it is done collecting galaxy statistics.
     * This method will also notify the main window to display the statistics.
     *
     * @see main_window#constructTree(Cluster)
     * @see main_window#constructTable(Cluster)
     * @see StatisticsFlag#GALAXY
     *
     * @see Cluster
     * @see Cluster#getCompletedMissions()
     * @see Cluster#getNotCompletedMissions()
     * @see Mission#getReward()
     * @see Mission#getKillsLeft()
     * @see Mission#getProgress()
     */
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

    /**
     * Method to collect completed statistics.
     * This method will wait for thread notification and then collect completed statistics while the current thread should continue.
     * This method will notify all threads when it is done collecting completed statistics.
     * This method will also notify the main window to display the statistics.
     *
     * @see main_window#displayStatistics(HashMap)
     * @see StatisticsFlag#COMPLETED
     */
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


    /**
     * Method to collect theoretical statistics.
     * This method will wait for thread notification and then collect theoretical statistics while the current thread should continue.
     * This method will notify all threads when it is done collecting theoretical statistics.
     */
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

    /**
     * Enum representing statistics flags.
     * These flags are used to notify threads to collect statistics.
     */
    public enum StatisticsFlag {
        MISSIONS,
        GALAXY,
        COMPLETED,
        THEORETICAL
    }

}
