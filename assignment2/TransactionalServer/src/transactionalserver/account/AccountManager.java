package transactionalserver.account;

import java.util.ArrayList;

/**
 *  Handles the accounts and initializing them.
 * 
 *  It implements the high-level operations & will have a list of all the 
 *  accounts. Without all of the network stuff, the AccountManager would be
 *  talking directly to the client.
 * 
 *  This class will call the LockManager to acquire locks for reads & writes
 */
public class AccountManager {
    
    private ArrayList<Account> accounts;

 /**
     * Constructor
     */
    public AccountManager(int numAccounts, int initialBalance){
        //create a new account
        accounts = new ArrayList <Account>();
        
        //initialize all of the accounts with the initial balance given
        for(int i=0; i<numAccounts; i++){
            accounts.add(new Account(initialBalance, i));
        }
        
    }
    /*
    get the account balance of accounts
    @param account id and transaction id
    */
    public int getBalance(int transactionID, int accountID){
        //balance is the specific account ID's balance
        int balance = accounts.get(accountID).getBalance();
        
        //call lock manager for READ and set a lock giving the current account id, transaction id
        
        return balance;
    }
    
    /*
    set the account balance
    @param account id, transaction id, amount being added to balance
    */
    public int setBalance(){
        //add the tranfer amount to the current balance
        
        //call lock manager for WRITE and set a lock giving the current account id, transaction id
        
//        return balance;
        return 0;
    }
    
}
