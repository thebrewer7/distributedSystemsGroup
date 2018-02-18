/*Assignment 3 - Distributed Chat
Logan Brewer, Chandler Hayes, Kelli Ruddy
CircularLinkedList
2/16/18
*/

class linkedList
{
    // size of list,head and tail nodes in list
    int listSize;
    Node head;
    Node tail;

    public linkedList()
    {
        //initialize list
        head = null;
        tail = null;
        listSize = 0;
    }

    //Get the size of the list at anytime
    public int getSize()
    {
        return listSize;
    }

    //Place a new node at the head of the list
    public void insertAtStart(int val)
    {
        Node node = new Node(val,null);
        node.setNext(head);
        //if there is nothing in the list - place new node as head
        if(head == null)
        {
            head = node;
            tail = head;
            node.setNext(head);
        }
        else
        {
            tail.setNext(node);
            head = node;
        }
        //increment size of the list
        listSize++ ;
    }
    //Insert node at end of list
    public void insertAtEnd(int val)
    {
        Node node = new Node(val,null);
        node.setNext(head);
        //if nothing in list
        if(head == null)
        {
            head = node;
            tail = head;
            node.setNext(head);
        }
        //if the list is not empty
        else
        {
            // place the new node after the tail and make tail be the new node
            tail.setNext(node);
            tail = node;
        }
        //increment the size of the list
        listSize++ ;
    }

    //delete from a specific position(not always going to be deleting from head or tail)
    public void deleteAtPos(int position)
    {
        //temp variable to store the head so we can manipulate it
        Node temp = head;

        //deleting the first node in list
        if(position == 0){
          head = temp.next;
        }
        //find temp.next so we can delete it
        for( int i = 0; i<position -1;i++){
          temp = temp.next;
        }
        //set next to now be two nodes over since its original next is being deleted
        Node next = temp.next.next;
        temp.next = next;

        //decrement size of list
        listSize--;
    }
}
class Node
{
    int value;
    Node next;

    public Node()
    {
        next = null;
        value = 0;
    }

    public Node(int val,Node n)
    {
        value = val;
        next = n;
    }

    public Node getNext()
    {
        return next;
    }

    public int getValue()
    {
        return value;
    }


    public void setNext(Node n)
    {
        next = n;
    }

    public void setValue(int val)
    {
        value = val;
    }
}


public class CircularLinkedList
{
    public static void main(String[] args)
    {
        //create a linked list
        linkedList list = new linkedList();
    }
}
