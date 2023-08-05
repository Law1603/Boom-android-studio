// Package declaration. This class is part of com.example.expensetracker package
package com.example.expensetracker;

// Public class Data
public class Data {
    // Data fields/attributes of the Data class
    String item, date, id, notes, itemday, itemweek, itemmonth;
    int amount, month, week;

    // Default constructor with no arguments
    public Data(){}

    // Parameterized constructor with arguments for all fields
    public Data(String item, String date, String id, String notes, String itemday, String itemweek, String itemmonth, int amount, int month, int week) {
        // Initialize instance variables with provided parameters
        this.item = item;
        this.date = date;
        this.id = id;
        this.notes = notes;
        this.itemday = itemday;
        this.itemweek = itemweek;
        this.itemmonth = itemmonth;
        this.amount = amount;
        this.month = month;
        this.week = week;
    }

    // Getters and setters for all instance variables

    // Getter for 'item'
    public String getItem() {
        return item;
    }

    // Setter for 'item'
    public void setItem(String item) {
        this.item = item;
    }

    // Getter for 'date'
    public String getDate() {
        return date;
    }

    // Setter for 'date'
    public void setDate(String date) {
        this.date = date;
    }

    // Getter for 'id'
    public String getId() {
        return id;
    }

    // Setter for 'id'
    public void setId(String id) {
        this.id = id;
    }

    // Getter for 'notes'
    public String getNotes() {
        return notes;
    }

    // Setter for 'notes'
    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Getter for 'itemday'
    public String getItemday() {
        return itemday;
    }

    // Setter for 'itemday'. The function name is probably a typo and should be setItemday instead of setItemdat.
    public void setItemdat(String itemday) {
        this.itemday = itemday;
    }

    // Getter for 'itemweek'
    public String getItemweek() {
        return itemweek;
    }

    // Setter for 'itemweek'
    public void setItemweek(String itemweek) {
        this.itemweek = itemweek;
    }

    // Getter for 'itemmonth'
    public String getItemmonth() {
        return itemmonth;
    }

    // Setter for 'itemmonth'
    public void setItemmonth(String itemmonth) {
        this.itemmonth = itemmonth;
    }

    // Getter for 'amount'
    public int getAmount() {
        return amount;
    }

    // Setter for 'amount'
    public void setAmount(int amount) {
        this.amount = amount;
    }

    // Getter for 'month'
    public int getMonth() {
        return month;
    }

    // Setter for 'month'
    public void setMonth(int month) {
        this.month = month;
    }

    // Getter for 'week'
    public int getWeek() {
        return week;
    }

    // Setter for 'week'
    public void setWeek(int week) {
        this.week = week;
    }
}
