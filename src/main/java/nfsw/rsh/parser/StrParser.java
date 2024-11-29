package nfsw.rsh.parser;

import io.netty.buffer.ByteBuf;
import nfsw.rsh.util.ConsoleColors;

public class StrParser implements IParser {
	private String playerInfo;

	public void parseInputData(byte[] packet) {
		System.out.println(ConsoleColors.info("Parsen der Eingabedaten..."));

		if (packet == null || packet.length == 0) {
			System.out.println(ConsoleColors.error("Das Paket ist null oder leer!"));
			return;
		}

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("player: [");
		stringBuilder.append((new String(packet)).trim());
		stringBuilder.append("]\n\n");
		this.playerInfo = stringBuilder.toString();

		System.out.println(ConsoleColors.success("Player Info erfolgreich geparst: " + this.playerInfo));
	}

	public boolean isOk() {
		if (this.playerInfo != null && !this.playerInfo.isEmpty()) {
			System.out.println(ConsoleColors.success("Player Info ist gültig."));
			return true;
		} else {
			System.out.println(ConsoleColors.error("Player Info ist null oder leer!"));
			return false;
		}
	}

	public byte[] getPlayerPacket(long timeDiff) {
		System.out.println(ConsoleColors.info("Erstelle Player Packet mit Zeitdifferenz: " + timeDiff));

		if (this.playerInfo != null && !this.playerInfo.isEmpty()) {
			byte[] packet = this.playerInfo.getBytes();
			System.out.println(ConsoleColors.success("Player Packet erfolgreich erstellt. Größe: " + packet.length + " Bytes"));
			return packet;
		} else {
			System.out.println(ConsoleColors.error("Player Info ist nicht verfügbar, Packet kann nicht erstellt werden."));
			return new byte[0];
		}
	}

	public boolean isCarStateOk() {
		System.out.println(ConsoleColors.info("Fahrzeugzustand ist immer nicht ok (false)."));
		return false;
	}

	public byte[] getCarStatePacket(long timeDiff) {
		System.out.println(ConsoleColors.info("Fahrzeugpaket ist nicht implementiert und wird daher null zurückgegeben."));
		return null;
	}

	public void parseInputData(ByteBuf buf) {
		System.out.println(ConsoleColors.info("Parsen von ByteBuf-Daten (nicht implementiert)."));
	}
}
