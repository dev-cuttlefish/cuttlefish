/*
  
    Copyright (C) 2011  Markus Michael Geipel, David Garcia Becerra,
    Petar Tsankov

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

package ch.ethz.sg.cuttlefish.gui.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import ch.ethz.sg.cuttlefish.gui.CuttlefishToolbars;
import ch.ethz.sg.cuttlefish.gui.NetworkPanel;
import ch.ethz.sg.cuttlefish.misc.Observer;
import ch.ethz.sg.cuttlefish.misc.Subject;
import ch.ethz.sg.cuttlefish.networks.BrowsableForestNetwork;
import ch.ethz.sg.cuttlefish.networks.BrowsableNetwork;
import ch.ethz.sg.cuttlefish.networks.Vertex;

public class LayoutMenu extends AbstractMenu implements Observer {

	private static final long serialVersionUID = 1L;
	private ButtonGroup layoutButtons;
	private JMenuItem stopButton;
	private JMenuItem repaintButton;
	private JRadioButtonMenuItem arf;
	private JRadioButtonMenuItem kcore;
	private JRadioButtonMenuItem weightedKcore;
	private JRadioButtonMenuItem fixed;
	private JRadioButtonMenuItem weightedArf;
	private JRadioButtonMenuItem kamada;
	private JRadioButtonMenuItem fruchterman;
	private JRadioButtonMenuItem yifanhu;
	private JRadioButtonMenuItem forceatlas;
	private JRadioButtonMenuItem isom;
	private JRadioButtonMenuItem circle;
	private JRadioButtonMenuItem tree;
	private JRadioButtonMenuItem radialTree;
	private JRadioButtonMenuItem lastSelectedLayout;
	private Map<JRadioButtonMenuItem, String> layoutMap;

	public LayoutMenu(NetworkPanel networkPanel, CuttlefishToolbars toolbars) {
		super(networkPanel, toolbars);
		initialize();
		this.setText("Layout");
	}

	private void initialize() {
		layoutButtons = new ButtonGroup();
		arf = new JRadioButtonMenuItem("ARF");
		kcore = new JRadioButtonMenuItem("KCore");
		weightedKcore = new JRadioButtonMenuItem("Weighted KCore");
		fixed = new JRadioButtonMenuItem("Fixed");
		weightedArf = new JRadioButtonMenuItem("Weighted ARF");
		kamada = new JRadioButtonMenuItem("Kamada Kawai");
		fruchterman = new JRadioButtonMenuItem("Fruchterman Reingold");
		yifanhu = new JRadioButtonMenuItem("Yifan Hu");
		forceatlas = new JRadioButtonMenuItem("ForceAtlas 2");
		isom = new JRadioButtonMenuItem("ISO M");
		circle = new JRadioButtonMenuItem("Circle");
		tree = new JRadioButtonMenuItem("Tree");
		radialTree = new JRadioButtonMenuItem("Radial Tree");

		arf.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,
				ActionEvent.ALT_MASK));
		weightedArf.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,
				ActionEvent.ALT_MASK));

		kcore.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3,
				ActionEvent.ALT_MASK));
		weightedKcore.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4,
				ActionEvent.ALT_MASK));

		fixed.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5,
				ActionEvent.ALT_MASK));
		circle.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6,
				ActionEvent.ALT_MASK));

		fruchterman.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_7,
				ActionEvent.ALT_MASK));
		yifanhu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_8,
				ActionEvent.ALT_MASK));
		forceatlas.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_9,
				ActionEvent.ALT_MASK));

		// isom.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6,
		// ActionEvent.ALT_MASK));
		// tree.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_8,
		// ActionEvent.ALT_MASK));
		// radialTree.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_9,
		// ActionEvent.ALT_MASK));
		// kamada.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4,
		// ActionEvent.ALT_MASK));

		layoutButtons.add(arf);
		layoutButtons.add(kcore);
		layoutButtons.add(weightedKcore);
		layoutButtons.add(fixed);
		layoutButtons.add(weightedArf);
		layoutButtons.add(kamada);
		layoutButtons.add(fruchterman);
		layoutButtons.add(yifanhu);
		layoutButtons.add(forceatlas);
		layoutButtons.add(isom);
		layoutButtons.add(circle);
		layoutButtons.add(tree);
		layoutButtons.add(radialTree);

		layoutMap = new HashMap<JRadioButtonMenuItem, String>();
		layoutMap.put(arf, "arf");
		layoutMap.put(kcore, "kcore");
		layoutMap.put(weightedKcore, "weighted-kcore");
		layoutMap.put(fixed, "fixed");
		layoutMap.put(weightedArf, "weighted-arf");
		layoutMap.put(kamada, "kamada-kawai");
		layoutMap.put(fruchterman, "fruchterman-reingold");
		layoutMap.put(yifanhu, "yifanhu");
		layoutMap.put(forceatlas, "force-atlas");
		layoutMap.put(isom, "iso-m");
		layoutMap.put(circle, "circle");
		layoutMap.put(tree, "tree");
		layoutMap.put(radialTree, "radial-tree");

		layoutSelected(arf);

		stopButton = new JMenuItem("Lock");
		repaintButton = new JMenuItem("Restart layout");
		repaintButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
				ActionEvent.ALT_MASK));
		repaintButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				networkPanel.resumeLayout();
			}
		});

		this.add(stopButton);
		this.add(repaintButton);
		this.addSeparator();

		this.add(arf);
		this.add(weightedArf);

		this.add(kcore);
		this.add(weightedKcore);

		this.add(fixed);
		this.add(circle);

		this.add(fruchterman);
		this.add(yifanhu);
		this.add(forceatlas);

		// this.add(tree);
		// this.add(radialTree);
		// this.add(kamada);
		// this.add(isom);

		stopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// ((AbstractLayout<Vertex,Edge>)networkPanel.getNetworkLayout()).lock(true);
				networkPanel.stopLayout();
			}
		});

		arf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				layoutSelected(arf);
			}
		});
		kcore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkKCoreLayout()) {
					layoutSelected(kcore);
				} else
					lastSelectedLayout.setSelected(true);
			}
		});
		weightedKcore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkKCoreLayout()) {
					if (checkWeightedKCoreLayout()) {
						layoutSelected(weightedKcore);
					}
				} else {
					lastSelectedLayout.setSelected(true);
				}
			}
		});
		fixed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				layoutSelected(fixed);
			}
		});
		weightedArf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				layoutSelected(weightedArf);
			}
		});
		kamada.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				layoutSelected(kamada);
			}
		});
		fruchterman.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				layoutSelected(fruchterman);
			}
		});
		yifanhu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				layoutSelected(yifanhu);
			}
		});
		forceatlas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				layoutSelected(forceatlas);
			}
		});
		isom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				layoutSelected(isom);
			}
		});
		circle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				layoutSelected(circle);
			}
		});
		tree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkTreeLayout()) {
					layoutSelected(tree);
				} else {
					lastSelectedLayout.setSelected(true);
				}
			}
		});
		radialTree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (checkTreeLayout()) {
					layoutSelected(radialTree);
				} else {
					lastSelectedLayout.setSelected(true);
				}
			}
		});
	}

	private Set<Vertex> dfs(BrowsableNetwork g, Set<Vertex> visited, Vertex v) {
		visited.add(v);
		for (Vertex w : g.getNeighbors(v)) {
			if (!visited.contains(w)) {
				visited.add(w);
				dfs(g, visited, w);
			}
		}
		return visited;
	}

	private boolean checkTreeLayout() {
		if (!(networkPanel.getNetwork() instanceof BrowsableForestNetwork)) {
			int answer = JOptionPane
					.showConfirmDialog(
							networkPanel,
							"The network is not a forest. This layout applies a Minimum spanning forest algorithm to convert it to a forest, this cannot be undone. Proceed?",
							"Not a forest warning", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null);
			if (answer == 0)
				return true;
			else
				return false;
		}
		return true;
	}

	// TODO ilias: Replace dfs with Gephi's ConnectedComponents
	private boolean checkKCoreLayout() {
		List<Set<Vertex>> cores = new LinkedList<Set<Vertex>>();
		Set<Vertex> visited = new HashSet<Vertex>();
		for (Vertex v : networkPanel.getNetwork().getVertices()) {
			if (visited.contains(v))
				continue;
			Set<Vertex> core = new HashSet<Vertex>();
			core = dfs(networkPanel.getNetwork(), core, v);
			visited.addAll(core);
			cores.add(core);
		}
		if (cores.size() > 1) {
			int answer = JOptionPane
					.showConfirmDialog(
							networkPanel,
							"The graph contains several components, the layout will apply to the largest connected component. This step cannot be undone. Continue?",
							"Warning message",
							JOptionPane.YES_NO_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE, null);
			if (answer == JOptionPane.NO_OPTION) {
				return false;
			} else {
				// leave only the largest connected component
				Set<Vertex> biggestCore = null;
				for (Set<Vertex> currentCore : cores) {
					if (biggestCore == null) {
						biggestCore = currentCore;
					} else {
						Set<Vertex> removeCore = null;
						if (biggestCore.size() < currentCore.size()) {
							removeCore = biggestCore;
							biggestCore = currentCore;
						} else {
							removeCore = currentCore;
						}
						for (Vertex v : removeCore) {
							networkPanel.getNetwork().removeVertex(v);
						}
					}
				}
			}
		}
		int answer = JOptionPane
				.showConfirmDialog(
						networkPanel,
						"This layout assigns colors to nodes. This operation cannot be undone. Proceed?",
						"Confirm layout", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null);
		return answer == JOptionPane.YES_OPTION;
	}

	private boolean checkWeightedKCoreLayout() {
		boolean accepted = true;
		String msg = "Please specify the parameters (alpha, beta) of Weighted";
		msg += "\nKCore Layout (comma separated). Defaults are";
		msg += "\n(alpha = 1, beta = 1):";
		String s = (String) JOptionPane.showInputDialog(null, msg,
				"Parameter configuration", JOptionPane.PLAIN_MESSAGE, null,
				null, "1, 1");

		double alpha = 1, beta = 1;
		if (s != null) {
			alpha = new Double(s.split(",")[0]);
			beta = new Double(s.split(",")[1]);
		} else {
			accepted = false;
		}

		networkPanel.setLayoutParameters(new Object[] { alpha, beta });
		return accepted;
	}

	private void layoutSelected(JRadioButtonMenuItem selected) {
		lastSelectedLayout = selected;
		selected.setSelected(true);
		networkPanel.setLayoutByName(layoutMap.get(selected));
	}

	@Override
	public void update(Subject o) {
		for (JRadioButtonMenuItem layoutButton : layoutMap.keySet()) {
			if (layoutMap.get(layoutButton) == networkPanel.getCurrentLayout()) {
				layoutButton.setSelected(true);
			}
		}
	}

}
