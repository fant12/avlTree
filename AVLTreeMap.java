import java.util.LinkedList;
import java.util.Queue;

/**
 * @class AVLTreeMap class
 * @brief The AVLTreeMap is a self-balancing generic binary search tree map.
 * @description Each node will identifies by a key. 
 * 
 * @author Christian Kusan
 * @param <K> the generic datatype K for the key
 * @param <V> the generic datatype V for the value
 */
public class AVLTreeMap<K extends Comparable<K>, V> {
    
    
    // -- attributes --------------------------
    
    /** The root node. */
    private Node<K, V> _root;
    
    /**
     * @class Node class
     * @brief Describes one single node in generic tree structure.
     * @param <K> the generic datatype K for the key
     * @param <V> the generic datatype V for the value
     */
    private class Node<K extends Comparable<K>, V> implements Comparable<Node<K, V>> {
 
	private K _key;
        private V _value;
        
	private int _depth;
        private int _level;
        
        private Node<K, V> _left;
	private Node<K, V> _right;
	
	public Node(K key, V value) {
            this(key, value, null, null);
	}
 
	public Node(K key, V value, Node<K, V> left, Node<K, V> right) {
	
            _key = key;
            _value = value;
            _left = left;
            _right = right;
	
            if (null == left && null == right)
                _depth = 1;
            else if (null == left)
                _depth = 1 + right._depth;
            else if (null == right)
                _depth = 1 + left._depth;
            else
                _depth = 1 + Math.max(left._depth, right._depth);
	}
 
	@Override
	public int compareTo(Node<K, V> other) {
            return this._key.compareTo(other._key);
	}
 
	@Override
	public String toString() {
            return "Level " + _level + ": " + _key + " - " + _value;
	}
 
    }
    
    
    // -- static ------------------------------
    
    /**
     * @brief Starts the application.
     * @param args the command line arguments
     */
    public static void main(String[] args){
            
        AVLTreeMap<Integer, Integer> intTree = new AVLTreeMap<>();
	
        for (int i = 0; 10 > i; ++i)
            intTree.insertData(i, i*i);

        intTree.traverse(Order.LEVELORDER);
    }
    
    
    // -- initializer -------------------------
    
    /**
     * @brief The default constructor.
     */
    public AVLTreeMap() {
        _root = null;
    }
    
    
    // -- methods -----------------------------
    
    /**
     * @brief Balances a tree.
     * @param node the head of tree
     * @return the current node
     */
    private Node<K, V> balance(Node<K, V> node){
        
        switch (isBalanced(node)) {
            case -1:
                return rotateRight(node);
            case 1:
                return rotateLeft(node);
	    default: 
                return node;
        }
    }
    
    /**
     * @brief Checks whether the tree contains a specific data object.
     * @param key the assigned key of searched value
     * @return true if the tree contains the object, otherwise false
     */
    public boolean contains(K key) {
        
        Node<K, V> node = _root;
		
        while (null != node) 
            if (0 == node._key.compareTo(key))
                return true;
            else if (0 < node._key.compareTo(key))
                node = node._left;
            else
                node = node._right;
        
        return false;
    }
    
    /**
     * @brief Gets the depth of a tree.
     * @param head the root node of the tree
     * @return the tree depth represents by a number
     */
    private int depth(Node<K, V> head) {
    
        return (null == head) ? 0 : head._depth;
    }
    
    /**
     * @brief Gets the key of a value.
     * @param value the value
     * @return the key object or null if not exists
     */
    public K getKey(V value){
        
        if(null != value)
            return searchValue(_root, value)._key;
        
        return null;
    }
    
    /**
     * @brief Gets the value identified by a key.
     * @param key the key
     * @return the value or null if not exists
     */
    public V getValue(K key) {
        
        Node<K, V> node = _root;
		
        while (null != node) 
            if (0 == node._key.compareTo(key))
                return node._value;
            else if (0 < node._key.compareTo(key))
                node = node._left;
            else
                node = node._right;
        
        return null;
    }
    
