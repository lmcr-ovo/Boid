import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class BordTest extends Application {

    private double anchorX, anchorY; // 鼠标拖动起点
    private double angleX = 0;       // 绕X当前角度
    private double angleY = 0;       // 绕Y当前角度
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    @Override
    public void start(Stage stage) {
        // 创建你的 Bord 对象
        Bord bord = new Bord(200, 150, 100);
        bord.setCenter(0, 0, 0); // 将中心放到原点

        // 为整个对象添加旋转变换
        bord.getTransforms().addAll(rotateX, rotateY);

        // 创建场景根节点
        Group root = new Group();
        root.getChildren().add(bord);

        // 设置相机
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000);
        camera.setTranslateZ(-500); // 拉远一点看

        // 创建 3D 场景
        Scene scene = new Scene(root, 800, 600, true, SceneAntialiasing.BALANCED);
        scene.setFill(Color.LIGHTBLUE);
        scene.setCamera(camera);

        // 鼠标拖动旋转模型
        scene.setOnMousePressed(event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
        });

        scene.setOnMouseDragged(event -> {
            angleX += (event.getSceneY() - anchorY);
            angleY += (event.getSceneX() - anchorX);
            rotateX.setAngle(angleX);
            rotateY.setAngle(angleY);
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
        });

        // 显示窗口
        stage.setTitle("Bord 线框测试");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
