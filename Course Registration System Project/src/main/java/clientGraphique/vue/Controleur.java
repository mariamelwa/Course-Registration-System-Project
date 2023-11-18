package clientGraphique.vue;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.text.Text;
import server.models.Course;
import server.models.RegistrationForm;

/**
 * Controleur du client graphique
 */
public class Controleur {

    private final static int PORT = 1337;
    private final static String REGISTER_COMMAND = "INSCRIRE";
    private final static String LOAD_COMMAND = "CHARGER";

    private ArrayList<Course> courses = new ArrayList<Course>();
    private String courseCode;
    private RegistrationForm registrationForm;
    private String session;

    private Alert alert;

    private Model model;

    private ObservableList<Course> coursesVue;

    Text text;

    /**
     * Constructeur
     * 
     * @param coursesVue liste des cours de l'interface graphique
     * @param alert      message de reussite d'inscription ou non
     */
    public Controleur(ObservableList<Course> coursesVue, Alert alert) {
        session = "Automne";
        this.coursesVue = coursesVue;
        this.alert = alert;
    }

    /**
     * Methode qui demande au client la liste de cours pour une session choisie
     */
    public void chooseCourse() {

        sendRequest(LOAD_COMMAND);
        this.updateCoursesVue();
        coursesVue.setAll(courses);
        this.courseCode = "";

    }

    private void updateCoursesVue() {
        this.coursesVue.setAll(courses);
    }

    /**
     * Methode qui demande que serveur de faire l'inscription
     * 
     * @param firstName premier nom de l'etudiant
     * @param lastName  nom de l'etudiant
     * @param email     email de l'etudiant
     * @param matricule matricule de l'etudiant
     */
    public void doInscription(String firstName, String lastName, String email, String matricule) {

        registrationForm = new RegistrationForm(firstName, lastName, email, matricule, null);

        for (int i = 0; i < courses.size(); i++) {

            if (courses.get(i).getCode().equals(courseCode)) {
                registrationForm.setCourse(courses.get(i));
                break;
            }
        }
        sendRequest(REGISTER_COMMAND);
    }

    private void sendRequest(String request) {

        try {
            model = new Model(PORT);//

            if (request.equals(LOAD_COMMAND)) {
                courses = model.charger(session, courses);
            } else {
                Boolean succes;
                succes = model.inscription(registrationForm, session);
                if (succes) {
                    alert.setContentText("Félicitations! Inscription réussie de " + registrationForm.getPrenom() +
                            "au cours " + registrationForm.getCourse().getCode());
                } else {
                    alert.setContentText("Vous devez choisir un cours.");

                }
                alert.showAndWait();
            }

        } catch (Exception e) {
            System.out.println("erreur : création du modele");
            e.printStackTrace();
        }
    }

    /**
     * actualise la session choisie par l'etudiant
     * 
     * @param session
     */
    public void setSession(String session) {
        this.session = session;
    }

    /**
     * actualise le cours choisi par l'etudiant
     * 
     * @param courseCode
     */
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

}