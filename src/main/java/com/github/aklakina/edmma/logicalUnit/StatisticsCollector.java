package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.ORMConfig;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.Cluster;
import com.github.aklakina.edmma.database.orms.Mission;
import com.github.aklakina.edmma.database.orms.MissionSource;
import com.github.aklakina.edmma.events.Event;
import com.github.aklakina.edmma.humanInterface.main_window;
import com.github.aklakina.edmma.logicalUnit.threading.CloserMethods;
import com.github.aklakina.edmma.logicalUnit.threading.RegisteredThread;
import com.github.aklakina.edmma.logicalUnit.threading.Threads;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.hibernate.Criteria;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

@Singleton
public class StatisticsCollector {
    private long clusterID;

    private final LinkedBlockingQueue<StatisticsFlag> statisticsToCollect = new LinkedBlockingQueue<>();

    private static final EntityManager entityManager = ORMConfig.sessionFactory.createEntityManager();

    /**
     * This method is used to notify the collector about the statistics that need to be collected.
     * It takes an array of StatisticsFlag enums as an argument.
     * For each flag in the array, it checks if the flag is already present in the statisticsToCollect queue.
     * If the flag is not present, it is added to the queue.
     *
     * @param flags An array of StatisticsFlag enums representing the statistics to be collected.
     */
    public void notifyCollector(StatisticsFlag[] flags) {
        for (StatisticsFlag flag : flags) {
            if (statisticsToCollect.contains(flag))
                continue;
            statisticsToCollect.offer(flag);
        }
    }

    /**
     * This is the constructor for the StatisticsCollector class.
     * It creates a new RegisteredThread and starts it.
     * The thread runs the collector method when it is started.
     * The thread is named "StatisticCollector" and it is interrupted when the application is closed.
     */
    public StatisticsCollector() {
        new RegisteredThread(this::collector, CloserMethods.INTERRUPT).setNamed("StatisticCollector").start();
    }

