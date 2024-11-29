package nfsw.rsh.pktvo.race.sync;

import nfsw.rsh.util.ConsoleColors;
import nfsw.rsh.pktvo.IPkt;
import nfsw.rsh.pktvo.race.ByteBufUtil;
import java.nio.ByteBuffer;

public class SrvPktMainSync implements ISrvPkt {

	private short counter = 1;
	private IPkt pktSync;

	public byte getSrvPktType() {
		byte srvPktType = 2;
		System.out.println(ConsoleColors.info("srvPktType zurückgegeben: " + srvPktType));
		return srvPktType;
	}

	public byte[] getPacket() {
		ByteBuffer bytebuff = ByteBuffer.allocate(128);
		this.counter = (short)(this.counter + 1);
		System.out.println(ConsoleColors.info("Counter erhöht: " + this.counter));

		bytebuff.putShort(this.counter);
		short walkBitsValue = walkBits();
		System.out.println(ConsoleColors.info("walkBits zurückgegeben: " + walkBitsValue));
		bytebuff.putShort(walkBitsValue);

		if (this.pktSync != null) {
			byte[] pktSyncPacket = this.pktSync.getPacket();
			System.out.println(ConsoleColors.info("pktSync Paketgröße: " + pktSyncPacket.length + " Bytes"));
			bytebuff.put(pktSyncPacket);
		} else {
			System.out.println(ConsoleColors.error("Fehler: pktSync ist null und wurde nicht hinzugefügt."));
		}

		byte[] result = ByteBufUtil.getByteBuffArray(bytebuff);
		System.out.println(ConsoleColors.info("Gesamtpaketgröße: " + result.length + " Bytes"));
		return result;
	}

	private short walkBits() {
		short counterTmp = (short)(this.counter - 1);
		short walkBitsValue;
		switch (counterTmp) {
			case 1: walkBitsValue = Short.MAX_VALUE; break;
			case 2: walkBitsValue = -16385; break;
			case 3: walkBitsValue = -8193; break;
			case 4: walkBitsValue = -4097; break;
			case 5: walkBitsValue = -2049; break;
			case 6: walkBitsValue = -1025; break;
			case 7: walkBitsValue = -513; break;
			case 8: walkBitsValue = -257; break;
			case 9: walkBitsValue = -129; break;
			case 10: walkBitsValue = -65; break;
			case 11: walkBitsValue = -33; break;
			case 12: walkBitsValue = -17; break;
			case 13: walkBitsValue = -9; break;
			case 14: walkBitsValue = -5; break;
			case 15: walkBitsValue = -3; break;
			case 16: walkBitsValue = -2; break;
			default:
				System.out.println(ConsoleColors.error("walkBits: Ungültiger Counter: " + counterTmp));
				walkBitsValue = -1;
		}
		return walkBitsValue;
	}

	public void setPktSync(IPkt pktSync) {
		if (pktSync == null) {
			System.out.println(ConsoleColors.error("pktSync darf nicht null sein."));
		} else {
			this.pktSync = pktSync;
			System.out.println(ConsoleColors.info("pktSync gesetzt: " + pktSync));
		}
	}

	public void setCounter(short counter) {
		if (counter < 0) {
			System.out.println(ConsoleColors.error("Counter darf nicht negativ sein."));
		} else {
			this.counter = counter;
			System.out.println(ConsoleColors.info("Counter gesetzt: " + counter));
		}
	}
}
