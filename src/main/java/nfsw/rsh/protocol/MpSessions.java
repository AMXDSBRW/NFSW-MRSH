package nfsw.rsh.protocol;

import java.util.HashMap;
import nfsw.rsh.util.ConsoleColors;

public class MpSessions {
	private static HashMap<Integer, MpSession> sessions = new HashMap<>();

	public static void put(MpSession mpSession) {
		if (mpSession == null) {
			System.err.println(ConsoleColors.error("MpSession ist null und kann nicht hinzugefügt werden."));
			return;
		}
		if (sessions.containsKey(mpSession.getSessionId())) {
			System.err.println(ConsoleColors.error("MpSession mit ID " + mpSession.getSessionId() + " existiert bereits."));
			return;
		}
		System.out.println(ConsoleColors.info("Füge MpSession mit ID " + mpSession.getSessionId() + " hinzu."));
		sessions.put(mpSession.getSessionId(), mpSession);
		System.out.println(ConsoleColors.success("MpSession erfolgreich hinzugefügt."));
	}

	public static MpSession get(MpTalker mpTalker) {
		if (mpTalker == null) {
			System.err.println(ConsoleColors.error("MpTalker ist null. Kann keine Sitzung abrufen."));
			return null;
		}
		Integer sessionId = mpTalker.getSessionId();
		System.out.println(ConsoleColors.info("Hole MpSession für Talker mit Session ID: " + sessionId));
		MpSession session = sessions.get(sessionId);

		if (session == null) {
			System.err.println(ConsoleColors.error("Fehler: Keine MpSession gefunden für Session ID: " + sessionId));
		} else {
			System.out.println(ConsoleColors.info("MpSession gefunden: " + session));
		}

		return session;
	}
}
