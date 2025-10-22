import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;

public class KDTree<T> implements Iterable<Node<T>>{
    Node<T> root;
    public KDTree() {
        root = new NullNode<>(null);
    }
    public void clear() {
        root = new NullNode<>(null);
    }

    public void insert(double x, double y, double z, T value) {
        root = Node.insert(root, x, y, z, value);
    }

    public Node<T> nearest(double x, double y, double z) {
        return Node.nearest(root, x, y, z);
    }

    public List<Node<T>> rangeSearch(Node<T> goal, double radius) {
        return Node.rangeSearch(root, goal, radius);
    }

    public void printTree() {
        Node.printTree(root, "", "└──", true);
    }

    /** 迭代器实现，中序遍历 */
    @Override
    public Iterator<Node<T>> iterator() {
        return new Iterator<Node<T>>() {
            private final Stack<Node<T>> stack = new Stack<>();
            {
                pushLeft(root);
            }

            private void pushLeft(Node<T> node) {
                while (!(node instanceof NullNode)) {
                    stack.push(node);
                    node = node.left;
                }
            }

            @Override
            public boolean hasNext() {
                return !stack.isEmpty();
            }

            @Override
            public Node<T> next() {
                if (!hasNext()) throw new NoSuchElementException();
                Node<T> current = stack.pop();
                pushLeft(current.right);
                return current;
            }
        };
    }

    // 测试
    public static void main(String[] args) {
        KDTree<String> t = new KDTree<>();
        t.insert(2, 3, 1, "B");
        t.insert(7, 4, 2, "C");
        t.insert(6, 8, 3, "D");
        t.insert(1, 1, 4, "E");
        t.insert(4, 5, 5, "F");
        t.insert(7, 8, 9, "G");

        for (Node<String> n : t) {
            System.out.println(n);
        }
    }
}