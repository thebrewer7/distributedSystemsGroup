package transactionalserver.account;

import java.util.UUID;

/**
 *  Represents a bank account. Accounts can have negative balances.
 * 
 *  It is handled by the AccountManager class
 */

public class Account{
    int balance;

    public Account(int balance) {
        this.balance = balance;
    }
  
    public void setBalance(int amount){
        balance+= amount;   
    }
    
    public int getBalance(){
        return balance;
    }
}
