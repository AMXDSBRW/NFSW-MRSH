package nfsw.rsh.protocol;

import nfsw.rsh.pktvo.IMainPacket;
import nfsw.rsh.pktvo.IPkt;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.Date;
import nfsw.rsh.util.ConsoleColors;

public class MpTalker {
	private ChannelHandlerContext ctx;
	private DatagramPacket datagramPacket;
	private IMainPacket mainPacket;
	private long mpTalkerTimerStart = (new Date()).getTime();

	public MpTalker(ChannelHandlerContext ctx, DatagramPacket datagramPacket, IMainPacket mainPacket) {
		if (ctx == null) {
			System.err.println(ConsoleColors.error("ChannelHandlerContext ist null."));
			throw new IllegalArgumentException("ChannelHandlerContext darf nicht null sein.");
		}
		if (datagramPacket == null) {
			System.err.println(ConsoleColors.error("DatagramPacket ist null."));
			throw new IllegalArgumentException("DatagramPacket darf nicht null sein.");
		}
		if (mainPacket == null) {
			System.err.println(ConsoleColors.error(" IMainPacket ist null."));
			throw new IllegalArgumentException("IMainPacket darf nicht null sein.");
		}

		this.ctx = ctx;
		this.datagramPacket = datagramPacket;
		this.mainPacket = mainPacket;

		System.out.println(ConsoleColors.info("Erstellte MpTalker:"));
		System.out.println(ConsoleColors.info("session: [" + mainPacket.getSessionId() + "]"));
		System.out.println(ConsoleColors.info("axUsers: [" + mainPacket.getMaxUsers().intValue() + "]"));
		System.out.println("");
	}

	public Integer getSessionId() {
		return this.mainPacket.getSessionId();
	}

	public Integer getPort() {
		return ((InetSocketAddress) this.datagramPacket.sender()).getPort();
	}

	public void send(byte[] packetData) {
		if (packetData == null) {
			System.err.println(ConsoleColors.error("Fehler: packetData ist null. Kann nicht senden."));
			return;
		}
		System.out.println(ConsoleColors.info("Sende Paket an Port: " + getPort()));
		this.ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(packetData), (InetSocketAddress) this.datagramPacket.sender()));
	}

	public void broadcastToSession(byte[] packetData) {
		if (packetData == null) {
			System.err.println(ConsoleColors.error("packetData ist null. Kann nicht senden."));
			return;
		}
		System.out.println(ConsoleColors.info("Broadcast an die Sitzung senden."));
		MpSession mpSession = MpSessions.get(this);
		if (mpSession == null) {
			System.err.println(ConsoleColors.error("Keine MpSession gefunden für Talker mit Session ID: " + getSessionId()));
			return;
		}
		mpSession.broadcastFrom(this, packetData);
	}

	public byte[] getMainPacket(IPkt pkt) {
		if (pkt == null) {
			System.err.println(ConsoleColors.error("IPkt ist null. Kann kein Paket abrufen."));
			return null;
		}
		return this.mainPacket.getPacket(pkt);
	}

	public IMainPacket getMainPacket() {
		return this.mainPacket;
	}

	public long getMpTalkerTimerStart() {
		return this.mpTalkerTimerStart;
	}
}
