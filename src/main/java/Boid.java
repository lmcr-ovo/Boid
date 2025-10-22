import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.text.NumberFormat;
import java.util.List;

public class Boid {
    static final double maxSpeed = 30;
    static final double SEPARATION_RANGE = 10;
    static final double SEPARATION_FACTOR = 5000;
    static final double ALIGNMENT_RANGE = 20;
    static final double ALIGNMENT_FACTOR = 20;
    static final double COHESION_RANGE = 20;
    static final double COHESION_FACTOR = 10;
    static final double BOUNDARY = 10;
    static final double DAMP = 10;
    static NumberFormat nf = format();
    static int count = 0;
    static Rotate RotateZero = new Rotate(0, 0, 0, 0, Rotate.Z_AXIS);
    public double x, y, z;   // 位置
    public double vx, vy, vz; // 速度
    public Shape3D shape;
    public Vector3D point;
    public int id;

    public Boid(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = count;
        count += 1;
        // 随机速度（-maxSpeed 到 maxSpeed）
        this.vx = (Math.random() * 2 - 1) * maxSpeed;
        this.vy = (Math.random() * 2 - 1) * maxSpeed;
        this.vz = (Math.random() * 2 - 1) * maxSpeed;
    }

    public static Boid createBoidShapeSetV(double x, double y, double z) {
        Boid b = new Boid(x, y, z);
        b.vx = 0;
        b.vy = 1;
        b.vz = 0;

        Cylinder cyl = new Cylinder(1, 100);
        cyl.setMaterial(new PhongMaterial(Color.RED));

        b.shape = cyl;
        b.shape.setTranslateX(x);
        b.shape.setTranslateY(y);
        b.shape.setTranslateZ(z);

        b.point = new Vector3D(0, 1, 0);
        b.updateShapeDirection();

        System.out.println("创建Boid\n" + b.toString());
        return b;
    }

    public static Boid createBoidShape(double x, double y, double z) {
        Boid b = new Boid(x, y, z);

        Cylinder cyl = new Cylinder(10, 20);
        cyl.setMaterial(new PhongMaterial(Color.RED));

        b.shape = cyl;
        b.shape.setTranslateX(x);
        b.shape.setTranslateY(y);
        b.shape.setTranslateZ(z);

        b.point = new Vector3D(0, 1, 0);
        b.updateShapeDirection();

        System.out.println("创建Boid\n" + b.toString());
        return b;
    }

    public void updateShapeDirection() {
        // 保证当前方向单位化
        Vector3D vs = point.normalize();
        // 目标方向单位化
        Vector3D vt = new Vector3D(vx, vy, vz).normalize();
        // 计算旋转
        Rotate r = updateDirection(vs, vt);
        // 如果是零旋转对象则不更新 transforms
        if (r == RotateZero) return;
        // 应用旋转
        shape.getTransforms().setAll(r);

        // 更新当前方向
        point = vt;
    }


    public Rotate updateDirection(Vector3D vs, Vector3D vt) {

        if (vt.getNorm() < 1e-8) return RotateZero; // 目标向量长度太小 → 不旋转

        double dot = vs.dotProduct(vt);

        // 如果几乎相同 → 不旋转
        if (Math.abs(dot - 1.0) < 1e-8) return RotateZero;

        Vector3D vertical;
        if (Math.abs(dot + 1.0) < 1e-8) {
            // 反方向 → 选一个任意垂直方向作为旋转轴
            vertical = vs.orthogonal().normalize();
        } else {
            vertical = vs.crossProduct(vt).normalize();
        }

        double angleDeg = Math.toDegrees(Math.acos(dot));
        Rotate rotate = new Rotate(angleDeg, x, y, z,
                new Point3D(vertical.getX(), vertical.getY(), vertical.getZ()));
        return rotate;
    }


    public void update(List<Node<Boid>> adj) {
        check1();
        System.out.println("\n===== 进入 Boid.update =====");
        System.out.printf("Boid ID: %d%n当前位置: (%.2f, %.2f, %.2f)%n当前速度: (%.2f, %.2f, %.2f)%n邻居数量: %d%n",
                id, x, y, z, vx, vy, vz, adj.size());


        Vector3D sep = separation(this, adj).scalarMultiply(SEPARATION_FACTOR);
        Vector3D ali = alignment(this, adj).scalarMultiply(ALIGNMENT_FACTOR);
        Vector3D coh = cohesion(this, adj).scalarMultiply(COHESION_FACTOR);

        // >>> 增加调试输出 <<<
        System.out.printf("分离(加权): (%.4f, %.4f, %.4f)%n", sep.getX(), sep.getY(), sep.getZ());
        System.out.printf("对齐(加权): (%.4f, %.4f, %.4f)%n", ali.getX(), ali.getY(), ali.getZ());
        System.out.printf("聚合(加权): (%.4f, %.4f, %.4f)%n", coh.getX(), coh.getY(), coh.getZ());

        // 合成加速度
        Vector3D acceleration = sep.add(ali).add(coh);
        //Vector3D acceleration = sep;
        Vector3D velocity = new Vector3D(vx, vy, vz).add(acceleration);

        if (velocity.getNorm() > maxSpeed) {
            velocity = velocity.normalize().scalarMultiply(maxSpeed);
        }

        vx = velocity.getX();
        vy = velocity.getY();
        vz = velocity.getZ();

        x += vx;
        y += vy;
        z += vz;

        bound();
        updateShapeDirection();
        shape.setTranslateX(x);
        shape.setTranslateY(y);
        shape.setTranslateZ(z);

        System.out.printf("更新后位置: (%.2f, %.2f, %.2f)%n更新后速度: (%.2f, %.2f, %.2f)%n", x, y, z, vx, vy, vz);
        System.out.println("===== 退出 Boid.update =====");
    }

