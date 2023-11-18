package server;

import javafx.util.Pair;
import server.models.Course;
import server.models.RegistrationForm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Cette classe represente le serveur qui ecoute les connexions des clients et
 * gere les requetes des clients.
 * Elle implemente l'interface 'EventHandler' qui definit une methode 'handle'
 * qui est appelee par le serveur
 * pour traiter les requetes des clients.
 * Le serveur ecoute les connexions des clients sur un port specifie en argument
 * du constructeur.
 * Le serveur lit les requetes des clients en utilisant un flux d'entree et les
 * traite en utilisant la methode 'handleEvents'.
 * Le serveur envoie les reponses aux clients en utilisant un flux de sortie.
 * Le serveur gere les exceptions si une erreur se produit lors de la lecture ou
 * de l'ecriture dans les flux.
 */
public class Server {
    /**
     * Cette interface definit une methode 'handle' qui est appelee par le serveur
     * pour traiter les requetes des clients.
     * La methode 'handle' prend deux arguments: la commande et les arguments de la
     * commande.
     */

    /** La commande pour inscrire un etudiant. */
    public final static String REGISTER_COMMAND = "INSCRIRE";

    /** La commande pour charger les cours d'une session. */
    public final static String LOAD_COMMAND = "CHARGER";

    private final ServerSocket server;
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private final ArrayList<EventHandler> handlers;

    /**
     * Construire un serveur qui ecoute les connexions des clients sur le port
     * specifie en argument.
     * 
     * @param port le port sur lequel le serveur ecoute les connexions des clients.
     * @throws IOException si une erreur se produit lors de la creation du serveur.
     */
    public Server(int port) throws IOException {
        this.server = new ServerSocket(port, 1);
        this.handlers = new ArrayList<EventHandler>();
        this.addEventHandler(this::handleEvents); // c'est quoi :: ?
    }

    /**
     * Ajouter un gestionnaire d'evenements (EventHandler) pour traiter les requetes
     * des clients.
     * 
     * @param h le gestionnaire d'evenements (EventHandler) a ajouter.
     */
    public void addEventHandler(EventHandler h) {
        this.handlers.add(h);
    }

    /**
     * Appeler la methode 'handle' de tous les gestionnaires d'evenements
     * (EventHandler) en passant la commande et les arguments de la commande.
     * 
     * @param cmd la commande.
     * @param arg les arguments de la commande.
     */
    private void alertHandlers(String cmd, String arg) {
        for (EventHandler h : this.handlers) {
            h.handle(cmd, arg);
        }
    }

    /**
     * Cette methode est appelee par le thread du serveur pour traiter les requetes
     * des clients.
     * Elle lit les requetes des clients en utilisant un flux d'entree et les traite
     * en utilisant la methode 'handleEvents'.
     * Elle envoie les reponses aux clients en utilisant un flux de sortie.
     * Elle gere les exceptions si une erreur se produit lors de la lecture ou de
     * l'ecriture dans les flux.
     */

