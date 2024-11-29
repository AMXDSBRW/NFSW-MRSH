package nfsw.rsh.handler.security;

import nfsw.rsh.util.UdpDebug;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import java.io.File;
import java.net.InetSocketAddress;
import nfsw.rsh.util.ConsoleColors;

public class SecurityHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println(ConsoleColors.info("### channelRead aufgerufen - Nachricht empfangen"));

		if (msg instanceof DatagramPacket) {
			DatagramPacket datagramPacket = (DatagramPacket) msg;
			ByteBuf buf = datagramPacket.content();

			System.out.println(ConsoleColors.info("### Paket empfangen, Überprüfe, ob es sich um ein Sicherheits-Paket handelt"));

			if (isSecurityPacket(buf) && isPasswordOk(buf)) {
				System.out.println(ConsoleColors.success("### Sicherheits-Paket validiert, beginne mit der Verarbeitung"));

				byte[] bytes = ByteBufUtil.getBytes(buf, 9, 16);
				String fileName = UdpDebug.byteArrayToHexString(bytes).replaceAll(":", "");
				File file = new File("keys/" + fileName);

				if (file.createNewFile()) {
					System.out.println(ConsoleColors.success("### Neue Datei erstellt: " + file.getAbsolutePath()));
				} else {
					System.out.println(ConsoleColors.warning("### Datei existiert bereits: " + file.getAbsolutePath()));
				}

				byte[] packetData = "0".getBytes();
				ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(packetData), (InetSocketAddress) datagramPacket.sender()));
			} else {
				System.out.println(ConsoleColors.error("### Ungültiges Sicherheits-Paket oder falsches Passwort"));
				byte[] packetData = "1".getBytes();
				ctx.writeAndFlush(new DatagramPacket(Unpooled.copiedBuffer(packetData), (InetSocketAddress) datagramPacket.sender()));
			}
		} else {
			System.out.println(ConsoleColors.warning("### Empfangenes Paket ist kein DatagramPacket, keine Verarbeitung"));
		}

		super.channelRead(ctx, msg);
	}

	private boolean isSecurityPacket(ByteBuf buf) {
		System.out.println(ConsoleColors.info("### Überprüfe, ob das Paket ein Sicherheits-Paket ist"));
		return buf.getByte(0) == -103;
	}

	private boolean isPasswordOk(ByteBuf buf) {
		System.out.println(ConsoleColors.info("### Überprüfe, ob das Passwort korrekt ist"));

		String sysPasswd = System.getProperty("password");
		if (sysPasswd == null) {
			System.out.println(ConsoleColors.warning("### Kein System-Passwort gesetzt, Paket wird zugelassen"));
			return true;
		}

		byte[] bytes = ByteBufUtil.getBytes(buf, 1, 8);
		String udpPasswd = new String(bytes);
		if (sysPasswd.equals(udpPasswd)) {
			System.out.println(ConsoleColors.success("### Passwort erfolgreich abgeglichen"));
			return true;
		} else {
			System.out.println(ConsoleColors.error("### Passwort stimmt nicht überein"));
		}
		return false;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println(ConsoleColors.error("### Ausnahme gefangen: " + cause.getMessage()));
		cause.printStackTrace();
		ctx.close();
		System.out.println(ConsoleColors.info("### Verbindung geschlossen nach Ausnahme"));
	}
}
