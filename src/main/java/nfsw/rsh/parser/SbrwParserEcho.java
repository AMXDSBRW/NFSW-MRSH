package nfsw.rsh.parser;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import nfsw.rsh.util.ConsoleColors;
import java.nio.ByteBuffer;

public class SbrwParserEcho implements IParser {
	private byte[] header;
	private byte[] playerInfo;
	private byte[] carState;
	private byte[] crc;

	public void parseInputData(byte[] inputData) {
		System.out.println(ConsoleColors.info("### Initialisiere SbrwParserEcho mit Eingabedaten."));

		byte[] fullPacket = inputData.clone();
		try {
			this.header = new byte[10];
			System.arraycopy(fullPacket, 0, this.header, 0, 10);
			this.crc = new byte[4];
			System.arraycopy(fullPacket, fullPacket.length - 5, this.crc, 0, 4);

			System.out.println(ConsoleColors.success("### Header und CRC erfolgreich geparst."));

			int subPacketStart = 10;
			int count = 0;
			while (fullPacket[subPacketStart] != -1 && count < 4) {
				int supPacketLength = fullPacket[subPacketStart + 1] + 2;

				if (fullPacket[subPacketStart] == 2) {
					System.out.println(ConsoleColors.info("### Parsen der Spielerinformationen..."));
					this.playerInfo = new byte[supPacketLength];
					System.arraycopy(fullPacket, subPacketStart, this.playerInfo, 0, supPacketLength);
					subPacketStart += supPacketLength;
					System.out.println(ConsoleColors.success("### Spielerinformationen erfolgreich geparst."));
				}

				supPacketLength = fullPacket[subPacketStart + 1] + 2;
				if (fullPacket[subPacketStart] == 18) {
					System.out.println(ConsoleColors.info("### Parsen des Fahrzeugzustands..."));
					this.carState = new byte[supPacketLength];
					System.arraycopy(fullPacket, subPacketStart, this.carState, 0, supPacketLength);
					subPacketStart += supPacketLength;
					System.out.println(ConsoleColors.success("### Fahrzeugzustand erfolgreich geparst."));
				}

				count++;
				System.out.println(ConsoleColors.info("### Anzahl der Unterpakete verarbeitet: " + count + "."));
			}

			fullPacket = null;
			System.out.println(ConsoleColors.success("### Fertig mit dem Parsen der Eingabedaten."));
		} catch (Exception e) {
			System.err.println(ConsoleColors.error("### Ausnahme während des Parsens: " + e.getMessage()));
		}
	}

	public byte[] getHeader() {
		System.out.println(ConsoleColors.info("### Header-Daten werden abgerufen."));
		return this.header;
	}

	public byte[] getPlayerInfo() {
		System.out.println(ConsoleColors.info("### Spielerinformationen werden abgerufen."));
		return this.playerInfo;
	}

	public byte[] getCarState() {
		System.out.println(ConsoleColors.info("### Fahrzeugzustand wird abgerufen."));
		return this.carState;
	}

	public byte[] getCrc() {
		System.out.println(ConsoleColors.info("### CRC-Daten werden abgerufen."));
		return this.crc;
	}

	public boolean isOk() {
		System.out.println(ConsoleColors.info("### Überprüfung, ob die Daten gültig sind."));
		if (this.playerInfo == null || this.carState == null) {
			System.out.println(ConsoleColors.warning("### Ungültige Daten: playerInfo oder carState sind null."));
			return false;
		}
		System.out.println(ConsoleColors.success("### Daten sind gültig."));
		return true;
	}

	public boolean isCarStateOk() {
		System.out.println(ConsoleColors.info("### Überprüfung, ob der Fahrzeugzustand gültig ist."));
		if (this.carState == null) {
			System.out.println(ConsoleColors.warning("### Fahrzeugzustand ist null."));
			return false;
		}
		System.out.println(ConsoleColors.success("### Fahrzeugzustand ist gültig."));
		return true;
	}

	public byte[] getPlayerPacket(long timeDiff) {
		System.out.println(ConsoleColors.info("### Erstelle Spieler-Paket."));
		if (isOk()) {
			byte[] statePosPacket = getStatePosPacket(timeDiff);
			int bufferSize = this.playerInfo.length + statePosPacket.length;
			ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
			byteBuffer.put(this.playerInfo);
			byteBuffer.put(statePosPacket);
			System.out.println(ConsoleColors.success("### Spieler-Paket erfolgreich erstellt."));
			return byteBuffer.array();
		}
		System.out.println(ConsoleColors.warning("### Spieler-Paket konnte nicht erstellt werden: Ungültige Daten."));
		return null;
	}

	public byte[] getCarStatePacket(long timeDiff) {
		System.out.println(ConsoleColors.info("### Erstelle Fahrzeugzustands-Paket."));
		if (isOk()) {
			byte[] statePosPacket = getStatePosPacket(timeDiff);
			ByteBuffer byteBuffer = ByteBuffer.allocate(statePosPacket.length);
			byteBuffer.put(this.carState);
			System.out.println(ConsoleColors.success("### Fahrzeugzustands-Paket erfolgreich erstellt."));
			return byteBuffer.array();
		}
		System.out.println(ConsoleColors.warning("### Fahrzeugzustands-Paket konnte nicht erstellt werden: Ungültige Daten."));
		return null;
	}

	public byte[] getStatePosPacket(long timeDiff) {
		System.out.println(ConsoleColors.info("### Erstelle State-Position-Paket."));
		if (isOk()) {
			byte[] clone = this.carState.clone();
			byte[] timeDiffBytes = ByteBuffer.allocate(2).putShort((short) timeDiff).array();
			clone[2] = timeDiffBytes[0];
			clone[3] = timeDiffBytes[1];
			ByteBuffer byteBuffer = ByteBuffer.allocate(clone.length);
			byteBuffer.put(clone);
			System.out.println(ConsoleColors.success("### State-Position-Paket erfolgreich erstellt."));
			return byteBuffer.array();
		}
		System.out.println(ConsoleColors.warning("### State-Position-Paket konnte nicht erstellt werden: Ungültige Daten."));
		return null;
	}

	public String getName() {
		System.out.println(ConsoleColors.info("### Spielernamen wird abgerufen."));
		if (isOk()) {
			byte[] playerPacket = getPlayerPacket(50000L);
			byte[] playerName = new byte[15];
			System.arraycopy(playerPacket, 39, playerName, 0, 15);
			String playerNameStr = new String(playerName).trim();
			System.out.println(ConsoleColors.success("### Spielername erfolgreich abgerufen: " + playerNameStr));
			return playerNameStr;
		}
		System.out.println(ConsoleColors.warning("### Spielername konnte nicht abgerufen werden: Ungültige Daten."));
		return null;
	}

	public void parseInputData(ByteBuf buf) {
		System.out.println(ConsoleColors.info("### Parsen der Eingabedaten aus ByteBuf."));
		parseInputData(ByteBufUtil.getBytes(buf));
	}
}
