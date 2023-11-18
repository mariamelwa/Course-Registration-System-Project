package client;

import java.util.ArrayList;

import server.models.Course;
import server.models.RegistrationForm;

/**
 * classe qui fait le lien entre la classe Client et la classe
 * ClientInterfaceSimple
 */
public class ClientControleurSimple {

    private final static int PORT = 1337;

    private final static String REGISTER_COMMAND = "INSCRIRE";
    private final static String LOAD_COMMAND = "CHARGER";

    private ArrayList<Course> courses = new ArrayList<Course>();

    private String session;

    private Client client;

    private RegistrationForm registrationForm;

    ClientInterfaceSimple interfaceSimple = new ClientInterfaceSimple();

    /**
     * Constructeur
     */
    public ClientControleurSimple() {

        chooseCourse();

        registrationForm = interfaceSimple.enterInformation(courses);

        sendRequest(REGISTER_COMMAND);
    }

    private void chooseCourse() {

        while (true) {
            session = interfaceSimple.initialisation();
            sendRequest(LOAD_COMMAND);

            client.displayCourses(courses);
            int choix = interfaceSimple.chooseCoursesInscription();
            if (choix == 2) {
                break;
            }
        }
    }

    private void sendRequest(String request) {

        try {
            client = new Client(PORT);//

            if (request.equals(LOAD_COMMAND)) {
                courses = client.charger(session, courses);
            } else {
                Boolean succes;
                succes = client.inscription(registrationForm, session);

                interfaceSimple.displayLastMessage(succes, registrationForm);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
