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
    OPEN_TRANSACTION,
    CLOSE_TRANSACTION,
    READ_REQUEST,
    WRITE_REQUEST
}
