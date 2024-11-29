package nfsw.rsh.handler.security;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import nfsw.rsh.util.ConsoleColors;

public class EndHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println(ConsoleColors.info("### Nachricht empfangen - channelRead aufgerufen"));

		if (msg instanceof DatagramPacket) {
			DatagramPacket datagramPacket = (DatagramPacket) msg;
			ByteBuf buf = datagramPacket.content();

			try {
				int readableBytes = buf.readableBytes();
				System.out.println(ConsoleColors.info("### Inhalt des Puffers - Lesbare Bytes: " + readableBytes));

			} finally {
				buf.release();
				System.out.println(ConsoleColors.success("### Puffer freigegeben nach Verarbeitung"));
			}
		} else {
			System.out.println(ConsoleColors.warning("### Unbekanntes Nachrichtenformat empfangen, keine Verarbeitung durchgeführt"));
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println(ConsoleColors.error("### Ausnahme im EndHandler gefangen: " + cause.getMessage()));
		cause.printStackTrace();
		ctx.close();
		System.out.println(ConsoleColors.info("### Verbindung geschlossen nach Ausnahme"));
	}
}
