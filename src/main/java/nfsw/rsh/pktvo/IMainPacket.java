package nfsw.rsh.pktvo;

public interface IMainPacket {
	Integer getMaxUsers();
  
	byte[] getPacket(IPkt paramIPkt);
  
	Integer getSessionId();
}
