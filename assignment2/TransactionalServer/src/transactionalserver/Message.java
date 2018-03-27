package transactionalserver.message;

/**
 * Represents a message that would check the request type
 * and also the account number
 */
public class Message {
    private String messageType;
    private int accountNumber;

    /**
     *  Constructor
     */
    public Message(String messageType, int accountNumber){
      this.messageType = messageType;
      this.accountNumber = accountNumber;
    }

    /**
     * returns the type of the message
     */
    public String getType(String messageType){
      return messageType;
    }

    /**
     * returns the account number of the message
     */
    public int getContent(){
      return accountNumber;
    }
}
