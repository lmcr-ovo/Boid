import java.util.List;

public class RangeSearchTest {
    public static void main(String[] args) {
        KDTree<Boid> tree = new KDTree<>();

        // 插入 Boid 数据（固定位置方便验证）
        Boid b1 = new Boid(0, 0, 0);
        Boid b2 = new Boid(10, 0, 0);
        Boid b3 = new Boid(30, 0, 0);
        Boid b4 = new Boid(0, 40, 0);

        tree.insert(b1.x, b1.y, b1.z, b1);
        tree.insert(b2.x, b2.y, b2.z, b2);
        tree.insert(b3.x, b3.y, b3.z, b3);
        tree.insert(b4.x, b4.y, b4.z, b4);

        // 取 b1 为目标，搜索半径 35
        Node<Boid> targetNode = new Node<>(b1.x, b1.y, b1.z, b1, 0);
        double radius = 35;

        List<Node<Boid>> neighbours = tree.rangeSearch(targetNode, radius);

        // 剔除自己
        neighbours.removeIf(n -> n.value == targetNode.value);

        // 打印结果
        System.out.println("范围搜索半径 = " + radius);
        for (Node<Boid> n : neighbours) {
            System.out.printf("找到邻居: (%.1f, %.1f, %.1f)%n", n.x, n.y, n.z);
        }
    }
}
