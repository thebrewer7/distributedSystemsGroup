package transactionalserver.account;

import java.util.UUID;

/**
 *  Represents a bank account. Accounts can have negative balances.
 * 
 *  It is handled by the AccountManager class
 */

public class Account{
    private int balance;
    private int accountID;

    public Account(int balance,int accountID) {
        this.balance = balance;
        this.accountID = accountID;   
    }

    public int getAccountID(){
        return accountID;
    }

    public void increaseBalance(int amount){
        balance += amount;
    }

    public int getBalance(){
        return balance;
    }

}
