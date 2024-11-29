package nfsw.rsh.pktvo.race;

import nfsw.rsh.util.ConsoleColors;
import nfsw.rsh.parser.SbrwParserEcho;
import nfsw.rsh.pktvo.IMainPacket;
import nfsw.rsh.pktvo.IPkt;
import nfsw.rsh.pktvo.race.player.PlayerInfo;
import nfsw.rsh.pktvo.race.player.PlayerMainPkt;
import nfsw.rsh.pktvo.race.sync.SrvPkt;
import nfsw.rsh.pktvo.race.sync.SrvPktHello;
import nfsw.rsh.pktvo.race.sync.SrvPktMainSync;
import nfsw.rsh.pktvo.race.sync.SrvPktSync;
import nfsw.rsh.pktvo.race.sync.SrvPktSyncKeepAlive;
import nfsw.rsh.pktvo.race.sync.SrvPktSyncStart;
import nfsw.rsh.util.UdpDebug;
import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;

public class MainRacePacket implements IMainPacket {
	private static final int BUFFER_SIZE = 1024;
	private Integer sessionId;
	private Integer maxUsers;
	private short cliTime;
	private SrvPkt srvPkt = new SrvPkt();
	private SrvPktMainSync srvPktMainSync = new SrvPktMainSync();
	private PlayerMainPkt playerMainPkt = new PlayerMainPkt();
	private ByteBuf helloSyncBuf;
	private byte[] crc = new byte[] { 1, 1, 1, 1 };
	private boolean syncHelloOk = false;
	private SbrwParserEcho sbrwParser = new SbrwParserEcho();

	// Konstruktor mit detaillierten Logs
	public MainRacePacket(Integer sessionId, Integer maxUsers, short cliTime) {
		if (sessionId == null || sessionId < 0) {
			System.err.println(ConsoleColors.error("### Fehler: sessionId muss positiv sein."));
			throw new IllegalArgumentException("### Fehler: sessionId muss positiv sein.");
		}
		if (maxUsers == null || maxUsers <= 0) {
			System.err.println(ConsoleColors.error("### Fehler: maxUsers muss größer als 0 sein."));
			throw new IllegalArgumentException("### Fehler: maxUsers muss größer als 0 sein.");
		}
		if (cliTime < 0) {
			System.err.println(ConsoleColors.error("### Fehler: cliTime muss nicht negativ sein."));
			throw new IllegalArgumentException("### Fehler: cliTime muss nicht negativ sein.");
		}
		this.sessionId = sessionId;
		this.maxUsers = maxUsers;
		this.cliTime = cliTime;

		System.out.println(ConsoleColors.success("### MainRacePacket erstellt mit sessionId: " + sessionId + ", maxUsers: " + maxUsers + ", cliTime: " + cliTime));
	}

	@Override
	public byte[] getPacket(IPkt pkt) {
		if (pkt == null) {
			System.err.println(ConsoleColors.error("### Fehler: IPkt darf nicht null sein."));
			throw new IllegalArgumentException("### Fehler: IPkt darf nicht null sein.");
		}

		byte[] pktData = pkt.getPacket();
		ByteBuffer bytebuff = ByteBuffer.allocate(BUFFER_SIZE + pktData.length);
		bytebuff.put(pktData);
		bytebuff.put(this.crc);

		byte[] result = new byte[bytebuff.position()];
		bytebuff.flip();
		bytebuff.get(result);

		System.out.println(ConsoleColors.success("### Paket generiert mit Länge: " + result.length + " Bytes."));
		return result;
	}

	public Integer getMaxUsers() {
		return this.maxUsers;
	}

	public Integer getSessionId() {
		return this.sessionId;
	}

	public SrvPkt getSrvPkt() {
		return this.srvPkt;
	}

	public SrvPktMainSync getSrvPktMainSync() {
		return this.srvPktMainSync;
	}

	public PlayerMainPkt getPlayerMainPkt() {
		return this.playerMainPkt;
	}

	public ByteBuf getHelloSyncBuf() {
		return this.helloSyncBuf;
	}

	public void setHelloSyncBuf(ByteBuf helloSyncBuf) {
		this.syncHelloOk = true;
		this.helloSyncBuf = helloSyncBuf;
		System.out.println(ConsoleColors.success("### HelloSyncBuf erfolgreich gesetzt."));
	}

	public void parsePlayerInfo(ByteBuf playerInfoBuf) {
		System.out.println(ConsoleColors.info("### Beginne mit der Verarbeitung von PlayerInfo..."));
		this.sbrwParser.parseInputData(playerInfoBuf);
	}

	public short getCliTime() {
		return this.cliTime;
	}

	public boolean isSyncHelloOk() {
		return this.syncHelloOk;
	}

	public boolean isPlayerInfoOk() {
		return this.sbrwParser.isOk();
	}

	public SbrwParserEcho getSbrwParser() {
		return this.sbrwParser;
	}

	public static void main(String[] args) {
		MainRacePacket mainPacket = new MainRacePacket(1, 2, (short) 0);

		System.out.println(ConsoleColors.info("### Sende SrvPktHello..."));
		SrvPkt srvPkt = new SrvPkt();
		SrvPktHello srvPktHello = new SrvPktHello();
		srvPkt.setSrvPkt(srvPktHello);
		System.out.println(UdpDebug.byteArrayToHexString(mainPacket.getPacket(srvPkt)));

		System.out.println(ConsoleColors.info("Sende SrvPktSyncStart..."));
		SrvPktMainSync srvPktMainSync = new SrvPktMainSync();
		SrvPktSyncStart srvPktSyncStart = new SrvPktSyncStart();
		srvPktSyncStart.setPlayerIdx((byte) 102);
		srvPktSyncStart.setSessionId(99999);
		srvPktMainSync.setPktSync(srvPktSyncStart);
		srvPkt.setSrvPkt(srvPktMainSync);
		System.out.println(UdpDebug.byteArrayToHexString(mainPacket.getPacket(srvPkt)));

		System.out.println(ConsoleColors.info("Sende SrvPktSync..."));
		SrvPktSync srvPktSync = new SrvPktSync();
		srvPktMainSync.setPktSync(srvPktSync);
		System.out.println(UdpDebug.byteArrayToHexString(mainPacket.getPacket(srvPkt)));

		System.out.println(ConsoleColors.info("Sende SrvPktSyncKeepAlive..."));
		SrvPktSyncKeepAlive srvPktSyncKeepAlive = new SrvPktSyncKeepAlive();
		srvPktMainSync.setPktSync(srvPktSyncKeepAlive);
		System.out.println(UdpDebug.byteArrayToHexString(mainPacket.getPacket(srvPkt)));

		System.out.println(ConsoleColors.info("Sende PlayerMainPkt..."));
		PlayerMainPkt playerMainPkt = new PlayerMainPkt();
		PlayerInfo playerInfo = new PlayerInfo();
		playerMainPkt.setPlayerInfo(playerInfo);
		System.out.println(UdpDebug.byteArrayToHexString(mainPacket.getPacket(playerMainPkt)));

		System.out.println(ConsoleColors.info("Aktiviere unbekannten Zähler und sende erneut..."));
		playerMainPkt.enableUnknownCounter();
		System.out.println(UdpDebug.byteArrayToHexString(mainPacket.getPacket(playerMainPkt)));

		System.out.println(ConsoleColors.info("Deaktiviere alle Infos und sende erneut..."));
		playerMainPkt.disableAllInfo();
		System.out.println(UdpDebug.byteArrayToHexString(mainPacket.getPacket(playerMainPkt)));
	}
}
