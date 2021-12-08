//Code written by Christian Neij
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class DescribedDialog extends NamedDialog {
	private TextField description = new TextField();
	private Text descLabel = new Text("Description:");
	
	public DescribedDialog() {
		super();
		descLabel.setFont(Font.font("Arial",FontWeight.BOLD,12));
		pane.add(descLabel, 0, 1);
		pane.add(description, 1, 1);
	}
	public String getDescription() {
		return description.getText();
	}
}
