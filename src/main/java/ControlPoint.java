import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

import java.util.ArrayList;

public class ControlPoint extends Circle {
    private ArrayList<ControlPoint> bindedPoints = new ArrayList<ControlPoint>();
    private PointType type;
    private boolean colorSwitched;
    private DoubleProperty bindX;
    private DoubleProperty bindY;

    ControlPoint(DoubleProperty bindX, DoubleProperty bindY, double radius, PointType type, BezierCurve bindCurve) {
        super();
        this.type = type;
        this.setRadius(radius);
        double strokeWidth = 3;
        this.setStrokeWidth(strokeWidth);
        this.setStrokeType(StrokeType.OUTSIDE);
        this.bindX = bindX;
        this.bindY = bindY;

        // bind the property with the control point
        this.bind();
    }

    // bind with bindX and bindY given when constructed
    public void bind() {
        this.centerXProperty().bindBidirectional(bindX);
        this.centerYProperty().bindBidirectional(bindY);
    }

    public void unbind() {
        this.centerXProperty().unbindBidirectional(bindX);
        this.centerYProperty().unbindBidirectional(bindY);
    }

    // add bind with the given point
    public void addBindBidirectional(ControlPoint point) {
        this.bindedPoints.add(point);
        point.addBindedPoints(this);
    }

    // bind with the given point
    public void bindBidirectional(ControlPoint point) {
        this.centerXProperty().bindBidirectional(point.centerXProperty());
        this.centerYProperty().bindBidirectional(point.centerYProperty());
    }

    // unbind with the given point
    public void unbindBidirectional(ControlPoint point) {
        this.centerXProperty().unbindBidirectional(point.centerXProperty());
        this.centerYProperty().unbindBidirectional(point.centerYProperty());
    }

    public void unBindAll() {
        this.bindedPoints.clear();
    }

    public void addBindedPoints(ControlPoint point) {
        this.bindedPoints.add(point);
    }

    public ArrayList<ControlPoint> getBindedPoints() {
        return bindedPoints;
    }

    public PointType getType() {
        return type;
    }

    public void setType(PointType type) {
        this.type = type;
    }

    public boolean isColorSwitched() {
        return colorSwitched;
    }

    void switchColor() {
        Paint fillColor = this.getFill();
        this.setFill(this.getStroke());
        this.setStroke(fillColor);
        if (colorSwitched) {
            colorSwitched = false;
        } else {
            colorSwitched = true;
        }
    }
}
