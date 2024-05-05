import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GraphRMO extends Remote{
    public String processRequests(String requests) throws RemoteException;
}
