package fr.efrei.transport;

public class CanalNotificationConsole implements CanalNotification {

    @Override
    public void envoyerEmail(String destinataire, String sujet, String corps) {
        System.out.println("[EMAIL] To: " + destinataire + " | Subject: " + sujet + " | Body: " + corps);
    }

    @Override
    public void envoyerSms(String destinataire, String message) {
        System.out.println("[SMS] To: " + destinataire + " | Message: " + message);
    }
}
