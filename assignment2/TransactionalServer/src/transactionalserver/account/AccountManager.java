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
    int numberAccounts;
    int initialBalance;
    /*
     Constructor
     */
    public AccountManager(int numberAccounts, int initialBalance){
        //create a new account
        accounts = new ArrayList();
    }
    
    /*
    Returns all accounts
    */
     public ArrayList<Account> getAccounts(){
        return accounts;
    }
     
     /*
     Read action for an account
     */
     
    public int read(Account account, TransactionClinet transaction){
        (transactionServer.lockManager).lock(account,transaction, READLOCK);
                return account.getBalance();
    }
    
    /*
    Write action for an account
    */
    public int write(Account account, Transaction transaction,int balance){
        (transactionServer.lockManager).lock(account,transaction, WRITELOCK);
        account.setBalance(balance);
        return balance;     
    } 
}
