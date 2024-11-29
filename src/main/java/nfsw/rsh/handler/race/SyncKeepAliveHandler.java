package nfsw.rsh.handler.race;

import nfsw.rsh.pktvo.IPkt;
import nfsw.rsh.pktvo.race.MainRacePacket;
import nfsw.rsh.pktvo.race.sync.ISrvPkt;
import nfsw.rsh.pktvo.race.sync.SrvPkt;
import nfsw.rsh.pktvo.race.sync.SrvPktMainSync;
import nfsw.rsh.pktvo.race.sync.SrvPktSyncKeepAlive;
import nfsw.rsh.protocol.MpAllTalkers;
import nfsw.rsh.protocol.MpTalker;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import nfsw.rsh.util.ConsoleColors;

public class SyncKeepAliveHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println(ConsoleColors.info("### channelRead aufgerufen - Nachricht empfangen"));

		if (msg instanceof DatagramPacket) {
			DatagramPacket datagramPacket = (DatagramPacket) msg;
			ByteBuf buf = datagramPacket.content();
			byte[] packet = ByteBufUtil.getBytes(buf);

			if (isSyncKeepAlive(packet)) {
				System.out.println(ConsoleColors.success("### SyncKeepAlive Paket erkannt, beginne mit der Verarbeitung"));

				MpTalker mpTalker = MpAllTalkers.get(datagramPacket);
				if (mpTalker != null) {
					System.out.println(ConsoleColors.success("### MpTalker gefunden, sende Antwort"));
					mpTalker.send(answer(mpTalker, buf));
				} else {
					System.out.println(ConsoleColors.error("### MpTalker nicht gefunden"));
				}
			} else {
				System.out.println(ConsoleColors.warning("### Kein SyncKeepAlive Paket"));
			}
		} else {
			System.out.println(ConsoleColors.warning("### Empfangenes Paket ist kein DatagramPacket, keine Verarbeitung"));
		}

		super.channelRead(ctx, msg);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println(ConsoleColors.error("### Ausnahme gefangen: " + cause.getMessage()));
		cause.printStackTrace();
		ctx.close();
		System.out.println(ConsoleColors.info("### Verbindung geschlossen nach Ausnahme"));
	}

	private boolean isSyncKeepAlive(byte[] dataPacket) {
		System.out.println(ConsoleColors.info("### Überprüfe, ob es sich um ein SyncKeepAlive Paket handelt"));

		if (dataPacket[0] == 0 && dataPacket[3] == 7 && dataPacket.length == 18) {
			System.out.println(ConsoleColors.success("### SyncKeepAlive Paket validiert"));
			return true;
		}
		System.out.println(ConsoleColors.error("### Ungültiges SyncKeepAlive Paket"));
		return false;
	}

	private byte[] answer(MpTalker mpTalker, ByteBuf buf) {
		System.out.println(ConsoleColors.info("### Generiere Antwort für MpTalker"));

		short counter = buf.getShort(9);
		MainRacePacket mainPacket = (MainRacePacket) mpTalker.getMainPacket();

		System.out.println(ConsoleColors.info("### Haupt-Renn-Paket abgerufen, setze Zähler: " + counter));

		SrvPktSyncKeepAlive srvPktSyncKeepAlive = new SrvPktSyncKeepAlive();
		SrvPkt srvPkt = mainPacket.getSrvPkt();
		SrvPktMainSync srvPktMainSync = mainPacket.getSrvPktMainSync();
		srvPktMainSync.setCounter(counter);
		srvPktMainSync.setPktSync((IPkt) srvPktSyncKeepAlive);
		mainPacket.getSrvPkt().setSrvPkt((ISrvPkt) srvPktMainSync);

		System.out.println(ConsoleColors.success("### Antwort-Paket erstellt, sende Antwort zurück"));

		return mpTalker.getMainPacket((IPkt) srvPkt);
	}
}