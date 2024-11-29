package nfsw.rsh.pktvo.race.player;

import nfsw.rsh.util.ConsoleColors;

public class PlayerInfo {
	private byte[] playerInfo;
	private byte[] playerStatePos;

	public byte[] getPlayerInfo() {
		if (this.playerInfo == null) {
			System.out.println(ConsoleColors.error("Player Info ist null!"));
		} else {
			System.out.println(ConsoleColors.info("Player Info abgerufen: " + byteArrayToString(this.playerInfo)));
		}
		return this.playerInfo;
	}

	public void setPlayerInfo(byte[] playerInfo) {
		if (playerInfo == null) {
			System.out.println(ConsoleColors.error("Player Info ist null und kann nicht gesetzt werden!"));
		} else {
			this.playerInfo = playerInfo;
			System.out.println(ConsoleColors.success("Player Info gesetzt: " + byteArrayToString(playerInfo)));
		}
	}

	public byte[] getPlayerStatePos() {
		if (this.playerStatePos == null) {
			System.out.println(ConsoleColors.error("Player State Position ist null!"));
		} else {
			System.out.println(ConsoleColors.info("Player State Position abgerufen: " + byteArrayToString(this.playerStatePos)));
		}
		return this.playerStatePos;
	}

	public void setPlayerStatePos(byte[] playerStatePos) {
		if (playerStatePos == null) {
			System.out.println(ConsoleColors.error("Player State Position ist null und kann nicht gesetzt werden!"));
		} else {
			this.playerStatePos = playerStatePos;
			System.out.println(ConsoleColors.success("Player State Position gesetzt: " + byteArrayToString(playerStatePos)));
		}
	}

	private String byteArrayToString(byte[] array) {
		if (array == null) {
			return "null";
		}
		StringBuilder sb = new StringBuilder();
		for (byte b : array) {
			sb.append(String.format("0x%02X ", b));
		}
		return sb.toString().trim();
	}
}
