package com.github.aklakina.edmma.humanInterface;

import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.orms.System;
import com.github.aklakina.edmma.database.orms.*;
import com.github.aklakina.edmma.humanInterface.components.*;
import com.github.aklakina.edmma.logicalUnit.Init;
import com.github.aklakina.edmma.logicalUnit.StatisticsCollector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;

/**
 * This class represents the main window of the application.
 * It contains methods for constructing the tree and table views, displaying statistics, and copying data to the clipboard.
 * It also contains a main method to start the application.
 */
@Singleton
public class main_window {
    private final HashMap<TreeObject<System>, HashMap<TreeObject<System>,
            HashMap<TreeObject<Station>, HashSet<TreeObject<Faction>>>>> treeData = new HashMap<>();
    private JPanel panel1;
    private JTable table1;
    private JTree tree1;
    private JTabbedPane tabbedPane1;
    private JSlider slider1;
    private JButton copyCalculatedDataToButton;
    private JButton copyFullDataToButton;
    private JButton copyOnlyCompletedDataButton;
    private TitleLabel clusterName;
    private DefaultLabel completedCounter;
    private DefaultLabel killCounter;
    private DefaultLabel PaymentCounter;
    private DefaultLabel paymentRatio;
    private DefaultLabel missionsLeft;
    private DefaultLabel killsLeft;
    private DefaultLabel paymentLeft;
    private DefaultLabel killEfficiency;
    private DefaultLabel theorKills;
    private DefaultLabel theorMissions;
    private DefaultLabel maxKills;
    private DefaultLabel theorReward;

    /**
     * The constructor sets the cell renderer for the tree view.
     */
    public main_window() {
        tree1.setCellRenderer(new TreeRenderer());
    }
    /**
     * The main method starts the application.
     * It checks if the directory containing the Elite Dangerous log files exists, starts the Init singleton, creates a JFrame, sets the content pane to the main window's panel, and makes the frame visible.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        if (!Paths.get(Globals.ELITE_LOG_HOME).toFile().exists()) {
            JOptionPane.showMessageDialog(null, "The directory " + Globals.ELITE_LOG_HOME + " does not exist. Please make sure that the directory exists and contains the Elite Dangerous log files. If the directory does not exists only unit tests can run on the computer.");
            java.lang.System.exit(0);
        }
        SingletonFactory.getSingleton(Init.class).start();
        JFrame frame = new JFrame("EDMMA");
        main_window main = SingletonFactory.getSingleton(main_window.class);
        frame.setContentPane(main.panel1);
        /*main.panel1.setBackground(new Color(34, 34, 34, 100));
        main.tree1.setBackground(new Color(34, 34, 34, 100));
        main.table1.setBackground(new Color(34, 34, 34, 100));
        main.tabbedPane1.setBackground(new Color(34, 34, 34, 100));*/

        // Create a JMenuBar
        JMenuBar menuBar = new JMenuBar();

        // Create a JMenu
        JMenu menu = new JMenu("File");

        // Create a JMenuItem with an action to close the application
        JMenuItem closeItem = new JMenuItem("Close");
        closeItem.addActionListener(e -> java.lang.System.exit(0));

        // Add the JMenuItem to the JMenu
        menu.add(closeItem);

        // Add the JMenu to the JMenuBar
        menuBar.add(menu);

