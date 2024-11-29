package nfsw.rsh.srv;

import nfsw.rsh.util.ConsoleColors;
import nfsw.rsh.handler.race.HelloHandler;
import nfsw.rsh.handler.race.PlayerInfoAfterHandler;
import nfsw.rsh.handler.race.PlayerInfoBeforeHandler;
import nfsw.rsh.handler.race.SyncHandler;
import nfsw.rsh.handler.race.SyncHelloHandler;
import nfsw.rsh.handler.race.SyncKeepAliveHandler;
import nfsw.rsh.handler.security.EndHandler;
import nfsw.rsh.handler.security.SecurityHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramChannel;

public class ServerChannelInitializer extends ChannelInitializer<DatagramChannel> {
	protected void initChannel(DatagramChannel ch) throws Exception {
		// Informationsmeldung in Blau
		System.out.println(ConsoleColors.info("Initialisiere den Kanal..."));

		ChannelPipeline pipeline = ch.pipeline();

		// Hinzufügen der Handler und informative Ausgaben
		pipeline.addLast("security", (ChannelHandler) new SecurityHandler());
		System.out.println(ConsoleColors.success("Handler 'security' hinzugefügt."));

		pipeline.addLast("hello", (ChannelHandler) new HelloHandler());
		System.out.println(ConsoleColors.success("Handler 'hello' hinzugefügt."));

		pipeline.addLast("syncHello", (ChannelHandler) new SyncHelloHandler());
		System.out.println(ConsoleColors.success("Handler 'syncHello' hinzugefügt."));

		pipeline.addLast("playerInfoBefore", (ChannelHandler) new PlayerInfoBeforeHandler());
		System.out.println(ConsoleColors.success("Handler 'playerInfoBefore' hinzugefügt."));

		pipeline.addLast("syncKeepAlive", (ChannelHandler) new SyncKeepAliveHandler());
		System.out.println(ConsoleColors.success("Handler 'syncKeepAlive' hinzugefügt."));

		pipeline.addLast("sync", (ChannelHandler) new SyncHandler());
		System.out.println(ConsoleColors.success("Handler 'sync' hinzugefügt."));

		pipeline.addLast("playerInfoAfter", (ChannelHandler) new PlayerInfoAfterHandler());
		System.out.println(ConsoleColors.success("Handler 'playerInfoAfter' hinzugefügt."));

		pipeline.addLast("endHandler", (ChannelHandler) new EndHandler());
		System.out.println(ConsoleColors.success("Handler 'endHandler' hinzugefügt."));

		// Abschlussmeldung
		System.out.println(ConsoleColors.info("Kanalinitialisierung abgeschlossen."));
	}
}
