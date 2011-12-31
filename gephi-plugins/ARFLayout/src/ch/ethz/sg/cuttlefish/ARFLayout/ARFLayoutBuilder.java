/*Copyright (C) 2009  Markus Michael Geipel, David Garcia Becerra, Petar
Tsankov

The ARF layout plugin is free software: you can redistribute it and/or
modify it under the terms of the GNU General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.ethz.sg.cuttlefish.ARFLayout;

import javax.swing.Icon;
import javax.swing.JPanel;
import org.gephi.layout.spi.Layout;
import org.gephi.layout.spi.LayoutBuilder;
import org.gephi.layout.spi.LayoutUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;


/**
 * @author Markus Michael Geipe
 * Plug-in developer Petar Tsankov (ptsankov@student.ethz.ch)
 */
@ServiceProvider(service = LayoutBuilder.class)
public class ARFLayoutBuilder implements LayoutBuilder {

    private ARFLayoutUI ui = new ARFLayoutUI();
    
    @Override
    public String getName() {
        return "ARF";
    }

    @Override
    public LayoutUI getUI() {
        return ui;
    }

    @Override
    public Layout buildLayout() {
        return new ARFLayout(this);
    }
    
    private static class ARFLayoutUI implements LayoutUI {

        @Override
        public String getDescription() {
            return NbBundle.getMessage(ARFLayout.class, "ARF Layout");
        }

        @Override
        public Icon getIcon() {
            return null;
        }

        @Override
        public JPanel getSimplePanel(Layout layout) {
            return null;
        }

        @Override
        public int getQualityRank() {
            return -1;
        }

        @Override
        public int getSpeedRank() {
            return -1;
        }
    }
    
}