        // Add the JMenuBar to the JFrame
        frame.setJMenuBar(menuBar);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        synchronized (SingletonFactory.getSingleton(StatisticsCollector.class)) {
            Globals.INITIALIZED = true;
            SingletonFactory.getSingleton(StatisticsCollector.class).notify();
        }

    }

    /**
     * This method sets the cluster name in the main window.
     *
     * @param clusterName The name of the cluster.
     */
    public void setCluster(String clusterName) {
        this.clusterName.setText(clusterName);
    }

    /**
     * This method starts the main window.
     * It adds change listeners to the slider and action listeners to the copy buttons.
     */
    public void start() {
        this.slider1.addChangeListener(e -> updateTheoreticalValues());
        this.copyCalculatedDataToButton.addActionListener(e -> copyDataToClipboard(getCalculatedData()));
        this.copyFullDataToButton.addActionListener(e -> copyDataToClipboard(getFullData()));
        this.copyOnlyCompletedDataButton.addActionListener(e -> copyDataToClipboard(getOnlyCompletedData()));
    }

    /**
     * This method updates the theoretical values by notifying the THEORETICAL flag of the StatisticsCollector.
     */
    private void updateTheoreticalValues() {
        StatisticsCollector.StatisticsFlag.THEORETICAL.notify();
    }

    /**
     * This method returns the value of the slider which represents the theoretical kills.
     *
     * @return The value of the slider.
     */
    public int getTheorKills() {
        return this.slider1.getValue();
    }

    /**
     * This method resets the slider by setting its value and minimum to 0.
     */
    public void resetSlider() {
        this.slider1.setValue(0);
        this.slider1.setMinimum(0);
    }

    /**
     * This method displays the statistics by setting the text of the corresponding labels.
     *
     * @param stats A map containing the statistics. The key is the name of the statistic and the value is the value of the statistic.
     */
    public void displayStatistics(HashMap<String, String> stats) {
        Class<?> clazz = this.getClass();
        for (Map.Entry<String, String> entry : stats.entrySet()) {
            try {
                Field field = clazz.getDeclaredField(entry.getKey());
                if (field.getType() == DefaultLabel.class) {
                    DefaultLabel label = (DefaultLabel) field.get(this);
                    label.setText(entry.getValue());
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method copies the given data to the clipboard.
     *
     * @param data The data to be copied to the clipboard.
     */
    private void copyDataToClipboard(String data) {
        StringSelection stringSelection = new StringSelection(data);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    /**
     * This method returns a string containing the calculated data.
     *
     * @return A string containing the calculated data.
     */
    private String getCalculatedData() {
        return "Cluster name: " + this.clusterName.getText() + "\n" +
                "Completed missions: " + this.completedCounter.getText() + "\n" +
                "Kills: " + this.killCounter.getText() + "\n" +
                "Payment: " + this.PaymentCounter.getText() + "\n" +
                "Payment ratio: " + this.paymentRatio.getText() + "\n" +
                "Missions left: " + this.missionsLeft.getText() + "\n" +
                "Kills left: " + this.killsLeft.getText() + "\n" +
                "Payment left: " + this.paymentLeft.getText() + "\n" +
                "Kill efficiency: " + this.killEfficiency.getText() + "\n" +
                "Theoretical kills: " + this.theorKills.getText() + "\n" +
                "Theoretical missions: " + this.theorMissions.getText() + "\n" +
                "Max kills: " + this.maxKills.getText() + "\n" +
                "Theoretical reward: " + this.theorReward.getText() + "\n";
    }

    /**
     * This method returns a string containing the full data.
     *
     * @return A string containing the full data.
     */
    private String getFullData() {
        return "Cluster name: " + this.clusterName.getText() + "\n" +
                "Completed missions: " + this.completedCounter.getText() + "\n" +
                "Kills: " + this.killCounter.getText() + "\n" +
                "Payment: " + this.PaymentCounter.getText() + "\n" +
                "Payment ratio: " + this.paymentRatio.getText() + "\n" +
                "Missions left: " + this.missionsLeft.getText() + "\n" +
                "Kills left: " + this.killsLeft.getText() + "\n" +
                "Payment left: " + this.paymentLeft.getText() + "\n" +
                "Kill efficiency: " + this.killEfficiency.getText() + "\n";
    }

    /**
     * This method returns a string containing only the completed data.
     *
     * @return A string containing only the completed data.
     */
    private String getOnlyCompletedData() {
        return "Cluster name: " + this.clusterName.getText() + "\n" +
                "Completed missions: " + this.completedCounter.getText() + "\n" +
                "Kills: " + this.killCounter.getText() + "\n" +
                "Payment: " + this.PaymentCounter.getText() + "\n" +
                "Payment ratio: " + this.paymentRatio.getText() + "\n";
    }

    /**
     * This method constructs the tree view for the given cluster.
     *
     * @param cluster The cluster for which the tree view is to be constructed.
     */
    public void constructTree(Cluster cluster) {
        this.clusterName.setText(cluster.getTargetFaction().getName());
        DefaultTreeModel model = (DefaultTreeModel) tree1.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(cluster.getTargetSystem().getName());
        model.setRoot(root);

        for (MissionSource missionSource : cluster.getMissionSources()) {
            TreeObject<System> system = new TreeObject<>(missionSource.getStation().getSystem());
            TreeObject<Station> station = new TreeObject<>(missionSource.getStation());
            TreeObject<Faction> faction = new TreeObject<>(missionSource.getFaction());

            // Fill the treeData object
            HashMap<TreeObject<System>, HashMap<TreeObject<Station>, HashSet<TreeObject<Faction>>>> targetSystemMap = treeData.computeIfAbsent(new TreeObject<>(cluster.getTargetSystem()), k -> {
                k.node = root;
                return new HashMap<>();
            });

            HashMap<TreeObject<Station>, HashSet<TreeObject<Faction>>> systemMap = targetSystemMap.computeIfAbsent(
                    system, k -> {
                        DefaultMutableTreeNode systemNode = new DefaultMutableTreeNode(system.object.getName());
                        root.add(systemNode);
                        k.node = systemNode;
                        return new HashMap<>();
                    });

            HashSet<TreeObject<Faction>> stationSet = systemMap.computeIfAbsent(station, k -> {
                DefaultMutableTreeNode stationNode = new DefaultMutableTreeNode(station.object.getName());
                system.node.add(stationNode);
                k.node = stationNode;
                return new HashSet<>();
            });

            if (stationSet.add(faction)) {
                DefaultMutableTreeNode factionNode = new DefaultMutableTreeNode(faction.object.getName());
                station.node.add(factionNode);
                faction.node = factionNode;
            }

        }

        model.reload();
    }

    /**
     * This method constructs the table view for the given cluster.
     *
     * @param cluster The cluster for which the table view is to be constructed.
     */
    public void constructTable(Cluster cluster) {
        // Clear the table
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) {
                    return Faction.class;
                } else if (columnIndex == getColumnCount() - 1) {
                    return Integer.class;
                } else {
                    return Mission.class;
                }
            }
        };
        table1.setModel(tableModel);

        // Create a map to hold the Faction and its corresponding missions
        Map<Faction, List<Mission>> factionMissionsMap = new HashMap<>();
        int maxMissions = 0;

        for (MissionSource missionSource : cluster.getMissionSources()) {
            Faction faction = missionSource.getFaction();
            if (!factionMissionsMap.containsKey(faction)) {
                factionMissionsMap.put(faction, new ArrayList<>());
            }
            Set<Mission> missions = missionSource.getNotCompletedMissions();
            factionMissionsMap.get(faction).addAll(missions);
            // sort by acceptance time
            factionMissionsMap.get(faction).sort(Comparator.comparing(Mission::getAcceptTime));
            maxMissions = Math.max(maxMissions, missions.size());
        }
        // set the default renderer for each class
        table1.setDefaultRenderer(Faction.class, new FactionColumnRenderer());
        table1.setDefaultRenderer(Mission.class, new MissionColumnRenderer());
        table1.setDefaultRenderer(Integer.class, new AggregationColumnRenderer());
        // Create a table model with the number of columns equal to the maxMissions + 2
        tableModel.addColumn("Faction Name");
        for (int i = 1; i <= maxMissions; i++) {
            tableModel.addColumn("Mission " + i);
        }
        tableModel.addColumn("Sum of kills");

        // Fill the table with the data
        for (Map.Entry<Faction, List<Mission>> entry : factionMissionsMap.entrySet()) {
            List<Mission> missions = entry.getValue();
            if (missions.isEmpty())
                continue;
            Object[] row = new Object[maxMissions + 2];
            row[0] = entry.getKey();
            for (int i = 0; i < missions.size(); i++) {
                row[i + 1] = missions.get(i);
            }
            row[maxMissions + 1] = 0;
            tableModel.addRow(row);
        }

        // Set the table model to the table
        table1.setModel(tableModel);
        // Sort the table by the last column
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setComparator(maxMissions + 1, Comparator.comparingInt(o -> (int) o));
        sorter.setSortKeys(Collections.singletonList(new RowSorter.SortKey(maxMissions + 1, SortOrder.DESCENDING)));
        table1.setRowSorter(sorter);
    }

    /**
     * This class represents an object in the tree view.
     * It contains the object and its corresponding tree node.
     *
     * @param <T> The type of the object.
     */
    private static class TreeObject<T> {
        T object;
        DefaultMutableTreeNode node;

        /**
         * The constructor sets the object and its corresponding tree node.
         *
         * @param object The object.
         * @param node The tree node.
         */
        public TreeObject(T object, DefaultMutableTreeNode node) {
            this.object = object;
            this.node = node;
        }

        /**
         * The constructor sets the object.
         *
         * @param object The object.
         */
        public TreeObject(T object) {
            this.object = object;
        }

        /**
         * This method checks if this object is equal to the given object.
         *
         * @param o The object to be compared with this object.
         * @return true if the objects are equal, false otherwise.
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TreeObject<?> that = (TreeObject<?>) o;
            return Objects.equals(object, that.object);
        }

        /**
         * This method returns a hash code value for the object.
         *
         * @return A hash code value for the object.
         */
        @Override
        public int hashCode() {
            return Objects.hash(object);
        }

    }
}
