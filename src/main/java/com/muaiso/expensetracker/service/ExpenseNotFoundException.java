package com.muaiso.expensetracker.service;

public class ExpenseNotFoundException extends RuntimeException {

    public ExpenseNotFoundException(Long id) {
        super("Ausgabe nicht gefunden: " + id);
    }
}
