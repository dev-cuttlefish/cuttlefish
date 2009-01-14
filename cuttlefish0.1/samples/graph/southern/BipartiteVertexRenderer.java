/*
 * Created on Jan 8, 2004
 */
package samples.graph.southern;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import org.apache.commons.collections.Predicate;

import samples.preview_new_graphdraw.VertexRenderer;
import samples.preview_new_graphdraw.VisVertex;
import edu.uci.ics.jung.graph.KPartiteGraph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.io.BipartiteGraphReader;

/**
 * @author danyelf
 */
public class BipartiteVertexRenderer implements VertexRenderer
{

	private StringLabeller dates;
	private StringLabeller women;
//	private BipartiteGraph bpg;
    private KPartiteGraph bpg;

	private List hiddenList;

	public BipartiteVertexRenderer(
//		BipartiteGraph bpg,
        KPartiteGraph bpg,
		StringLabeller women,
		StringLabeller dates)
	{
		this.bpg = bpg;
		this.women = women;
		this.dates = dates;
	}

	public BipartiteVertexRenderer(StringLabeller women, StringLabeller dates)
	{

		this.women = women;
		this.dates = dates;
	}

	public void renderVertex(Graphics g, VisVertex vc)
	{
//		BipartiteVertex bpv = (BipartiteVertex) vc.getVertex();
//		Choice c = BipartiteGraph.CLASSA;
//		if (bpg != null)
//		{
//			c = bpg.getPartition(bpv);
//		}
//		StringLabeller sl = (c == BipartiteGraph.CLASSA) ? women : dates;
        Vertex bpv = (Vertex) vc.getVertex();
        Predicate part = BipartiteGraphReader.PART_A;
        if (bpg != null)
            part = BipartiteGraphReader.getPartition((Vertex) bpv.getEqualVertex(bpg));
        boolean in_a = part == (BipartiteGraphReader.PART_A);
        StringLabeller sl = in_a ? women : dates;
		String s = sl.getLabel((Vertex) bpv.getEqualVertex(bpg));

//		g.setColor((c == BipartiteGraph.CLASSA) ? Color.RED : Color.BLUE);
        g.setColor(in_a ? Color.RED : Color.BLUE);
		if (hiddenList != null && hiddenList.contains(s))
		{
			int x = (int) vc.getX();
			int y = (int) vc.getY();
			g.drawLine(x - 4, y - 4, x + 4, y + 4);
			g.drawLine(x + 4, y - 4, x - 4, y + 4);
		}
		else
			g.fillRect((int) vc.getX() - 4, (int) vc.getY() - 4, 8, 8);

		g.setColor(Color.black);
		g.drawString(s, (int) vc.getX() - 4, (int) vc.getY() + 16);
	}
	/**
	 * @param list
	 */
	public void setHiddenList(List list)
	{
		this.hiddenList = list;
	}

}