    /**
     * @brief Inorder traverse.
     * @param node the current node
     */
    private void inOrderTraverse(Node<K, V> node){
        
        if(null == node)
            return;
        
        inOrderTraverse(node._left);
        
        System.out.println(node._key);
        
        inOrderTraverse(node._right); 
    }
    
    /**
     * @brief Inserts data.
     * @param key the key
     * @param value the value
     * @return the current node
     */
    private Node<K, V> insert(K key, V value) {
        
        _root = insert(_root, key, value);
	
        // balancing
        
        switch (isBalanced(_root)) {
            case -1:
                _root = rotateRight(_root);
                break;
            case 1:
                _root = rotateLeft(_root);
                break;
            // default case isn't necessary here    
        }
	
        return _root;
    }
    
    /**
     * @brief Inserts a new node by recursive process.
     * @param node the current node
     * @param key the key
     * @param value the value
     * @return the next current node
     */
    private Node<K, V> insert(Node<K, V> node, K key, V value) {
        
        if (null == node)
            return new Node<>(key, value);
	
        if (0 < node._key.compareTo(key)) 
            node = new Node<>(node._key, node._value, insert(node._left, key, value), node._right);
	else if (0 > node._key.compareTo(key))
            node = new Node<>(node._key, node._value, node._left, insert(node._right, key, value));
	
        // rebalancing
        return balance(node);
    }
    
    /**
     * @brief Inserts data.
     * @param key the key
     * @param value the value
     */
    public void insertData(K key, V value){
        
        insert(key, value);
    }
    
    /**
     * @brief Checks whether a tree is equals in depth 
     * level on both sides.
     * @param head the root node of the tree
     * @return a number that defines whether the tree is balanced or not.<br />
     * <ul>
     *  <li>0: the tree is balanced</li>
     *  <li>-1: the tree is deeper on the left side</li>
     *  <li>1: the tree is deeper on the right side</li>
     * </ul>
     */
    private int isBalanced(Node<K, V> head) {
        
        int leftDepth = depth(head._left);
        int rightDepth = depth(head._right);
	
        if(2 <= leftDepth - rightDepth)
            return -1;
	else if (-2 >= leftDepth - rightDepth)
            return 1;
	
        return 0;
    }

    /**
     * @brief Traverses through the tree by using level order.
     */
    private void levelOrderTraverse() {
        
        _root._level = 0;
        Queue<Node<K, V>> queue = new LinkedList<>();
        queue.add(_root);
	
        while (!queue.isEmpty()) {
	
            Node<K, V> node = queue.poll();
            System.out.println(node);
            int level = node._level;
            
            Node<K, V> leftNode = node._left;
            Node<K, V> rightNode = node._right;
	
            if (null != leftNode) {
                leftNode._level = 1 + level;
                queue.add(leftNode);
            }
	
            if (null != rightNode) {
                rightNode._level = 1 + level;
                queue.add(rightNode);
            }
        }
    }
    
    /**
     * @brief Gets the highest key in tree structure.
     * @return the key object of generic type K if the tree 
     * is not empty, otherwise null
     */
    public K maxKey() {
        
        if(null != _root)
            return maxKey(_root);

        return null;
    }
    
    /**
     * @brief Gets the highest key in tree structure by 
     * recursive process.
     * @return the key object of generic type K
     */
    private K maxKey(Node<K, V> node) {
        
        if(null == node._right)
            return node._key;
        
        return maxKey(node._right);
    }
 
    /**
     * @brief Gets the least key in tree structure.
     * @return the key object of generic type K if the tree 
     * is not empty, otherwise null
     */
    public K minKey() {
        
        if(null != _root)
            return minKey(_root);
        
        return null;
    }
    
    /**
     * @brief Gets the least key in tree structure 
     * by recursive process.
     * @return the key object of generic type K
     */
    private K minKey(Node<K, V> node) {
        
        if(null == node._left)
            return node._key;
        
        return minKey(node._left);
    }
 
    /**
     * @brief Postorder traverse.
     * @param node the current node
     */
    private void postOrderTraverse(Node<K, V> node){
    
        if (null == node)
            return;
            
        postOrderTraverse(node._left);
        postOrderTraverse(node._right); 
        
        System.out.println(node);
    }
    
