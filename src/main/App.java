package main;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.chart.*;
import javafx.stage.Stage;
import main.service.ExpenseService;

import java.io.*;
import java.security.MessageDigest;
import java.util.*;

public class App extends Application {

    private String currentUser = "";
    private Map<String, ExpenseService> userData = new HashMap<>();
    private final String USER_FILE = "users.txt";

    @Override
    public void start(Stage stage) {
        showLogin(stage);
    }

    // 🔐 LOGIN (UNCHANGED)
    private void showLogin(Stage stage) {

        TextField user = new TextField();
        user.setPromptText("Email / Username");
        user.setMaxWidth(250);

        PasswordField pass = new PasswordField();
        pass.setPromptText("Password");
        pass.setMaxWidth(250);

        String inputStyle =
                "-fx-background-color:#1e293b;" +
                "-fx-text-fill:white;" +
                "-fx-prompt-text-fill:#94a3b8;" +
                "-fx-background-radius:10;" +
                "-fx-padding:10;";

        user.setStyle(inputStyle);
        pass.setStyle(inputStyle);

        Label title = new Label("🔐 Login");
        title.setStyle("-fx-text-fill:white; -fx-font-size:26; -fx-font-weight:bold;");

        Button login = new Button("Login");
        Button signup = new Button("Sign Up");

        login.setStyle("-fx-background-color:#22c55e; -fx-text-fill:white;");
        signup.setStyle("-fx-background-color:#3b82f6; -fx-text-fill:white;");

        Hyperlink createAccount = new Hyperlink("Create New Account");
        createAccount.setStyle("-fx-text-fill:#38bdf8;");

        Label msg = new Label("");
        msg.setStyle("-fx-text-fill:red;");

        login.setOnAction(e -> {
            if (validateUser(user.getText(), pass.getText())) {
                currentUser = user.getText();
                userData.putIfAbsent(currentUser, new ExpenseService());
                showDashboard(stage);
            } else msg.setText("❌ Invalid login");
        });

        signup.setOnAction(e -> showSignup(stage));
        createAccount.setOnAction(e -> showSignup(stage));

        VBox root = new VBox(15, title, user, pass, login, signup, createAccount, msg);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(80, 20, 20, 20));
        root.setStyle("-fx-background-color: linear-gradient(#0f172a,#1e293b);");

