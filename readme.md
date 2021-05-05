
# Bezier-Curve-Designer
A canvas that support bezier curve drawing, with tools to modify the styles and shapes.
 
## Install
```sh
You will need to install the following:  
* [OpenJDK 11.0.10] or later version.  (https://www.oracle.com/java/technologies/javase-downloads.html)
* [Gradle 6.8.1] or later version. (https://gradle.org/install/)
```
## Usage
Execute the following command in the root directory.
```sh
$ gradle run
```

## Demo
### Drawing Tool
Instruction: Click and drag to draw each segment; press ESC to stop drawing.
<p>
<img src="https://github.com/DaveHJT/Bezier-Curve-Designer/blob/main/demo/Basic%20Drawing.gif?raw=true" width="600">
</p>

### Selecting&Editing Tool
Instruction: 1. Click on the curve to select the curve; drag the curve to move it; drag the vertices and control points to chage the shape. 
2. When a curve is selected, the style of the curve can be changed by the bottom left  style palette.
3. Press DELETE or BACK_SPACE to delete the selected curve.
4. Press ESC to cancel selection.
<p>
<img src="https://github.com/DaveHJT/Bezier-Curve-Designer/blob/main/demo/Editing.gif?raw=true" width="600">
</p>

### Erasing Tool
Instruction: Click on the curve to delete. 

### Vertex Type Tool
Instruction: When a curve is selected, click on a vertex to swap its type between "sharp" and "smooth".
ps: If no curve is selected and it's using point type tool, it will directly switch to select tool.
<p>
<img src="https://github.com/DaveHJT/Bezier-Curve-Designer/blob/main/demo/Vertex%20Type.gif?raw=true" width="600">
</p>

### Save and Load
<p>
<img src="https://github.com/DaveHJT/Bezier-Curve-Designer/blob/main/demo/Save%20and%20load.gif?raw=true" width="600">
</p>

### Credit
All icons are drawn by myself.



## License
[MIT License](https://github.com/DaveHJT/Bezier-Curve-Designer/blob/main/LICENSE) ©






