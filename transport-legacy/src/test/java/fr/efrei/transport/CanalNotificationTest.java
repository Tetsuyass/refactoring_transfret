package fr.efrei.transport;

import java.util.ArrayList;
import java.util.List;

public class CanalNotificationTest implements CanalNotification {

    public final List<String[]> emails = new ArrayList<>();
    public final List<String[]> sms = new ArrayList<>();

    @Override
    public void envoyerEmail(String destinataire, String sujet, String corps) {
        emails.add(new String[]{destinataire, sujet + " | " + corps});
    }

    @Override
    public void envoyerSms(String destinataire, String message) {
        sms.add(new String[]{destinataire, message});
    }

    public void clear() {
        emails.clear();
        sms.clear();
    }
}
