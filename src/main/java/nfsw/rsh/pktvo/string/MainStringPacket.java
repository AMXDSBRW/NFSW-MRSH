package nfsw.rsh.pktvo.string;

import nfsw.rsh.pktvo.IMainPacket;
import nfsw.rsh.pktvo.IPkt;
import nfsw.rsh.util.ConsoleColors;

public class MainStringPacket implements IMainPacket {
	private Integer sessionId;
	private Integer maxUsers;

	public MainStringPacket(Integer sessionId, Integer maxUsers) {
		if (sessionId == null || sessionId < 0) {
			System.err.println(ConsoleColors.error("Ungültige sessionId. Sie darf nicht null oder negativ sein."));
			throw new IllegalArgumentException("sessionId darf nicht null oder negativ sein.");
		}
		if (maxUsers == null || maxUsers <= 0) {
			System.err.println(ConsoleColors.error("Ungültige maxUsers. Sie darf nicht null oder kleiner/gleich null sein."));
			throw new IllegalArgumentException("maxUsers darf nicht null oder kleiner/gleich null sein.");
		}

		this.sessionId = sessionId;
		this.maxUsers = maxUsers;

		System.out.println(ConsoleColors.info("MainStringPacket erstellt mit sessionId: " + sessionId + " und maxUsers: " + maxUsers));
	}

	@Override
	public byte[] getPacket(IPkt pkt) {
		if (pkt == null) {
			System.err.println(ConsoleColors.error("IPkt ist null. Kann das Paket nicht abrufen."));
			return null;
		}

		byte[] packetData = pkt.getPacket();
		if (packetData == null) {
			System.err.println(ConsoleColors.error("Paketdaten sind null. Kann das Paket nicht zurückgeben."));
		} else {
			System.out.println(ConsoleColors.info("Paketdaten erfolgreich abgerufen mit Länge: " + packetData.length));
		}
		return packetData;
	}

	@Override
	public Integer getMaxUsers() {
		return this.maxUsers;
	}

	@Override
	public Integer getSessionId() {
		return this.sessionId;
	}
}
