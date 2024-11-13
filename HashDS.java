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
            if (other.hashTable.get(i) != null) {
                T item = other.hashTable.get(i).getItem();
                int frequency = other.hashTable.get(i).getFrequency();
                hashTable.set(new HashEntry<>(item));
                hashTable.get(i).setFrequency(frequency);
            } else {
                hashTable.set(null);
            }
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
        size++;
    }

    @Override
    public T itemAt(int position) {
        if (position < 0 || position >= size) {
            throw new IndexOutOfBoundsException("Invalid position: " + position);
        }

        Node<T> current = head;
        int currentIndex = 0;

        while (current != null) {
            if (currentIndex == position) {
                return current.data;
            }
            current = current.next;
            currentIndex++;
        }

        return null;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T first() {
        if (isEmpty()) {
            throw new EmptySequenceException("Cannot retrieve first element: Sequence is empty.");
        }
        return head.data;
    }

    @Override
    public T last() {
        if (isEmpty()) {
            throw new EmptySequenceException("Cannot retrieve last element: Sequence is empty.");
        }
        return tail.data;
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
        hashTable.clear();

        for (int i = 0; i < capacity; i++) {
            hashTable.add(null);
        }
    }

    @Override
    public T deleteHead() {
        if (isEmpty()) {
            throw new EmptySequenceException("Cannot delete head: Sequence is empty.");
        }

        T data = head.data;
        head = head.next;
        if (head == null) {
            tail = null;
        }

        size--;
        return data;
    }

    @Override
    public T deleteTail() {
        if (isEmpty()) {
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

        size--;
        return data;
    }

    private int hash(T item) {
        return Math.abs(item.hashCode()) % capacity;
    }

    @Override
    public boolean remove(T item) {
        int index = hash(item);
        while (hashTable.get(index) != null) {
            if (hashTable.get(index).getItem().equals(item)) {
                hashTable.set(index, null);
                size--;
                return true;
            }
            index = (index + 1) % capacity;
        }
        return false;
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
            size++;
        }
        updateHashTable(item);

        if ((double) size / capacity >= 0.5) {
            resize();
        }
    }

    @Override
    public String toString() {
        if (head == null) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        Node<T> current = head;

        while (current != null) {
            result.append(current.data);
            current = current.next;
        }

        return result.toString();
    }
}



