//Code written by Christian Neij
public class Position {
	private int xCoord;
	private int yCoord;
	private int code;
	public Position(int x, int y) {
		xCoord = x;
		yCoord = y;
		code = (1/2)*(xCoord+yCoord)*(xCoord+yCoord+1)+xCoord;
	}
	public int getX() {
		return xCoord;
	}
	public int getY() {
		return yCoord;
	}
	@Override
	public boolean equals(Object o) {
		if(o==this) {
			return true;
		}
		if(!(o instanceof Position)) {
			return false;
		}
		Position pos = (Position) o;
		return (this.xCoord==pos.getX())&&(this.yCoord==pos.getY());
	}
	@Override
	public int hashCode() {
		return code;
	}
}
