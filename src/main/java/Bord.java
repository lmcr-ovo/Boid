import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Bord extends Group {
    public Cylinder[] arrisX = new Cylinder[4];
    public Cylinder[] arrisY = new Cylinder[4];
    public Cylinder[] arrisZ = new Cylinder[4];
    static double radius = 0.5; // 棱的粗细

    public Bord(double xLength, double yLength, double zLength) {
        PhongMaterial mat = new PhongMaterial(Color.RED);

        double hx = xLength / 2;
        double hy = yLength / 2;
        double hz = zLength / 2;

        // X方向棱（上下前后）
        arrisX[0] = createEdgeX(-hy, -hz, xLength, mat);
        arrisX[1] = createEdgeX(hy, -hz, xLength, mat);
        arrisX[2] = createEdgeX(-hy, hz, xLength, mat);
        arrisX[3] = createEdgeX(hy, hz, xLength, mat);

        // Y方向棱（左右前后）
        arrisY[0] = createEdgeY(-hx, -hz, yLength, mat);
        arrisY[1] = createEdgeY(hx, -hz, yLength, mat);
        arrisY[2] = createEdgeY(-hx, hz, yLength, mat);
        arrisY[3] = createEdgeY(hx, hz, yLength, mat);

        // Z方向棱（上下左右）
        arrisZ[0] = createEdgeZ(-hx, -hy, zLength, mat);
        arrisZ[1] = createEdgeZ(hx, -hy, zLength, mat);
        arrisZ[2] = createEdgeZ(-hx, hy, zLength, mat);
        arrisZ[3] = createEdgeZ(hx, hy, zLength, mat);

        // 加到组中显示
        getChildren().addAll(arrisX);
        getChildren().addAll(arrisY);
        getChildren().addAll(arrisZ);
    }

    // 沿X方向创建圆柱
    private Cylinder createEdgeX(double y, double z, double length, PhongMaterial mat) {
        Cylinder cyl = new Cylinder(radius, length);
        cyl.setMaterial(mat);
        cyl.getTransforms().addAll(
                new Translate(0, y, z),
                new Rotate(90, Rotate.Z_AXIS) // 竖立到X方向
        );
        return cyl;
    }

    // 沿Y方向创建圆柱
    private Cylinder createEdgeY(double x, double z, double length, PhongMaterial mat) {
        Cylinder cyl = new Cylinder(radius, length);
        cyl.setMaterial(mat);
        cyl.getTransforms().addAll(
                new Translate(x, 0, z)
                // 默认Cylinder沿Y轴，不用旋转
        );
        return cyl;
    }

    // 沿Z方向创建圆柱
    private Cylinder createEdgeZ(double x, double y, double length, PhongMaterial mat) {
        Cylinder cyl = new Cylinder(radius, length);
        cyl.setMaterial(mat);
        cyl.getTransforms().addAll(
                new Translate(x, y, 0),
                new Rotate(90, Rotate.X_AXIS) // 竖立到Z方向
        );
        return cyl;
    }

    // 中心定位方法
    public void setCenter(double cx, double cy, double cz) {
        setTranslateX(cx);
        setTranslateY(cy);
        setTranslateZ(cz);
    }
}
