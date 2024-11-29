package nfsw.rsh.handler.string;

import nfsw.rsh.pktvo.string.MainStringPacket;
import nfsw.rsh.pktvo.string.StrHelloPkt;
import nfsw.rsh.protocol.MpAllTalkers;
import nfsw.rsh.protocol.MpSession;
import nfsw.rsh.protocol.MpSessions;
import nfsw.rsh.protocol.MpTalker;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import nfsw.rsh.util.ConsoleColors;

import java.util.Map;

public class StrHelloHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println(ConsoleColors.info("### Empfangene Nachricht wird verarbeitet..."));

		DatagramPacket datagramPacket = (DatagramPacket) msg;
		ByteBuf buf = datagramPacket.content();

		if (isHelloPacket(buf)) {
			System.out.println(ConsoleColors.success("### Hello-Paket erkannt."));

			try {
				char charSession = (char) buf.getByte(1);
				char charMaxUsers = (char) buf.getByte(2);
				Integer sessionId = Integer.parseInt(String.valueOf(charSession));
				Integer maxUsers = Integer.parseInt(String.valueOf(charMaxUsers));

				System.out.println(ConsoleColors.info("### Extrahierte sessionId: " + sessionId + ", maxUsers: " + maxUsers));

				MainStringPacket mainStringPacket = new MainStringPacket(sessionId, maxUsers);
				MpTalker mpTalker = new MpTalker(ctx, datagramPacket, mainStringPacket);

				System.out.println(ConsoleColors.success("### MpTalker-Objekt erfolgreich erstellt."));

				MpSession mpSession = MpSessions.get(mpTalker);
				if (mpSession == null) {
					System.out.println(ConsoleColors.warning("### Keine bestehende Session gefunden. Erstelle neue MpSession."));
					mpSession = new MpSession(mpTalker);
				}

				mpSession.put(mpTalker);
				MpSessions.put(mpSession);

				System.out.println(ConsoleColors.success("### MpTalker zur MpSession hinzugefügt."));

				if (mpSession.isFull()) {
					System.out.println(ConsoleColors.info("### Session ist voll. Versende Hello-Pakete an alle Talker..."));

					Map<Integer, MpTalker> mpTalkers = mpSession.getMpTalkers();
					for (MpTalker mpTalkerTmp : mpTalkers.values()) {
						MainStringPacket mainPacket = (MainStringPacket) mpTalkerTmp.getMainPacket();

						System.out.println(ConsoleColors.info(
								"### Versende Hello-Paket an MpTalker mit Session-Startzeit: " + mpSession.getSessionTimeStart()
						));

						long sessionTimeStart = mpSession.getSessionTimeStart();
						StrHelloPkt strHelloPkt = new StrHelloPkt(sessionTimeStart, mpTalkerTmp);

						mpTalkerTmp.send(mainPacket.getPacket(strHelloPkt));
					}

					System.out.println(ConsoleColors.success("### Alle Hello-Pakete erfolgreich versendet."));
				}

				MpAllTalkers.put(mpTalker);
				System.out.println(ConsoleColors.success("### MpTalker zu MpAllTalkers hinzugefügt."));

			} catch (Exception e) {
				System.err.println(ConsoleColors.error("### Fehler bei der Verarbeitung des Hello-Pakets: " + e.getMessage()));
			}

		} else {
			System.out.println(ConsoleColors.warning("### Kein Hello-Paket. Ignoriere Nachricht."));
		}
	}

	private boolean isHelloPacket(ByteBuf buf) {
		System.out.println(ConsoleColors.info("### Überprüfe, ob es sich um ein Hello-Paket handelt..."));

		char charTmp = (char) buf.getByte(0);
		if (charTmp == '!') {
			System.out.println(ConsoleColors.success("### Dies ist ein Hello-Paket."));
			return true;
		}

		System.out.println(ConsoleColors.warning("### Dies ist KEIN Hello-Paket."));
		return false;
	}
}
