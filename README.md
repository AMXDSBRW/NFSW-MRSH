# Need For Speed: World Race Synchronization Host (NFSW-RSH)


## 📖 Projektübersicht

**Need For Speed: World Race Synchronization Host (NFSW-RSH)** ist ein Serverprogramm, das für die Synchronisation von Multiplayer-Daten für *Need For Speed: World* entwickelt wurde. Es dient als zentraler Knotenpunkt zur Verwaltung von Verbindungen und synchronisiert Renninformationen in Echtzeit zwischen Clients.

Das Projekt wurde entwickelt, um die Synchronisation von Spielerdaten (wie Positionen, Geschwindigkeiten und Events) in einem Rennumfeld zu erleichtern. Es nutzt das **UDP-Protokoll** für schnelle und effiziente Datenübertragung und bietet eine robuste Architektur für Server-Management.

---

## ✨ Funktionen

### Hauptfunktionen:
- **Netty-Server:** Aufbau eines UDP-Servers für effiziente Netzwerkkommunikation.
- **Automatische Initialisierung:** Erstellt automatisch notwendige Ordner und Dateien.
- **Synchronisation der Spieler:** Verarbeitet Spielerinformationen und synchronisiert sie in Echtzeit.
- **Authentifizierung:** Überprüfung von Spielertickets und Schlüsseldateien.
- **Statusmeldungen:** Farbige Konsolenmeldungen zur einfachen Identifikation von Problemen oder Erfolgen.

### Logging-Funktionen:
Die Software gibt Meldungen in verschiedenen Kategorien aus, um Nutzern sofortige Einblicke in den Status zu geben:
- **Erfolgsnachrichten**: Grüne Meldungen für erfolgreiche Operationen.
- **Warnungen**: Gelbe Meldungen für nicht kritische Probleme.
- **Informationen**: Blaue Meldungen für allgemeine Informationen.
- **Fehler**: Rote und hervorgehobene Meldungen für kritische Probleme.

---

## 📂 Aufbau und Ordnerstruktur

Beim Start des Programms wird automatisch ein Ordner mit dem Namen `keys` erstellt, falls dieser nicht bereits vorhanden ist. Dieser Ordner dient als Speicherplatz für Schlüssel oder andere benötigte Dateien, die für die Synchronisation verwendet werden.

---

## 📜 Ablauf des Programms

### Allgemeiner Ablauf:
1. **Begrüßung und Initialisierung**:
   - Beim Start zeigt die Software ein ASCII-Logo und eine Willkommensnachricht an, einschließlich der Version und Port-Informationen.

2. **Erstellung von Verzeichnissen**:
   - Überprüft, ob der `keys`-Ordner vorhanden ist. 
   - Erstellt ihn bei Bedarf.

3. **Serverstart**:
   - Startet den UDP-Server über den Standardport `9998` (oder einen benutzerdefinierten Port, falls angegeben).
   - Zeigt den Serverstatus in der Konsole an.

4. **Datenverarbeitung**:
   - Pakete werden empfangen, validiert und verarbeitet.
   - Synchronisations- und Statusinformationen werden zwischen Spielern und Sitzungen ausgetauscht.

---

## 📋 Nutzung

### Standardausführung

### Anpassung des Ports

Der Standardport ist `9998`. Um ihn zu ändern, übergib einen anderen Port als Argument:

```bash

java -jar nfsw-rsh.jar 8080

```

---

## 🏗️ Architekturübersicht

### Hauptkomponenten und ihre Aufgaben

### **`PlayerInfoBeforeHandler`**

- **Aufgabe:** Verarbeitet Spielerinformationen, die vor dem Start eines Rennens gesendet werden.
- **Wichtigste Methoden:**
    - `isPlayerInfoPacket`: Erkennt, ob ein Paket Spielerinformationen enthält.
    - `sendPlayerInfo`: Bereitet Daten zur Synchronisation vor.

