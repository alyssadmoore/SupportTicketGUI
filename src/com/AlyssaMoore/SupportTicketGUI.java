package com.AlyssaMoore;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Scanner;

public class SupportTicketGUI extends JFrame {
    private JPanel rootPanel;
    private JButton addTicketButton;
    private JButton showAllTicketsButton;
    private JButton quitButton;
    private JButton searchButton;
    private JButton resolveTicketButton;
    private JComboBox priorityComboBox;
    private JTextField searchField;
    private JList resultsList;
    private DefaultListModel<Ticket> listModel;
    private JTextField resolutionTextField;
    private JTextField reporterTextField;
    private JTextField issueTextField;
    private JComboBox idNumberComboBox;
    private JLabel resolutionLabel;
    private JLabel searchLabel;
    private JButton showAllResolvedButton;

    BufferedWriter openWriter = new BufferedWriter(new FileWriter("open_tickets.txt", true));
    static LinkedList<Ticket> ticketQueue = new LinkedList<>();
    LinkedList<Ticket> resolvedTickets = new LinkedList<>();
    Scanner inputFile = new Scanner(new File("open_tickets.txt"));
    String dateFormat = "EEE MMM dd hh:mm:ss z yyyy";
    SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

    public SupportTicketGUI() throws Exception {
        super("Ticket Manager");

        // Creating ticket objects from open_tickets.txt
        while (inputFile.hasNext()) {
            Ticket openTicket = new Ticket(inputFile.nextLine(), Integer.parseInt(inputFile.nextLine()),
                            inputFile.nextLine(), formatter.parse(inputFile.nextLine()), null, null);
            ticketQueue.add(openTicket);
        }

        setContentPane(rootPanel);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        listModel = new DefaultListModel<Ticket>();
        resultsList.setModel(listModel);

        // When the program is opened, the JList shows open tickets
        for (Ticket t : ticketQueue) {
            listModel.addElement(t);
            idNumberComboBox.addItem(t.getTicketID());
        }

        addListeners();
        pack();
        setSize(800, 350);
        setVisible(true);

        // Priorities must only be 1-5
        priorityComboBox.addItem(1);
        priorityComboBox.addItem(2);
        priorityComboBox.addItem(3);
        priorityComboBox.addItem(4);
        priorityComboBox.addItem(5);

    }

    // Finds the highest ticket ID from ticketQueue, for use with Ticket class
    public static int getCurrentID() {
        int highestID = 1;
        for (Ticket t : ticketQueue) {
            if (t.getTicketID() > highestID) {
                highestID = t.getTicketID();
            }
        }
        // We need to add 1 for the next ticket ID
        highestID += 1;
        return highestID;
    }

    public void addListeners(){

        // Adds new tickets to ticketQueue, adds the new ticket to the JList, and adds the ID# to idNumberComboBox
        addTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newReporter = reporterTextField.getText();
                String newIssue = issueTextField.getText();
                int newPriority = (Integer)priorityComboBox.getSelectedItem();
                Date newDate = new Date();
                Ticket t = new Ticket(newIssue, newPriority, newReporter, newDate, null, null);
                addTicketInPriorityOrder(ticketQueue, t);
                listModel.addElement(t);
                idNumberComboBox.addItem(t.getTicketID());
            }
        });

        // Searches 'Issue' of tickets (ignoring case), shows results in JList
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listModel.clear();
                String searchString = searchField.getText();
                for (Ticket t : ticketQueue) {
                    if (t.getDescription().equalsIgnoreCase(searchString)) {
                        listModel.addElement(t);
                    }
                }
            }
        });

        // Shows all open tickets in the JList
        showAllTicketsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listModel.clear();
                for (Ticket t : ticketQueue) {
                    listModel.addElement(t);
                }
            }
        });

        // Shows all resolved tickets in the JList
        showAllResolvedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listModel.clear();
                for (Ticket t : resolvedTickets) {
                    listModel.addElement(t);
                }
            }
        });

        // Finds the ticket in ticketQueue with the indicated ticketID, adds to list of resolved tickets,
        // and removes it from the JList, ticketQueue and idNumberComboBox
        resolveTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int idNumber = (Integer)idNumberComboBox.getSelectedItem();
                String resolution = resolutionTextField.getText();
                Date today = new Date();
                for (Ticket t : ticketQueue) {
                    if (t.getTicketID() == idNumber) {
                        t.setResolution(resolution);
                        t.setDate(today);
                        resolvedTickets.add(t);
                        ticketQueue.remove(t);
                        listModel.removeElement(t);
                        idNumberComboBox.removeItem(t.ticketID);
                        break;
                    }
                }
            }
        });

        // Updates open_tickets.txt and creates resolved_tickets, if this cannot be completed
        // the application tells the user there was a problem and does NOT close the application
        // (The user can still force quit with the X button if the error cannot be avoided)
        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {// This lets me overwrite what's already in the file so there aren't repeats
                    BufferedWriter writer = new BufferedWriter(new FileWriter("open_tickets.txt"));
                    // Adding ticket information to file
                    for (Ticket t : ticketQueue) {
                        writer.write(t.getDescription() + "\r\n" + t.getPriority() + "\r\n" +
                                t.getReporter() + "\r\n" + t.getDateReported() + "\r\n");
                    }
                    // Creating file with resolved tickets
                    String date = new SimpleDateFormat("MMM_dd_yyyy").format(new Date());
                    String closedTickets = "Resolved_tickets_as_of_" + date + ".txt";
                    BufferedWriter closedWriter = new BufferedWriter(new FileWriter(closedTickets));
                    for (Ticket t : resolvedTickets) {
                        closedWriter.write("Problem: " + t.getDescription() + "\r\nPriority: " + t.getPriority() +
                                "\r\nReporter: " + t.getReporter() + "\r\nDate reported: " + t.getDateReported() +
                                "\r\nResolved Date: " + t.getResolvedDate() + "\r\nResolution: " + t.getResolution() + "\r\n");
                    }
                    closedWriter.close();
                    openWriter.close();
                    writer.close();
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(SupportTicketGUI.this, "There was an error updating open_tickets.txt " +
                            "or creating today's resolved tickets file. Please try again.");
                }
                System.exit(0);
            }
        });
    }

    // Unchanged from original code by you (Clara)
    public static void addTicketInPriorityOrder(LinkedList<Ticket> tickets, Ticket newTicket){

        //Logic: assume the list is either empty or sorted

        if (tickets.size() == 0 ) {//Special case - if list is empty, add ticket and return
            tickets.add(newTicket);
            return;
        }
        //Tickets with the HIGHEST priority number go at the front of the list. (e.g. 5=server on fire)
        //Tickets with the LOWEST value of their priority number (so the lowest priority) go at the end
        int newTicketPriority = newTicket.getPriority();
        for (int x = 0; x < tickets.size() ; x++) {    //use a regular for loop so we know which element we are looking
            // at if newTicket is higher or equal priority than the this element, add it in front of this one, and return
            if (newTicketPriority >= tickets.get(x).getPriority()) {
                tickets.add(x, newTicket);
                return;
            }
        }
        //Will only get here if the ticket is not added in the loop
        //If that happens, it must be lower priority than all other tickets. So, add to the end.
        tickets.addLast(newTicket);
    }
}