    /**
     * @brief Preorder traverse
     * @param node the current node
     */
    private void preOrderTraverse(Node<K, V> node){
    
        if(null == node)
            return;
        
        System.out.println(node);
        
        preOrderTraverse(node._left);
        preOrderTraverse(node._right); 
    }
    
    /**
     * @brief Removes data.
     * @param key the key of value
     */
    public void removeKey(K key) {
        _root = removeKey(_root, key);
    }

    /**
     * @brief Removes a node by recursive process.
     * @param node the root of the tree
     * @param key the key of value to be deleted
     * @return the current node
     */
    private Node<K, V> removeKey(Node<K, V> node, K key) {
        
        if(null == node)
            return null;
        
        if(0 > key.compareTo(node._key))
            node._left = removeKey(node._left, key);
        else if(0 < key.compareTo(node._key))
            node._right = removeKey(node._right, key);
        else {
            
            // no children
            if(null == node._left && null == node._right)
                return null;
	
            // one child - guaranteed to be balanced
            if(null == node._left)
                return node._right;
            
            if(null == node._right)
                return node._left;
			
            // two children
            K smallestValue = minKey(node._right);
            node._key = smallestValue;
            node._right = removeKey(node._right, smallestValue);
        }
        
        // rebalancing
        return balance(node);
    }
    
    /**
     * @brief Removes a value of tree.
     * @param value the value
     */
    public void removeValue(V value){
        
        if(null != value){
            K key = getKey(value);
            
            if(null != key)
                removeKey(key);
        }
    }
    
    /**
     * @brief Rotates to the left.
     * @param node the current node
     * @return the right node of current
     */
    private Node<K, V> rotateLeft(Node<K, V> node) {
        
        Node<K, V> leftNode = node._left;
        Node<K, V> rightNode = node._right;
        
        Node<K, V> rightLeftNode = rightNode._left;
        Node<K, V> rightrightNode = rightNode._right;
        
        node = new Node<>(node._key, node._value, leftNode, rightLeftNode);
        rightNode = new Node<>(rightNode._key, rightNode._value, node, rightrightNode);
        
        return rightNode;
    }
    
    /**
     * @brief Rotates to the right.
     * @param node the current node
     * @return the left node of current
     */
    private Node<K, V> rotateRight(Node<K, V> node) {

        Node<K, V> leftNode = node._left;
        Node<K, V> rightNode = node._right;
        
        Node<K, V> leftLeftNode = leftNode._left;
        Node<K, V> leftRightNode = leftNode._right;
        
        node = new Node<>(node._key, node._value, leftRightNode, rightNode);
        leftNode = new Node<>(leftNode._key, leftNode._value, leftLeftNode, node);
        
        return leftNode;
    }

    /**
     * @brief Searches a node with a certain value.
     * @param node the root of the tree
     * @param value the searched value
     * @return the found node or null if tree does not contains the 
     * value
     */
    private Node<K, V> searchValue(Node<K, V> node, V value){
        
        while (null != node) 
            if(node._value.equals(value))
                return node;
            else if(null == node._right)
                node = node._left;
            else
                node = node._right;
        
        return null; 
    }
    
    /**
     * @brief Returns itself as string.
     * @return a string that describes the root node
     */
    @Override
    public String toString() {
        return _root.toString();
    }
    
    /**
     * @brief Traverses through the tree structure.
     * @param order the traverse order
     */
    void traverse(Order order){
        
        switch(order){
            case INORDER:
                inOrderTraverse(_root);
                break;
            case LEVELORDER:
                levelOrderTraverse();
                break;
            case PREORDER:
                preOrderTraverse(_root);
                break;
            case POSTORDER:
                postOrderTraverse(_root);
        }
    }
    
}

/**
 * @class Order enumeration class
 * @brief An enumeration of possible order types to traverses the 
 * tree structure.
 * @author Christian Kusan
 */
enum Order {
    INORDER, LEVELORDER, PREORDER, POSTORDER;
}