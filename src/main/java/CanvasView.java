import javafx.scene.layout.*;
import javafx.scene.shape.*;

import java.util.ArrayList;

public class CanvasView extends Pane implements IView {
    // some codes are modified from cs 349 sample code hello_mvc3
    private Model model; // reference to the model

    private BezierCurve previewCurve;

    private boolean displayWidgets;

    CanvasView(Model model) {
        // keep track of the model
        this.model = model;

        displayWidgets = false;

        // init preview curve
        previewCurve = new BezierCurve();
        model.updatePreviewCurve(previewCurve, 0, 0);
        previewCurve.setVisible(false);
        previewCurve.setDisable(true);
        previewCurve.createControlWidgets();
        // init preview bar and point
        // previewPoint = new ControlPoint(this.controlX2Property(),
                // this.controlY2Property(), radiusControlPoint, PointType.CONTROL, this);
        // this.getChildren().add(previewCurve);

        final Rectangle clip = new Rectangle();
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        this.setClip(clip);
        this.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            clip.setWidth(newValue.getWidth());
            clip.setHeight(newValue.getHeight());
        });

        // update the canvas size
        updateSize();

        // on click
        this.setOnMousePressed(mouseEvent -> {
            double mouseX = mouseEvent.getX();
            double mouseY = mouseEvent.getY();

            DrawModes mode = model.getDrawMode();

            switch (mode) {
                case DRAW:
                    // if not started, set the start point and preview
                    if (!model.isDrawStarted()) {
                        model.setDrawStart(mouseX, mouseY);

                        model.updatePreviewCurve(previewCurve, mouseX, mouseY);
                        previewCurve.updateControlWidgets();
                        // enable preview
                        previewCurve.setVisible(true);
                        previewCurve.setDisable(false);
                        updateView();

                    } else {
                        BezierCurve newCurve = model.drawBezierCurve(mouseX, mouseY);
                    }
                    mouseEvent.consume();
                    break;
            }
        });

        // on drag
        this.setOnMouseDragged(mouseEvent -> {
            double mouseX = mouseEvent.getX();
            double mouseY = mouseEvent.getY();

            DrawModes mode = model.getDrawMode();

            switch (mode) {
                case DRAW:
                    // if not started, set the start point and preview
                    if (model.isDrawStarted()) {
                        if (model.getLastCurve() == null) {
                            model.setDrawControl1(mouseX, mouseY);

                            previewCurve.setControlX1(mouseX);
                            previewCurve.setControlY1(mouseY);
                            previewCurve.setControlX2(mouseX);
                            previewCurve.setControlY2(mouseY);

                            // disable preview and don't update the view so only control
                            // widgets are displayed
                            previewCurve.setVisible(false);
                            previewCurve.setDisable(true);
                        } else {
                            // drag the control point of the selected curve just drawn
                            BezierCurve selectedCurve = model.getSelectedCurve();
                            if (selectedCurve != null) {
                                selectedCurve.setControlX2(mouseX);
                                selectedCurve.setControlY2(mouseY);

                                // disable preview and don't update the view so only control
                                // widgets are displayed
                                previewCurve.setVisible(false);
                                previewCurve.setDisable(true);
                            }
                            // drag the control point of preview curve to the opposite direction
                            // mirror
                            double diffX = selectedCurve.getControlX2() - selectedCurve.getEndX();
                            double diffY = selectedCurve.getControlY2() - selectedCurve.getEndY();
                            previewCurve.setControlX2(selectedCurve.getEndX() - diffX);
                            previewCurve.setControlY2(selectedCurve.getEndY() - diffY);
                        }
                    }
                    mouseEvent.consume();
                    break;
            }
        });

        // on release
        this.setOnMouseReleased(mouseEvent -> {
            double mouseX = mouseEvent.getX();
            double mouseY = mouseEvent.getY();
            DrawModes mode = model.getDrawMode();
            switch (mode) {
                case DRAW:
                    // if not started, set the start point and preview
                    if (model.isDrawStarted()) {
                        // enable preview
                        previewCurve.setVisible(true);
                        previewCurve.setDisable(false);

                        model.updatePreviewCurve(previewCurve, mouseX, mouseY);
                        previewCurve.updateControlWidgets();
                    }
                    break;
            }
        });

        // preview on hover
        this.setOnMouseMoved(mouseEvent -> {
            double mouseX = mouseEvent.getX();
            double mouseY = mouseEvent.getY();

            DrawModes mode = model.getDrawMode();

            switch (mode) {
                case DRAW:
                    // if not started, set the start point and preview
                    if (model.isDrawStarted()) {
                        model.updatePreviewCurve(previewCurve, mouseX, mouseY);
                        previewCurve.updateControlWidgets();
                    }
                    break;
            }
        });


        // register with the model when we're ready to start receiving data
        model.addView(this);
    }

    private void updateSize() {
        double windowHeight = model.getWindowHeight();
        double width = model.getCanvasWidth();
        double menuHeight = model.getMenuHeight();
        double height = windowHeight - menuHeight;
        this.setMaxSize(width, height);
        this.setPrefSize(width, height);
    }

    private void addPointsToChildren(BezierCurve curve, Direction dir) {
        if (curve != null) {
            // add other segments
            ArrayList widgets = curve.getDisplayedWidgets();;
            switch (dir) {
                case START:
                    widgets.remove(curve.getEnd());
                    this.getChildren().addAll(widgets);
                    addPointsToChildren(curve.getStartBind(), dir);
                    break;
                case END:
                    widgets.remove(curve.getStart());
                    this.getChildren().addAll(widgets);
                    addPointsToChildren(curve.getEndBind(), dir);
                    break;
                case BOTH:
                    this.getChildren().addAll(widgets);
                    addPointsToChildren(curve.getStartBind(), Direction.START);
                    addPointsToChildren(curve.getEndBind(), Direction.END);
                    break;
            }
        }
    }

    @Override
    public void updateView() {
        // stop preview when drawing
        if (!model.isDrawStarted() && !previewCurve.isDisabled()) {
            previewCurve.setVisible(false);
            previewCurve.setDisable(true);
        }

        //  determine whether the select widgets is displayed
        DrawModes mode = model.getDrawMode();
        switch (mode) {
            case SELECT:
            case DRAW:
            case POINT:
                BezierCurve selectedCurve = model.getSelectedCurve();
                if (selectedCurve != null) {
                    displayWidgets = true;
                } else {
                    displayWidgets = false;
                }
                break;
            case ERASE:
                displayWidgets = false;
                break;

        }
        // reset children
        this.getChildren().clear();
        this.getChildren().addAll(model.getCurves());
        if (mode == DrawModes.DRAW) {
            this.getChildren().add(previewCurve);
            if (previewCurve.isVisible()) {
                this.getChildren().addAll(previewCurve.getDisplayedWidgets());
            }
        }
        if (displayWidgets) {
            addPointsToChildren(model.getSelectedCurve(), Direction.BOTH);
        }
        updateSize();
    }
}

