import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class BoidSimulation extends Application {
    static final double WIDTH = 800;
    static final double HEIGHT = 600;
    static final double DEPTH = 600;
    static final int NUM_BOIDS = 500;
    static final double VIEW_RADIUS = 100;
    static final double CAMERA_RADIUS = DEPTH * 2.5;

    @Override
    public void start(Stage stage) {
        // 创建边框和中心球
        Bord bord = new Bord(WIDTH, HEIGHT, DEPTH);
        bord.setCenter(WIDTH / 2, HEIGHT / 2, DEPTH / 2);

        Sphere center = new Sphere(10);
        center.setTranslateX(WIDTH / 2);
        center.setTranslateY(HEIGHT / 2);
        center.setTranslateZ(DEPTH / 2);

        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(0);
        pointLight.setTranslateY(-1000);
        pointLight.setTranslateZ(-2000);

        Group root = new Group(ambientLight, pointLight, bord, center);

        Scene scene = new Scene(root, WIDTH, HEIGHT, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.SILVER);

        // === 关键：把场景中心平移到原点 ===
        root.setTranslateX(-WIDTH / 2);
        root.setTranslateY(-HEIGHT / 2);
        root.setTranslateZ(-DEPTH / 2);

        // 相机放在原点前方 z 轴上
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(500000);
        camera.setTranslateX(0);
        camera.setTranslateY(0);
        camera.setTranslateZ(-CAMERA_RADIUS);
        scene.setCamera(camera);

        // 添加 Boids
        List<Boid> boids = new ArrayList<>();
        for (int i = 0; i < NUM_BOIDS; i++) {
            Boid b = Boid.createBoidShapeSetV(
                    Math.random() * WIDTH,
                    Math.random() * HEIGHT,
                    Math.random() * DEPTH
            );
            boids.add(b);
            root.getChildren().add(b.shape);
        }

        // 键盘状态记录
        List<KeyCode> keysDown = new ArrayList<>();
        scene.setOnKeyPressed(e -> {
            if (!keysDown.contains(e.getCode())) {
                keysDown.add(e.getCode());
            }
        });
        scene.setOnKeyReleased(e -> keysDown.remove(e.getCode()));

        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0;
            private final long interval = 100_000_000; // 100ms

            @Override
            public void handle(long now) {
                double ROTATE_SPEED = 1.5;
                // 绕原点旋转
                if (keysDown.contains(KeyCode.LEFT)) {
                    root.getTransforms().add(new Rotate(-ROTATE_SPEED, 0, 0, 0, Rotate.Y_AXIS));
                }
                if (keysDown.contains(KeyCode.RIGHT)) {
                    root.getTransforms().add(new Rotate(ROTATE_SPEED, 0, 0, 0, Rotate.Y_AXIS));
                }
                if (keysDown.contains(KeyCode.UP)) {
                    root.getTransforms().add(new Rotate(-ROTATE_SPEED, 0, 0, 0, Rotate.X_AXIS));
                }
                if (keysDown.contains(KeyCode.DOWN)) {
                    root.getTransforms().add(new Rotate(ROTATE_SPEED, 0, 0, 0, Rotate.X_AXIS));
                }

                // Boid 更新
                if (now - lastUpdate >= interval) {
                    KDTree<Boid> tree = new KDTree<>();
                    for (Boid b : boids) {
                        tree.insert(b.x, b.y, b.z, b);
                    }
                    for (Node<Boid> node : tree) {
                        List<Node<Boid>> neighbours = tree.rangeSearch(node, VIEW_RADIUS);
                        neighbours.removeIf(n -> n.value == null || n.value == node.value);
                        node.value.update(neighbours);
                    }
                    lastUpdate = now;
                }
            }
        };
        timer.start();

        stage.setScene(scene);
        stage.setTitle("Boid Simulation 3D - 居中root旋转");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
