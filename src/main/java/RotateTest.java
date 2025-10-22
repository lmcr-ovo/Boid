import javafx.application.Application;
import javafx.scene.*;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class RotateTest extends Application {

    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

    @Override
    public void start(Stage stage) {
        // === 创建一个立方体 ===
        Box cube = new Box(100, 100, 100);
        cube.setMaterial(new PhongMaterial(Color.RED));
        cube.setTranslateX(0);
        cube.setTranslateY(0);
        cube.setTranslateZ(0);

        // === 中心球体 ===
        Sphere center = new Sphere(10);
        center.setMaterial(new PhongMaterial(Color.YELLOW));
        center.setTranslateX(0);
        center.setTranslateY(0);
        center.setTranslateZ(0);

        // 用 group 承载物体
        Group objectGroup = new Group(cube, center);
        objectGroup.getTransforms().addAll(rotateX, rotateY);

        // === 场景根节点 ===
        Group root = new Group(objectGroup);

        // === 场景 ===
        Scene scene = new Scene(root, 800, 600, true); // true 开启深度缓冲
        scene.setFill(Color.LIGHTGRAY);

        // === 相机 ===
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(2000.0);
        camera.setTranslateZ(-400); // 初始位置拉远 400
        scene.setCamera(camera);

        // === 鼠标拖动旋转 ===
        initMouseControl(objectGroup, scene);

        // === 键盘移动相机 ===
        scene.setOnKeyPressed(event -> {
            double step = 10;
            if (event.getCode() == KeyCode.W) {
                camera.setTranslateZ(camera.getTranslateZ() + step);
            } else if (event.getCode() == KeyCode.S) {
                camera.setTranslateZ(camera.getTranslateZ() - step);
            } else if (event.getCode() == KeyCode.A) {
                camera.setTranslateX(camera.getTranslateX() - step);
            } else if (event.getCode() == KeyCode.D) {
                camera.setTranslateX(camera.getTranslateX() + step);
            }
        });

        // === 显示窗口 ===
        stage.setTitle("JavaFX 3D Demo");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * 鼠标左键拖动旋转
     */
    private void initMouseControl(Group group, Scene scene) {
        scene.setOnMousePressed((MouseEvent event) -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();
        });

        scene.setOnMouseDragged((MouseEvent event) -> {
            rotateX.setAngle(anchorAngleX - (anchorY - event.getSceneY()));
            rotateY.setAngle(anchorAngleY + (anchorX - event.getSceneX()));
        });
    }

    /**
     * 使用 Apache Commons Math 计算旋转：vs->vt
     */
    public static void updateShapeDirection(Vector3D vs, Vector3D vt, Shape3D node) {
        if (vt.getNorm() < 1e-8 || vs.getNorm() < 1e-8) {
            return;
        }
        Rotation r = new Rotation(vs.normalize(), vt.normalize());
        Vector3D axis = r.getAxis();
        double angle = Math.toDegrees(r.getAngle());

        node.getTransforms().clear();
        node.getTransforms().setAll(
                new Rotate(angle, axis.getX(), axis.getY(), axis.getZ())
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
