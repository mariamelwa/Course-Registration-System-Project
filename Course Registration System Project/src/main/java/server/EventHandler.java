package server;

/**
 * Cette interface fonctionnelle qui permet au serveur d'ajouter des evenements.
 */

@FunctionalInterface
public interface EventHandler {
    void handle(String cmd, String arg);
}
