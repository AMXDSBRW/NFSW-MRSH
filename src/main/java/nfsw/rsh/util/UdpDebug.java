package nfsw.rsh.util;

public class UdpDebug {
	public static byte[] hexStringToByteArray(String s) {
		System.out.println(ConsoleColors.info("UdpDebug: Konvertiere Hex-String zu Byte-Array: " + s));

		s = s.replace(":", "");
		int len = s.length();
		byte[] data = new byte[len / 2];

		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}

		System.out.println(ConsoleColors.success("UdpDebug: Konvertierung abgeschlossen. Byte-Array Länge: " + data.length));
		return data;
	}

	public static String byteArrayToHexString(byte[] b) {
		System.out.println(ConsoleColors.info("UdpDebug: Konvertiere Byte-Array zu Hex-String. Länge des Arrays: " + b.length));

		StringBuilder data = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			data.append(Integer.toHexString(b[i] >> 4 & 0xF));
			data.append(Integer.toHexString(b[i] & 0xF));
			data.append(":");
		}
		String hexString = data.toString();

		System.out.println(ConsoleColors.success("UdpDebug: Konvertierung abgeschlossen. Hex-String: " + hexString));
		return hexString;
	}
}
