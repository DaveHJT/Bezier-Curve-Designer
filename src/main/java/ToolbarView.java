import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class ToolbarView extends VBox implements IView {

    // some codes are modified from cs 349 sample code hello_mvc3

    // draw modes palette
    private GridPane toolPalette;

    // color bar, style palette and thickness palette
    private VBox propertyBox;
    private HBox stylePalette;
    private HBox thicknessPalette;

    // draw mode buttons
    private ToggleButton pointButton;
    private ToggleButton selectButton;
    private ToggleButton eraseButton;
    private ToggleButton drawButton;

    // thickness buttons
    private ToggleButton thickness1Button;
    private ToggleButton thickness2Button;
    private ToggleButton thickness3Button;
    private ToggleButton thickness4Button;

    // style buttons
    private ToggleButton lineButton;
    private ToggleButton dashButton;
    private ToggleButton dashDotButton;
    private ToggleButton dotButton;



    private Model model; // reference to the model

    private ColorPicker colorPicker;
    // customize button size
    private double gridButtonSize = 50;
    private double hBoxButtonSize = 30;

    ToolbarView(Model model) {
        // keep track of the model
        this.model = model;

        // customize the style
        double cornerRadiiSize = 0;
        double insetsSizeBg = 0;
        Color color = Color.GRAY;
        Background toolBg = new Background(new BackgroundFill(color, new CornerRadii(cornerRadiiSize), new Insets(insetsSizeBg)));
        this.setBackground(toolBg);
        this.setAlignment(Pos.CENTER);

        // set up tool grid and property box buttons
        toolPalette = new GridPane();
        propertyBox = new VBox();
        initToolGridButtons(toolPalette);
        initPropertyBoxButtons(propertyBox);

        // set up and customize the tool palette
        updateSize();

        // set up the propertyBox
        propertyBox.setAlignment(Pos.CENTER);

        // add tool palette and property box to the Vbox
        this.getChildren().addAll(toolPalette, propertyBox);

        // register with the model when we're ready to start receiving data
        model.addView(this);
    }

    private void updateSize() {
        double windowWidth = model.getWindowWidth();
        double windowHeight = model.getWindowHeight();
        double menuHeight = model.getMenuHeight();
        // double width = windowWidth * widthRatio;
        double width = model.getToolBarWidth();
        double gap = gridButtonSize / 2;
        double height = windowHeight - menuHeight;
        double upperHalfRatio = 1;
        double upperHeight = upperHalfRatio * height;
        double lowerHeight = height - upperHeight;
        this.setMaxSize(width, height);
        this.setPrefSize(width, height);
        // toolPalette.setGridLinesVisible(true);
        toolPalette.setPrefSize(width, upperHeight);
        toolPalette.setHgap(gap);
        toolPalette.setVgap(gap);
        propertyBox.setPrefSize(width, lowerHeight);
        double gridPadding = (width - 2 * gridButtonSize - gap) / 2;
        this.setPadding(new Insets(gridPadding));
        this.setSpacing(20.0);
    }

    private void initPropertyBoxButtons(VBox propertyBox) {
        // set up color picker
        colorPicker = new ColorPicker();
        colorPicker.setValue(Color.BLACK);
        colorPicker.setPrefSize(hBoxButtonSize * 4, hBoxButtonSize);
        colorPicker.setMaxSize(hBoxButtonSize * 4, hBoxButtonSize);
        colorPicker.setMinSize(hBoxButtonSize * 4, hBoxButtonSize);
        // init color in model
        model.setColor(colorPicker.getValue());

        colorPicker.setOnAction(new EventHandler() {
            public void handle(Event t) {
                model.setColor(colorPicker.getValue());
            }
        });

        // set up thickness palette
        thicknessPalette = new HBox();
        thicknessPalette.setAlignment(Pos.CENTER);

        // set up thickness button toggle group
        ToggleGroup thicknessBtnGroup = new ToggleGroup();

        // thickness1 button
        ImageView thickness1Icon = createImageView("thickness3.png", hBoxButtonSize);
        thickness1Button = createToggleButton(thicknessBtnGroup, thickness1Icon, hBoxButtonSize);
        thickness1Button.setOnAction(actionEvent -> {
            model.setThicknessLevel(1);
            thickness1Button.setSelected(true);
        });
        thicknessPalette.getChildren().add(thickness1Button);

        // thickness2 button
        ImageView thickness2Icon = createImageView("thickness4.png", hBoxButtonSize);
        thickness2Button = createToggleButton(thicknessBtnGroup, thickness2Icon, hBoxButtonSize);
        thickness2Button.setOnAction(actionEvent -> {
            model.setThicknessLevel(2);
            thickness2Button.setSelected(true);
        });
        thicknessPalette.getChildren().add(thickness2Button);

        // thickness3 button
        ImageView thickness3Icon = createImageView("thickness5.png", hBoxButtonSize);
        thickness3Button = createToggleButton(thicknessBtnGroup, thickness3Icon, hBoxButtonSize);
        thickness3Button.setOnAction(actionEvent -> {
            model.setThicknessLevel(3);
            thickness3Button.setSelected(true);
        });
        thicknessPalette.getChildren().add(thickness3Button);

        // thickness4 button
        ImageView thickness4Icon = createImageView("thickness6.png", hBoxButtonSize);
        thickness4Button = createToggleButton(thicknessBtnGroup, thickness4Icon, hBoxButtonSize);
        thickness4Button.setOnAction(actionEvent -> {
            model.setThicknessLevel(4);
            thickness4Button.setSelected(true);
        });
        thicknessPalette.getChildren().add(thickness4Button);
        // thicknessPalette.setSpacing(hBoxButtonSize / 3);

        // init thickness in model
        model.setThicknessLevel(2);
        thickness2Button.setSelected(true);

        // set up style palette
        stylePalette = new HBox();
        stylePalette.setAlignment(Pos.CENTER);

        // set up style button toggle group
        ToggleGroup styleBtnGroup = new ToggleGroup();

        // dot button
        ImageView dotIcon = createImageView("dot.png", hBoxButtonSize);
        dotButton = createToggleButton(styleBtnGroup, dotIcon, hBoxButtonSize);
        dotButton.setOnAction(actionEvent -> {
            model.setDrawStyle(DrawStyles.DOT);
            dotButton.setSelected(true);
        });
        stylePalette.getChildren().add(dotButton);

        // dashDot button
        ImageView dashDotIcon = createImageView("dash-dot.png", hBoxButtonSize);
        dashDotButton = createToggleButton(styleBtnGroup, dashDotIcon, hBoxButtonSize);
        dashDotButton.setOnAction(actionEvent -> {
            model.setDrawStyle(DrawStyles.DASHDOT);
            dashDotButton.setSelected(true);
        });
        stylePalette.getChildren().add(dashDotButton);

        // dash button
        ImageView dashIcon = createImageView("dash.png", hBoxButtonSize);
        dashButton = createToggleButton(styleBtnGroup, dashIcon, hBoxButtonSize);
        dashButton.setOnAction(actionEvent -> {
            model.setDrawStyle(DrawStyles.DASH);
            dashButton.setSelected(true);
        });
        stylePalette.getChildren().add(dashButton);

        // line button
        ImageView lineIcon = createImageView("thickness4.png", hBoxButtonSize);
        lineButton = createToggleButton(styleBtnGroup, lineIcon, hBoxButtonSize);
        lineButton.setOnAction(actionEvent -> {
            model.setDrawStyle(DrawStyles.LINE);
            lineButton.setSelected(true);
        });
        stylePalette.getChildren().add(lineButton);

        // init style in model
        model.setDrawStyle(DrawStyles.LINE);
        lineButton.setSelected(true);

        // add these palettes to property box
        propertyBox.getChildren().addAll(colorPicker, thicknessPalette, stylePalette);

        // property box style
        propertyBox.setSpacing(hBoxButtonSize / 2);
    }

    private void initToolGridButtons(GridPane toolPalette) {
        // Toggle button group of draw modes buttons
        ToggleGroup drawModeBtnGroup = new ToggleGroup();
        // draw button
        ImageView drawIcon = createImageView("draw.png", gridButtonSize);
        drawButton = createToggleButton(drawModeBtnGroup, drawIcon, gridButtonSize);
        toolPalette.add(drawButton, 0 ,0);
        drawButton.setOnAction(actionEvent -> {
            drawButton.setSelected(true);
            model.setDrawMode(DrawModes.DRAW);
        });

        // point button
        ImageView pointIcon = createImageView("point.png", gridButtonSize);
        pointButton = createToggleButton(drawModeBtnGroup, pointIcon, gridButtonSize);
        toolPalette.add(pointButton, 1 ,1);
        pointButton.setOnAction(actionEvent -> {
            pointButton.setSelected(true);
            model.setDrawMode(DrawModes.POINT);
        });

        // erase button
        ImageView eraseIcon = createImageView("erase.png", gridButtonSize);
        eraseButton = createToggleButton(drawModeBtnGroup, eraseIcon, gridButtonSize);
        toolPalette.add(eraseButton, 0 ,1);
        eraseButton.setOnAction(actionEvent -> {
            eraseButton.setSelected(true);
            model.setDrawMode(DrawModes.ERASE);
        });

        // select button
        ImageView selectIcon = createImageView("select.png", gridButtonSize);
        selectButton = createToggleButton(drawModeBtnGroup, selectIcon, gridButtonSize);
        toolPalette.add(selectButton, 1 ,0);
        selectButton.setOnAction(actionEvent -> {
            selectButton.setSelected(true);
            model.setDrawMode(DrawModes.SELECT);
        });

        // initially select draw mode
        drawButton.setSelected(true);

        // init draw mode in model
        model.setDrawMode(DrawModes.DRAW);
    }

    private ImageView createImageView(String path, double size) {
        ImageView icon = new ImageView(new Image(path));
        icon.setFitHeight(size);
        icon.setFitWidth(size);
        return icon;
    }

    private ToggleButton createToggleButton(ToggleGroup toggleGroup, ImageView icon, double size) {
        ToggleButton btn = new ToggleButton();
        btn.setToggleGroup(toggleGroup);
        btn.setGraphic(icon);
        btn.setPrefSize(size, size);
        btn.setMaxSize(size, size);
        btn.setMinSize(size, size);
        btn.setFocusTraversable(true);
        return btn;
    }


    private void updateToolStates() {
        colorPicker.setValue((Color)model.getColor());

        DrawModes mode = model.getDrawMode();
        switch (mode) {
            case SELECT:
                selectButton.setSelected(true);
                break;
            case DRAW:
                drawButton.setSelected(true);
                break;
            case POINT:
                pointButton.setSelected(true);
                break;
            case ERASE:
                eraseButton.setSelected(true);
                break;
        }

        switch (mode) {
            case SELECT:
                // update the property btns with the selected curve
                BezierCurve selectedCurve = model.getSelectedCurve();
                // update the style group
                if (selectedCurve != null) {
                    propertyBox.setDisable(false);
                    DrawStyles style = selectedCurve.getDrawStyle();
                    switch (style) {
                        case DOT:
                            dotButton.setSelected(true);
                            break;
                        case DASH:
                            dashButton.setSelected(true);
                            break;
                        case LINE:
                            lineButton.setSelected(true);
                            break;
                        case DASHDOT:
                            dashDotButton.setSelected(true);
                            break;
                    }

                    int thicknessLevel = selectedCurve.getThicknessLevel();
                    switch (thicknessLevel) {
                        case 1:
                            thickness1Button.setSelected(true);
                            break;
                        case 2:
                            thickness2Button.setSelected(true);
                            break;
                        case 3:
                            thickness3Button.setSelected(true);
                            break;
                        case 4:
                            thickness4Button.setSelected(true);
                            break;
                    }
                } else {
                    propertyBox.setDisable(true);
                }
                break;
            case DRAW:
                if (model.isDrawStarted()) {
                    propertyBox.setDisable(true);
                } else {
                    propertyBox.setDisable(false);
                }
                break;
            case POINT:
            case ERASE:
                propertyBox.setDisable(true);
                break;
        }

    }

    @Override
    public void updateView() {
        updateSize();
        updateToolStates();
    }
}
