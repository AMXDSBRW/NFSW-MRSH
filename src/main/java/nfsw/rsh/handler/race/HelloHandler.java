package nfsw.rsh.handler.race;

import nfsw.rsh.pktvo.IMainPacket;
import nfsw.rsh.pktvo.IPkt;
import nfsw.rsh.pktvo.race.MainRacePacket;
import nfsw.rsh.pktvo.race.sync.ISrvPkt;
import nfsw.rsh.pktvo.race.sync.SrvPkt;
import nfsw.rsh.pktvo.race.sync.SrvPktHello;
import nfsw.rsh.protocol.MpAllTalkers;
import nfsw.rsh.protocol.MpSession;
import nfsw.rsh.protocol.MpSessions;
import nfsw.rsh.protocol.MpTalker;
import nfsw.rsh.util.UdpDebug;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import nfsw.rsh.util.ConsoleColors;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

public class HelloHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println(ConsoleColors.info("### channelRead aufgerufen - Nachricht empfangen"));

		if (msg instanceof DatagramPacket) {
			DatagramPacket datagramPacket = (DatagramPacket) msg;
			ByteBuf buf = datagramPacket.content();

			// Überprüfen, ob es sich um ein Hello-Paket handelt und ob das Ticket gültig ist
			if (isHelloPacket(buf) && isTicketOk(buf)) {
				System.out.println(ConsoleColors.success("### Hello-Paket und Ticket validiert, beginne Verarbeitung"));

				Integer sessionId = Integer.valueOf(buf.getInt(9));
				byte maxUsers = buf.getByte(13);
				short cliTime = buf.getShort(69);

				MainRacePacket mainRacePacket = new MainRacePacket(sessionId, Integer.valueOf(maxUsers), cliTime);
				MpTalker mpTalker = new MpTalker(ctx, datagramPacket, (IMainPacket) mainRacePacket);

				MpSession mpSession = MpSessions.get(mpTalker);
				if (mpSession == null) {
					System.out.println(ConsoleColors.info("### Keine Session gefunden, erstelle neue Session"));
					mpSession = new MpSession(mpTalker);
				}
				mpSession.put(mpTalker);
				MpSessions.put(mpSession);

				// Wenn die Session voll ist, sende Antwort an alle Talker
				if (mpSession.isFull()) {
					System.out.println(ConsoleColors.success("### Session ist voll, sende Antwort an alle Talker"));
					Map<Integer, MpTalker> mpTalkers = mpSession.getMpTalkers();
					Iterator<Map.Entry<Integer, MpTalker>> iterator = mpTalkers.entrySet().iterator();
					while (iterator.hasNext()) {
						Map.Entry<Integer, MpTalker> next = iterator.next();
						MpTalker mpTalkerTmp = next.getValue();
						mpTalkerTmp.send(answer(mpTalkerTmp));
					}
				}

				MpAllTalkers.put(mpTalker);
			} else {
				System.out.println(ConsoleColors.error("### Ungültiges Hello-Paket oder Ticket"));
			}
		} else {
			System.out.println(ConsoleColors.warning("### Empfangenes Paket ist kein DatagramPacket, keine Verarbeitung"));
		}

		super.channelRead(ctx, msg);
	}

	private boolean isHelloPacket(ByteBuf buf) {
		System.out.println(ConsoleColors.info("### Überprüfe, ob es sich um ein Hello-Paket handelt"));

		if (buf.getByte(0) == 0 && buf.getByte(3) == 6) {
			System.out.println(ConsoleColors.success("### Hello-Paket validiert"));
			return true;
		}
		System.out.println(ConsoleColors.error("### Ungültiges Hello-Paket"));
		return false;
	}

	private boolean isTicketOk(ByteBuf buf) {
		System.out.println(ConsoleColors.info("### Überprüfe, ob Ticket gültig ist"));

		String sysPasswd = System.getProperty("password");
		if (sysPasswd == null) {
			System.out.println(ConsoleColors.info("### Kein Passwort gesetzt, Ticket ist ok"));
			return true;
		}

		byte[] bytes = ByteBufUtil.getBytes(buf, 52, 16);
		String keyFile = UdpDebug.byteArrayToHexString(bytes).replaceAll(":", "");
		File file = new File("keys/" + keyFile);
		if (file.isFile()) {
			System.out.println(ConsoleColors.success("### Ticket gültig, lösche Schlüsseldatei"));
			file.delete();
			return true;
		}
		System.out.println(ConsoleColors.error("### Ungültiges Ticket, Schlüsseldatei nicht gefunden"));
		return false;
	}

	private byte[] answer(MpTalker mpTalker) {
		System.out.println(ConsoleColors.info("### Generiere Antwort für MpTalker"));

		MainRacePacket mainPacket = (MainRacePacket) mpTalker.getMainPacket();
		SrvPktHello srvPktHello = new SrvPktHello();
		SrvPkt srvPkt = mainPacket.getSrvPkt();
		srvPkt.setHelloCliTime(mainPacket.getCliTime());
		srvPkt.setSrvPkt((ISrvPkt) srvPktHello);

		System.out.println(ConsoleColors.success("### Antwort generiert, sende zurück an MpTalker"));
		return mpTalker.getMainPacket((IPkt) srvPkt);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println(ConsoleColors.error("### Ausnahme gefangen: " + cause.getMessage()));
		cause.printStackTrace();
	}
}
