/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appserver.job.impl;

import appserver.job.Tool;

/**
 *
 * @author Chandler
 */
public class Fibonacci implements Tool{
    public Object go(Object parameters){
        return fibonacciNumber((Integer) parameters);  
    }
    
    private Integer fibonacciNumber(Integer num){
        // check for base case of 0
        if(num == 0){
            return 0;
        }
        // check for base case of 1
        else if(num == 1){
            return 1;
        }
        // use recursion
        else{
            return fibonacciNumber(num - Integer.valueOf(1)) + fibonacciNumber
            (num - Integer.valueOf(2));
        }
    }
}
