public class NullNode<T> extends Node<T> {
    Node<T> preNode;
    public NullNode(Node<T> preNode) {
        super(0, 0, 0, null, 0);
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.value = null;
        this.level = 0;
        left = null;
        right = null;
    }

    @Override
    public String toString() {
        return "Null";
    }

    public static <T> Node<T> createNullNode(Node<T> preNode) {
        Node<T> node = new NullNode<>(preNode);
        node.nextLevel(preNode);
        return node;
    }
}