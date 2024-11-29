package nfsw.rsh.pktvo.race;

import java.nio.ByteBuffer;

import nfsw.rsh.util.ConsoleColors;

public class ByteBufUtil {

	public static byte[] getByteBuffArray(ByteBuffer bytebuff) {
		if (bytebuff == null) {
			System.err.println(ConsoleColors.error("Fehler: ByteBuffer darf nicht null sein."));
			throw new IllegalArgumentException("ByteBuffer darf nicht null sein.");
		}

		System.out.println(ConsoleColors.info("Konvertiere ByteBuffer zu Byte-Array..."));

		byte[] result = new byte[bytebuff.position()];

		bytebuff.flip();
		bytebuff.get(result);

		System.out.println(ConsoleColors.success("ByteBuffer erfolgreich zu Byte-Array konvertiert. Größe: " + result.length + " Bytes."));
		return result;
	}
}
