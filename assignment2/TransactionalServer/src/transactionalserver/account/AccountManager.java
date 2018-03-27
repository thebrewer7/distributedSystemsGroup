package transactionalserver.account;

import java.util.ArrayList;
import transactionalserver.client.TransactionClient;
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
            LockManager lockMaanger){
        //create all of the accounts
        accounts = new ArrayList();
       for(int i=0; i<numberAccounts; i++){
           accounts.add(new Account(initialBalance));
       }
       
       this.lockManager = lockManager;
    }
    
    /*
    Returns all accounts
    */
     public ArrayList<Account> getAccounts(){
        return accounts;
    }
     
     public Account getAccount(int accountID){
         return accounts.get(accountID);
     }
     
     /*
     Read action for an account
     */
     
    public int read(Account account, Transaction transaction){
        lockManager.lock(transaction, account, LockType.READ);
                return account.getBalance();
    }
    
    /*
    Write action for an account
    */
    public int write(Account account, Transaction transaction,int balance){
        lockManager.lock(transaction, account, LockType.WRITE);
        account.setBalance(balance);
        return balance;     
    }  
}
