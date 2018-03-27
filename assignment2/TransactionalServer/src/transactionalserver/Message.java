package transactionalserver;

/**
 * Represents a message that would check the request type
 * and also the account number
 */
public class Message {
    private String messageType;
    private int accountNumber;
    private int amount;

    /**
     *  Constructor for read request
     */
    public Message(String messageType, int accountNumber){
      this.messageType = messageType;
      this.accountNumber = accountNumber;
    }

    /**
     *  Constructor for write request
     */
    public Message(String messageType, int accountNumber, int amount){
      this.messageType = messageType;
      this.accountNumber = accountNumber;
      this.amount = amount;
    }

    
    /**
     * returns the type of the message
     */
    public String getType(String messageType){
      return messageType;
    }

    /**
     * returns the account number of the message for a read request
     * returns the amount and account number for a write request
     */
    public int[] getContent(){
      contentArray[] = [null, null];
      if(messageType == "READ_REQUEST"){
          contentArray[0] = accountNumber;
      }
      else if(messageType == "WRITE_REQUEST"){
          contentArray[0] = accountNumber;
          contentArray[1] = amount;
      }
      else{
           contentArray[0] = accountNumber;
      }
    }
    
    
}
