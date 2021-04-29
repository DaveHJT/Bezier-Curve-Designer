    import javafx.beans.property.DoubleProperty;
    import javafx.scene.paint.Color;
    import javafx.scene.paint.Paint;
    import javafx.scene.shape.*;

    import java.util.ArrayList;

    public class BezierCurve extends CubicCurve {
        private DrawStyles style;
        private int thicknessLevel;
        private BezierCurve startBind;
        private BezierCurve endBind;

        // select widgets
        private ControlPoint start;
        private ControlPoint control1;
        private ControlPoint control2;
        private ControlPoint end;
        private Line startBar;
        private Line endBar;



        // constants
        private final double radiusControlPoint = 8;
        private final double radiusCurvePoint = 12;

        // thickness levels
        private final double thicknessLevel1 = 3;
        private final double thicknessLevel2 = 6;
        private final double thicknessLevel3 = 8;
        private final double thicknessLevel4 = 11;

        public void createControlWidgets() {
            control1 = new ControlPoint(this.controlX1Property(),
                    this.controlY1Property(), radiusControlPoint, PointType.CONTROL, this);
            control2 = new ControlPoint(this.controlX2Property(),
                    this.controlY2Property(), radiusControlPoint, PointType.CONTROL, this);
            start = new ControlPoint(this.startXProperty(),
                    this.startYProperty(), radiusCurvePoint, PointType.SEGMENT, this);
            end = new ControlPoint(this.endXProperty(),
                    this.endYProperty(), radiusCurvePoint, PointType.SEGMENT, this);
            startBar = createControlBar(this.startXProperty(),
                    this.startYProperty(), this.controlX1Property(),
                    this.controlY1Property());
            endBar = createControlBar(this.endXProperty(),
                    this.endYProperty(), this.controlX2Property(),
                    this.controlY2Property());

            start.addBindBidirectional(control1);
            end.addBindBidirectional(control2);
            updateControlWidgets();
        }

        // set the actual thickness value of the levels to line
        public void setThicknessLevel(int thicknessLevel) {
            this.thicknessLevel = thicknessLevel;
            double thickness = thicknessLevel2;
            switch (thicknessLevel) {
                case 1:
                    thickness = thicknessLevel1;
                    break;
                case 2:
                    thickness = thicknessLevel2;
                    break;
                case 3:
                    thickness = thicknessLevel3;
                    break;
                case 4:
                    thickness = thicknessLevel4;
                    break;
            }
            this.setStrokeWidth(thickness);
            this.setDrawStyle(getDrawStyle());
        }

        public int getThicknessLevel() {
            return thicknessLevel;
        }

        // apply the draw style to the line
        public void setDrawStyle(DrawStyles drawStyle) {
            this.style = drawStyle;
            //clear the previous style first
            this.getStrokeDashArray().clear();
            double gap = 5d * thicknessLevel;
            switch (drawStyle) {
                case DASH:
                    this.getStrokeDashArray().addAll(50d, gap);
                    break;
                case DOT:
                    this.getStrokeDashArray().addAll(5d , gap);
                    break;
                case DASHDOT:
                    this.getStrokeDashArray().addAll(40d, gap, 10d, gap);
                    break;
                case LINE:
                    // do nothing
                    break;
            }
        }

        public void setStartBind(BezierCurve startBind) {
            this.startBind = startBind;
        }

        public BezierCurve getStartBind() {
            return startBind;
        }

        public void setEndBind(BezierCurve endBind) {
            this.endBind = endBind;
        }

        public BezierCurve getEndBind() {
            return endBind;
        }

        private Line createControlBar(DoubleProperty startX, DoubleProperty startY, DoubleProperty controlX, DoubleProperty controlY) {
            Line controlBar = new Line();

            // set up the style
            double strokeWidth = 1;
            Color color = (Color)getStroke();
            if (color == null) color = Color.BLACK;
            Color strokeColor = getBrighterColor(color);
            controlBar.setStrokeWidth(strokeWidth);
            controlBar.setStroke(strokeColor);
            controlBar.setStrokeLineCap(StrokeLineCap.SQUARE);
            controlBar.getStrokeDashArray().setAll(10d, 5d);
            // bind the start and end
            controlBar.startXProperty().bindBidirectional(startX);
            controlBar.startYProperty().bindBidirectional(startY);
            controlBar.endXProperty().bindBidirectional(controlX);
            controlBar.endYProperty().bindBidirectional(controlY);

            return controlBar;
        }

        private Color getBrighterColor(Color color) {
            return color.deriveColor(1, 1.5, 0.8, 0.2);
        }

        private Color getDarkerColor(Color color) {
            return color.deriveColor(1, 1.2, 0.8, 1);
        }

        public DrawStyles getDrawStyle() {
            return style;
        }

        public ArrayList<Shape> getControlWidgets() {
            ArrayList<Shape> widgets = new ArrayList<Shape>();
            // add to widgets
                widgets.add(startBar);
                widgets.add(start);
                widgets.add(control1);
                widgets.add(endBar);
                widgets.add(end);
                widgets.add(control2);

            return widgets;
        }

        public ArrayList<Shape> getDisplayedWidgets() {
            ArrayList<Shape> widgets = new ArrayList<Shape>();
            // add to widgets
            // hide the control widgets if the point type is sharp
            widgets.add(start);
            if (start.getType() != PointType.SHARP) {
                widgets.add(startBar);
                widgets.remove(start);
                widgets.add(start);
                widgets.add(control1);
            }
            widgets.add(end);
            if (end.getType() != PointType.SHARP) {
                widgets.add(endBar);
                widgets.remove(end);
                widgets.add(end);
                widgets.add(control2);
            }
            return widgets;
        }

        public void updateControlWidgets() {
            for (Shape widget : getControlWidgets()) {
                // set up the styles
                Paint color = getStroke();
                Color fillColor = (Color)color;
                Color strokeColor = (Color)color;
                fillColor = getBrighterColor(fillColor);
                strokeColor = getDarkerColor(strokeColor);
                widget.setFill(fillColor);
                widget.setStroke(strokeColor);
            }
        }

        public ArrayList<ControlPoint> getControlPoints() {
            ArrayList<ControlPoint> controlPoints = new ArrayList<ControlPoint>();
            controlPoints.add(start);
            if (end != null) controlPoints.add(end);
            controlPoints.add(control1);
            controlPoints.add(control2);
            return  controlPoints;
        }

        public ControlPoint getStart() {
            return start;
        }

        public void combineCurves(BezierCurve newCurve) {
            if (newCurve != null) {
                setEndBind(newCurve);
                newCurve.setStartBind(this);

                ControlPoint newEnd = newCurve.getStart();
                // rebind end bar with the new end
                endBar.startXProperty().unbindBidirectional(end.centerXProperty());
                endBar.startYProperty().unbindBidirectional(end.centerYProperty());
                endBar.startXProperty().bindBidirectional(newEnd.centerXProperty());
                endBar.startYProperty().bindBidirectional(newEnd.centerYProperty());

                // rebind curve end with the new end
                this.endXProperty().unbindBidirectional(end.centerXProperty());
                this.endYProperty().unbindBidirectional(end.centerYProperty());
                this.endXProperty().bindBidirectional(newEnd.centerXProperty());
                this.endYProperty().bindBidirectional(newEnd.centerYProperty());

                control2.unBindAll();
                newEnd.addBindBidirectional(control2);
                newEnd.setType(PointType.SMOOTH);
                this.end = newEnd;
            }
        }

        public ControlPoint getEnd() {
            return end;
        }

        // move the whole curve
        public void move(Double deltaX, Double deltaY) {
            moveHelper(deltaX, deltaY, Direction.BOTH);
        }

        private void moveHelper(Double deltaX, Double deltaY, Direction dir) {
            // when it's SHARP control point are bound, so avoid double movement
            if (start.getType() != PointType.SHARP) {
                this.setControlX1(getControlX1() + deltaX);
                this.setControlY1(getControlY1() + deltaY);
            }
            if (end.getType() != PointType.SHARP) {
                this.setControlX2(getControlX2() + deltaX);
                this.setControlY2(getControlY2() + deltaY);
            }
            switch (dir) {
                case START:
                    this.setStartX(getStartX() + deltaX);
                    this.setStartY(getStartY() + deltaY);
                    if (startBind != null) getStartBind().moveHelper(deltaX, deltaY, dir);
                    break;
                case END:
                    this.setEndX(getEndX() + deltaX);
                    this.setEndY(getEndY() + deltaY);
                    if (endBind != null) getEndBind().moveHelper(deltaX, deltaY, dir);
                    break;
                case BOTH:
                    this.setEndX(getEndX() + deltaX);
                    this.setEndY(getEndY() + deltaY);
                    this.setStartX(getStartX() + deltaX);
                    this.setStartY(getStartY() + deltaY);
                    if (startBind != null) getStartBind().moveHelper(deltaX, deltaY, Direction.START);
                    if (endBind != null) getEndBind().moveHelper(deltaX, deltaY, Direction.END);
                    break;
            }
        }

        // get start segment of the curve
        public BezierCurve getStartSegment() {
            if (startBind != null) return startBind.getStartSegment();
            return this;
        }
    }
