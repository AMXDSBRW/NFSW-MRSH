package nfsw.rsh.pktvo.race.sync;

import nfsw.rsh.pktvo.IPkt;
import nfsw.rsh.util.ConsoleColors;
import nfsw.rsh.pktvo.race.ByteBufUtil;
import java.nio.ByteBuffer;

public class SrvPktSyncKeepAlive implements IPkt {

	@Override
	public byte[] getPacket() {
		ByteBuffer bytebuff = ByteBuffer.allocate(1);

		byte keepAliveByte = (byte) -1;

		System.out.println(ConsoleColors.info("KeepAlive Byte wird hinzugefügt: " + keepAliveByte));

		bytebuff.put(keepAliveByte);

		byte[] packet = ByteBufUtil.getByteBuffArray(bytebuff);

		System.out.println(ConsoleColors.info("Paketgröße zurückgegeben: " + packet.length + " Bytes"));

		return packet;
	}
}
