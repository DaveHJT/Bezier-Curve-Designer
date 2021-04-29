<<<<<<< HEAD
package PACKAGE_NAME;public class Model {
=======
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

// modified from cs 349 sample code hello_mvc_3
public class Model {
    // data
    // window sizes
    private Stage stage;
    private double toolBarWidth;
    private double menuHeight;

    // draw properties
    private DrawModes drawMode = DrawModes.DRAW;
    private Paint color = Color.BLACK;
    private int thicknessLevel;
    private DrawStyles drawStyle = DrawStyles.LINE;

    // draw data
    private double drawStartX;
    private double drawStartY;
    private double drawControl1X;
    private double drawControl1Y;
    private boolean drawStarted;
    private ArrayList<BezierCurve> curves;

    // select
    private BezierCurve selectedCurve;

    // the last curve drawn, keep record to draw subsequent curves
    private BezierCurve lastCurve;

    // drag offsets of control points
    private double offsetX = 0;
    private double offsetY = 0;

    // drag offsets of control points
    private double lastMouseX = 0;
    private double lastMouseY = 0;

    // save file constants
    final String DELIMITER = ",";
    final String ENDL = "\n";

    // determin whether need to save
    private boolean isSaved = false;

    Model(Stage stage, double menuHeight) {
        curves = new ArrayList<>();
        this.stage = stage;
        this.menuHeight = menuHeight;
        this.drawStarted = false;

        lastCurve = null;

        // Toolbar width
        toolBarWidth = 160;

        // notify observers
        // notify the observers when the window is resized
        stage.widthProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldWidth, Number newWidth) {
                notifyObservers();
            }
        });
        stage.heightProperty().addListener(new ChangeListener<Number>(){
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldHeight, Number newHeight) {
                notifyObservers();
            }
        });

        stage.addEventHandler(KeyEvent.KEY_PRESSED, stopKeyHandler);
        stage.addEventHandler(KeyEvent.KEY_PRESSED, deleteKeyHandler);
    }

    // del to delete selected curve
    private EventHandler<KeyEvent> deleteKeyHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent e) {
            if (e.getCode() == KeyCode.DELETE || e.getCode() == KeyCode.BACK_SPACE) {
                removeCurve(selectedCurve);
                stopAction(null);
                notifyObservers();
            }
        }
    };

    // esc to exit draw mode
    private EventHandler<KeyEvent> stopKeyHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent e) {
            if (e.getCode() == KeyCode.ESCAPE) {
                stopAction(null);
            }
        }
    };

    // mouse click to exit draw mode
    public EventHandler<MouseEvent> stopMouseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            stopAction(null);
        }
    };

    // set the drawing start point
    public void setDrawStart(double drawStartX, double drawStartY) {
        this.drawControl1X = drawStartX;
        this.drawControl1Y = drawStartY;
        this.drawStartX = drawStartX;
        this.drawStartY = drawStartY;
        drawStarted = true;
    }

    // set the drawing start control1
    public void setDrawControl1(double drawControl1X, double drawControl1Y) {
        this.drawControl1X = drawControl1X;
        this.drawControl1Y = drawControl1Y;
    }

    // stop drawing
    public void stopDraw() {
        drawStarted = false;
        lastCurve = null;
        selectedCurve = null;
        notifyObservers();
    }

    // stop selecting
    public void stopSelect() {
        selectedCurve = null;
        notifyObservers();
    }

    // stop pointing
    public void stopPoint() {
        drawMode = DrawModes.SELECT;
        notifyObservers();
    }

    // stop all the actions
    public void stopAction(DrawModes nextMode) {
        switch (drawMode) {
            case DRAW:
                stopDraw();
                break;
            case SELECT:
                if (nextMode == DrawModes.ERASE || nextMode == DrawModes.DRAW ||
                        nextMode == null) {
                    stopSelect();
                }
                break;
            case POINT:
                stopPoint();
                break;
        }
    }

    // when the drawing start point is set, draw the curve
    public BezierCurve drawBezierCurve(double drawEndX, double drawEndY) {
        if (isDrawStarted()) {
            BezierCurve curve = new BezierCurve();

            // the curve is auto selected
            selectedCurve = curve;

            updatePreviewCurve(curve, drawEndX, drawEndY);
            curve.createControlWidgets();
            initControlActions(curve);
            // add to model
            curves.add(curve);

            // init curve actions
            initCurveActions(curve);


            // bind the last curve end point with the new curve start point
            if (lastCurve != null) {
                // bind the curve point

                lastCurve.combineCurves(curve);
                // lastCurve.endXProperty().bindBidirectional(curve.startXProperty());
                // lastCurve.endYProperty().bindBidirectional(curve.startYProperty());
            }

            // prepare for subsequent drawing of the next curve
            setDrawStart(drawEndX, drawEndY);
            lastCurve = curve;

            // changes made
            isSaved = false;
            notifyObservers();
            return curve;
        }
        return null;
    }

    private void initCurveActions(BezierCurve curve) {
        // set the behaviour when mouse click
        curve.setOnMouseClicked(mouseEvent -> {
            DrawModes mode = getDrawMode();
            switch (mode) {
                case SELECT:
                    // select a curve
                    selectedCurve = null;
                    selectedCurve = curve;

                    // update the properties
                    color = curve.getStroke();
                    thicknessLevel = curve.getThicknessLevel();
                    drawStyle = curve.getDrawStyle();
                    notifyObservers();
                    break;
                case ERASE:
                    removeCurve(curve);
                    notifyObservers();
                    break;
            }
        });

        // set the behaviour when mouse press
        curve.setOnMousePressed(mouseEvent -> {
            DrawModes mode = getDrawMode();
            switch (mode) {
                case SELECT:
                    if (curve == selectedCurve) {
                        // record the mouse pos
                        lastMouseX= mouseEvent.getX();
                        lastMouseY = mouseEvent.getY();
                    }
                    break;
            }
        });


        // set the behaviour when mouse drag
        curve.setOnMouseDragged(mouseEvent -> {
            double mouseX = mouseEvent.getX();
            double mouseY = mouseEvent.getY();
            DrawModes mode = getDrawMode();
            switch (mode) {
                case SELECT:
                    if (selectedCurve == curve) {
                        double deltaX = mouseX - lastMouseX;
                        double deltaY = mouseY - lastMouseY;
                        selectedCurve.move(deltaX, deltaY);
                        lastMouseX = mouseX;
                        lastMouseY = mouseY;
                        // changes made
                        isSaved = false;
                        // notifyObservers();
                    }
                    break;
            }
        });
    }


    public void updatePreviewCurve(BezierCurve curve, double drawEndX, double drawEndY) {
        double diffX = drawEndX - drawStartX;
        double diffY = drawEndY - drawStartY;
        // double length = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));

        // default length of the control bar
        // double controlLength = length * 0.2;
        // double controlX = diffX / length * controlLength;
        // double controlY = diffY / length * controlLength;

        // set position data
        curve.setStartX(drawStartX);
        curve.setStartY(drawStartY);
        curve.setEndX(drawEndX);
        curve.setEndY(drawEndY);

        // set the control points
        if (lastCurve == null) {
            curve.setControlX1(drawControl1X);
            curve.setControlY1(drawControl1Y);
        } else {
            // mirror
            double lastDiffX = lastCurve.getControlX2() - drawStartX;
            double lastDiffY = lastCurve.getControlY2() - drawStartY;
            curve.setControlX1(drawStartX - lastDiffX);
            curve.setControlY1(drawStartY - lastDiffY);
        }
        curve.setControlX2(drawEndX);
        curve.setControlY2(drawEndY);

        // set style
        Paint defaultFill = Color.TRANSPARENT;
        curve.setFill(defaultFill);
        curve.setStrokeLineCap(StrokeLineCap.ROUND);
        curve.setDrawStyle(drawStyle);
        curve.setThicknessLevel(thicknessLevel);
        curve.setStroke(getColor());
    }

    public void removeCurve(BezierCurve curve) {
        removeCurve(curve, Direction.BOTH);
        // changes made
        isSaved = false;
    }

    public void removeCurve(BezierCurve curve, Direction dir) {
        if (curve != null) {
            curves.remove(curve);
            // update other segments
            switch (dir) {
                case START:
                    removeCurve(curve.getStartBind(), dir);
                    break;
                case END:
                    removeCurve(curve.getEndBind(), dir);
                    break;
                case BOTH:
                    removeCurve(curve.getStartBind(), Direction.START);
                    removeCurve(curve.getEndBind(), Direction.END);
                    break;
            }
            notifyObservers();
        }
    }
    public void updateCurveProperties(BezierCurve curve) {
        updateCurveProperties(curve, Direction.BOTH);
    }

    public void updateCurveProperties(BezierCurve curve, Direction dir) {
        if (curve != null) {
            curve.setDrawStyle(drawStyle);
            curve.setStroke(color);
            curve.setThicknessLevel(thicknessLevel);

            curve.updateControlWidgets();
            // update other segments
            switch (dir) {
                case START:
                    updateCurveProperties(curve.getStartBind(), dir);
                    break;
                case END:
                    updateCurveProperties(curve.getEndBind(), dir);
                    break;
                case BOTH:
                    updateCurveProperties(curve.getStartBind(), Direction.START);
                    updateCurveProperties(curve.getEndBind(), Direction.END);
                    break;
            }
            notifyObservers();
        }
    }

    public void initControlActions(BezierCurve curve) {
        ArrayList<ControlPoint> points = curve.getControlPoints();
        for (ControlPoint controlPoint : points) {
            // add mouse control
            controlPoint.setOnMousePressed(mouseEvent -> {
                if (drawMode == DrawModes.SELECT) {
                    // record a delta distance for the drag and drop operation.
                    offsetX = controlPoint.getCenterX() - mouseEvent.getX();
                    offsetY = controlPoint.getCenterY() - mouseEvent.getY();
                    // switch color
                    controlPoint.switchColor();
                    mouseEvent.consume();
                } else if (drawMode == DrawModes.POINT) {
                    PointType pointType = controlPoint.getType();
                    // switch between smooth and sharp connection type
                    switch (pointType) {
                        case SEGMENT:
                        case SMOOTH:
                            controlPoint.setType(PointType.SHARP);
                            controlPoint.switchColor();
                            for (ControlPoint sharpPoint : controlPoint.getBindedPoints()) {
                                sharpPoint.bindBidirectional(controlPoint);
                            }
                            break;
                        case SHARP:
                            ArrayList<ControlPoint> bindedPoints = controlPoint.getBindedPoints();
                            if (bindedPoints.size() == 1) {
                                controlPoint.setType(PointType.SEGMENT);
                            } else {
                                controlPoint.setType(PointType.SMOOTH);
                            }
                            double defaultLength = 40;
                            double direction = 1;
                            double centerX = controlPoint.getCenterX();
                            double centerY = controlPoint.getCenterY();
                            for (ControlPoint smoothPoint : controlPoint.getBindedPoints()) {
                                smoothPoint.unbindBidirectional(controlPoint);
                                smoothPoint.setCenterX(centerX + defaultLength * direction);
                                smoothPoint.setCenterY(centerY);
                                direction *= -1;
                            }
                            controlPoint.switchColor();
                            break;
                    }
                    // changes made
                    isSaved = false;
                    notifyObservers();
                }
            });

            controlPoint.setOnMouseReleased(mouseEvent -> {
                if (controlPoint.isColorSwitched()) {
                    controlPoint.switchColor();
                }
            });
/*
                controlPoint.setOnMouseExited(mouseEvent -> {
                    if (controlPoint.isColorSwitched()) {
                        controlPoint.switchColor();
                    }
                });
*/
            controlPoint.setOnMouseDragged(mouseEvent -> {
                if (drawMode == DrawModes.SELECT) {
                    double newX = mouseEvent.getX() + offsetX;
                    double newY = mouseEvent.getY() + offsetY;

                    PointType pointType = controlPoint.getType();
                    switch (pointType) {
                        case SHARP:
                        case SEGMENT:
                        case SMOOTH:
                            for (ControlPoint bindControlPoint : controlPoint.getBindedPoints()) {
                                if (bindControlPoint != null) {
                                    double diffX = bindControlPoint.getCenterX() - controlPoint.getCenterX();
                                    double diffY = bindControlPoint.getCenterY() - controlPoint.getCenterY();
                                    bindControlPoint.setCenterX(newX + diffX);
                                    bindControlPoint.setCenterY(newY + diffY);
                                }
                            }
                            break;
                        case CONTROL:
                            ArrayList<ControlPoint> segmentPoints = controlPoint.getBindedPoints();
                            if (segmentPoints.size() == 1) {
                                ControlPoint segmentPoint = segmentPoints.get(0);
                                if (segmentPoint.getType() == PointType.SMOOTH) {
                                    for (ControlPoint anotherControlPoint : segmentPoint.getBindedPoints()) {
                                        if (anotherControlPoint != controlPoint) {
                                            double centerX = segmentPoint.getCenterX();
                                            double centerY = segmentPoint.getCenterY();
                                            // get the relative position from the current control point to the segmant point
                                            double diffX1 = controlPoint.getCenterX() - centerX;
                                            double diffY1 = controlPoint.getCenterY() - centerY;
                                            // get the relative position from the other control point to the segmant point
                                            double diffX2 = anotherControlPoint.getCenterX() - centerX;
                                            double diffY2 = anotherControlPoint.getCenterY() - centerY;
                                            // calculate the original length of the other control bar
                                            double length1 = Math.sqrt(Math.pow(diffX1, 2) + Math.pow(diffY1, 2));
                                            double length2 = Math.sqrt(Math.pow(diffX2, 2) + Math.pow(diffY2, 2));
                                            double ratio = length2;
                                            if (length1 != 0) {
                                                if (length2 == 0) ratio += 1;
                                                ratio /= length1;
                                            }
                                            diffX2 = ratio * diffX1;
                                            diffY2 = ratio * diffY1;
                                            anotherControlPoint.setCenterX(centerX - diffX2);
                                            anotherControlPoint.setCenterY(centerY - diffY2);
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                    }

                    controlPoint.setCenterX(newX);
                    controlPoint.setCenterY(newY);

                    // changes made
                    isSaved = false;
                    mouseEvent.consume();
                }
            });
        }
    }

    // is the drawing started
    public boolean isDrawStarted() {
        return drawStarted;
    }

    // all views of this model
    private ArrayList<IView> views = new ArrayList<IView>();

    // method that the views can use to register themselves with the Model
    // once added, they are told to update and get state from the Model
    public void addView(IView view) {
        views.add(view);
        view.updateView();
    }

    // get window size
    public double getWindowWidth() {
        return stage.getWidth();
    }

    public double getWindowHeight() {
        return stage.getHeight();
    }

    // get/set draw mode
    public DrawModes getDrawMode() {
        return drawMode;
    }

    public void setDrawMode(DrawModes drawMode) {
        stopAction(drawMode);
        this.drawMode = drawMode;
        if (drawMode == DrawModes.POINT && selectedCurve == null) {
            stopPoint();
        }
        notifyObservers();
    }

    // get/set color
    public Paint getColor() {
        return color;
    }

    public void setColor(Paint color) {
        this.color = color;
        updateCurveProperties(selectedCurve);
    }

    // get/set thickness
    // set four levels 1-4
    public void setThicknessLevel(int thicknessLevel) {
        this.thicknessLevel = thicknessLevel;
        updateCurveProperties(selectedCurve);
        notifyObservers();
    }

    // get/set draw style
    public void setDrawStyle(DrawStyles drawStyle) {
        this.drawStyle = drawStyle;
        updateCurveProperties(selectedCurve);
        notifyObservers();
    }


    // get toolBar width
    public double getToolBarWidth() {
        return toolBarWidth;
    }

    // get canvas width
    public double getCanvasWidth() {
        return getWindowWidth() - toolBarWidth;
    }

    // get menu height
    public double getMenuHeight() {
        return menuHeight;
    }

    // get curves
    public ArrayList<BezierCurve> getCurves() {
        return curves;
    }

    // get selected curve
    public BezierCurve getSelectedCurve() {
        return selectedCurve;
    }

    public BezierCurve getLastCurve() {
        return lastCurve;
    }

    public boolean isProjectEmpty() {
        if (curves.size() == 0) {
            return true;
        }
        return false;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void newProject() {
        curves.clear();
        isSaved = true;
        notifyObservers();
    }

    // modified from cs349 sample code: data
    public void saveProject(BufferedWriter writer) {
        try {
            int row = 0;
            ArrayList<BezierCurve> unsavedCurves = (ArrayList<BezierCurve>) curves.clone();
            while (!unsavedCurves.isEmpty()) {
                BezierCurve unsavedCurve = unsavedCurves.get(0);
                BezierCurve savingCurve = unsavedCurve.getStartSegment();
                String color = savingCurve.getStroke().toString();
                int thicknessLevel = savingCurve.getThicknessLevel();
                String style = savingCurve.getDrawStyle().toString();
                writer.write(
                        ++row + DELIMITER +
                                color + DELIMITER +
                                thicknessLevel + DELIMITER +
                                style + DELIMITER
                );
                int count = 0;
                while (true) {
                    double startX = savingCurve.getStartX();
                    double startY = savingCurve.getStartY();
                    double endX = savingCurve.getEndX();
                    double endY = savingCurve.getEndY();
                    double controlX1 = savingCurve.getControlX1();
                    double controlY1 = savingCurve.getControlY1();
                    double controlX2 = savingCurve.getControlX2();
                    double controlY2 = savingCurve.getControlY2();
                    String startPointType = savingCurve.getStart().getType().toString();
                    String endPointType = savingCurve.getEnd().getType().toString();
                    writer.write(
                            ++count + DELIMITER +
                                    startX + DELIMITER +
                                    startY + DELIMITER +
                                    endX + DELIMITER +
                                    endY + DELIMITER +
                                    controlX1 + DELIMITER +
                                    controlY1 + DELIMITER +
                                    controlX2 + DELIMITER +
                                    controlY2 + DELIMITER +
                                    startPointType + DELIMITER +
                                    endPointType + DELIMITER
                    );
                    unsavedCurves.remove(savingCurve);
                    savingCurve = savingCurve.getEndBind();
                    if (savingCurve == null) break;
                }
                // all segments of the current curve are saved
                writer.write(ENDL);
            }
            writer.close();

            // saved
            isSaved = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // modified from cs349 sample code: data
    public void loadProject(BufferedReader reader) {
        try {
            String[] values;
            String line;

            // save the current choices
            Paint colorSave = getColor();
            int thicknessLevelSave = getThicknessLevel();
            DrawStyles styleSave = getDrawStyle();
            DrawModes modeSave = getDrawMode();

            // set draw mode
            setDrawMode(DrawModes.DRAW);
            while ((line = reader.readLine()) != null) {
                // DELIMITER separates values on a row
                values = line.split(DELIMITER);

                // process the values
                int row = Integer.parseInt(values[0]);
                Color color = Color.web(values[1]);
                int thicknessLevel = Integer.parseInt(values[2]);
                DrawStyles style = DrawStyles.valueOf(values[3]);
                setColor(color);
                setThicknessLevel(thicknessLevel);
                setDrawStyle(style);
                int base = 4;
                int dataSize = 11;
                int count = 0;
                // do something with values here e.g. print them
                while (values.length - base - count * dataSize > 0) {
                    int lineStart = base + count * dataSize;
                    count = Integer.parseInt(values[lineStart]);
                    double startX = Double.parseDouble(values[lineStart + 1]);
                    double startY = Double.parseDouble(values[lineStart + 2]);
                    double endX = Double.parseDouble(values[lineStart + 3]);
                    double endY = Double.parseDouble(values[lineStart + 4]);
                    double controlX1 = Double.parseDouble(values[lineStart + 5]);
                    double controlY1 = Double.parseDouble(values[lineStart + 6]);
                    double controlX2 = Double.parseDouble(values[lineStart + 7]);
                    double controlY2 = Double.parseDouble(values[lineStart + 8]);
                    PointType startPointType = PointType.valueOf(values[lineStart + 9]);
                    PointType endPointType = PointType.valueOf(values[lineStart + 10]);

                    // draw the curve
                    this.setDrawStart(startX, startY);
                    this.setDrawControl1(controlX1, controlY1);
                    BezierCurve newCurve = this.drawBezierCurve(endX, endY);
                    newCurve.setControlX2(controlX2);
                    newCurve.setControlY2(controlY2);
                    newCurve.getStart().setType(startPointType);
                    newCurve.getEnd().setType(endPointType);
                }
                // stop draw
                stopDraw();
            }
            // load the choices
            setColor(colorSave);
            setThicknessLevel(thicknessLevelSave);
            setDrawStyle(styleSave);
            setDrawMode(modeSave);
            // saved
            isSaved = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getThicknessLevel() {
        return thicknessLevel;
    }

    public DrawStyles getDrawStyle() {
        return drawStyle;
    }

    // the model uses this method to notify all of the Views that the data has changed
    // the expectation is that the Views will refresh themselves to display new data when appropriate
    private void notifyObservers() {
        for (IView view : this.views) {
            // System.out.println("Model: notify View");
            view.updateView();
        }
    }
>>>>>>> d5eed7ed92c6d42e48bda6ae061e950156340942
}
