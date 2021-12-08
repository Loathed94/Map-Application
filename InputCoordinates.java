//Code written by Christian Neij
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class InputCoordinates extends Alert{
	private GridPane pane = new GridPane();
	private Text x = new Text("X: ");
	private Text y = new Text("Y: ");
	private TextField xField = new TextField();
	private TextField yField = new TextField();
	
	public InputCoordinates() {
		super(AlertType.CONFIRMATION);
		setHeaderText(null);
		setTitle("Input Coordinates:");
		x.setFont(Font.font("Arial",FontWeight.BOLD,12));
		y.setFont(Font.font("Arial",FontWeight.BOLD,12));
		pane.add(x, 0, 0);
		pane.add(xField, 1, 0);
		pane.add(y, 0, 1);
		pane.add(yField,1,1);
		getDialogPane().setContent(pane);
	}
	public int getXCoords() {
		return Integer.parseInt(xField.getText());
	}
	public int getYCoords() {
		return Integer.parseInt(yField.getText());
	}
}
