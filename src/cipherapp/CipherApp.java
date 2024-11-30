package cipherapp;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CipherApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // ======= Home Page =======

        // Load the welcome image 
        ImageView welcomeImage = new ImageView(new Image("/img/XYNTRA.jpg"));
        welcomeImage.setFitWidth(400); // Set image width
        welcomeImage.setFitHeight(500); // Set image height
        welcomeImage.setPreserveRatio(false); // Disable aspect ratio preservation

        // Create a "Start" button for transitioning to the main application
        Button goToAppButton = new Button("Start");        
        goToAppButton.setStyle("-fx-background-color:#85FBFF; -fx-text-fill: #2E3O3C; -fx-font-size: 20px; -fx-font-weight: bold;");
        goToAppButton.setPrefWidth(80); // Set button width
        goToAppButton.setPrefHeight(30); // Set button height

        // Create a layout for the home page with the image and button
        StackPane welcomeLayout = new StackPane(welcomeImage, goToAppButton);
        welcomeLayout.setAlignment(goToAppButton, Pos.BOTTOM_CENTER); // Align button at the bottom center
        welcomeLayout.setMargin(goToAppButton, new Insets(90)); // Set margin for the button

        // Create a scene for the home page
        Scene welcomeScene = new Scene(welcomeLayout, 400, 500);

        // ======= Cipher Page =======

        // Input field for entering text to encrypt/decrypt
        Label inputLabel = new Label("Enter text:");
        inputLabel.setStyle("-fx-text-fill: #85FBFF; -fx-font-size: 16px;");
        TextField inputField = new TextField();

        // Label and dropdown for selecting the cipher type
        Label cipherLabel = new Label("Choose Cipher:");
        cipherLabel.setStyle("-fx-text-fill: #85FBFF; -fx-font-size: 16px;");
        ComboBox<String> cipherChoice = new ComboBox<>();
        cipherChoice.setPrefWidth(120); // Set dropdown width
        cipherChoice.setPrefHeight(20); // Set dropdown height
        cipherChoice.getItems().addAll("Caesar Cipher", "Atbash Cipher"); // Add cipher options
        cipherChoice.setValue("Caesar Cipher"); // Set default value to Caesar Cipher

        // Label and input for the shift value (Caesar Cipher only)
        Label shiftLabel = new Label("Enter Shift (for Caesar Cipher):");
        shiftLabel.setStyle("-fx-text-fill: #85FBFF; -fx-font-size: 16px;");
        TextField shiftField = new TextField();
        shiftField.setPromptText("Enter shift value (1-25)"); // Display placeholder text

        // Initially show the shift field since Caesar Cipher is the default
        shiftLabel.setVisible(true);
        shiftField.setVisible(true);

        // Toggle visibility of shift field based on cipher selection
        cipherChoice.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean isCaesarCipher = newValue.equals("Caesar Cipher");
            shiftLabel.setVisible(isCaesarCipher);
            shiftField.setVisible(isCaesarCipher);
        });

        // Toggle buttons for choosing encryption or decryption
        Label actionLabel = new Label("Choose Action:");
        actionLabel.setStyle("-fx-text-fill: #85FBFF; -fx-font-size: 16px;");
        ToggleGroup actionGroup = new ToggleGroup();
        RadioButton encryptOption = new RadioButton("Encrypt");
        encryptOption.setStyle("-fx-text-fill: #85FBFF; -fx-font-size: 16px;");
        encryptOption.setToggleGroup(actionGroup);
        encryptOption.setSelected(true); // Default to "Encrypt"
        RadioButton decryptOption = new RadioButton("Decrypt");
        decryptOption.setStyle("-fx-text-fill: #85FBFF; -fx-font-size: 16px;");
        decryptOption.setToggleGroup(actionGroup);
        
        // Label and field for displaying the result
        Label resultLabel = new Label("Result:");
        resultLabel.setStyle("-fx-text-fill: #85FBFF; -fx-font-size: 16px;");
        TextField resultField = new TextField();
        resultField.setEditable(false); // Make the result field read-only

        // Button for processing the input (encrypt/decrypt)
        Button actionButton = new Button("Process");
        actionButton.setPrefWidth(90); // Set button width
        actionButton.setPrefHeight(20); // Set button height
        actionButton.setOnAction(e -> {
            String text = inputField.getText();
            String selectedCipher = cipherChoice.getValue();
            boolean isEncrypt = encryptOption.isSelected();
            String result = "";

            // Handle Caesar Cipher processing
            if (selectedCipher.equals("Caesar Cipher")) {
                try {
                    int shift = Integer.parseInt(shiftField.getText());
                    if (shift < 1 || shift > 25) {
                        showAlert("Invalid Shift Value", "Please enter a valid number for shift.");
                    } else {
                        result = isEncrypt ? caesarEncrypt(text, shift) : caesarDecrypt(text, shift);
                    }
                } catch (NumberFormatException ex) {
                    showAlert("Invalid Shift Value", "Please enter a valid number for shift.");
                }
            } 
            // Handle Atbash Cipher processing
            else if (selectedCipher.equals("Atbash Cipher")) {
                result = Atbash(text);
            }
            resultField.setText(result); // Display the result
        });

        // Layout for the main application screen
        VBox appLayout = new VBox(10); // Vertical layout with spacing
        appLayout.setPadding(new Insets(20)); // Add padding around the layout
        appLayout.setStyle("-fx-background-color: #2E303C;"); // Set background color
        appLayout.getChildren().addAll(
                inputLabel, inputField,
                cipherLabel, cipherChoice,
                shiftLabel, shiftField,
                actionLabel, encryptOption, decryptOption,
                actionButton, resultLabel, resultField
        );

        // Create a scene for the cipher application
        Scene appScene = new Scene(appLayout, 400, 500);

        // Switch to the main application when the "Start" button is clicked
        goToAppButton.setOnAction(e -> primaryStage.setScene(appScene));

        // Set the initial scene and display the stage
        primaryStage.setScene(welcomeScene);
        primaryStage.setTitle("Cipher Application");
        primaryStage.show();
    }
    
    // =======Caesar Cipher Encrypt=======
    private String caesarEncrypt(String text, int shift) {
    StringBuilder encryptedMessage = new StringBuilder();
    text = text.toLowerCase(); // Convert the input text to lowercase for consistency
    for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i); // Get the current character
        if (Character.isLetter(c)) { 
            // Encrypt alphabetic characters with a circular shift
            c = (char) ((c - 'a' + shift + 26) % 26 + 'a');
        } else if (Character.isDigit(c)) {
            // Encrypt numeric characters with a circular shift
            c = (char) ((c - '0' + shift + 10) % 10 + '0');
        }
        encryptedMessage.append(c); // Append the encrypted character to the result
    }
    return encryptedMessage.toString(); // Return the final encrypted string
}  
    
    //=======Caesar Cipher Decrypt=======
    private String caesarDecrypt(String text, int shift) {
    StringBuilder decryptedMessage = new StringBuilder();
    text = text.toLowerCase(); // Convert the input text to lowercase for consistency
    for (int i = 0; i < text.length(); i++) {
        char c = text.charAt(i); // Get the current character
        if (Character.isLetter(c)) {
            // Decrypt alphabetic characters with a circular shift
            c = (char) ((c - 'a' - shift + 26) % 26 + 'a');
        } else if (Character.isDigit(c)) {
            // Decrypt numeric characters with a circular shift
            c = (char) ((c - '0' - shift + 10) % 10 + '0');
        }
        decryptedMessage.append(c); // Append the decrypted character to the result
    }
    return decryptedMessage.toString(); // Return the final decrypted string
}   
    
    
    // =======Atbash Cipher=======  
    private static String Atbash(String message) {
    String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // Standard alphabet
    String reverseAlpha = "ZYXWVUTSRQPONMLKJIHGFEDCBA"; // Reversed alphabet
    String numbers = "0123456789"; // Standard numeric sequence
    String reversedNumbers = "9876543210"; // Reversed numeric sequence
    message = message.toUpperCase(); // Convert the message to uppercase for consistency
    String encryptText = ""; // Initialize the encrypted message

    for (int i = 0; i < message.length(); i++) {
        char currentChar = message.charAt(i); // Get the current character
        if (currentChar == ' ') {
            // Preserve spaces in the message
            encryptText += " ";
        } else if (Character.isLetter(currentChar)) {
            // Substitute alphabetic characters using the reversed alphabet
            for (int j = 0; j < alpha.length(); j++) {
                if (currentChar == alpha.charAt(j)) {
                    encryptText += reverseAlpha.charAt(j);
                    break;
                }
            }
        } else if (Character.isDigit(currentChar)) {
            // Substitute numeric characters using the reversed numbers
            for (int j = 0; j < numbers.length(); j++) {
                if (currentChar == numbers.charAt(j)) {
                    encryptText += reversedNumbers.charAt(j);
                    break;
                }
            }
        } else {
            // Preserve special characters as-is
            encryptText += currentChar;
        }
    }
    return encryptText.toLowerCase(); // Return the encrypted text in lowercase
}

    
    // Utility method to show an alert dialog with a custom title and message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR); // Create an error alert
        alert.setTitle(title); // Set the alert title
        alert.setContentText(message); // Set the alert message

        // Add a confirmation button to the alert dialog
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(okButton);

        alert.showAndWait(); // Display the alert and wait for user response
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
