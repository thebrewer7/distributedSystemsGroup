package transactionalserver.account;

import java.util.ArrayList;
import transactionalserver.lock.LockManager;
import transactionalserver.lock.LockType;
import transactionalserver.transaction.Transaction;

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
    private LockManager lockManager;
    private ArrayList<Account> accounts;
    int numberAccounts;
    int initialBalance;
    /*
      Constructor
     */
    public AccountManager(int numberAccounts, int initialBalance, 
            LockManager lockManager){
        //create all of the accounts
        accounts = new ArrayList();
       for(int i=0; i<numberAccounts; i++){
           accounts.add(new Account(initialBalance));
       }
       
       this.lockManager = lockManager;
    }
    
    /*
    * Returns all accounts
    */
     public ArrayList<Account> getAccounts(){
        return accounts;
    }
     
     /**
      * Retrieves a single account from the account list
      * @param accountID    the ID of the account to get
      * @return             the account to return
      */
     public Account getAccount(int accountID){
         return accounts.get(accountID);
     }
     
     /*
     * Read action for an account
     */

     /**
      * Read balance for an account
      * 
      * @param account      the account to read the balance for
      * @param transaction  the transaction that has the lock on the account
      * @return             the balance of the account
      */
    public int read(Account account, Transaction transaction){
        lockManager.lock(account, transaction, LockType.READ);
        return account.getBalance();
    }
    
    /*
    Write action for an account
    */
    public int write(Account account, Transaction transaction,int balance){
        lockManager.lock(account, transaction, LockType.WRITE);
        account.setBalance(balance);
        return balance;     
    }  
}
