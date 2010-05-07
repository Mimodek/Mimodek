package mimodek.tracking;

import mimodek.configuration.Configurator;
import mimodek.facade.FacadeFactory;

public class TrackingInfo {

	public static final int UPDATE = 0;
	public static final int REMOVE = 1;
	public static boolean FLIP_HORIZONTAL = true;
	public static boolean FLIP_VERTICAL = true;
	public long id;
	public int type;
	public float x;
	public float y;

	public TrackingInfo(int type ,long id, float x, float y) {
		this.id = id;
		this.type = type;
		if(FLIP_HORIZONTAL){
			this.x = FacadeFactory.getFacade().width-x;
		}else{
			this.x = x;
		}
		if(FLIP_VERTICAL){
			this.y = FacadeFactory.getFacade().height-y;
		}else{
			this.y = y;
		}
	}
	
	public String toString(){
		return id+" : "+type+", ("+x+","+y+")";
	}
}
