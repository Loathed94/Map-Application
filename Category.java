//Code written by Christian Neij
import javafx.scene.paint.Color;

public enum Category {
	Bus(Color.RED),Underground(Color.BLUE),Train(Color.GREEN),None(Color.BLACK);
	private Color colour;
	
	Category(Color color) {
		this.colour = color;
	}
	public Color getColor() {
		return colour;
	}
}
