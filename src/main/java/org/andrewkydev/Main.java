package org.andrewkydev;

import org.andrewkydev.rcon.Rcon;
import org.andrewkydev.rcon.ex.AuthenticationException;

import java.io.IOException;
import java.nio.file.*;

/**
 * The class watches a directory for changes and sends a reload command to the server when a file in the directory is modified.
 */
public class Main {
    /**
     * The main method of the class.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // The host and port of the Minecraft server
        String host = "127.0.0.1";
        int port = 25575;
        // The password of the RCON connection
        String password = "1YjdkNTJiN";
        // The command to send to the server
        String command = "reload true";
        // The directory to watch for changes
        String pathToWatch = "/home/andrew/projects/1/plugins/";

        try {
            // Create a new watch service
            WatchService watchService = FileSystems.getDefault().newWatchService();

            // Register the directory with the watch service
            Path path = Paths.get(pathToWatch);
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            // Wait for changes
            WatchKey key;
            while ((key = watchService.take()) != null) {
                // Process the events
                for (WatchEvent<?> event : key.pollEvents()) {
                    // Print the event details
                    System.out.println("Event kind: " + event.kind() + ". File affected: " + event.context() + ".");
                    if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                        // Create a new Rcon instance and send the command
                        Rcon rcon = new Rcon(host, port, password.getBytes());
                        String response = rcon.command(command);
                        System.out.println(response);
                    }
                }
                // Reset the key
                key.reset();
            }
        } catch (IOException | InterruptedException | AuthenticationException e) {
            e.printStackTrace();
        }
    }
}
