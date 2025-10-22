import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;

public class KDTest {

    @Test
    public void testKDTreeRangeSearchByNearest() {
        KDTree<String> tree = new KDTree<>();

        tree.insert(0, 0, 0, "A");
        tree.insert(10, 0, 0, "B");
        tree.insert(30, 0, 0, "C");
        tree.insert(0, 40, 0, "D");
        tree.insert(0, 0, 50, "E");

        // 用 nearest 找到距离 (10,0,0) 最近的节点
        Node<String> target = tree.nearest(10, 0, 0);
        assertEquals("最近点应该是 B", "B", target.value);

        double radius = 35;
        List<Node<String>> neighbours = tree.rangeSearch(target, radius);

        // 剔除自己
        neighbours.removeIf(n -> n.value.equals(target.value));

        // 验证结果数量
        assertEquals("应该找到 2 个邻居", 2, neighbours.size());

        // 验证包含 A 和 C，且不包含 D/E
        boolean foundA = neighbours.stream().anyMatch(n -> "A".equals(n.value));
        boolean foundC = neighbours.stream().anyMatch(n -> "C".equals(n.value));
        boolean foundD = neighbours.stream().anyMatch(n -> "D".equals(n.value));
        boolean foundE = neighbours.stream().anyMatch(n -> "E".equals(n.value));

        assertTrue("结果应包含 A", foundA);
        assertTrue("结果应包含 C", foundC);
        assertFalse("结果不应包含 D", foundD);
        assertFalse("结果不应包含 E", foundE);
    }

    @Test
    public void testUpdateWithFewBoids() {
        KDTree<Boid> tree = new KDTree<>();

        // 创建 boid
        Boid b1 = Boid.createBoidShapeSetV(0, 0, 0);   // vx=1
        Boid b2 = Boid.createBoidShape(15, 0, 0);
        Boid b3 = Boid.createBoidShape(-10, 0, 0);

        // 插入到 KDTree
        tree.insert(b1.x, b1.y, b1.z, b1);
        tree.insert(b2.x, b2.y, b2.z, b2);
        tree.insert(b3.x, b3.y, b3.z, b3);

        // 半径选择稍大，确保有邻居
        double radius = 30;

        // 对每个 boid 调用 update
        for (Boid b : new Boid[]{b1, b2, b3}) {
            Node<Boid> target = tree.nearest(b.x, b.y, b.z);
            List<Node<Boid>> neighbours = tree.rangeSearch(target, radius);
            b.update(neighbours);
        }

        // 验证速度和位置有变化
        assertNotEquals("b1 应更新位置", 0, b1.x, 1e-8);
        assertTrue("b1 的速度应变化或保持合理范围", b1.vx <= Boid.maxSpeed);

        assertNotEquals("b2 应更新位置", 15, b2.x, 1e-8);
        assertTrue(b2.vx <= Boid.maxSpeed);

        assertNotEquals("b3 应更新位置", -10, b3.x, 1e-8);
        assertTrue(b3.vx <= Boid.maxSpeed);
    }
}
