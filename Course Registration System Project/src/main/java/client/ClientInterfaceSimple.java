package client;

import java.util.ArrayList;
import java.util.Scanner;

import server.models.Course;
import server.models.RegistrationForm;

/**
 * classe pour l'interface simple sur la ligne de console
 */
public class ClientInterfaceSimple {

    private Scanner scanner = new Scanner(System.in);

    private String session;

    public ClientInterfaceSimple() {
        System.out.println("***Bienvenue au portail d'inscription de cours de l'UdeM***");

    }

    /**
     * Imprime les sessions pour que l'utilisateur puisse choisir
     * 
     * @return la session choisie
     */
    public String initialisation() {

        displaySessions();

        return session;
    }

    private void displaySessions() {
        System.out.println("Veuillez choisir la session pour laquelle vous voulez consulter la liste des cours:");
        System.out.println("1. Automne");
        System.out.println("2. Hiver");
        System.out.println("3. Ete");
        System.out.print("Choix: ");

        int choix = scanner.nextInt();

        switch (choix) {
            case 1:
                session = "Automne";
                break;
            case 2:
                session = "Hiver";
                break;
            default:
                session = "Ete";
                break;
        }

        System.out.println("Les cours offerts pendant la session d'" + session + " sont");

    }

    /**
     * Sert a que l'utilisateur puisse faire l'action de choisir de s'inscrire ou de
     * consulter la liste des cours pour une autre session
     * 
     * @return le choix que l'utilisateur fait
     */
    public Integer chooseCoursesInscription() {
        System.out.println("Choix:");

        System.out.println("1. Consulter les cours offerts pour une autre session");
        System.out.println("2. Inscription à un cours");

        System.out.print("Choix: ");

        scanner = new Scanner(System.in);
        return scanner.nextInt();

    }

    /**
     * Prends les informations de l'utilisateur necessaires pour l'inscription
     * 
     * @param courses prends la liste des cours de la session choisie
     * @return un formulaire qui contient les informations
     */

    public RegistrationForm enterInformation(ArrayList<Course> courses) {

        scanner = new Scanner(System.in);

        System.out.print("Veuillez saisir votre prénom: ");
        String firstName = scanner.nextLine();

        System.out.print("Veuillez saisir votre nom: ");
        String lastName = scanner.nextLine();

        System.out.print("Veuillez saisir votre email: ");
        String email = scanner.nextLine();

        System.out.print("Veuillez saisir votre matricule: ");
        String matriculation = scanner.nextLine();

        System.out.print("Veuillez saisir le code du cours: ");
        String courseCode = scanner.nextLine();

        RegistrationForm registrationForm = new RegistrationForm(firstName, lastName, email, matriculation, null);

        Course course;
        for (int i = 0; i < courses.size(); i++) {

            course = courses.get(i);
            if (course.getCode().equals(courseCode)) {
                registrationForm.setCourse(course);
                break;
            }
        }
        return registrationForm;
    }

    /**
     * Imprime le message a l'utilisateur si l'inscription est reussite ou pas
     * 
     * @param succes           true si l'inscription a ete effectue avec succes,
     *                         false sinon
     * @param registrationForm les informations du cours que l'etudiant veut
     *                         s'inscrire
     */
    public void displayLastMessage(Boolean succes, RegistrationForm registrationForm) {
        if (succes) {
            System.out.println("Félicitations! Inscription réussie de " + registrationForm.getPrenom() +
                    "au cours " + registrationForm.getCourse().getCode());
        } else {
            System.out.println(
                    "Le code du cours n'est pas valide ou ce cours n'est pas disponible pour la session choisie");
        }

    }
}
