package nfsw.rsh.pktvo.race.sync;

import nfsw.rsh.util.ConsoleColors;
import nfsw.rsh.pktvo.IPkt;
import nfsw.rsh.pktvo.race.ByteBufUtil;
import java.nio.ByteBuffer;
import java.util.Date;

public class SrvPkt implements IPkt {
	private short counter = 0;
	private short helloCliTime = 0;
	private ISrvPkt srvPkt;

	public void setSrvPkt(ISrvPkt srvPkt) {
		if (srvPkt == null) {
			System.err.println(ConsoleColors.error("srvPkt darf nicht null sein."));
			throw new IllegalArgumentException("srvPkt darf nicht null sein.");
		}
		this.srvPkt = srvPkt;
		System.out.println(ConsoleColors.success("srvPkt erfolgreich gesetzt: " + srvPkt));
	}

	public short getHelloCliTime() {
		System.out.println(ConsoleColors.info("Rückgabe von helloCliTime: " + this.helloCliTime));
		return this.helloCliTime;
	}

	public void setHelloCliTime(short helloCliTime) {
		if (helloCliTime < 0) {
			System.err.println(ConsoleColors.error("helloCliTime darf nicht negativ sein."));
			throw new IllegalArgumentException("helloCliTime darf nicht negativ sein.");
		}
		this.helloCliTime = helloCliTime;
		System.out.println(ConsoleColors.success("helloCliTime erfolgreich gesetzt: " + helloCliTime));
	}

	public byte[] getPacket() {
		System.out.println(ConsoleColors.info("Erstelle Paket..."));
		ByteBuffer bytebuff = ByteBuffer.allocate(128);
		bytebuff.put((byte)0);

		this.counter = (short)(this.counter + 1);
		System.out.println(ConsoleColors.info("Counter erhöht: " + this.counter));

		bytebuff.putShort(this.counter);
		bytebuff.put(this.srvPkt.getSrvPktType());

		short currentTime = getTime();
		System.out.println(ConsoleColors.info("Aktuelle Zeit (Millisekunden): " + currentTime));
		bytebuff.putShort(currentTime);

		bytebuff.putShort(getHelloCliTime());
		bytebuff.put(this.srvPkt.getPacket());

		System.out.println(ConsoleColors.success("Paketgröße: " + bytebuff.position() + " Bytes"));
		return ByteBufUtil.getByteBuffArray(bytebuff);
	}

	public short getTime() {
		short time = (short)(int)(new Date()).getTime();
		System.out.println(ConsoleColors.info("Zeit zurückgegeben: " + time));
		return time;
	}
}
