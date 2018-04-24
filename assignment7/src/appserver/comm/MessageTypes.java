
package appserver.comm;

/**
 * Interface [MessageTypes] Defines the different message types used in the application.
 * Any entity using objects of class Message needs to implement this interface.
 * 
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public interface MessageTypes {
    public static int JOB_REQUEST = 1;
    public static int REGISTER_SATELLITE = 3;
}
