package clientGraphique.vue;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import server.models.Course;
import server.models.RegistrationForm;

/**
 * Model pour les client graphique
 */
public class Model {

    private final static String REGISTER_COMMAND = "INSCRIRE";
    private final static String LOAD_COMMAND = "CHARGER";
    private final Socket clientSocket;

    /**
     * Constructeur
     * 
     * @param port numero de port pour la connextion avec le serveur
     * @throws UnknownHostException
     * @throws IOException
     */
    public Model(int port) throws UnknownHostException, IOException {
        clientSocket = new Socket("127.0.0.1", port);
    }

    /**
     * Demande au serveur la liste des cours pour une session choisie
     * 
     * @param session session que l'etudiant souhaite s'inscrire
     * @param courses liste de cours de la session choisie
     * @return la liste de cours
     * @throws IOException
     */
    public ArrayList<Course> charger(String session, ArrayList<Course> courses) throws IOException {

        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        String arg = LOAD_COMMAND + " " + session;
        objectOutputStream.writeObject(arg);

        try {

            InputStream inputStream = clientSocket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            courses = (ArrayList<Course>) objectInputStream.readObject();

        } catch (IOException e) {
            System.out.println("Erreur à l'ouverture du fichier ou objet");
        } catch (ClassNotFoundException ex) {
            System.out.println("La classe lue n'existe pas dans le programme");
            ex.printStackTrace();
        } catch (ClassCastException ex) {
            System.out.println("Problème dans le cast");
            ex.printStackTrace();
        }
        return courses;

    }

    /**
     * Demande au serveur d'inscrire un etudiant dans un cours
     * 
     * @param registrationForm formulaire qui contient les information pour
     *                         l'iscription
     * @param session          session que l'etudiant souhaite s'inscrire
     * @return true si l'inscription est reussite, false sinon
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public Boolean inscription(RegistrationForm registrationForm, String session)
            throws IOException, ClassNotFoundException {

        OutputStream outputStream = clientSocket.getOutputStream();

        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        objectOutputStream.writeObject(REGISTER_COMMAND);

        objectOutputStream.writeObject(session);

        objectOutputStream.writeObject(registrationForm);

        InputStream inputStream = clientSocket.getInputStream();

        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        Boolean succes = (Boolean) objectInputStream.readObject();

        return succes;

    }

}