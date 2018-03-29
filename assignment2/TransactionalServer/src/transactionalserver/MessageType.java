/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package transactionalserver;

/**
 *
 * @author Chandler
 */
public enum MessageType {
    OPEN_TRANSACTION (0),
    CLOSE_TRANSACTION (1),
    READ_REQUEST (2),
    WRITE_REQUEST (3);
    
    private int value;
    
    MessageType(int value){
        this.value = value;
    }
}
