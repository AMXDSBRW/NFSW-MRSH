package nfsw.rsh.pktvo.race.sync;

import nfsw.rsh.util.ConsoleColors;
import nfsw.rsh.pktvo.IPkt;
import nfsw.rsh.pktvo.race.ByteBufUtil;
import java.nio.ByteBuffer;

public class SrvPktSync implements IPkt {

	@Override
	public byte[] getPacket() {
		ByteBuffer bytebuff = ByteBuffer.allocate(20);

		byte byte1 = (byte) 1;
		byte byte2 = (byte) 3;
		byte byte3 = (byte) 0;
		byte byte4 = (byte) 64;
		byte byte5 = (byte) -73;
		byte byte6 = (byte) -1;

		System.out.println(ConsoleColors.info("Bytes werden hinzugefügt:"));
		logByte("Byte 1", byte1);
		logByte("Byte 2", byte2);
		logByte("Byte 3", byte3);
		logByte("Byte 4", byte4);
		logByte("Byte 5", byte5);
		logByte("Byte 6", byte6);

		bytebuff.put(byte1);
		bytebuff.put(byte2);
		bytebuff.put(byte3);
		bytebuff.put(byte4);
		bytebuff.put(byte5);
		bytebuff.put(byte6);

		byte[] packet = ByteBufUtil.getByteBuffArray(bytebuff);

		System.out.println(ConsoleColors.info("Paketgröße zurückgegeben: " + packet.length + " Bytes"));

		return packet;
	}

	private void logByte(String byteName, byte byteValue) {
		System.out.println(ConsoleColors.info(byteName + ": " + byteValue));
	}
}
