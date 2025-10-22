import java.util.ArrayList;
import java.util.List;

public class Node<T> {
    public double x;
    public double y;
    public double z;
    public Node<T> left;
    public Node<T> right;
    public T value;
    public int level; // 0表示按x分割，1表示按y分割

    private static final double EPS = 1e-9;

    public Node(double x, double y, double z, T value, int level) {
        if (this instanceof NullNode ||this instanceof FarNode) return;
        this.x = x;
        this.y = y;
        this.z = z;

        this.value = value;
        this.level = level;
        // 初始化左右为哨兵空节点
        left = new NullNode<>(this);
        right = new NullNode<>(this);
    }

    public double compareTo(Node<T> o) {
        if (level == 0)
            //return Double.compare(this.x, o.x);
            return this.x - o.x;
        else if (level == 1)
            //return Double.compare(this.y, o.y);
            return this.y - o.y;
        else
            //return Double.compare(this.z, o.z);
            return this.z - o.z;
    }

    @Override
    public String toString() {
        return value + " (" + x + ", " + y + ", " + z + "), level=" + level;
    }

    public void nextLevel(Node<T> preNode) {
        this.level = (preNode.level + 1) % 3;
    }

    private static <T> double distance(Node<T> node1, Node<T> node2) {
        if (node1 instanceof FarNode || node2 instanceof FarNode)
            return Double.POSITIVE_INFINITY;
        double deltaX = node1.x - node2.x;
        double deltaY = node1.y - node2.y;
        double deltaZ = node1.z - node2.z;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }

    private static <T> boolean samePositon(Node<T> node1, Node<T> node2) {
        return Math.abs(node1.x - node2.x) < EPS
                && Math.abs(node1.y - node2.y) < EPS
                && Math.abs(node1.z - node2.z) < EPS;
    }

    public static <T> Node<T> insert(Node<T> node, double x, double y, double z, T value) {
        return insert(node, new Node<>(x, y, z, value, 0));
    }

    private static <T> Node<T> insert(Node<T> node, Node<T> inNode) {
        if (node instanceof NullNode) {
            // 直接返回新节点，父层递归会赋值
            inNode.level =  node.level;
            inNode.left = NullNode.createNullNode(inNode);
            inNode.right = NullNode.createNullNode(inNode);
            return inNode;
        }

        if (samePositon(node, inNode)) {
            node.value = inNode.value;
            return node;
        }

        double cmp = node.compareTo(inNode);
        if (cmp > 0)
            node.left = insert(node.left, inNode);
        else
            node.right = insert(node.right, inNode);
        return node;
    }

    public static <T> Node<T> search(Node<T> node, double x, double y, double z) {
        return search(node, new Node<>(x, y, z,null, 0));
    }

    private static <T> Node<T> search(Node<T> node, Node<T> sNode) {
        if (node instanceof NullNode) return node;
        if (samePositon(node, sNode)) return node;
        double cmp = node.compareTo(sNode);
        if (cmp > 0) return search(node.left, sNode);
        else return search(node.right, sNode);
    }

    public static <T> Node<T> nearest(Node<T> node, double xGoal, double yGoal, double zGoal) {
        return nearest(node, new Node<>(xGoal, yGoal, zGoal, null, 0), new FarNode<>());
    }

    private static <T> Node<T> nearest(Node<T> node, Node<T> goal, Node<T> best) {
        if (node instanceof NullNode) return best;
        if (distance(node, goal) < distance(best, goal)) best = node;
        Node<T> goodSide;
        Node<T> badSide;
        if (node.compareTo(goal) > 0) {
            goodSide = node.left;
            badSide = node.right;
        } else {
            goodSide = node.right;
            badSide = node.left;
        }
        best = nearest(goodSide, goal, best);
        if (distance(node, goal) <= distance(best, goal))
            best = nearest(badSide, goal, best);
        return best;
    }

    public static <T> List<Node<T>> rangeSearch(Node<T> node, Node<T> goal, double radius) {
        List<Node<T>> result = new ArrayList<>();
        rangeSearchHelper(node, goal, radius, result);
        return result;
    }

    private static <T> void rangeSearchHelper(Node<T> node, Node<T> goal, double radius ,List<Node<T>> result) {
        if (node instanceof NullNode || goal instanceof NullNode) return;
        if (distance(node, goal) < radius) result.add(node);
        Node<T> goodSide, badSide;
        double dist = node.compareTo(goal);
        if (dist > 0) {
            goodSide = node.left;
            badSide = node.right;
        } else {
            goodSide = node.right;
            badSide = node.left;
        }
        rangeSearchHelper(goodSide, goal, radius, result);
        if (Math.abs(dist) <= radius)
            rangeSearchHelper(badSide, goal, radius, result);
    }

    public static <T> void printTree(Node<T> node, String indent, String branchLabel, boolean isLast) {
        if (node == null) return;
        if (node instanceof NullNode) {
            System.out.println(indent + branchLabel + " Null");
            return;
        }
        System.out.println(indent + branchLabel + " " + node);
        String childIndent = indent + (isLast ? "    " : "│   ");
        String leftLabel = node.level == 0 ? "├──左" : "├──下";
        String rightLabel = node.level == 0 ? "└──右" : "└──上";
        printTree(node.left, childIndent, leftLabel, false);
        printTree(node.right, childIndent, rightLabel, true);
    }
}
