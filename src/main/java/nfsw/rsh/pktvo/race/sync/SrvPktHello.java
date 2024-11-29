package nfsw.rsh.pktvo.race.sync;

import nfsw.rsh.util.ConsoleColors;

public class SrvPktHello implements ISrvPkt {

	public byte getSrvPktType() {
		byte srvPktType = 1;
		System.out.println(ConsoleColors.info("srvPktType zurückgegeben: " + srvPktType));
		return srvPktType;
	}

	public byte[] getPacket() {
		byte[] packet = new byte[0];
		System.out.println(ConsoleColors.info("Paket zurückgegeben: " + packet.length + " Bytes"));
		return packet;
	}
}