### **`PlayerInfoAfterHandler`**

- **Aufgabe:** Synchronisiert Spielerinformationen während eines Rennens (z. B. Positionen und Zustände).
- **Wichtigste Methoden:**
    - `isPlayerInfoPacket`: Erkennt Pakete mit Spielerstatusinformationen.
    - `sendPlayerInfo`: Extrahiert und verteilt aktuelle Zustandsdaten.

### **`HelloHandler`**

- **Aufgabe:** Initialisiert Spieler beim Beitritt zum Server und validiert Authentifizierung.
- **Wichtigste Methoden:**
    - `isHelloPacket`: Prüft, ob ein Paket ein Begrüßungspaket ist.
    - `isTicketOk`: Validiert das Authentifizierungsticket.
    - `answer`: Sendet eine Bestätigung an den Spieler.

### **`SrvPktSyncStart`**

- **Allgemeine Beschreibung:** Repräsentiert ein Paket, das im Kontext eines Mehrspieler-Spiels verwendet wird und implementiert das `IPkt`Interface für Netzwerkprotokolle.
- **Felder:**
    - `playerIdx`: Ein byte, das den Index des Spielers darstellt.
    - `sessionId`: Ein int, das die Sitzungs-ID darstellt.
    - `maxPlayers`: Ein int, das die maximale Anzahl von Spielern in der Sitzung angibt.
    - `slotsBits`: Ein int, das eine Bitmaske für die belegten Spielerplätze darstellt.
- **Wichtigste Methoden:**
    - `getPacket()`: Erstellt ein Byte-Paket für die Sitzung und den Spieler.
    - `generateSlotsBits(int maxPlayers)`: Erstellt eine Bitmaske für die belegten Spielerplätze.

### **`SbrwParser`**

- **Allgemeine Beschreibung:** Verarbeitet Datenpakete und extrahiert Header, CRC, Channel Info, Player Info und Car State.
- **Wichtigste Methoden:**
    - `parseInputData(byte[] inputData)`: Parsen und Verarbeiten von Eingangsdaten.
    - `getPlayerPacket(long timeDiff)`: Erzeugt ein Paket für den Spieler.
    - `getStatePosPacket(long timeDiff)`: Erzeugt ein Paket für den Fahrzeugstatus.

---

## 🛠️ Erweiterungen und Anpassungen

Die Software kann leicht erweitert werden, um weitere Funktionen für NFS:W bereitzustellen:

1. **Erweiterte Authentifizierung**: Integration von OAuth oder anderen Mechanismen.
2. **Erweiterte Protokollierung**: Logs in Dateien speichern oder an externe Überwachungssysteme senden.
3. **Verbesserte Synchronisation**: Zusätzliche Metriken für bessere Leistung.

---

## 📝 Lizenz

Dieses Projekt steht unter der **Lizenz**. Details findest du in der Datei `LICENSE`.

---

## 🤝 Beitrag leisten

1. **Fork** das Repository.
2. **Erstelle** einen neuen Branch:
    
    ```bash

    Code kopierengit checkout -b feature/neue-funktion
    
    ```
    
3. **Commit** deine Änderungen:
    
    ```bash

    Code kopierengit commit -m "Neue Funktion hinzugefügt"
    
    ```
    
4. **Sende einen Pull-Request** für die Integration deiner Änderungen.

---

## 🛠️ Fehlerbehebung

### Allgemeine Probleme

- **Ungültiger Port:** Stelle sicher, dass der Port eine gültige Zahl ist.
- **Fehlende Berechtigungen:** Überprüfe die Schreibrechte für den Ordner `keys`.
- **Netzwerkprobleme:** Vergewissere dich, dass der Serverport nicht durch eine Firewall blockiert ist.

---

Wenn du Fragen oder Probleme hast, erstelle ein Issue auf GitHub oder kontaktiere uns direkt. 🚗💨