    private static Vector3D separation(Boid b, List<Node<Boid>> adjacence) {
        System.out.println("\n--- 进入 separation ---");
        System.out.printf("Boid ID: %d%n邻居数量: %d%n", b.id, adjacence.size());

        double forceX = 0, forceY = 0, forceZ = 0;
        int count = 0;
        for (Node<Boid> node : adjacence) {
            Boid other = node.value;
            double dist = distance(b, other);

            if (dist > 0 && dist < SEPARATION_RANGE) {
                double dx = b.x - other.x;
                double dy = b.y - other.y;
                double dz = b.z - other.z;

                dx /= dist;
                dy /= dist;
                dz /= dist;
                double strength = 1 / dist;

                forceX += dx * strength;
                forceY += dy * strength;
                forceZ += dz * strength;
                count++;
            }
        }
        if (count > 0) {
            forceX /= count;
            forceY /= count;
            forceZ /= count;
        }
        Vector3D steer = count == 0 ? Vector3D.ZERO : new Vector3D(forceX, forceY, forceZ);
        System.out.printf("分离向量: %s%n", steer.toString(nf));
        System.out.println("--- 退出 separation ---");
        return steer;
    }

    private static Vector3D alignment(Boid b, List<Node<Boid>> adjacence) {
        System.out.println("\n--- 进入 alignment ---");
        System.out.printf("Boid ID: %d%n邻居数量: %d%n", b.id, adjacence.size());

        if (adjacence.isEmpty()) {
            System.out.println("无邻居，返回零向量");
            System.out.println("--- 退出 alignment ---");
            return Vector3D.ZERO;
        }

        double vxAcc = 0, vyAcc = 0, vzAcc = 0;
        int count = 0;
        for (Node<Boid> node : adjacence) {
            double dist = distance(b, node.value);
            if (dist > 0 && dist < ALIGNMENT_RANGE) {
                vxAcc += node.value.vx;
                vyAcc += node.value.vy;
                vzAcc += node.value.vz;
                count++;
            }
        }
        if (count > 0) {
            vxAcc /= count;
            vyAcc /= count;
            vzAcc /= count;
        }

        Vector3D avgVel = new Vector3D(vxAcc, vyAcc, vzAcc);
        System.out.printf("平均速度向量: %s%n", avgVel.toString(nf));
        System.out.println("--- 退出 alignment ---");
        return avgVel;
    }

    private static Vector3D cohesion(Boid b, List<Node<Boid>> adjacence) {
        System.out.println("\n--- 进入 cohesion ---");
        System.out.printf("Boid ID: %d%n邻居数量: %d%n", b.id, adjacence.size());

        double xAcc = 0, yAcc = 0, zAcc = 0;
        int count = 0;
        for (Node<Boid> node : adjacence) {
            double dist = distance(b, node.value);
            if (dist > 0 && dist < COHESION_RANGE) {
                xAcc += node.value.x;
                yAcc += node.value.y;
                zAcc += node.value.z;
                count++;
            }
        }
        if (count == 0) {
            System.out.println("无邻居，返回零向量");
            System.out.println("--- 退出 cohesion ---");
            return Vector3D.ZERO;
        }

        Vector3D steer = new Vector3D(
                (xAcc / count) - b.x,
                (yAcc / count) - b.y,
                (zAcc / count) - b.z
        );
        System.out.printf("聚合向量: %s%n", steer.toString(nf));
        System.out.println("--- 退出 cohesion ---");
        return steer;
    }

    private void check() {
        if (x < 0 || x > 800 || y < 0 || y > 600 || z < 0 || z > 600) {
            System.out.printf("[警告] Boid %d 越界: (%.2f, %.2f, %.2f)%n", id, x, y, z);
        }
    }


    private void bound() {
        if (x < 0) x += 800;
        if (x > 800) x -= 800;
        if (y < 0) y += 600;
        if (y > 600) y -= 600;
        if (z < 0) z += 600;
        if (z > 600) z -= 600;
    }

    private void check1() {
        if (x < BOUNDARY) vx += DAMP;
        if (x > 800 - BOUNDARY) vx -= DAMP;
        if (y < BOUNDARY) vy += DAMP;
        if (y > 600 - BOUNDARY) vy -= DAMP;
        if (z < DAMP) vz += DAMP;
        if (z > 600 - BOUNDARY) vz -= DAMP;
    }

    private static double distance(Boid b1, Boid b2) {
        double dx = b1.x - b2.x;
        double dy = b1.y - b2.y;
        double dz = b1.z - b2.z;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    @Override
    public String toString() {
        return String.format(
                "\n\t位置：(%.2f, %.2f, %.2f)\n" +
                        "\t速度：(%.2f, %.2f, %.2f)\n" +
                        "\t速度矢量：%s",
                x, y, z,
                vx, vy, vz,
                point.toString(nf)
        );
    }

    private static NumberFormat format() {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        return nf;
    }

    class SmartGroup extends Group {

        Rotate r;
        Transform t = new Rotate();

        void rotateByX(int ang) {
            r = new Rotate(ang, Rotate.X_AXIS);
            t = t.createConcatenation(r);
            this.getTransforms().clear();
            this.getTransforms().addAll(t);
        }

        void rotateByY(int ang) {
            r = new Rotate(ang, Rotate.Y_AXIS);
            t = t.createConcatenation(r);
            this.getTransforms().clear();
            this.getTransforms().addAll(t);
        }


        void rotateByZ(int ang) {
            r = new Rotate(ang, Rotate.Z_AXIS);
            t = t.createConcatenation(r);
            this.getTransforms().clear();
            this.getTransforms().addAll(t);
        }
    }
}
