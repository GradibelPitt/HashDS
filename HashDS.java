import java.util.ArrayList; 

public class HashDS<T> implements SequenceInterface<T> {
    private Node<T> head;
    private Node<T> tail;
    private ArrayList<HashEntry<T>> hashTable;
    private int size;
    private int capacity;   

    //class Node for LinkedList
    private class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private static class HashEntry<T> {
        T item;
        int frequency;

        HashEntry(T item) {
            this.item = item;
            this.frequency = 1;
        }

        public T getItem() {
            return item;
        }

        public int getFrequency() {
            return frequency;
        }

        public void incrementFrequency() {
            frequency++;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }
    }    


    //default consturctor 
    public HashDS() {
        this.capacity = 16;
        this.size = 0;
        this.head = null;
        this.tail = null;
        this.hashTable = new ArrayList<>(capacity);

        for (int i = 0; i < capacity; i++) {
            hashTable.add(null);
        }
    }

    //deep copy constructor
    public HashDS(HashDS<T> other) {
        this.capacity = other.capacity;

        this.size = other.size;
        this.head = null;
        this.tail = null;

        this.hashTable = new ArrayList<>(capacity);

        for (int i = 0; i < capacity; i++) {
            hashTable.add(null);
        }

        for (int i = 0; i < capacity; i++) {
            if (other.hashTable.get(i) != null) {
                T item = other.hashTable.get(i).getItem();
                int frequency = other.hashTable.get(i).getFrequency();
                hashTable.set(i, new HashEntry<>(item));
                hashTable.get(i).setFrequency(frequency);
            } 
        }

        Node<T> current = other.head;
        while (current != null) {
            this.append(current.data);
            current = current.next;
        }
    }

    @Override
    public int size() {
        return size;
    }

    private void resize() {
        capacity *= 2;
        ArrayList<HashEntry<T>> newHashTable = new ArrayList<>(capacity);

        for (int i = 0; i < capacity; i++) {
            newHashTable.add(null);
        }

        for (HashEntry<T> entry : hashTable) {
            if (entry != null) {
                int index = hash(entry.getItem());
                while (newHashTable.get(index) != null) {
                    index = (index + 1) % capacity;
                }
                newHashTable.set(index, entry);
            }
        }

        hashTable = newHashTable;
    }

    
    @Override
    public void append(T item) {
        Node<T> newNode = new Node<>(item);
        if (tail == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        size++;
        updateHashTable(item);

        if ((double) size / capacity >= 0.5) {
            resize();
        }
    }

    @Override
    public void prefix(T item) {
        Node<T> newNode = new Node<>(item);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head = newNode;
        }
        updateHashTable(item);

        if ((double) size / capacity >= 0.5) {
            resize();
        }
    }

    private void updateHashTable(T item) {
        int index = hash(item);
        while (hashTable.get(index) != null) {
            if (hashTable.get(index).getItem().equals(item)) {
                hashTable.get(index).incrementFrequency();
                return;
            }
            index = (index + 1) % capacity;
        }
        hashTable.set(index, new HashEntry<>(item));
    }

    @Override
    public T itemAt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }

        Node<T> current = head;
        for (int i = 0; i < index; i++){
            if (current == null){
                throw new IndexOutOfBoundsException("Index " + index + " is out of bounds. Size of list: " + size);
            }
            current = current.next;
        }
        return current.data;
    }

    @Override
    public T first() {
        if (size == 0 || head == null) {
            return null;
        }
        return head.data;
    }

    @Override
    public T last() {
        if (size == 0 || tail == null) {
            return null;
        }
        return tail.data;
    }
    
    
    @Override
    public boolean isEmpty() {
        return size == 0 && head == null;
    }


    @Override
    public int getFrequencyOf(T item) {
        int index = hash(item);
        while (hashTable.get(index) != null) {
            if (hashTable.get(index).getItem().equals(item)) {
                return hashTable.get(index).getFrequency();
            }
            index = (index + 1) % capacity;
        }
        return 0;
    }

    @Override
    public void clear() {
        head = null;
        tail = null;
        size = 0;
        hashTable = new ArrayList<>(capacity);

        for (int i = 0; i < capacity; i++) {
            hashTable.add(null);
        }
    }

    @Override
    public T deleteHead() {
        if (isEmpty() || head == null) {
            throw new EmptySequenceException("Cannot delete head: Sequence is empty.");
        }

        T data = head.data;
        head = head.next;
        if (head == null) {
            tail = null;
        }
        if (size > 0){
            size--;
        }
        return data;
    }

    @Override
    public T deleteTail() {
        if (isEmpty() || tail == null) {
            throw new EmptySequenceException("Cannot delete tail: Sequence is empty.");
        }

        T data = tail.data;
        if (head == tail) {
            head = null;
            tail = null;
        } else {
            Node<T> current = head;
            while (current.next != tail) {
                current = current.next;
            }
            current.next = null;
            tail = current;
        }
        if (size > 0){
            size--;
        }
        return data;
    }

    private int hash(T item) {
        return (item.hashCode() & 0x7fffffff) % capacity; //this prevents from negative value that causes overflow
    }

    @Override
    public boolean remove(T item) {
        boolean removed = false;
        int index = hash(item);

        //remove all matched item from hash table
        while (hashTable.get(index) != null) {
            if (hashTable.get(index).getItem().equals(item)) {
                hashTable.set(index, null);
                removed = true;
            }
            index = (index + 1) % capacity;
        }

        //EXTRA CREDIT: remove all matched item from linked list
        while (head != null && head.data.equals(item)) {
            deleteHead();
            removed = true;
            }

        
        Node<T> current = head;
        Node<T> previous = null;

                while (current != null) {
                    if (current.data.equals(item)) {
                        if (previous != null) { 
                           previous.next = current.next;
                        }
                        if (current == tail) {
                            tail = previous;
                        }
                        size--;
                        removed = true;
                    } else {
                        previous = current;
                    }
                    current = current.next;
        }

        return removed;
    }



    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        Node<T> current = head;
        while (current != null) {
            result.append(current.data);
            current = current.next;
        }
        return result.toString();
    }
}
