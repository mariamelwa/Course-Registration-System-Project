package server;

/**
 * Cette classe sert a initialiser le serveur pour que les etudiants puissent
 * s'inscrire a un cours.
 */
public class ServerLauncher {
    public final static int PORT = 1337;

    public static void main(String[] args) {
        Server server;
        try {
            server = new Server(PORT);//
            System.out.println("Server is running...");
            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}