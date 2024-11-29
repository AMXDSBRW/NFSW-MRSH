package nfsw.rsh.handler.race;

import nfsw.rsh.pktvo.IPkt;
import nfsw.rsh.pktvo.race.MainRacePacket;
import nfsw.rsh.pktvo.race.player.PlayerInfo;
import nfsw.rsh.pktvo.race.player.PlayerMainPkt;
import nfsw.rsh.protocol.MpAllTalkers;
import nfsw.rsh.protocol.MpSession;
import nfsw.rsh.protocol.MpSessions;
import nfsw.rsh.protocol.MpTalker;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import nfsw.rsh.util.ConsoleColors;

import java.util.Iterator;
import java.util.Map;

public class PlayerInfoBeforeHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println(ConsoleColors.info("### channelRead aufgerufen - Nachricht empfangen"));

		if (msg instanceof DatagramPacket) {
			DatagramPacket datagramPacket = (DatagramPacket) msg;
			ByteBuf buf = datagramPacket.content();

			// Überprüfen, ob es sich um ein PlayerInfo-Paket handelt
			if (isPlayerInfoPacket(buf)) {
				System.out.println(ConsoleColors.success("### PlayerInfo Paket erkannt, beginne mit der Verarbeitung"));

				MpTalker mpTalker = MpAllTalkers.get(datagramPacket);
				if (mpTalker != null) {
					System.out.println(ConsoleColors.success("### MpTalker gefunden, verarbeite PlayerInfo"));
					MainRacePacket mainPacket = (MainRacePacket) mpTalker.getMainPacket();
					mainPacket.parsePlayerInfo(buf);
					playerInfoBeforeOk(mpTalker);
				} else {
					System.out.println(ConsoleColors.error("### MpTalker nicht gefunden"));
				}
			} else {
				System.out.println(ConsoleColors.warning("### Kein PlayerInfo Paket"));
			}
		} else {
			System.out.println(ConsoleColors.warning("### Empfangenes Paket ist kein DatagramPacket, keine Verarbeitung"));
		}

		super.channelRead(ctx, msg);
	}

	private void playerInfoBeforeOk(MpTalker mpTalker) {
		System.out.println(ConsoleColors.info("### Überprüfe, ob alle PlayerInfo korrekt sind"));

		MpSession mpSession = MpSessions.get(mpTalker);
		Map<Integer, MpTalker> mpTalkersTmp = mpSession.getMpTalkers();
		if (isAllPlayerInfoBeforeOk(mpSession)) {
			System.out.println(ConsoleColors.success("### Alle PlayerInfo korrekt, sende Broadcast an die Session"));

			Iterator<Map.Entry<Integer, MpTalker>> iterator = mpTalkersTmp.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<Integer, MpTalker> next = iterator.next();
				MpTalker mpTalkerTmp = next.getValue();
				mpTalkerTmp.broadcastToSession(sendPlayerInfo(mpTalkerTmp));
			}
		} else {
			System.out.println(ConsoleColors.warning("### Nicht alle PlayerInfo sind korrekt"));
		}
	}

	private boolean isAllPlayerInfoBeforeOk(MpSession mpSession) {
		System.out.println(ConsoleColors.info("### Überprüfe, ob alle PlayerInfo vor der Verarbeitung korrekt sind"));

		Map<Integer, MpTalker> mpTalkersTmp = mpSession.getMpTalkers();
		Iterator<Map.Entry<Integer, MpTalker>> iterator = mpTalkersTmp.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<Integer, MpTalker> next = iterator.next();
			MpTalker mpTalker = next.getValue();
			MainRacePacket mainPacket = (MainRacePacket) mpTalker.getMainPacket();
			if (!mainPacket.isPlayerInfoOk()) {
				System.out.println(ConsoleColors.error("### PlayerInfo ist nicht korrekt für einen Spieler"));
				return false;
			}
		}
		System.out.println(ConsoleColors.success("### Alle PlayerInfo sind korrekt"));
		return true;
	}

	private boolean isPlayerInfoPacket(ByteBuf buf) {
		System.out.println(ConsoleColors.info("### Überprüfe, ob es sich um ein PlayerInfo Paket handelt"));

		if (buf.getByte(0) == 1 && buf.getByte(6) == -1 && buf.getByte(7) == -1 && buf.getByte(8) == -1 && buf.getByte(9) == -1) {
			System.out.println(ConsoleColors.success("### PlayerInfo Paket validiert"));
			return true;
		}
		System.out.println(ConsoleColors.error("### Ungültiges PlayerInfo Paket"));
		return false;
	}

	private byte[] sendPlayerInfo(MpTalker mpTalker) {
		System.out.println(ConsoleColors.info("### Generiere PlayerInfo für MpTalker"));

		MainRacePacket mainPacket = (MainRacePacket) mpTalker.getMainPacket();
		PlayerMainPkt playerMainPkt = mainPacket.getPlayerMainPkt();
		PlayerInfo playerInfo = new PlayerInfo();
		playerInfo.setPlayerInfo(mainPacket.getSbrwParser().getPlayerInfo());
		playerInfo.setPlayerStatePos(mainPacket.getSbrwParser().getCarState());
		playerMainPkt.setPlayerInfo(playerInfo);

		System.out.println(ConsoleColors.success("### PlayerInfo erstellt, sende Antwort zurück"));

		return mpTalker.getMainPacket((IPkt) playerMainPkt);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println(ConsoleColors.error("### Ausnahme gefangen: " + cause.getMessage()));
		cause.printStackTrace();
		ctx.close();
		System.out.println(ConsoleColors.info("### Verbindung geschlossen nach Ausnahme"));
	}
}
