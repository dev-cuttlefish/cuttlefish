/*
 * Created on Jan 7, 2004
 */
package samples.preview_new_graphdraw.iter;

import samples.preview_new_graphdraw.EmittedLayout;
import samples.preview_new_graphdraw.impl.GraphLayoutPanel;

/**
 * @author danyelf
 */
public class LayoutIterator implements Runnable {

	protected IterableLayout iterableLayout;
	protected GraphLayoutPanel jgp;
	protected boolean stop;
	protected Thread runningThread;
	protected long iterationDelay = 0;
//	protected TransformerPipeline pipeline;
//	protected EmittedLayout lastDrawnLayout;

	/**
	 * @param jgp
	 * @param la
	 * @param g
	 */
	public LayoutIterator(GraphLayoutPanel jgp, IterableLayout la/* , TransformerPipeline pipeline */) {
		this.jgp = jgp;
		this.iterableLayout = la;
		this.stop = false;
//		this.pipeline = pipeline;
	}

	/**
	 *  
	 */
	public void startIterations() {
		this.runningThread = new Thread(this);
		stop = false;
		runningThread.start();
	}

	public void stopIterations() {
		stop = true;
	}

	public void run() {
		while (!stop) {
			EmittedLayout el = iterableLayout.emit();

			jgp.setLayoutDisplay(el);
			if( iterableLayout.isFinite() && iterableLayout.iterationsAreDone() )  {			    
			} else {
				iterableLayout.advance();			    
			}
			if( stop ) break;

			if ( iterationDelay > 0 ) {
				try {
					Thread.sleep( iterationDelay );
				} catch (InterruptedException e) {
				}				
			}
		}
	}

	/**
	 * @param i
	 */
	public void setIterationDelay(long i) {
		iterationDelay = i;
	}

//	public EmittedLayout getLastDrawnLayout() {
//		return lastDrawnLayout;
//	}
	
}
