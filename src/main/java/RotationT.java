import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class RotationT extends Application {
    static double SCENE_WIDTH = 600;
    static double SCENE_HEIGHT = 600;
    @Override
    public void start(Stage stage) throws Exception {
        Group group = new Group();
        Camera camera = new PerspectiveCamera(true);

        camera.setNearClip(1);
        camera.setFarClip(1000);
        camera.setTranslateX(0);
        camera.setTranslateY(0);

        camera.setTranslateZ(-300);
        Scene scene = new Scene(group, SCENE_WIDTH, SCENE_HEIGHT);

        //默认体心在原点，z轴向屏幕里
        Shape3D box = new Box(10, 10, 10);

        box.setMaterial(new PhongMaterial(Color.RED));

        Sphere sphere = new Sphere(6);
        sphere.setMaterial(new PhongMaterial(Color.RED));
        sphere.setTranslateX(0);
        sphere.setTranslateY(0);
        sphere.setTranslateZ(0);

        Sphere sphere1 = new Sphere(6);
        sphere1.setMaterial(new PhongMaterial(Color.RED));
        sphere1.setTranslateX(0);
        sphere1.setTranslateY(0);
        sphere1.setTranslateZ(0);

        Point3D p = new Point3D(0, 1, 0);
        Rotate r = new Rotate(180, 6, 0, 0, p);
        sphere1.getTransforms().add(r);
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);

        group.getChildren().addAll(sphere, sphere1, box, ambientLight);
        scene.setCamera(camera);

        stage.setScene(scene);
        stage.show();
    }
}