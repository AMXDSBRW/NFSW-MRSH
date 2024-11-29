package nfsw.rsh.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MemoryUsage implements Runnable {
	private String filename;

	public MemoryUsage(Integer port) {
		this.filename = "memorylog-" + port.toString() + ".log";
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleWithFixedDelay(this, 0L, 1L, TimeUnit.HOURS);
		System.out.println(ConsoleColors.info("MemoryUsage: Speicherprotokollierung gestartet. Daten werden in die Datei: " + filename));
	}

	public void run() {
		Long freeMemory = Long.valueOf(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
		freeMemory = Long.valueOf(freeMemory.longValue() / 1024L / 1024L);
		System.out.println(ConsoleColors.info("MemoryUsage: Aktueller Speicherverbrauch: " + freeMemory + " MB"));
		writeToFile(freeMemory.toString() + "\n");
	}

	private void writeToFile(String data) {
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			File file = new File(this.filename);
			if (!file.exists()) {
				file.createNewFile();
				System.out.println(ConsoleColors.success("MemoryUsage: Neue Datei erstellt: " + file.getAbsolutePath()));
			}
			fw = new FileWriter(file.getAbsoluteFile(), true);
			bw = new BufferedWriter(fw);
			bw.write(data);
			System.out.println(ConsoleColors.success("MemoryUsage: Daten in die Datei geschrieben: " + data.trim()));
		} catch (IOException e) {
			System.err.println(ConsoleColors.error("MemoryUsage: Fehler beim Schreiben in die Datei: " + e.getMessage()));
			e.printStackTrace();
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (IOException ex) {
				System.err.println(ConsoleColors.error("MemoryUsage: Fehler beim Schließen der Datei: " + ex.getMessage()));
				ex.printStackTrace();
			}
		}
	}
}
