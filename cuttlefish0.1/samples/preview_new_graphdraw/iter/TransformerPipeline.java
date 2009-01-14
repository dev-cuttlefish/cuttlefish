/*
 * Created on Mar 23, 2004
 */
package samples.preview_new_graphdraw.iter;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import samples.preview_new_graphdraw.EmittedLayout;
import samples.preview_new_graphdraw.impl.GraphLayoutPanel;
import samples.preview_new_graphdraw.transform.LayoutTransformer;


/**
 * 
 * @author danyelf
 */
public class TransformerPipeline {

	protected GraphLayoutPanel panel;
    protected List list;

    /**
     * @param jgp
     */
    public TransformerPipeline(GraphLayoutPanel jgp) {
        this.panel = jgp;
        list = new ArrayList();
		jgp.addComponentListener( new PipelineComponentListener(this) );
    }

    /**
     * @param lt
     */
    public  synchronized void add(LayoutTransformer lt) {
	    list.add( lt );
	    adjustSizes(panel.getSize());
    }

    /**
     * @param dimension
     */
    public synchronized void adjustSizes(Dimension dimension) {
        for (int i = list.size() - 1 ; i >= 0; i--) {
            LayoutTransformer lt = (LayoutTransformer) list.get(i);
            lt.adjustSize( dimension );
        }
    }

    /**
     * @param el
     */
    public synchronized EmittedLayout transformSequentially(EmittedLayout el) {
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			LayoutTransformer lt = (LayoutTransformer) iter.next();
//		    System.out.println("Transforming " + lt);
				el = lt.transform(el);				
		}
//		System.out.println("--");
        return el;
    }
    
    

}

class PipelineComponentListener extends ComponentAdapter {

    private TransformerPipeline pipeline;

    public PipelineComponentListener(TransformerPipeline pipeline) {
        this.pipeline = pipeline;
    }

    public void componentResized(ComponentEvent ce) {
        System.out.println("Starting resize!");
        pipeline.adjustSizes( ce.getComponent().getSize() );
        pipeline.panel.resizeLayouts();
        System.out.println("Ending resize!");
    }

}