    public void run() {
        while (true) {
            try {
                client = server.accept();
                System.out.println("Connecté au client: " + client);
                objectInputStream = new ObjectInputStream(client.getInputStream());
                objectOutputStream = new ObjectOutputStream(client.getOutputStream());
                listen();
                disconnect();
                System.out.println("Client déconnecté!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Cette methode lit les requetes des clients en utilisant un flux d'entree et
     * les traite en utilisant la methode "handleEvents'.
     * Elle gere les exceptions si une erreur se produit lors de la lecture dans le
     * flux.
     * 
     * @throws IOException            si une erreur se produit lors de la lecture
     *                                dans le flux.
     * @throws ClassNotFoundException si une erreur se produit lors de la lecture
     *                                dans le flux.
     */
    public void listen() throws IOException, ClassNotFoundException {
        String line;
        if ((line = this.objectInputStream.readObject().toString()) != null) {
            Pair<String, String> parts = processCommandLine(line);
            String cmd = parts.getKey();
            String arg = parts.getValue();
            this.alertHandlers(cmd, arg);

        }

    }

    /**
     * Cette methode separe la commande et les arguments de la commande d'une
     * requete d'un client.
     * 
     * @param line la requete du client
     * @return un objet 'Pair' contenant la commande et les arguments de la commande
     */
    public Pair<String, String> processCommandLine(String line) {
        String[] parts = line.split(" ");
        String cmd = parts[0];
        String args = String.join(" ", Arrays.asList(parts).subList(1,
                parts.length));
        return new Pair<>(cmd, args);
    }

    /**
     * Cette methode est appelé par la méthode 'listen' pour traiter la commande
     * 'INSCRIRE'.
     * Elle gere les exceptions si une erreur se produit lors de l'ecriture la
     * l'objet dans le flux.
     * Elle deconnecte le client en fermant les flux de donnees et la socket.
     */
    public void disconnect() throws IOException {
        objectOutputStream.close();
        objectInputStream.close();
        client.close();
    }

    /**
     * Cette methode est appelé par la méthode 'listen pour traiter la commande
     * recue par le client 'INSCRIRE' OU 'CHARGER'.
     * Si la commande est 'INSCRIRE', elle appele la méthode 'handleRegistration
     * ()'.
     * Si la commande est 'CHARGER', elle appele la méthode 'handleLoadCourses
     * (arg)'.
     * 
     * @param cmd la commande reçue du client
     * @param arg les arguments de la commande
     */
    public void handleEvents(String cmd, String arg) {
        if (cmd.equals(REGISTER_COMMAND)) {
            handleRegistration();
        } else if (cmd.equals(LOAD_COMMAND)) {
            handleLoadCourses(arg);
        }
    }

    /**
     * Cette methode lit un fichier texte contenant des informations sur les cours
     * et
     * les transofmer en liste d'objets 'Course'.
     * La méthode filtre les cours par
     * Elle renvoie la liste des cours de la session spécifiée en argument au
     * client.
     * La méthode gère les exceptions si une erreur se produit lors de la lecture du
     * fichier ou de l'écriture de l'objet dans le flux.
     * 
     * @param arg la session pour laquelle on veut récupérer la liste des cours
     */
    public void handleLoadCourses(String arg) {
        ArrayList<Course> courses = new ArrayList<Course>();

        try {
            FileReader infoCours = new FileReader(
                    "src/main/java/server/data/cours.txt");

            BufferedReader reader = new BufferedReader(infoCours);

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("\t");

                if (parts[2].equals(arg)) {
                    courses.add(new Course(parts[1], parts[0], parts[2]));
                }
            }
            reader.close();

        } catch (IOException ex) {
            System.out.println("Erreur dans l'ouverture du fichier");
        }

        try {
            objectOutputStream.writeObject(courses);

        } catch (IOException ex) {
            System.out.println("Erreur à l'écriture");
            ex.printStackTrace();

        }

    }

    /**
     * Cette méthode récupére l'objet 'RegistrationForm' envoyé par le client,
     * enregistrer les informations pour l'inscription dans
     * un fichier texte et envoye un message de confirmation au client.
     * La méthode gére les exceptions si une erreur se produit lors de la lecture de
     * l'objet, l'écriture dans un fichier ou dans le flux de sortie.
     */
    public void handleRegistration() {

        // String message = "Le code du cours n'est pas valide ou ce cours n'est pas
        // disponible pour la session choisie";

        Boolean succes = false;
        try {

            String session = (String) objectInputStream.readObject();
            RegistrationForm registrationForm = (RegistrationForm) objectInputStream.readObject();

            FileWriter fw = new FileWriter("src/main/java/server/data/inscription.txt", true);

            BufferedWriter writer = new BufferedWriter(fw);

            String s = "\n" + session + "\t"
                    + registrationForm.getCourse().getCode() + "\t"
                    + registrationForm.getMatricule() + "\t"
                    + registrationForm.getPrenom() + "\t"
                    + registrationForm.getNom() + "\t"
                    + registrationForm.getEmail();

            writer.append(s);
            writer.close();
            // message = "Félicitations! Inscription réussie de " +
            // registrationForm.getPrenom() + " au cours "
            // + registrationForm.getCourse().getCode();
            succes = true;

        } catch (ClassNotFoundException e) {
            System.out.println("La classe lu n'existe pas dans le programme");
        } catch (IOException e) {
            System.out.println("Erreur à la lecture ou à ecriture du fichier");
        } catch (NullPointerException e) {
            System.out.println("Code du cours invalide.");
        }

        try {
            // objectOutputStream.writeObject(message);
            objectOutputStream.writeObject(succes);
        } catch (IOException ex) {
            System.out.println("Erreur dans l'envoie de la dernier message au client.");
        }
    }
}
