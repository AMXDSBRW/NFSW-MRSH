package nfsw.rsh.protocol;

import io.netty.channel.socket.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import nfsw.rsh.util.ConsoleColors;

public class MpAllTalkers {
	private static HashMap<Integer, MpTalker> mpTalkers = new HashMap<>();

	public static void put(MpTalker mpTalker) {
		if (mpTalker == null) {
			System.err.println(ConsoleColors.error("MpTalker ist null und kann nicht hinzugefügt werden."));
			return;
		}
		System.out.println(ConsoleColors.info("Füge MpTalker hinzu mit Port: " + mpTalker.getPort()));
		mpTalkers.put(mpTalker.getPort(), mpTalker);
		System.out.println(ConsoleColors.success("MpTalker erfolgreich hinzugefügt."));
	}

	public static MpTalker get(DatagramPacket datagramPacket) {
		if (datagramPacket == null) {
			System.err.println(ConsoleColors.error("DatagramPacket ist null."));
			return null;
		}

		int port = ((InetSocketAddress) datagramPacket.sender()).getPort();
		System.out.println(ConsoleColors.info("Hole MpTalker für Port: " + port));
		MpTalker talker = mpTalkers.get(port);

		if (talker != null) {
			System.out.println(ConsoleColors.success("MpTalker gefunden: " + talker));
		} else {
			System.err.println(ConsoleColors.error("Kein MpTalker gefunden für Port: " + port));
		}

		return talker;
	}

	public static Map<Integer, MpTalker> getMpTalkers() {
		System.out.println(ConsoleColors.info("Hole alle MpTalker. Anzahl der Talker: " + mpTalkers.size()));
		return mpTalkers;
	}
}
