package nfsw.rsh.parser;

import nfsw.rsh.util.ConsoleColors;
import java.math.BigInteger;

public class SbrwParser {
	private byte[] header;
	private byte[] channelInfo;
	private byte[] playerInfo;
	private byte[] carState;
	private byte[] crc;
	private int lastSeq = 0;
	private int lastTime = 0;

	public SbrwParser(byte[] inputData) {
		System.out.println(ConsoleColors.info("### Initialisiere SbrwParser mit Eingabedaten."));
		parseInputData(inputData);
	}

	public boolean parseInputData(byte[] inputData) {
		System.out.println(ConsoleColors.info("### Starte die Methode parseInputData."));
		byte[] fullPacket = inputData.clone();
		boolean good2go = false;
		try {
			System.out.println(ConsoleColors.info("### Klone Eingabedaten, verarbeite Header und CRC."));
			this.header = new byte[16];
			System.arraycopy(fullPacket, 0, this.header, 0, 16);
			this.crc = new byte[4];
			System.arraycopy(fullPacket, fullPacket.length - 4, this.crc, 0, 4);

			byte[] seqBytes = {this.header[0], this.header[1]};
			byte[] timeBytes = {this.header[3], this.header[4]};
			int seq = new BigInteger(seqBytes).intValue();
			int time = new BigInteger(timeBytes).intValue();

			System.out.println(ConsoleColors.success("### Geparstes Sequenznummer: " + seq + ", Zeit: " + time));

			if (seq == -1) {
				System.out.println(ConsoleColors.warning("### Sequenzlimit erreicht, zurücksetzen."));
				seq = 0;
				this.lastSeq = 0;
			}

			if (seq < this.lastSeq && time < this.lastTime) {
				System.err.println(ConsoleColors.error("### Fehler: Reihenfolge von seq/time ist inkorrekt: " + seq + "/" + time));
				System.err.println(ConsoleColors.error("### Paket aus der Reihenfolge: seq=" + seq + ", Zeit=" + time));
			} else {
				good2go = true;
				System.out.println(ConsoleColors.success("### Paket-Sequenz und Zeit validiert."));
			}

			this.lastSeq = seq;
			this.lastTime = time;

			int subPacketStart = 16;
			int count = 0;
			System.out.println(ConsoleColors.info("### Beginne mit dem Parsen der Unterpakete."));

			while (fullPacket[subPacketStart] != -1 && count < 4) {
				int supPacketLength = fullPacket[subPacketStart + 1] + 2;
				System.out.println(ConsoleColors.info("### Verarbeite Unterpaket an Index " + subPacketStart + " mit Länge " + supPacketLength + "."));

				if (fullPacket[subPacketStart] == 0) {
					this.channelInfo = new byte[supPacketLength];
					System.arraycopy(fullPacket, subPacketStart, this.channelInfo, 0, supPacketLength);
					subPacketStart += supPacketLength;
					System.out.println(ConsoleColors.success("### Kanalinformationen erfolgreich geparsed."));
				}

				supPacketLength = fullPacket[subPacketStart + 1] + 2;
				if (fullPacket[subPacketStart] == 1) {
					this.playerInfo = new byte[supPacketLength];
					System.arraycopy(fullPacket, subPacketStart, this.playerInfo, 0, supPacketLength);
					subPacketStart += supPacketLength;
					System.out.println(ConsoleColors.success("### Spielerinformationen erfolgreich geparsed."));
				}

				supPacketLength = fullPacket[subPacketStart + 1] + 2;
				if (fullPacket[subPacketStart] == 18) {
					this.carState = new byte[supPacketLength];
					System.arraycopy(fullPacket, subPacketStart, this.carState, 0, supPacketLength);
					subPacketStart += supPacketLength;
					System.out.println(ConsoleColors.success("### Fahrzeugzustand erfolgreich geparsed."));
				}

				count++;
				System.out.println(ConsoleColors.info("### Anzahl der Unterpakete verarbeitet: " + count + "."));
			}

			fullPacket = null;
			System.out.println(ConsoleColors.success("### Fertig mit dem Parsen der Eingabedaten."));
		} catch (Exception e) {
			System.err.println(ConsoleColors.error("### Ausnahme gefangen: " + e.getMessage()));
		}
		return good2go;
	}

	// Getter-Methoden mit Logging
	public byte[] getHeader() {
		System.out.println(ConsoleColors.info("### getHeader aufgerufen."));
		return this.header;
	}

	public byte[] getChannelInfo() {
		System.out.println(ConsoleColors.info("### getChannelInfo aufgerufen."));
		return this.channelInfo;
	}

	public byte[] getPlayerInfo() {
		System.out.println(ConsoleColors.info("### getPlayerInfo aufgerufen."));
		return this.playerInfo;
	}

	public byte[] getCarState() {
		System.out.println(ConsoleColors.info("### getCarState aufgerufen."));
		return this.carState;
	}

	public byte[] getCrc() {
		System.out.println(ConsoleColors.info("### getCrc aufgerufen."));
		return this.crc;
	}

	public int getTime() {
		System.out.println(ConsoleColors.info("### getTime aufgerufen."));
		if (isOk()) {
			return new BigInteger(new byte[]{this.header[3], this.header[4]}).intValue();
		}
		System.out.println(ConsoleColors.warning("### Daten ungültig, gebe -1 für Zeit zurück."));
		return -1;
	}

	public boolean isOk() {
		System.out.println(ConsoleColors.info("### isOk aufgerufen, überprüfe Gültigkeit der Daten."));
		if (this.channelInfo == null || this.playerInfo == null || this.carState == null) {
			System.out.println(ConsoleColors.warning("### Daten ungültig: Mindestens eine Komponente ist null."));
			return false;
		}
		System.out.println(ConsoleColors.success("### Daten sind gültig."));
		return true;
	}
}
