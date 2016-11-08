package com.AlyssaMoore;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Scanner;

public class Ticket{

    private int priority;
    private String reporter; //Stores person or department who reported issue
    private String description;
    private Date dateReported;
    private Date resolvedDate;
    private String resolution;

    public static int staticTicketIDCounter = SupportTicketGUI.getCurrentID();

    //The ID for each ticket - instance variable. Each Ticket will have it's own ticketID variable
    public int ticketID;

    public Ticket(String desc, int p, String rep, Date date, Date rDate, String resolution) {
        this.description = desc;
        this.priority = p;
        this.reporter = rep;
        this.dateReported = date;
        this.resolvedDate = rDate;
        this.resolution = resolution;
        this.ticketID = SupportTicketGUI.getCurrentID();
        staticTicketIDCounter++;
    }

    protected String getDescription() {
        return description;
    }

    protected int getPriority() {
        return priority;
    }

    protected String getReporter() {
        return reporter;
    }

    protected Date getDateReported() {
        return dateReported;
    }

    protected Date getResolvedDate() {
        return resolvedDate;
    }

    protected String getResolution() {
        return resolution;
    }

    protected int getTicketID() {
        return ticketID;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public void setDate(Date rDate) {
        this.resolvedDate = rDate;
    }

    public String toString(){
        return("ID: " + this.ticketID + " Issues: " + this.description + " Priority: " + this.priority +
                " Reported by: " + this.reporter + " Reported on: " + this.dateReported + " Resolution date: " +
                this.resolvedDate + " Resolution: " + this.resolution);
    }
}