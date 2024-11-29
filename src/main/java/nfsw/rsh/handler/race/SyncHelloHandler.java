package nfsw.rsh.handler.race;

import nfsw.rsh.pktvo.IPkt;
import nfsw.rsh.pktvo.race.MainRacePacket;
import nfsw.rsh.pktvo.race.sync.ISrvPkt;
import nfsw.rsh.pktvo.race.sync.SrvPkt;
import nfsw.rsh.pktvo.race.sync.SrvPktMainSync;
import nfsw.rsh.pktvo.race.sync.SrvPktSyncStart;
import nfsw.rsh.protocol.MpAllTalkers;
import nfsw.rsh.protocol.MpSession;
import nfsw.rsh.protocol.MpSessions;
import nfsw.rsh.protocol.MpTalker;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import java.util.Iterator;
import java.util.Map;
import nfsw.rsh.util.ConsoleColors;

public class SyncHelloHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println(ConsoleColors.info("### channelRead aufgerufen - Nachricht empfangen"));

		if (msg instanceof DatagramPacket) {
			DatagramPacket datagramPacket = (DatagramPacket) msg;
			ByteBuf buf = datagramPacket.content();

			if (isHelloSync(ByteBufUtil.getBytes(buf))) {
				System.out.println(ConsoleColors.success("### HelloSync Paket erkannt, beginne mit der Verarbeitung"));

				MpTalker mpTalker = MpAllTalkers.get(datagramPacket);
				if (mpTalker != null) {
					System.out.println(ConsoleColors.success("### MpTalker gefunden, bearbeite HelloSync"));

					MainRacePacket mainPacket = (MainRacePacket) mpTalker.getMainPacket();
					mainPacket.setHelloSyncBuf(buf);
					byte playerIdx = parsePlayerIdx(buf);
					mainPacket.getPlayerMainPkt().setPlayerIdx(playerIdx);

					System.out.println(ConsoleColors.info("### PlayerIdx geparsed: " + playerIdx));

					mpTalker.send(answer(mpTalker));
					syncHelloOk(mpTalker);
				} else {
					System.out.println(ConsoleColors.error("### MpTalker nicht gefunden"));
				}
			} else {
				System.out.println(ConsoleColors.warning("### Kein HelloSync Paket"));
			}
		} else {
			System.out.println(ConsoleColors.warning("### Empfangenes Paket ist kein DatagramPacket, keine Verarbeitung"));
		}

		super.channelRead(ctx, msg);
	}

	private void syncHelloOk(MpTalker mpTalker) {
		System.out.println(ConsoleColors.info("### Überprüfe, ob alle SyncHello für MpTalker OK sind"));

		MpSession mpSession = MpSessions.get(mpTalker);
		if (isAllSyncHelloOk(mpSession)) {
			System.out.println(ConsoleColors.success("### Alle SyncHello OK, sende Antworten an alle Talker"));

			Map<Integer, MpTalker> mpTalkers = mpSession.getMpTalkers();
			Iterator<Map.Entry<Integer, MpTalker>> iterator = mpTalkers.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<Integer, MpTalker> next = iterator.next();
				MpTalker mpTalkerTmp = next.getValue();
				mpTalkerTmp.send(answer(mpTalkerTmp));
			}
		} else {
			System.out.println(ConsoleColors.warning("### Nicht alle SyncHello sind OK"));
		}
	}

	private boolean isAllSyncHelloOk(MpSession mpSession) {
		System.out.println(ConsoleColors.info("### Überprüfe, ob alle Talker in der Sitzung SyncHello OK haben"));

		Map<Integer, MpTalker> mpTalkersTmp = mpSession.getMpTalkers();
		Iterator<Map.Entry<Integer, MpTalker>> iterator = mpTalkersTmp.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, MpTalker> next = iterator.next();
			MpTalker mpTalker = next.getValue();
			MainRacePacket mainPacket = (MainRacePacket) mpTalker.getMainPacket();
			if (!mainPacket.isSyncHelloOk()) {
				System.out.println(ConsoleColors.error("### SyncHello ist nicht OK für MpTalker: " + mpTalker));
				return false;
			}
		}
		System.out.println(ConsoleColors.success("### Alle SyncHello sind OK für diese Sitzung"));
		return true;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println(ConsoleColors.error("### Ausnahme gefangen: " + cause.getMessage()));
		cause.printStackTrace();
		ctx.close();
		System.out.println(ConsoleColors.info("### Verbindung geschlossen nach Ausnahme"));
	}

	private boolean isHelloSync(byte[] dataPacket) {
		System.out.println(ConsoleColors.info("### Überprüfe, ob es sich um ein HelloSync Paket handelt"));

		if (dataPacket[0] == 0 && dataPacket[3] == 7 && dataPacket.length == 26) {
			System.out.println(ConsoleColors.success("### HelloSync Paket validiert"));
			return true;
		}
		System.out.println(ConsoleColors.error("### Ungültiges HelloSync Paket"));
		return false;
	}

	private byte[] answer(MpTalker mpTalker) {
		System.out.println(ConsoleColors.info("### Generiere Antwort für MpTalker"));

		MainRacePacket mainPacket = (MainRacePacket) mpTalker.getMainPacket();
		SrvPkt srvPkt = mainPacket.getSrvPkt();
		ByteBuf helloSyncBuf = mainPacket.getHelloSyncBuf();
		short counter = helloSyncBuf.getShort(9);
		int sessionId = helloSyncBuf.getInt(16);
		byte maxPlayers = parseMaxPlayers(helloSyncBuf);

		System.out.println(ConsoleColors.info("### Counter: " + counter + ", SessionId: " + sessionId + ", MaxPlayers: " + maxPlayers));

		SrvPktSyncStart srvPktSyncStart = new SrvPktSyncStart();
		srvPktSyncStart.setSessionId(sessionId);
		srvPktSyncStart.setMaxPlayers(maxPlayers);
		SrvPktMainSync srvPktMainSync = mainPacket.getSrvPktMainSync();
		srvPktMainSync.setPktSync((IPkt) srvPktSyncStart);
		srvPktMainSync.setCounter(counter);
		srvPkt.setSrvPkt((ISrvPkt) srvPktMainSync);

		System.out.println(ConsoleColors.success("### Antwort-Paket erstellt, sende Antwort zurück"));

		return mpTalker.getMainPacket((IPkt) srvPkt);
	}

	private byte parsePlayerIdx(ByteBuf helloSyncBuf) {
		byte playerIdx = helloSyncBuf.getByte(20);
		playerIdx = (byte) (playerIdx & 0xE0);
		playerIdx = (byte) (playerIdx >> 1);
		playerIdx = (byte) (playerIdx & Byte.MAX_VALUE);
		playerIdx = (byte) (playerIdx >> 4);
		System.out.println(ConsoleColors.info("### Geparster PlayerIdx: " + playerIdx));
		return playerIdx;
	}

	private byte parseMaxPlayers(ByteBuf helloSyncBuf) {
		byte maxPlayers = helloSyncBuf.getByte(20);
		maxPlayers = (byte) (maxPlayers & 0xE);
		maxPlayers = (byte) (maxPlayers >> 1);
		System.out.println(ConsoleColors.info("### Geparster MaxPlayers: " + maxPlayers));
		return maxPlayers;
	}
}
