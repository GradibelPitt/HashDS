import java.util.NoSuchElementException;

public class HashDS<T> implements SequenceInterface<T> {
    private Node<T> head;
    private Node<T> tail;
    private HashEntry<T, Integer>[] hashTable;
    private int size;
    private int capacity;   

    //class Node for LinkedList
    private static class Node<T> {
        T data;
        Node<T> next;

        Node(T data) {
            this.data = data;
        }
    }

    private static class HashEntry<K, V> {
        K key;
        V value;

        HashEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }
    }


    //default consturctor 
    public HashDS() {
        this.capacity = 16;
        @SuppressWarnings("unchecked")
        this.hashTable = (HashEntry<T>[]) new HashEntry[capacity];
        this.size = 0;
        this.head = null;
        this.tail = null;

    }

    //deep copy constructor
    public HashDS(HashDS<T> other) {
        this.capacity = other.capacity;
        @SuppressWarnings("unchecked")
        this.hashTable = (HashEntry<T, Integer>[]) new HashEntry[capacity];
        this.size = other.size;

        //deep copy LinkedList
        if (other.head != null) {
            this.head = new Node<>(other.head.data);
            Node<T> current = this.head;
            Node<T> otherCurrent = other.head.next;
            while (otherCurrent != null) {
                current.next = new Node<>(otherCurrent.data);
                current = current.next;
                otherCurrent = otherCurrent.next;
            }
            this.tail = current;
        }

        //deep copy hashTable
        for (int i = 0; i < other.capacity; i++) {
            if (other.hashTable[i] != null) {
                T key = other.hashTable[i].getKey();
                Integer value = other.hashTable[i].getValue();
                this.hashTable[i] = new HashEntry<>(key, value);
            }
        }

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
    updateHashTable(item);

    if((double) size / capacity >= 0.5) {
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
        throw new EmptySequenceException("Cannot retrive first element: Sequence is empty.");
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
    while (hashTable[index] != null) {
        if (hashTable[index].key.equals(item)) {
            return hashTable[index].getValue();
        }
        index = (index + 1) % hashTable.length;
    }
    return 0;
}

@Override
public void clear() {
    head = null;
    tail = null;
    size = 0;
    hashTable = new HashEntry[capacity];
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

private void updateHashTable(T item) {
    int index = hash(item);
    while (hashTable[index] != null) {
        if (hashTable[index].getKey().equals(item)) {
            hashTable[index].setValue(hashTable[index].getValue() + 1);
            return;    
        }
        index = (index + 1) % hashTable.length;
    }
    hashTable[index] = new HashEntry<>(item, 1);
    size++;
}

private int hash(T item) {
    return Math.abs(item.hashCode()) % hashTable.length;
}

@Override
public boolean remove(T item) {
    int index = hash(item);
    while (hashTable[index] != null) {
        if (hashTable[index].getKey().equals(item)) {
            hashTable[index] = null;
            size--;
            return true;
        }
        index = (index + 1) % hashTable.length;
    }
    return false;
}

private void resize() {
    capacity *= 2;
    HashEntry<T, Integer>[] newHashTable = (HashEntry<T, Integer>[]) new HashEntry[capacity];

    for (HashEntry<T, Integer> entry : hashTable) {
        if (entry != null) {
            int index = hash(entry.getKey());
            while (newHashTable[index] != null) {
                index = (index + 1) % newHashTable.length;
            }
            newHashTable[index] = entry;
        }
    }

    hashTable = newHashTable;
}

@Override
public String toString() {
   if (head == null) {
    return "";
   }

   String result = "";
   Node<T> current = head;

   while (current != null) {
        result += current.data;
        current = current.next;
    }

    return result;
    }
  
}





