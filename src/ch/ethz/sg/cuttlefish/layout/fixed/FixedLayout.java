/*
  
    Copyright (C) 2009  Markus Michael Geipel, David Garcia Becerra

	This file is part of Cuttlefish.
	
 	Cuttlefish is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 
 */
package ch.ethz.sg.cuttlefish.layout.fixed;

import java.util.ArrayList;
import java.util.List;

import org.gephi.graph.api.GraphModel;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutProperty;

public class FixedLayout implements Layout {

	private LayoutBuilder layoutBuilder = null;
	private GraphModel graphModel = null;
	
	private boolean finished = false;

	public FixedLayout(LayoutBuilder layoutBuilder) {
		this.layoutBuilder = layoutBuilder;
	}

	@Override
	public void initAlgo() {

		if (graphModel == null) {
			throw new RuntimeException(
					"The GraphModel for this layout cannot be null!");
		}
	}

	@Override
	public void setGraphModel(GraphModel graphModel) {
		this.graphModel = graphModel;
	}

	@Override
	public void goAlgo() {
		finished = true;
	}

	@Override
	public boolean canAlgo() {
		return !finished;
	}

	@Override
	public void endAlgo() {

	}

	@Override
	public LayoutProperty[] getProperties() {
		List<LayoutProperty> properties = new ArrayList<LayoutProperty>();
		return properties.toArray(new LayoutProperty[0]);
	}

	@Override
	public void resetPropertiesValues() {
		finished = false;
	}

	@Override
	public LayoutBuilder getBuilder() {
		return layoutBuilder;
	}

}
