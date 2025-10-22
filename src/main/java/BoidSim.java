import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BoidSim extends Application {
    static final double WIDTH = 800;
    static final double HEIGHT = 600;
    static final double DEPTH = 600;
    static final int NUM_BOIDS = 500;
    static final double VIEW_RADIUS = 100;
    static final double CAMERA_RADIUS =  DEPTH * 2.5;

    static double phi = Math.PI / 2;
    static double theta = 0;
    static Vector3D vs = new Vector3D(0, 0, CAMERA_RADIUS);


    @Override
    public void start(Stage stage) {
        Bord bord = new Bord(WIDTH, HEIGHT, DEPTH);
        bord.setCenter(WIDTH / 2, HEIGHT / 2, DEPTH / 2);
        List<Boid> boids = new ArrayList<>();
        KDTree<Boid> tree = new KDTree<>();
        Sphere center = new Sphere(10);
        center.setTranslateX(WIDTH / 2);
        center.setTranslateY(HEIGHT / 2);
        center.setTranslateZ(DEPTH / 2);

        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(0);
        pointLight.setTranslateY(-1000);
        pointLight.setTranslateZ(-2000);


        Group root = new Group(ambientLight, pointLight, bord,center);
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);
        scene.setFill(Color.SILVER);


        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(500000);
        camera.setTranslateX(WIDTH / 2);
        camera.setTranslateY(HEIGHT / 2);
        camera.setTranslateZ(-CAMERA_RADIUS); // 眼睛拉远一点
        scene.setCamera(camera);


        for (int i = 0; i < NUM_BOIDS; i++) {
            Boid b = Boid.createBoidShapeSetV(
                    Math.random() * 800 - 400,
                    Math.random() * 600 - 300,
                    Math.random() * 500 + 200
            );
            boids.add(b);
            root.getChildren().add(b.shape);
        }


        /*
        for (int i = 0; i < 100; i++) {
            Boid b = Boid.createBoidShapeSetV(
                    i*2, 100, 0
            );
            boids.add(b);
            root.getChildren().add(b.shape);
        }
        Boid b1 = Boid.createBoidShape(10,100,0);
        boids.add(b1);
        b1.shape.setMaterial(new PhongMaterial(Color.BLACK));
        root.getChildren().add(b1.shape);
*/
        stage.setScene(scene);
        stage.setTitle("Boid Simulation 3D");
        stage.show();

        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0; // 记录上次更新时间
            private final long interval = 200_000_000; // 间隔时间，单位纳秒 (200 ms)

            @Override
            public void handle(long now) {
                cameraControl(camera, scene);

                if (now - lastUpdate >= interval) {
                    // ===== 更新逻辑 =====
                    KDTree<Boid> tree = new KDTree<>();
                    for (Boid b : boids) {
                        tree.insert(b.x, b.y, b.z, b);
                    }
                    for (Node<Boid> node : tree) {
                        List<Node<Boid>> neighbours = tree.rangeSearch(node, VIEW_RADIUS);
                        neighbours.removeIf(n -> n.value == null || n.value == node.value);
                        node.value.update(neighbours);
                    }
                    // ====================

                    lastUpdate = now; // 更新最后执行时间
                }
            }
        };
        timer.start();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private void cameraControl(Camera camera, Scene scene) {
        // 键盘事件
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.UP) {
                theta += 0.05;
            } else if (event.getCode() == KeyCode.DOWN) {
                theta -= 0.05;
            } else if (event.getCode() == KeyCode.LEFT) {
                phi -= 0.05;
            } else if (event.getCode() == KeyCode.RIGHT) {
                phi += 0.05;
            }
        });

        double dVtX = CAMERA_RADIUS * Math.cos(phi) * Math.cos(theta);
        double dVtY = CAMERA_RADIUS * Math.sin(theta);
        double dVtZ = CAMERA_RADIUS * Math.cos(theta) * Math.sin(phi);
        Vector3D vt = new Vector3D(dVtX, dVtY, dVtZ);

        if (vt.getNorm() < 1e-8) {
            return;
        }

        Rotation r = new Rotation(vs, vt);
        double[] angles = r.getAngles(RotationOrder.XYZ);

        camera.getTransforms().setAll(
                new Rotate(Math.toDegrees(angles[0]), Rotate.X_AXIS),
                new Rotate(Math.toDegrees(angles[1]), Rotate.Y_AXIS),
                new Rotate(Math.toDegrees(angles[2]), Rotate.Z_AXIS)
        );

        camera.setTranslateX(WIDTH / 2 + dVtX);
        camera.setTranslateY(HEIGHT / 2 + dVtY);
        camera.setTranslateZ(DEPTH / 2 + dVtZ);
        vs = vt;
    }

    private void cameraControl(Camera camera) {
        // 球面坐标 -> 相机位置
        double camX = CAMERA_RADIUS * Math.cos(phi) * Math.cos(theta);
        double camY = CAMERA_RADIUS * Math.sin(theta);
        double camZ = CAMERA_RADIUS * Math.cos(theta) * Math.sin(phi);

        // 场景中心
        double cx = WIDTH / 2;
        double cy = HEIGHT / 2;
        double cz = DEPTH / 2;

        // 更新位置
        Vector3D camPos = new Vector3D(cx + camX, cy + camY, cz + camZ);
        camera.setTranslateX(camPos.getX());
        camera.setTranslateY(camPos.getY());
        camera.setTranslateZ(camPos.getZ());

        // 计算朝向（lookAt）
        Vector3D forward = new Vector3D(cx, cy, cz).subtract(camPos).normalize();
        Vector3D initialForward = new Vector3D(0, 0, -1); // 默认朝向
        Rotation rotation = new Rotation(initialForward, forward);
        double[] angles = rotation.getAngles(RotationOrder.XYZ);

        camera.getTransforms().setAll(
                new Rotate(Math.toDegrees(angles[0]), Rotate.X_AXIS),
                new Rotate(Math.toDegrees(angles[1]), Rotate.Y_AXIS),
                new Rotate(Math.toDegrees(angles[2]), Rotate.Z_AXIS)
        );
    }

}