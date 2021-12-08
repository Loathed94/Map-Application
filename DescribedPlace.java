//Code written by Christian Neij
public class DescribedPlace extends Place {
	private String description;
	
	public DescribedPlace(String name, Category category, int x, int y, String description) {
		super(name, category, x, y);
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	@Override
	public String toString() {
		return "Described,"+category+","+pos.getX()+","+pos.getY()+","+placeName+","+description;
	}
}
