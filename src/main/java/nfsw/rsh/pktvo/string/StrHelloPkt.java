package nfsw.rsh.pktvo.string;

import nfsw.rsh.pktvo.IPkt;
import nfsw.rsh.protocol.MpTalker;
import java.util.Date;
import nfsw.rsh.util.ConsoleColors;

public class StrHelloPkt implements IPkt {
	private long sessionTimeStart;
	private MpTalker mpTalker;

	public StrHelloPkt(long sessionTimeStart, MpTalker mpTalker) {
		if (mpTalker == null) {
			System.err.println(ConsoleColors.error("Fehler: MpTalker darf nicht null sein."));
			throw new IllegalArgumentException("mpTalker darf nicht null sein.");
		}

		this.sessionTimeStart = sessionTimeStart;
		this.mpTalker = mpTalker;

		System.out.println(ConsoleColors.info("StrHelloPkt erstellt mit sessionTimeStart: " + sessionTimeStart));
	}

	@Override
	public byte[] getPacket() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Welcome!\n");
		stringBuilder.append("sessionTimeStart@").append(this.sessionTimeStart).append("\n");
		stringBuilder.append("mpTimeStart@").append(this.mpTalker.getMpTalkerTimerStart()).append("\n");
		long now = (new Date()).getTime();
		stringBuilder.append("now@").append(now).append("\n");
		stringBuilder.append("timediff[").append(now - this.mpTalker.getMpTalkerTimerStart()).append("]\n");
		byte[] packetData = stringBuilder.toString().getBytes();
		System.out.println(ConsoleColors.info("Paket generiert mit Länge: " + packetData.length + " Bytes."));
		return packetData;
	}
}
