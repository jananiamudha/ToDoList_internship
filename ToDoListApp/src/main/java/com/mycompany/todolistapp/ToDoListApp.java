package com.mycompany.todolistapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ToDoListApp {
    private JFrame frame;
    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskList;
    private JTextField taskField;
    private static final String DATA_FILE = "tasks.dat";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoListApp::new);
    }

    public ToDoListApp() {
        frame = new JFrame("To-Do List");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        taskListModel = new DefaultListModel<>();
        loadTasks();

        taskList = new JList<>(taskListModel);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(taskList);

        taskField = new JTextField();
        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton completeButton = new JButton("Complete");

        addButton.addActionListener(new AddTaskAction());
        updateButton.addActionListener(new UpdateTaskAction());
        deleteButton.addActionListener(new DeleteTaskAction());
        completeButton.addActionListener(new CompleteTaskAction());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(1, 2));
        inputPanel.add(taskField);
        inputPanel.add(addButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(completeButton);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void loadTasks() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            List<Task> tasks = (List<Task>) ois.readObject();
            for (Task task : tasks) {
                taskListModel.addElement(task);
            }
        } catch (IOException | ClassNotFoundException e) {
            // No tasks to load or file not found
        }
    }

    private void saveTasks() {
        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < taskListModel.size(); i++) {
            tasks.add(taskListModel.getElementAt(i));
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(tasks);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class AddTaskAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String taskDescription = taskField.getText().trim();
            if (!taskDescription.isEmpty()) {
                taskListModel.addElement(new Task(taskDescription));
                taskField.setText("");
                saveTasks();
            }
        }
    }

    private class UpdateTaskAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                Task selectedTask = taskListModel.getElementAt(selectedIndex);
                String newDescription = JOptionPane.showInputDialog("Update Task", selectedTask.getDescription());
                if (newDescription != null && !newDescription.trim().isEmpty()) {
                    selectedTask.setDescription(newDescription);
                    taskList.repaint();
                    saveTasks();
                }
            }
        }
    }

    private class DeleteTaskAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                taskListModel.removeElementAt(selectedIndex);
                saveTasks();
            }
        }
    }

    private class CompleteTaskAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = taskList.getSelectedIndex();
            if (selectedIndex != -1) {
                Task selectedTask = taskListModel.getElementAt(selectedIndex);
                selectedTask.setComplete(!selectedTask.isComplete());
                taskList.repaint();
                saveTasks();
            }
        }
    }
}
