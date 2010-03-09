package mimodek.tracking;

import mimodek.Mimodek;

public class TrackingInfo {
	
	public static final int UPDATE = 0;
	public static final int REMOVE = 1;
	public static boolean FLIP_HORIZONTAL = true;
	public long id;
	public int type;
	public float x;
	public float y;

	public TrackingInfo(int type ,long id, float x, float y) {
		this.id = id;
		this.type = type;
		if(FLIP_HORIZONTAL){
			this.x = Mimodek.screenWidth-x;
		}else{
			this.x = x;
		}
		this.y = y;
	}
	
	public String toString(){
		return id+" : "+type+", ("+x+","+y+")";
	}
}
