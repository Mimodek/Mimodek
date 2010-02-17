package mimodek.tracking;

public class TrackingInfo {
	public static int UPDATE = 0;
	public static int REMOVE = 1;
	public long id;
	public int type;
	public float x;
	public float y;

	public TrackingInfo(int type ,long id, float x, float y) {
		this.id = id;
		this.type = type;
		this.x = x;
		this.y = y;
	}
}
