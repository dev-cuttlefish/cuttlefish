/*
 * Created on Dec 9, 2003
 */
package samples.preview_new_graphdraw.event;

import java.awt.event.MouseEvent;
import java.util.EventObject;

import edu.uci.ics.jung.graph.Element;


public class ClickEvent extends EventObject {
	protected double dist;
	protected MouseEvent originator;
	protected Element graphObject;

	public ClickEvent(
		Object src,
		Element udc,
		double dist,
		MouseEvent e) {
		super(src);
		this.dist = dist;
		this.originator = e;
		this.graphObject = udc;
	}

	public double getDist() {
		return dist;
	}

	public MouseEvent getOriginator() {
		return originator;
	}

	public Element getGraphObject() {
		return graphObject;
	}
	
	public String toString() { 
	    return "ClickEvent[" + graphObject + "," + originator + "]";
	}

}