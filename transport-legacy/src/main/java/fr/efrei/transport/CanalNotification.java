package fr.efrei.transport;

public interface CanalNotification {
    void envoyerEmail(String destinataire, String sujet, String corps);
    void envoyerSms(String destinataire, String message);
}
