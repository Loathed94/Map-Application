//Code written by Christian Neij
public class NamedPlace extends Place {

	public NamedPlace(String name, Category category, int x, int y) {
		super(name, category, x, y);
	}
	@Override
	public String toString() {
		return "Named,"+category+","+pos.getX()+","+pos.getY()+","+placeName;
	}
}
