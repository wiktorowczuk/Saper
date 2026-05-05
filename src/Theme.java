import java.awt.Color;

public class Theme {
    Color background;
    Color panel;
    Color buttonBackground;
    Color buttonHover;
    Color buttonText;
    Color gridBackground;

    public Theme(Color background, Color panel, Color buttonBackground, Color buttonHover, Color buttonText, Color gridBackground) {
        this.background = background;
        this.panel = panel;
        this.buttonBackground = buttonBackground;
        this.buttonHover = buttonHover;
        this.buttonText = buttonText;
        this.gridBackground = gridBackground;
    }
}
