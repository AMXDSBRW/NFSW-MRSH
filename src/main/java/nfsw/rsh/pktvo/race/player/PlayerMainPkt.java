package nfsw.rsh.pktvo.race.player;

import nfsw.rsh.pktvo.IPkt;
import nfsw.rsh.pktvo.race.ByteBufUtil;
import nfsw.rsh.util.ConsoleColors;
import java.nio.ByteBuffer;

public class PlayerMainPkt implements IPkt {
	private byte playerIdx = 0;
	private short counter = 0;
	private short unknownCounter = -1;
	private boolean counterRunning = false;
	private boolean allInfo = true;
	private PlayerInfo playerInfo;

	public byte[] getPacket() {
		ByteBuffer bytebuff = ByteBuffer.allocate(2048);

		System.out.println(ConsoleColors.info("Erstelle PlayerMainPkt mit den folgenden Werten:"));
		System.out.println(ConsoleColors.info("Player Index: " + this.playerIdx));

		this.counter = (short)(this.counter + 1);
		System.out.println(ConsoleColors.info("Counter (vorher): " + (this.counter - 1) + " | Counter (nachher): " + this.counter));
		bytebuff.put((byte)1);
		bytebuff.put(this.playerIdx);
		bytebuff.putShort(this.counter);

		if (this.counterRunning) {
			this.unknownCounter = (short)(this.unknownCounter + 1);
			System.out.println(ConsoleColors.info("Unknown Counter (laufend): " + this.unknownCounter));
		} else {
			System.out.println(ConsoleColors.info("Unknown Counter (statisch): " + this.unknownCounter));
		}
		bytebuff.putShort(this.unknownCounter);

		bytebuff.put((byte)-1);
		bytebuff.put((byte)-1);

		if (this.allInfo) {
			System.out.println(ConsoleColors.success("Alle Informationen werden gesendet."));
			bytebuff.put(this.playerInfo.getPlayerInfo());
		} else {
			System.out.println(ConsoleColors.info("Nur Spielerstatus wird gesendet."));
			bytebuff.put(this.playerInfo.getPlayerStatePos());
		}
		bytebuff.put((byte)-1);

		byte[] packet = ByteBufUtil.getByteBuffArray(bytebuff);
		System.out.println(ConsoleColors.success("Paketgröße zurückgegeben: " + packet.length + " Bytes"));
		return packet;
	}

	public void enableUnknownCounter() {
		this.counterRunning = true;
		this.unknownCounter = 1;
		System.out.println(ConsoleColors.success("Unknown Counter aktiviert: " + this.unknownCounter));
	}

	public void enableAllInfo() {
		this.allInfo = true;
		System.out.println(ConsoleColors.success("Alle Informationen aktiviert."));
	}

	public void disableAllInfo() {
		this.allInfo = false;
		System.out.println(ConsoleColors.success("Alle Informationen deaktiviert."));
	}

	public void setPlayerInfo(PlayerInfo playerInfo) {
		this.playerInfo = playerInfo;
		System.out.println(ConsoleColors.success("Player Info gesetzt."));
	}

	public void setPlayerIdx(byte playerIdx) {
		this.playerIdx = playerIdx;
		System.out.println(ConsoleColors.success("Player Index gesetzt: " + playerIdx));
	}
}
