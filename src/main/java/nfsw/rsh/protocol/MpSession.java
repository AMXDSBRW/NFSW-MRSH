package nfsw.rsh.protocol;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import nfsw.rsh.util.ConsoleColors;

public class MpSession {
	private HashMap<Integer, MpTalker> mpTalkers = new HashMap<>();
	private Integer sessionId;
	private Integer maxUsers;
	private long sessionTimeStart = (new Date()).getTime();

	public MpSession(MpTalker mpTalker) {
		if (mpTalker == null) {
			System.err.println(ConsoleColors.error("MpTalker ist null und kann nicht zur Sitzung hinzugefügt werden."));
			return;
		}
		this.maxUsers = mpTalker.getMainPacket().getMaxUsers();
		this.sessionId = mpTalker.getSessionId();
		put(mpTalker);
	}

	public Integer getSessionId() {
		return this.sessionId;
	}

	public void put(MpTalker mpTalker) {
		if (mpTalker == null) {
			System.err.println(ConsoleColors.error("MpTalker ist null und kann nicht hinzugefügt werden."));
			return;
		}
		if (this.mpTalkers.containsKey(mpTalker.getPort())) {
			System.err.println(ConsoleColors.error("MpTalker mit Port " + mpTalker.getPort() + " ist bereits in der Sitzung vorhanden."));
			return;
		}
		System.out.println(ConsoleColors.info("Füge MpTalker hinzu mit Port: " + mpTalker.getPort()));
		this.mpTalkers.put(mpTalker.getPort(), mpTalker);
		System.out.println(ConsoleColors.success("MpTalker erfolgreich hinzugefügt."));
	}

	public boolean isFull() {
		boolean full = (this.mpTalkers.size() == this.maxUsers.intValue());
		if (full) {
			System.out.println(ConsoleColors.info("Sitzung ist voll. Anzahl der Talker: " + this.mpTalkers.size()));
		}
		return full;
	}

	public Map<Integer, MpTalker> getMpTalkers() {
		System.out.println(ConsoleColors.info("Hole alle MpTalker. Anzahl der Talker: " + this.mpTalkers.size()));
		return this.mpTalkers;
	}

	public long getSessionTimeStart() {
		return this.sessionTimeStart;
	}

	public void broadcastFrom(MpTalker mpTalker, byte[] packetData) {
		if (mpTalker == null) {
			System.err.println(ConsoleColors.error("MpTalker ist null. Kann nicht senden."));
			return;
		}
		if (packetData == null) {
			System.err.println(ConsoleColors.error("packetData ist null. Kann nicht senden."));
			return;
		}
		System.out.println(ConsoleColors.info("Sende Broadcast von MpTalker mit Port: " + mpTalker.getPort()));
		Iterator<Map.Entry<Integer, MpTalker>> iterator = this.mpTalkers.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, MpTalker> next = iterator.next();
			if (!next.getKey().equals(mpTalker.getPort())) {
				MpTalker value = next.getValue();
				value.send(packetData);
			}
		}
	}

	public void broadcast(byte[] packetData) {
		if (packetData == null) {
			System.err.println(ConsoleColors.error("packetData ist null. Kann nicht senden."));
			return;
		}
		System.out.println(ConsoleColors.info("Sende Broadcast an alle MpTalker."));
		Iterator<Map.Entry<Integer, MpTalker>> iterator = this.mpTalkers.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, MpTalker> next = iterator.next();
			MpTalker value = next.getValue();
			value.send(packetData);
		}
	}
}
