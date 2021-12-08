//Code written by Christian Neij
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class NamedDialog extends Alert{
	private TextField name = new TextField();
	protected GridPane pane = new GridPane();
	private Text nameLabel = new Text("Name:");

	
	public NamedDialog() {
		super(AlertType.CONFIRMATION);
		setHeaderText(null);
		setTitle("Named Place");
		nameLabel.setFont(Font.font("Arial",FontWeight.BOLD,12));
		pane.add(nameLabel, 0, 0);
		pane.add(name, 1, 0);
		getDialogPane().setContent(pane);
	}
	public String getName() {
		return name.getText();
	}
}