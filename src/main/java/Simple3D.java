import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class Simple3D extends Application {

    private PerspectiveCamera camera;

    @Override
    public void start(Stage stage) {
        Cylinder cyl = new Cylinder(50, 120);
        cyl.setMaterial(new PhongMaterial(Color.RED));
        cyl.setTranslateX(0);
        cyl.setTranslateY(0);
        cyl.setTranslateZ(0); // 放在原点

        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(0);
        pointLight.setTranslateY(-100);
        pointLight.setTranslateZ(-200); // 放在相机和圆柱之间

        Group root = new Group(cyl, ambientLight, pointLight);

        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(2000);
        camera.setTranslateX(0);
        camera.setTranslateY(0);
        camera.setTranslateZ(-500); // 远离物体
        //camera.getTransforms().add(new Rotate(-20, Rotate.X_AXIS)); // 微微俯视

        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.LIGHTGRAY);
        scene.setCamera(camera);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W: camera.setTranslateZ(camera.getTranslateZ() + 10); break;
                case S: camera.setTranslateZ(camera.getTranslateZ() - 10); break;
                case A: camera.setTranslateX(camera.getTranslateX() - 10); break;
                case D: camera.setTranslateX(camera.getTranslateX() + 10); break;
                case UP: camera.setTranslateY(camera.getTranslateY() - 10); break;
                case DOWN: camera.setTranslateY(camera.getTranslateY() + 10); break;
            }
        });

        stage.setTitle("Cylinder3DTest");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