    public Cluster getCluster() {
        try {
            Cluster temp = Queries_.getClusterById(entityManager, clusterID);
            entityManager.refresh(temp);
            for (Mission mission : temp.getMissions()) {
                entityManager.refresh(mission);
            }
            for (MissionSource missionSource : temp.getMissionSources()) {
                entityManager.refresh(missionSource);
            }
            return temp;
        } catch (NoResultException e) {
            return null;
        }
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
     * @param id the id of the cluster to be set
     */
    public void setCluster(Long id) {
        this.clusterID = id;
        notifyCollector(new StatisticsFlag[]{StatisticsFlag.MISSIONS, StatisticsFlag.GALAXY, StatisticsFlag.COMPLETED, StatisticsFlag.THEORETICAL});
        SingletonFactory.getSingleton(main_window.class).resetSlider();
        SingletonFactory.getSingleton(main_window.class).setCluster(getCluster().getTargetSystem().getName());
    }

    /**
     * The main loop that collects statistics and provides them to the main window.
     * This method will wait for a statistics flag to be added to the statisticsToCollect queue.
     * When a flag is added, it will collect the statistics and add them to a map.
     * It will then collect all the flags in the queue and collect the statistics for each flag.
     *
     * @see StatisticsFlag
     * @see main_window
     * @see Cluster
     *
     * @see StatisticsFlag#collectStats(HashMap)
     */
    private void collector() {
        while (RegisteredThread.currentThread().shouldContinue()) {
            StatisticsFlag flag;
            try {
                synchronized (this) {
                    this.notifyAll();
                }
                flag = statisticsToCollect.take();
                RegisteredThread eventHandler = SingletonFactory.getSingleton(Threads.class).getThreadByName("EventHandler");
                if (!eventHandler.isWaiting()) {
                    synchronized (SingletonFactory.getSingleton(EventHandler.class)) {
                        SingletonFactory.getSingleton(EventHandler.class).wait();
                    }
                }
            } catch (InterruptedException e) {
                continue;
            }
            HashMap<String, String> statistics = new HashMap<>();
            flag.collectStats(statistics);
            for (;flag != null; flag = statisticsToCollect.poll()) {
                flag.collectStats(statistics);
            }
            SingletonFactory.getSingleton(main_window.class).displayStatistics(statistics);
        }
        entityManager.close();
    }

    /**
     * Enum representing statistics flags.
     * These flags are used to notify threads to collect statistics.
     */
    public enum StatisticsFlag {
        /**
         * Enum to collect mission statistics.
         */
        MISSIONS {
            @Override
            public void collectStats(HashMap<String, String> statistics) {
                Cluster cluster = SingletonFactory.getSingleton(StatisticsCollector.class).getCluster();
                int stackHeight = cluster.getStackHeight();
                statistics.put("killCounter", String.valueOf(cluster.getMissions().stream().mapToInt(m -> m.getProgress()).sum()));
                statistics.put("killsLeft", String.valueOf(cluster.getNotCompletedMissions().stream().mapToInt(m -> m.getKillsLeft()).sum()));
                statistics.put("killEfficiency", String.valueOf(cluster.getNotCompletedMissions().stream().mapToDouble(m -> m.getKillsLeft()).sum() / (stackHeight==0?1.0:stackHeight)));
                statistics.put("maxKills", String.valueOf(cluster.getStackHeight()));
            }
        },
        /**
         * Enum to collect galaxy statistics.
         */
        GALAXY {
            @Override
            public void collectStats(HashMap<String, String> statistics) {
                Cluster cluster = SingletonFactory.getSingleton(StatisticsCollector.class).getCluster();
                SingletonFactory.getSingleton(main_window.class).constructTree(cluster);
            }
        },
        /**
         * Enum to collect completed statistics.
         */
        COMPLETED {
            @Override
            public void collectStats(HashMap<String, String> statistics) {
                Cluster cluster = SingletonFactory.getSingleton(StatisticsCollector.class).getCluster();
                int stackHeight = cluster.getStackHeight();
                statistics.put("completedCounter", String.valueOf(cluster.getCompletedMissions().size()));
                statistics.put("missionsLeft", String.valueOf(cluster.getNotCompletedMissions().size()));
                statistics.put("PaymentCounter", String.valueOf(cluster.getCompletedMissions().stream().mapToDouble(m -> m.getReward()).sum()));
                statistics.put("paymentRatio", String.valueOf(cluster.getNotCompletedMissions().stream().mapToDouble(m -> m.getReward()).sum() / (stackHeight==0?1.0:stackHeight)));
                statistics.put("paymentLeft", String.valueOf(cluster.getNotCompletedMissions().stream().mapToDouble(m -> m.getReward()).sum()));
            }
        },

        /**
         * Enum to collect theoretical statistics.
         */
        THEORETICAL {
            @Override
            public void collectStats(HashMap<String, String> statistics) {
                Cluster cluster = SingletonFactory.getSingleton(StatisticsCollector.class).getCluster();
                int theorKills = SingletonFactory.getSingleton(main_window.class).getTheorKills();
                statistics.put("theorKills", String.valueOf(theorKills));
                statistics.put("theorMissions", String.valueOf(
                        cluster.getCompletedMissions().size() + cluster.getNotCompletedMissions().stream().filter(m ->
                                m.getKillsLeft() <= theorKills).count()));
                statistics.put("theorReward", String.valueOf(
                        cluster.getCompletedMissions().stream().mapToDouble(Mission::getReward).sum() +
                                cluster.getNotCompletedMissions().stream().filter(m ->
                                        m.getKillsLeft() <= theorKills).mapToDouble(Mission::getReward).sum()));
            }
        };

        /**
         * Method to collect statistics.
         * This method is overridden by each enum to collect the correct statistics.
         *
         * @param statistics the statistics map to collect the statistics into
         */
        public abstract void collectStats(HashMap<String, String> statistics);
    }

}
