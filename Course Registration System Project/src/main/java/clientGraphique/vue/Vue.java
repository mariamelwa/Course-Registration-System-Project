package clientGraphique.vue;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import server.models.Course;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/*
 * Dans cette classe nous definissons les éléments graphiques de notre
 * application.
 * 
 * Elle sert également comme le point d'entrée de l'application.
 * Pour l'executer, on peut faire la commande suivante sur le terminal.
 * >> mvn javafx:run
 */
public class Vue extends Application {

        private static Controleur controleur;

        private TableView<Course> table;

        /**
         * Methode qui initialise l'application.
         */
        @Override
        public void start(Stage primaryStage) {

                BorderPane root = new BorderPane();

                // Cree le panel gauche avec la liste des cours.
                VBox coursePane = new VBox();

                TableColumn<Course, String> codeColumn = new TableColumn<>("Code");
                codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));

                TableColumn<Course, String> courseColumn = new TableColumn<>("Cours");
                courseColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

                table = new TableView<>();

                final Label label = new Label("Liste des cours");
                label.setFont(new Font("Arial", 20));
                label.setCenterShape(false);

                table.setEditable(true);

                table.getColumns().addAll(codeColumn, courseColumn);

                ObservableList<Course> coursesVue = FXCollections.observableArrayList();

                table.setItems(coursesVue);

                table.setOnMouseClicked(event -> controleur
                                .setCourseCode(table.getSelectionModel().getSelectedItem().getCode()));

                coursePane.getChildren().addAll(label, table);

                HBox hBoxSessions = new HBox();

                String[] sessions = { "Automne", "Hiver", "Ete" };
                ChoiceBox<String> optionSessions = new ChoiceBox<String>();
                optionSessions.setPadding(new Insets(10));

                optionSessions.getItems().setAll(sessions);
                optionSessions.setOnAction(event -> controleur.setSession(optionSessions.getValue()));
                hBoxSessions.getChildren().addAll(optionSessions);

                // Bouton qui envoie la session choisie
                Button loadButton = new Button("Charger");
                loadButton.setPadding(new Insets(10));

                loadButton.setOnAction(event -> controleur.chooseCourse());
                hBoxSessions.getChildren().addAll(loadButton);

                hBoxSessions.setPadding(new Insets(10));
                coursePane.getChildren().addAll(hBoxSessions);

                coursePane.setPadding(new Insets(10));

                // Cree le formulaire d'inscription a droite
                GridPane formPane = new GridPane();
                formPane.setHgap(6);
                formPane.setVgap(6);

                Text formTitle = new Text("Formulaire d'inscription");
                formTitle.setFont(Font.font("serif", 25));
                formPane.addRow(0, formTitle);

                Label firstNameLabel = new Label("Prénom:");
                TextField firstName = new TextField();
                formPane.addRow(1, firstNameLabel, firstName);

                Label lastNameLabel = new Label("Nom:");
                TextField lastName = new TextField();
                formPane.addRow(2, lastNameLabel, lastName);

                Label emailLabel = new Label("Email:");
                TextField email = new TextField();
                formPane.addRow(3, emailLabel, email);

                Label matriculeLabel = new Label("Matricule:");
                TextField matricule = new TextField();
                formPane.addRow(4, matriculeLabel, matricule);

                Button submitButton = new Button("envoyer");

                submitButton.setOnAction(e -> {
                        controleur.doInscription(firstName.getText(), lastName.getText(), email.getText(),
                                        matricule.getText());
                });
                formPane.addRow(5, submitButton);

                root.setLeft(coursePane);
                root.setRight(formPane);

                Alert alert = new Alert(AlertType.CONFIRMATION, null, ButtonType.OK);
                //
                controleur = new Controleur(coursesVue, alert);

                primaryStage.setScene(new Scene(root, 800, 400));
                primaryStage.setTitle("Inscription UdeM");
                primaryStage.show();

        }

}