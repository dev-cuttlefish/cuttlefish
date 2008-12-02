/*
 * Created on Mar 22, 2004
 */
package samples.preview_new_graphdraw.iterablelayouts;

import samples.preview_new_graphdraw.iter.UpdatableIterableLayout;

/**
 * This iterable layout simply iterates forever.
 * 
 * @author danyelf
 */
public class UnmovingIterableLayout extends UpdatableIterableLayout {
	protected static UnmovingIterableLayout instance;

	/**
	 * @see samples.preview_new_graphdraw.iter.IterableLayout#calculate()
	 */
	protected void calculate() {
		return;
	}

	/**
	 * @see samples.preview_new_graphdraw.iter.IterableLayout#iterationsAreDone()
	 */
	public boolean iterationsAreDone() {
		return false;
	}

	/**
	 * @see samples.preview_new_graphdraw.iter.IterableLayout#isFinite()
	 */
	public boolean isFinite() {
		return false;
	}
	
	public static UnmovingIterableLayout getInstance() {
		if ( instance == null) {
			instance = new UnmovingIterableLayout();
		}
		return instance;
	}
}
