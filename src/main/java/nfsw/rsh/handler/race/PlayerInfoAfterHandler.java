package nfsw.rsh.handler.race;

import nfsw.rsh.pktvo.IPkt;
import nfsw.rsh.pktvo.race.MainRacePacket;
import nfsw.rsh.pktvo.race.player.PlayerInfo;
import nfsw.rsh.pktvo.race.player.PlayerMainPkt;
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class PlayerInfoAfterHandler extends ChannelInboundHandlerAdapter {

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
					playerInfoAfterOk(mpTalker, buf);
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

	private void playerInfoAfterOk(MpTalker mpTalker, ByteBuf buf) {
		System.out.println(ConsoleColors.info("### Überprüfe, ob alle PlayerInfo korrekt sind, bevor Broadcast gesendet wird"));

		MpSession mpSession = MpSessions.get(mpTalker);
		if (isAllPlayerInfoBeforeOk(mpSession)) {
			System.out.println(ConsoleColors.success("### Alle PlayerInfo korrekt, sende Broadcast an die Session"));
			mpTalker.broadcastToSession(sendPlayerInfo(mpTalker, buf));
		} else {
			System.out.println(ConsoleColors.warning("### Nicht alle PlayerInfo sind korrekt, überspringe Broadcast"));
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

	private byte[] sendPlayerInfo(MpTalker mpTalker, ByteBuf buf) {
		System.out.println(ConsoleColors.info("### Generiere PlayerInfo für MpTalker"));

		MainRacePacket mainPacket = (MainRacePacket) mpTalker.getMainPacket();
		PlayerMainPkt playerMainPkt = mainPacket.getPlayerMainPkt();
		playerMainPkt.disableAllInfo();
		playerMainPkt.enableUnknownCounter();
		PlayerInfo playerInfo = new PlayerInfo();

		byte[] bytesTmp = ByteBufUtil.getBytes(buf);
		int limit = bytesTmp.length;
		limit -= 6;
		byte[] copyOfRange = Arrays.copyOfRange(bytesTmp, 10, limit);
		playerInfo.setPlayerStatePos(copyOfRange);
		bytesTmp = null;
		copyOfRange = null;

		playerMainPkt.setPlayerInfo(playerInfo);
		System.out.println(ConsoleColors.success("### PlayerInfo erstellt, sende Antwort zurück"));

		return mpTalker.getMainPacket((IPkt) playerMainPkt);
	}

	private boolean isPlayerInfoPacket(ByteBuf buf) {
		System.out.println(ConsoleColors.info("### Überprüfe, ob es sich um ein PlayerInfo Paket handelt"));

		if (buf.getByte(0) == 1) {
			System.out.println(ConsoleColors.success("### PlayerInfo Paket validiert"));
			return true;
		}
		System.out.println(ConsoleColors.error("### Ungültiges PlayerInfo Paket"));
		return false;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println(ConsoleColors.error("### Ausnahme gefangen: " + cause.getMessage()));
		cause.printStackTrace();
		ctx.close();
		System.out.println(ConsoleColors.info("### Verbindung geschlossen nach Ausnahme"));
	}

	public static void main(String[] args) {
		System.out.println(ConsoleColors.info("### Hauptmethode gestartet - Debugging UDP Paket"));

		byte[] fullPacket = UdpDebug.hexStringToByteArray("01:00:00:6e:00:07:00:05:ff:ff:10:45:00:01:00:00:00:09:00:00:00:a4:32:c1:1c:65:00:00:00:00:00:00:00:00:00:80:40:ac:fa:18:00:a0:56:20:1d:bc:fa:18:00:ac:a0:0f:1d:90:18:16:1d:80:41:68:00:00:00:00:00:d8:fa:18:00:30:3d:69:00:80:41:68:00:a4:e4:7c:22:12:1a:a4:e5:98:08:72:36:d0:e5:9b:e9:c4:25:89:c4:1f:3e:fb:f9:d3:96:96:96:9b:34:08:1f:ff:aa:56:c9:3c:ff:");
		int subPacketStart = 10;
		int count = 0;
		byte nextType = -1;

		while (fullPacket[subPacketStart] != -1 && count < 4) {
			int supPacketLength = fullPacket[subPacketStart + 1] + 2;
			subPacketStart += supPacketLength;
			nextType = fullPacket[subPacketStart];
			if ((nextType == 18 || nextType == 16 || nextType != 2) && nextType == 18) {
				System.out.println(ConsoleColors.success("### Debugging: Type 18 gefunden, beginne Verarbeitung"));
			}
			System.out.println(ConsoleColors.info("### Schleifeniteration: count = " + count));
			count++;
		}
	}
}
