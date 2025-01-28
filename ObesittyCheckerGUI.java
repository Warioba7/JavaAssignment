/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package obesittycheckergui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ObesittyCheckerGUI extends JFrame {
    private JTextArea textArea;
    private JTextField nameField, ageField, addressField, dateField, heightField, weightField, searchField;
    private JButton saveButton, viewButton, searchButton;
    private List<List<String>> clients;  // List of client details as lists of strings

    public ObesittyCheckerGUI() {
        clients = new ArrayList<>();

        // Setting up the frame
        setTitle("Obesity Checker");
        setSize(400, 550); // Increased size to accommodate search field
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Using GridLayout for organized input
        setLayout(new GridLayout(9, 2, 10, 10));  // 9 rows, 2 columns with 10px gap between components

        // Client details input fields
        nameField = new JTextField(15);
        ageField = new JTextField(5);
        addressField = new JTextField(20);
        dateField = new JTextField(10);
        heightField = new JTextField(5);
        weightField = new JTextField(5);
        searchField = new JTextField(15); // Search field for client name

        // Adding labels and text fields to the frame
        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("Age:"));
        add(ageField);
        add(new JLabel("Address:"));
        add(addressField);
        add(new JLabel("Date of Check-up:"));
        add(dateField);
        
        // Search label and search field
        add(new JLabel("Search by Name:"));
        add(searchField);

        // Save, View, and Search buttons
        saveButton = new JButton("Save");
        viewButton = new JButton("View");
        searchButton = new JButton("Search");

        // Setting equal preferred sizes for buttons
        saveButton.setPreferredSize(new Dimension(100, 30));
        viewButton.setPreferredSize(new Dimension(100, 60));
        searchButton.setPreferredSize(new Dimension(100, 30));

        saveButton.addActionListener(new SaveButtonListener());
        viewButton.addActionListener(new ViewButtonListener());
        searchButton.addActionListener(new SearchButtonListener());

        add(saveButton);
        add(viewButton);
        add(searchButton);

        // Text area for displaying stored clients
        textArea = new JTextArea(30, 30);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane);

        // Load clients from file
        loadClientsFromFile();

        setVisible(true);
    }

    private void saveClient() {
        String name = nameField.getText();
        String age = ageField.getText();
        String address = addressField.getText();
        String date = dateField.getText();

        // Validating the input
        if (name.isEmpty() || age.isEmpty() || address.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled out.");
            return;
        }

        // Now ask for height and weight
        String height = JOptionPane.showInputDialog(this, "Enter Height (in cm):");
        String weight = JOptionPane.showInputDialog(this, "Enter Weight (in kg):");

        // Validate height and weight
        if (height.isEmpty() || weight.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Height and Weight must be provided.");
            return;
        }

        try {
            // Convert height and weight to numbers
            double heightVal = Double.parseDouble(height);
            double weightVal = Double.parseDouble(weight);

            // Calculate BMI
            double bmi = calculateBMI(heightVal, weightVal);
            String bmiCategory = classifyBMI(bmi);

            // Store client details in a list format
            List<String> clientDetails = new ArrayList<>();
            clientDetails.add("Name: " + name);
            clientDetails.add("Age: " + age);
            clientDetails.add("Address: " + address);
            clientDetails.add("Date of Check-up: " + date);
            clientDetails.add("Height: " + height + " cm");
            clientDetails.add("Weight: " + weight + " kg");
            clientDetails.add("BMI: " + String.format("%.2f", bmi));
            clientDetails.add("Category: " + bmiCategory);

            // Add the client details list to the clients list
            clients.add(clientDetails);

            // Save the updated client list to a file after saving
            saveClientsToFile();

            // Clear the fields after saving
            nameField.setText("");
            ageField.setText("");
            addressField.setText("");
            dateField.setText("");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for height and weight.");
        }
    }

    private double calculateBMI(double height, double weight) {
        // Convert height to meters and calculate BMI
        double heightInMeters = height / 100;
        return weight / (heightInMeters * heightInMeters);
    }

    private String classifyBMI(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi >= 18.5 && bmi < 24.9) {
            return "Normal weight";
        } else if (bmi >= 25 && bmi < 29.9) {
            return "Overweight";
        } else {
            return "Obesity";
        }
    }

    private void saveClientsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("clients.dat"))) {
            oos.writeObject(clients);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadClientsFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("clients.dat"))) {
            clients = (List<List<String>>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void viewClients() {
        textArea.setText(""); // Clear text area before displaying
        for (List<String> client : clients) {
            for (String detail : client) {
                textArea.append(detail + "\n");
            }
            textArea.append("\n"); // Add space between clients
        }
        
        // After displaying the details, save the client data to file
        saveClientsToFile();
    }

    // Search button listener
    private class SearchButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String searchName = searchField.getText().toLowerCase().trim(); // Get search text

            if (searchName.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please enter a client name to search.");
                return;
            }

            textArea.setText(""); // Clear the text area before displaying search results
            boolean found = false;

            // Search through the client list by name (case-insensitive)
            for (List<String> client : clients) {
                String clientName = client.get(0).toLowerCase(); // Assuming the name is the first detail in the list
                if (clientName.contains(searchName)) {
                    // Display matching client details
                    for (String detail : client) {
                        textArea.append(detail + "\n");
                    }
                    textArea.append("\n");
                    found = true;
                }
            }

            if (!found) {
                textArea.append("No client found with the name: " + searchName + "\n");
            }

            // Clear the search field after the search is complete
            searchField.setText("");
        }
    }

    // Save button listener
    private class SaveButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            saveClient();
        }
    }

    // View button listener
    private class ViewButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            viewClients();
        }
    }

    public static void main(String[] args) {
        new ObesittyCheckerGUI();
    }
}
