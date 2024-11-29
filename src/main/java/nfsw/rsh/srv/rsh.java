package nfsw.rsh.srv;

import nfsw.rsh.util.ConsoleColors;
import nfsw.rsh.util.MemoryUsage;
import io.netty.channel.ChannelFuture;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class rsh {
	public static void main(String[] args) {
		String version = "1.0.0";
		String softwareName = "Need For Speed: World Race Synchronization Host";

		displayStartupMessage(softwareName, version);

		int port = 9998;
		if (args.length == 1) {
			port = Integer.parseInt(args[0]);
		}

		String folderName = "keys";
		Path path = Paths.get(folderName);
		try {
			if (!Files.exists(path)) {
				Files.createDirectory(path);
				System.out.println(ConsoleColors.success("Der Ordner '" + folderName + "' wurde erfolgreich erstellt."));
			} else {
				System.out.println(ConsoleColors.info("Der Ordner '" + folderName + "' existiert bereits."));
			}
		} catch (Exception e) {
			System.err.println(ConsoleColors.error("Fehler beim Erstellen des Ordners: " + e.getMessage()));
		}

		try {
			new MemoryUsage(Integer.valueOf(port));
			NettyUdpServer server = new NettyUdpServer(port);
			ChannelFuture future = server.start();
			System.out.println(ConsoleColors.success(softwareName + " Modul wurde gestartet, an Port: " + port));
			future.channel().closeFuture().sync();
		} catch (InterruptedException ex) {
			System.err.println(ConsoleColors.error("Fehler beim Starten des Servers: " + ex.getMessage()));
		}
	}

	private static void displayStartupMessage(String softwareName, String version) {
		String logo = ConsoleColors.white(
				"  _   _   ______    _____      __          __    _____     _____   _    _ \n" +
						" | \\ | | |  ____|  / ____|  _  \\ \\        / /   |  __ \\   / ____| | |  | | \n" +
						" |  \\| | | |__    | (___   (_)  \\ \\  /\\  / /    | |__) | | (___   | |__| | \n" +
						" | . ` | |  __|    \\___ \\        \\ \\/  \\/ /     |  _  /   \\___ \\  |  __  | \n" +
						" | |\\  | | |       ____) |  _     \\  /\\  /      | | \\ \\   ____) | | |  | | \n" +
						" |_| \\_| |_|      |_____/  (_)     \\/  \\/       |_|  \\_\\ |_____/  |_|  |_| \n"
		);
		System.out.println(logo);

		System.out.println(ConsoleColors.white("|------------------------------------------------------------------------|"));
		System.out.println(ConsoleColors.white("|  " + ConsoleColors.BOLD + "Willkommen bei " + softwareName + "!" + ConsoleColors.RESET + "       |"));
		System.out.println(ConsoleColors.white("|------------------------------------------------------------------------|"));
		System.out.println(ConsoleColors.white("|  " + ConsoleColors.BOLD + "Version: " + version + ConsoleColors.RESET + "                                                        |"));
		System.out.println(ConsoleColors.white("|------------------------------------------------------------------------|"));
		System.out.println(ConsoleColors.white("|------------------------------------------------------------------------|"));
		System.out.println(ConsoleColors.white("|  " + ConsoleColors.BOLD + "Das Synchronisationstool für NFS World wurde erfolgreich gestartet!" + ConsoleColors.RESET + "   |"));
		System.out.println(ConsoleColors.white("|------------------------------------------------------------------------|"));
		System.out.println(ConsoleColors.white("|------------------------------------------------------------------------|"));
		System.out.println(ConsoleColors.info("Diese Anwendung zeigt die folgenden Fehlermeldungsformate an:"));
		System.out.println(ConsoleColors.error("Ein schwerwiegender Fehler ist aufgetreten!"));
		System.out.println(ConsoleColors.info("Informationsmeldungen."));
		System.out.println(ConsoleColors.warning("Warnungen."));
		System.out.println(ConsoleColors.success("Erfolgreiche Operationen."));
		System.out.println(ConsoleColors.white("|------------------------------------------------------------------------|"));
		System.out.println(ConsoleColors.white("|------------------| Meldungen Beginnen ab Hier: |-----------------------|"));
		System.out.println(ConsoleColors.white("|------------------------------------------------------------------------|"));
	}
}
