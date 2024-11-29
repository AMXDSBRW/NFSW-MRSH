package nfsw.rsh.pktvo.race.sync;

import nfsw.rsh.util.ConsoleColors;
import nfsw.rsh.pktvo.IPkt;
import nfsw.rsh.pktvo.race.ByteBufUtil;
import java.nio.ByteBuffer;

public class SrvPktSyncStart implements IPkt {
	private byte playerIdx = 0;
	private int sessionId;
	private int maxPlayers = 2;

	@Override
	public byte[] getPacket() {
		ByteBuffer bytebuff = ByteBuffer.allocate(20);

		System.out.println(ConsoleColors.info("Erstelle Paket mit den folgenden Werten:"));
		System.out.println(ConsoleColors.info("Byte 1: " + (byte) 0));
		System.out.println(ConsoleColors.info("Byte 2: " + (byte) 6));
		System.out.println(ConsoleColors.info("Player Index: " + this.playerIdx));
		System.out.println(ConsoleColors.info("Session ID: " + this.sessionId));
		System.out.println(ConsoleColors.info("Max Players: " + this.maxPlayers));

		bytebuff.put((byte) 0);
		bytebuff.put((byte) 6);
		bytebuff.put(this.playerIdx);
		bytebuff.putInt(this.sessionId);

		byte maxUsersBits = getMaxUsersBits();
		System.out.println(ConsoleColors.info("Max Users Bits: " + maxUsersBits));
		bytebuff.put(maxUsersBits);

		bytebuff.put((byte) -1);

		byte[] packet = ByteBufUtil.getByteBuffArray(bytebuff);

		System.out.println(ConsoleColors.info("Paketgröße zurückgegeben: " + packet.length + " Bytes"));

		return packet;
	}

	private byte getMaxUsersBits() {
		switch (this.maxPlayers) {
			case 2:
				return 3;
			case 3:
				return 7;
			case 4:
				return 15;
			case 5:
				return 31;
			case 6:
				return 63;
			case 7:
				return Byte.MAX_VALUE;
			case 8:
				return -1;
			default:
				System.out.println(ConsoleColors.error("Ungültige Max Players: " + this.maxPlayers));
				return -1;
		}
	}

	public void setPlayerIdx(byte playerIdx) {
		this.playerIdx = playerIdx;
		System.out.println(ConsoleColors.info("Player Index gesetzt: " + playerIdx));
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
		System.out.println(ConsoleColors.info("Session ID gesetzt: " + sessionId));
	}

	public void setMaxPlayers(byte maxPlayers) {
		this.maxPlayers = maxPlayers;
		System.out.println(ConsoleColors.info("Max Players gesetzt: " + maxPlayers));
	}

	private static byte generateSlotsBits(int numberOfPlayers) {
		byte res = 0;
		for (int i = 0; i < numberOfPlayers; i++) {
			res = (byte) (res | 1 << i);
		}
		return res;
	}
}
