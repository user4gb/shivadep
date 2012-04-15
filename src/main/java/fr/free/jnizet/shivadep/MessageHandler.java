package fr.free.jnizet.shivadep;

public interface MessageHandler {
    void displayError(String message);
    void displayInfo(String message);
    void clear();
}