        stage.setScene(new Scene(root, 400, 500));
        stage.show();
    }

    // 📝 SIGNUP (UNCHANGED)
    private void showSignup(Stage stage) {

        TextField user = new TextField();
        user.setPromptText("Email / Username");
        user.setMaxWidth(250);

        PasswordField pass = new PasswordField();
        pass.setPromptText("Password");
        pass.setMaxWidth(250);

        String inputStyle =
                "-fx-background-color:#1e293b;" +
                "-fx-text-fill:white;" +
                "-fx-prompt-text-fill:#94a3b8;" +
                "-fx-background-radius:10;" +
                "-fx-padding:10;";

        user.setStyle(inputStyle);
        pass.setStyle(inputStyle);

        Label title = new Label("📝 Create Account");
        title.setStyle("-fx-text-fill:white; -fx-font-size:26; -fx-font-weight:bold;");

        Button create = new Button("Create");
        Button back = new Button("Back");

        create.setStyle("-fx-background-color:#22c55e; -fx-text-fill:white;");
        back.setStyle("-fx-background-color:#3b82f6; -fx-text-fill:white;");

        Label msg = new Label("");
        msg.setStyle("-fx-text-fill:red;");

        create.setOnAction(e -> {
            if (saveUser(user.getText(), pass.getText())) {
                msg.setText("✅ Account Created");
            } else msg.setText("❌ User exists");
        });

        back.setOnAction(e -> showLogin(stage));

        VBox root = new VBox(15, title, user, pass, create, back, msg);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60, 20, 20, 20));
        root.setStyle("-fx-background-color: linear-gradient(#0f172a,#1e293b);");

        stage.setScene(new Scene(root, 400, 500));
    }

    // 💸 DASHBOARD (BAR CHART ADDED)
    private void showDashboard(Stage stage) {

        ExpenseService service = userData.get(currentUser);

        // 🔥 BAR CHART (ADDED CORRECTLY)
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        barChart.getData().add(series);
        PieChart pieChart = new PieChart();
pieChart.setTitle("Category Distribution");
pieChart.setLegendVisible(true);
pieChart.setLabelsVisible(true);

pieChart.setStyle(
    "-fx-background-color: transparent;" +
    "-fx-text-fill: white;"
);

        barChart.setPrefWidth(350);
        barChart.setPrefHeight(300);
        barChart.setStyle("-fx-background-color: transparent;");

        Label title = new Label("💸 Expense Splitter");
        title.setStyle("-fx-text-fill:white; -fx-font-size:22; -fx-font-weight:bold;");

        TextField paidBy = new TextField();
        paidBy.setPromptText("Who paid?");
        paidBy.setMaxWidth(250);

        TextField amount = new TextField();
        amount.setPromptText("Amount");
        amount.setMaxWidth(250);

        TextField userCount = new TextField();
        userCount.setPromptText("Number of Users");
        userCount.setMaxWidth(250);

        TextField users = new TextField();
        users.setPromptText("Users (A,B,C)");
        users.setMaxWidth(250);

        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Food","Trip","Cab","Tickets","Spotify","Groceries","Other");
        categoryBox.setPromptText("Select Category");
        categoryBox.setMaxWidth(250);

        String inputStyle =
                "-fx-background-color:#1e293b;" +
                "-fx-text-fill:white;" +
                "-fx-prompt-text-fill:#94a3b8;" +
                "-fx-background-radius:8;" +
                "-fx-padding:8;";

        paidBy.setStyle(inputStyle);
        amount.setStyle(inputStyle);
        users.setStyle(inputStyle);
        userCount.setStyle(inputStyle);
        categoryBox.setStyle(inputStyle);

        TextField customCategory = new TextField();
        customCategory.setPromptText("Custom Category");
        customCategory.setVisible(false);

        categoryBox.setOnAction(e ->
                customCategory.setVisible("Other".equals(categoryBox.getValue()))
        );

        Button add = new Button("Add Expense");
        Button logout = new Button("Logout");

        ListView<String> list = new ListView<>();
        Label total = new Label("Total: ₹0");
        total.setStyle("-fx-text-fill:white;");
add.setOnAction(e -> {
    try {
        // 🔥 VALIDATE INPUTS FIRST
        if (paidBy.getText().trim().isEmpty() ||
            amount.getText().trim().isEmpty()) {
            list.getItems().setAll("❌ Enter all required fields");
            return;
        }

        String p = paidBy.getText().trim();
        double amt = Double.parseDouble(amount.getText().trim());

        // 🔥 USERS
        List<String> u = new ArrayList<>();
        if (!userCount.getText().trim().isEmpty()) {
            int n = Integer.parseInt(userCount.getText().trim());
            for (int i = 1; i <= n; i++) u.add("User" + i);
        } else if (!users.getText().trim().isEmpty()) {
            for (String s : users.getText().split(",")) {
                if (!s.trim().isEmpty()) u.add(s.trim());
            }
        } else {
            list.getItems().setAll("❌ Enter users");
            return;
        }

        // 🔥 CATEGORY SAFE
        String category = null;

        if (categoryBox.getValue() != null) {
            if ("Other".equals(categoryBox.getValue())) {
                category = customCategory.getText().trim();
            } else {
                category = categoryBox.getValue();
            }
        }

        if (category == null || category.isEmpty()) {
            list.getItems().setAll("❌ Select category");
            return;
        }

        // 🔥 MULTI CATEGORY
        List<String> categoryList = new ArrayList<>();
        for (String c : category.split(",")) {
            if (!c.trim().isEmpty()) categoryList.add(c.trim());
        }

        if (categoryList.isEmpty()) {
            list.getItems().setAll("❌ Invalid category");
            return;
        }

        // 🔥 ADD EXPENSE (NO CLEAR)
        for (String c : categoryList) {
            service.addExpense(p, amt / categoryList.size(), u, c);
        }

        // 🔥 UPDATE UI
        list.getItems().setAll(service.simplifyDebts());
        total.setText("Total: ₹" + service.getTotalExpense());

        // 🔥 UPDATE BAR CHART (CATEGORY)
       barChart.getData().clear();  // 🔥 clear full chart

Map<String, Double> map = service.getCategoryTotals();

for (String k : map.keySet()) {
    XYChart.Series<String, Number> s = new XYChart.Series<>();
    s.setName(k);  // category name

    s.getData().add(new XYChart.Data<>(k, map.get(k)));

    barChart.getData().add(s); // 🔥 add each separately
}
// 🔥 PIE CHART UPDATE
ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

Map<String, Double> map2 = service.getCategoryTotals();

for (String k : map2.keySet()) {
    pieData.add(new PieChart.Data(k, map2.get(k)));
}

pieChart.setData(pieData);

// 🎨 premium colors
String[] colors = {"#6366f1", "#22c55e", "#f59e0b", "#ef4444", "#0ea5e9"};

int i = 0;
for (PieChart.Data data : pieChart.getData()) {
    data.getNode().setStyle("-fx-pie-color: " + colors[i % colors.length] + ";");
    i++;
}

    } catch (NumberFormatException ex) {
        list.getItems().setAll("❌ Amount must be number");
    } catch (Exception ex) {
        list.getItems().setAll("❌ Error occurred");
        ex.printStackTrace(); // 🔥 THIS WILL SHOW REAL ERROR
    }
});
        logout.setOnAction(e -> showLogin(stage));

VBox left = new VBox(15,
        title, paidBy, amount, userCount, users,
        categoryBox, customCategory, add, total, list, logout
);
left.setAlignment(Pos.CENTER);

// ✅ ADD THIS (YOU WERE MISSING THIS)
VBox right = new VBox(20, barChart, pieChart);
right.setAlignment(Pos.TOP_CENTER);

// ✅ NOW THIS WILL WORK
left.setPrefWidth(450);
right.setPrefWidth(450);

HBox root = new HBox(left, right);
HBox.setHgrow(left, Priority.ALWAYS);
HBox.setHgrow(right, Priority.ALWAYS);

root.setStyle("-fx-background-color: linear-gradient(#0f172a,#1e293b);");

        stage.setScene(new Scene(root, 900, 650));
    }

    // 🔐 HASH + USER SYSTEM (UNCHANGED)

    private String hash(String p) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] b = md.digest(p.getBytes());
            StringBuilder s = new StringBuilder();
            for (byte x : b) s.append(String.format("%02x", x));
            return s.toString();
        } catch (Exception e) { return p; }
    }

    private boolean saveUser(String u, String p) {
        try {
            File file = new File(USER_FILE);
            if (!file.exists()) file.createNewFile();

            BufferedReader r = new BufferedReader(new FileReader(file));
            String line;
            while ((line = r.readLine()) != null) {
                if (line.split(",")[0].equals(u)) return false;
            }
            r.close();

            BufferedWriter w = new BufferedWriter(new FileWriter(file, true));
            w.write(u + "," + hash(p));
            w.newLine();
            w.close();

            return true;
        } catch (Exception e) { return false; }
    }

    private boolean validateUser(String u, String p) {
        try {
            BufferedReader r = new BufferedReader(new FileReader(USER_FILE));
            String line;
            while ((line = r.readLine()) != null) {
                String[] d = line.split(",");
                if (d[0].equals(u) && d[1].equals(hash(p))) return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    public static void main(String[] args) {
        launch();
    }
}