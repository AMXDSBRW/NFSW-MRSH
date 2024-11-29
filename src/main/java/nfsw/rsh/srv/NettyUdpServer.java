package nfsw.rsh.srv;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import nfsw.rsh.util.ConsoleColors;

import java.net.InetSocketAddress;

public class NettyUdpServer {
	private int port;
	private Channel channel;
	private EventLoopGroup workerGroup;

	public NettyUdpServer(int port) {
		this.port = port;
	}

	public ChannelFuture start() throws InterruptedException {
		// Info: Server wird gestartet
		System.out.println(ConsoleColors.info("Starte Netty UDP-Server auf Port: " + port));

		this.workerGroup = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(this.workerGroup)
				.channel(NioDatagramChannel.class)
				.handler(new ServerChannelInitializer());

		ChannelFuture channelFuture = bootstrap.bind(new InetSocketAddress(this.port)).syncUninterruptibly();
		this.channel = channelFuture.channel();

		// Erfolg oder Fehler beim Starten des Servers
		if (channelFuture.isSuccess()) {
			System.out.println(ConsoleColors.success("Netty UDP-Server erfolgreich gestartet und auf Port " + port + " gebunden."));
		} else {
			System.err.println(ConsoleColors.error("Fehler beim Starten des Netty UDP-Servers."));
		}

		return channelFuture;
	}

	public void stop() {
		// Info: Server wird gestoppt
		System.out.println(ConsoleColors.info("Stoppe den Netty UDP-Server..."));

		if (this.channel != null) {
			this.channel.close();
			// Erfolg: Server wurde gestoppt
			System.out.println(ConsoleColors.success("Netty UDP-Server wurde erfolgreich gestoppt."));
		} else {
			// Info: Server war nicht aktiv
			System.out.println(ConsoleColors.info("Netty UDP-Server war nicht aktiv."));
		}

		workerGroup.shutdownGracefully();
		// Erfolg: Worker-Gruppe wurde heruntergefahren
		System.out.println(ConsoleColors.success("Worker-Gruppe wurde erfolgreich heruntergefahren."));
	}
}
