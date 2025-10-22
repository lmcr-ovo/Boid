import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class BoidSimu extends Application {
    static final double WIDTH = 600;
    static final double HEIGHT = 600;
    static final double DEPTH = 600;
    static final int NUM_BOIDS = 500;
    static final double VIEW_RADIUS = 300;
    static final double CAMERA_RADIUS =  DEPTH * 2.5;




    @Override
    public void start(Stage stage) {
        Bord bord = new Bord(WIDTH, HEIGHT, DEPTH);
        bord.setCenter(0, 0, 0);



        List<Boid> boids = new ArrayList<>();
        KDTree<Boid> tree = new KDTree<>();
        Sphere center = new Sphere(10);

        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(0);
        pointLight.setTranslateY(-1000);
        pointLight.setTranslateZ(-2000);


        //SmartGroup root = new SmartGroup(ambientLight, pointLight, bord,center);
        SmartGroup root = new SmartGroup();
        root.getChildren().addAll(ambientLight, pointLight, bord, center);
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);
        scene.setFill(Color.SILVER);


        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(500000);
        camera.setTranslateX(0);
        camera.setTranslateY(0);
        camera.setTranslateZ(-CAMERA_RADIUS); // 眼睛拉远一点
        scene.setCamera(camera);


        for (int i = 0; i < NUM_BOIDS; i++) {
            Boid b = Boid.createBoidShape(
                    Math.random() * WIDTH - WIDTH / 2,
                    Math.random() * HEIGHT - HEIGHT / 2,
                    Math.random() * DEPTH - DEPTH / 2
            );
            boids.add(b);
            root.getChildren().add(b.shape);
        }


        stage.setScene(scene);
        stage.setTitle("Boid Simulation 3D");
        stage.show();

        AnimationTimer timer = new AnimationTimer() {
            private long lastUpdate = 0; // 记录上次更新时间
            //private final long interval = 200_000_000; // 间隔时间，单位纳秒 (200 ms)


            @Override
            public void handle(long now) {
                KDTree<Boid> tree = new KDTree<>();
                for (Boid b : boids) {
                    tree.insert(b.x, b.y, b.z, b);
                }
                for (Node<Boid> node : tree) {
                    List<Node<Boid>> neighbours = tree.rangeSearch(node, VIEW_RADIUS);
                    neighbours.removeIf(n -> n.value == null || n.value == node.value);
                    node.value.update(neighbours);
                }
            }

        };
        timer.start();

       stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                case W:
                    root.translateZProperty().set(root.getTranslateZ() + 100);
                    break;
                case S:
                   root.translateZProperty().set(root.getTranslateZ() - 100);
                    break;
                case Q:
                    root.rotateByX(10);
                    break;
                case E:
                    root.rotateByX(-10);
                    break;
                case NUMPAD6:
                    root.rotateByY(10);
                    break;
                case NUMPAD4:
                    root.rotateByY(-10);
                    break;
            }
        });

        stage.setTitle("Genuine Coder");
        stage.setScene(scene);
        stage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
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
