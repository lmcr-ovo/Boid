import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.stage.Stage;

public class Cylinder3DTest extends Application {

    private PerspectiveCamera camera;

    @Override
    public void start(Stage stage) {
        // 创建一个圆柱，放在相机前方
        Cylinder cyl = new Cylinder(50, 120); // 半径=50，高度=120
        cyl.setMaterial(new PhongMaterial(Color.RED));
        cyl.setTranslateX(0);
        cyl.setTranslateY(0);
        cyl.setTranslateZ(-500); // 放在相机前方（相机默认为看向 -Z）

        // 光照：全局环境光 + 一个点光源
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(100);
        pointLight.setTranslateY(-100);
        pointLight.setTranslateZ(-300);

        Group root = new Group(cyl, ambientLight, pointLight);

        // 相机
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(2000);
        camera.setTranslateZ(0); // 在世界坐标原点

        // 场景（开启深度缓冲支持3D）
        Scene scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.LIGHTGRAY);
        scene.setCamera(camera);

        // 键盘控制相机移动
        scene.setOnKeyPressed((KeyEvent event) -> {
            switch (event.getCode()) {
                case W: camera.setTranslateZ(camera.getTranslateZ() + 10); break;
                case S: camera.setTranslateZ(camera.getTranslateZ() - 10); break;
                case A: camera.setTranslateX(camera.getTranslateX() - 10); break;
                case D: camera.setTranslateX(camera.getTranslateX() + 10); break;
                case UP: camera.setTranslateY(camera.getTranslateY() - 10); break;
                case DOWN: camera.setTranslateY(camera.getTranslateY() + 10); break;
            }
        });

        // 显示窗口
        stage.setTitle("Cylinder3DTest");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
