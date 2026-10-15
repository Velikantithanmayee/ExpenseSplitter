package main.service;

import java.util.*;

public class ExpenseService {

    static class Expense {
        String paidBy;
        double amount;
        List<String> users;
        String category;

        Expense(String p, double a, List<String> u, String c) {
            paidBy = p;
            amount = a;
            users = u;
            category = c;
        }
    }

private List<Expense> expenses = new ArrayList<>();

// 🔥 ADD THIS EXACTLY HERE
public void clearExpenses() {
    expenses.clear();
}

public void addExpense(String paidBy, double amount, List<String> users, String category) {
        expenses.add(new Expense(paidBy, amount, users, category));
    }

    public double getTotalExpense() {
        return expenses.stream().mapToDouble(e -> e.amount).sum();
    }

    public Map<String, Double> getUserTotals() {
        Map<String, Double> map = new HashMap<>();
        for (Expense e : expenses) {
            map.put(e.paidBy, map.getOrDefault(e.paidBy, 0.0) + e.amount);
        }
        return map;
    }

    public Map<String, Double> getCategoryTotals() {
        Map<String, Double> map = new HashMap<>();
        for (Expense e : expenses) {
            map.put(e.category, map.getOrDefault(e.category, 0.0) + e.amount);
        }
        return map;
    }

    public List<String> simplifyDebts() {
        Map<String, Double> balance = new HashMap<>();

        for (Expense e : expenses) {
            double split = e.amount / e.users.size();

            for (String u : e.users) {
                balance.put(u, balance.getOrDefault(u, 0.0) - split);
            }

            balance.put(e.paidBy, balance.getOrDefault(e.paidBy, 0.0) + e.amount);
        }

        List<String> result = new ArrayList<>();
        for (String u : balance.keySet()) {
            result.add(u + " : ₹" + String.format("%.2f", balance.get(u)));
        }

        return result;
    }
}