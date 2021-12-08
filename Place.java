//Code written by Christian Neij
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public abstract class Place extends Polygon{
	protected String placeName;
	protected Category category;
	protected Position pos;
	private boolean selected = false;
	
	public Place(String name, Category category, int x, int y) {
		super(x-15,y-30,x+15,y-30,x,y);
		placeName = name;
		this.category = category;
		setFill(this.category.getColor());
		pos = new Position(x,y);
	}
	public Position getPosition() {
		return pos;
	}
	public String getName() {
		return placeName;
	}
	public Category getCategory() {
		return category;
	}
	public boolean isSelected() {
		return selected;
	}
	public void changeSelectionStatus(boolean select) {
		if(select) selected = true;
		else selected = false;
		//selected = !selected;
		changeColour();
	}
	private void changeColour() {
		if(selected) {
			setFill(Color.YELLOW);
			setStroke(Color.BLACK);
		}else if(!selected) {
			setFill(this.category.getColor());
			setStroke(this.category.getColor());
		}
	}
	@Override
	public abstract String toString();
}
