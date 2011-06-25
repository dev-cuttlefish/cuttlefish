package ch.ethz.sg.cuttlefish.gui2.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import ch.ethz.sg.cuttlefish.gui2.CuttlefishToolbars;
import ch.ethz.sg.cuttlefish.gui2.NetworkPanel;
import ch.ethz.sg.cuttlefish.misc.Edge;
import ch.ethz.sg.cuttlefish.misc.Observer;
import ch.ethz.sg.cuttlefish.misc.Subject;
import ch.ethz.sg.cuttlefish.misc.Vertex;
import ch.ethz.sg.cuttlefish.networks.BrowsableForestNetwork;
import edu.uci.ics.jung.graph.SparseGraph;

public class LayoutMenu extends AbstractMenu implements Observer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ButtonGroup layoutButtons;
	private JMenuItem stopButton;
	private JMenuItem restartButton;
	private JRadioButtonMenuItem arf;
	private JRadioButtonMenuItem kcore;
	private JRadioButtonMenuItem fixed;
	private JRadioButtonMenuItem weightedArf;
	private JRadioButtonMenuItem spring;
	private JRadioButtonMenuItem kamada;
	private JRadioButtonMenuItem fruchterman;
	private JRadioButtonMenuItem isom;
	private JRadioButtonMenuItem circle;
	private JRadioButtonMenuItem tree;
	private JRadioButtonMenuItem radialTree;
	private JRadioButtonMenuItem lastSelectedLayout;
	private Map<JRadioButtonMenuItem, String> layoutMap;

	public LayoutMenu(NetworkPanel networkPanel, CuttlefishToolbars toolbars) {
		super(networkPanel, toolbars);
		initialize();
		restartButton.doClick();
		this.setText("Layout");
	}
	
	private void initialize() {
		layoutButtons = new ButtonGroup();		
		arf = new JRadioButtonMenuItem("ARF");
		kcore = new JRadioButtonMenuItem("KCore");
		fixed = new JRadioButtonMenuItem("Fixed");
		weightedArf = new JRadioButtonMenuItem("Weighted ARF");
		spring = new JRadioButtonMenuItem("Spring");
		kamada = new JRadioButtonMenuItem("Kamada Kawai");
		fruchterman = new JRadioButtonMenuItem("Fruchterman Reingold");
		isom = new JRadioButtonMenuItem("ISO M");
		circle = new JRadioButtonMenuItem("Circle");
		tree = new JRadioButtonMenuItem("Tree");
		radialTree = new JRadioButtonMenuItem("Radial Tree");
		arf.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
		kcore.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.ALT_MASK));
		spring.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.ALT_MASK));
		kamada.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.ALT_MASK));
		fruchterman.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, ActionEvent.ALT_MASK));
		isom.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, ActionEvent.ALT_MASK));
		circle.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_7, ActionEvent.ALT_MASK));
		tree.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_8, ActionEvent.ALT_MASK));
		radialTree.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_9, ActionEvent.ALT_MASK));
		layoutButtons.add(arf);
		layoutButtons.add(kcore);
		layoutButtons.add(fixed);
		layoutButtons.add(weightedArf);
		layoutButtons.add(spring);
		layoutButtons.add(kamada);
		layoutButtons.add(fruchterman);
		layoutButtons.add(isom);
		layoutButtons.add(circle);
		layoutButtons.add(tree);
		layoutButtons.add(radialTree);
		
		layoutMap = new HashMap<JRadioButtonMenuItem, String>();
		layoutMap.put(arf, "ARFLayout");
		layoutMap.put(kcore, "KCore");
		layoutMap.put(fixed, "Fixed");
		layoutMap.put(weightedArf, "WeightedARFLayout");
		layoutMap.put(spring, "SpringLayout");
		layoutMap.put(kamada, "Kamada-Kawai");
		layoutMap.put(fruchterman, "Fruchterman-Reingold");
		layoutMap.put(isom, "ISOMLayout");
		layoutMap.put(circle, "CircleLayout");
		layoutMap.put(tree, "TreeLayout");
		layoutMap.put(radialTree, "RadialTreeLayout");
		
		layoutSelected(arf);
		
		stopButton = new JMenuItem("Stop");
		restartButton = new JMenuItem("Restart");
		
		restartButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK));
		stopButton.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));

		
		this.add(stopButton);
		this.add(restartButton);
		
		this.addSeparator();
		
		this.add(arf);
		this.add(kcore);
		this.add(fixed);
		this.add(weightedArf);
		this.add(spring);
		this.add(kamada);
		this.add(fruchterman);
		this.add(isom);
		this.add(circle);
		this.add(tree);
		this.add(radialTree);
		
		stopButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				networkPanel.stopLayout();
				stopButton.setEnabled(false);
				restartButton.setEnabled(true);
			}
		});
		
		restartButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				networkPanel.resumeLayout();
				restartButton.setEnabled(false);
				stopButton.setEnabled(true);
			}
		});
		
		arf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { layoutSelected(arf); }
		});
		kcore.addActionListener(new ActionListener() {			
			public void actionPerformed(ActionEvent e) {
				if(checkKCoreLayout()) {
					layoutSelected(kcore);
				}
				else
					lastSelectedLayout.setSelected(true);
			}
		});
		fixed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { layoutSelected(fixed); }
		});
		weightedArf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { layoutSelected(weightedArf); }
		});
		spring.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { layoutSelected(spring); }
		});
		kamada.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { layoutSelected(kamada); }
		});
		fruchterman.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { layoutSelected(fruchterman); }
		});
		isom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { layoutSelected(isom); }
		});
		circle.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { layoutSelected(circle);}
		});
		tree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { 
				if(checkTreeLayout() ) {
					layoutSelected(tree);
				} else {
					lastSelectedLayout.setSelected(true);
				}
			}
		});
		radialTree.addActionListener(new ActionListener() {			
				public void actionPerformed(ActionEvent e) { 
					if(checkTreeLayout() ) {
						layoutSelected(radialTree);						
					} else {
						lastSelectedLayout.setSelected(true);
					}
				}
		});
	}
	
	private Set<Vertex> dfs(SparseGraph<Vertex, Edge> g, Set<Vertex> visited, Vertex v) {
		for(Vertex w : g.getNeighbors(v) ) {
			if(!visited.contains(w)) {
				visited.add(w);
				dfs(g,visited,w);
			}
		}
		return visited;
	}
	
	private boolean checkTreeLayout() {
		if(!(networkPanel.getNetwork() instanceof BrowsableForestNetwork) ) {
			int answer = JOptionPane.showConfirmDialog(networkPanel, "The network is not a forest. This layout applies a Minimum spanning forest algorithm to convert it to a forest, this cannot be undone. Proceed?", "Not a forest warning", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
			if(answer == 0)
				return true;
			else
				return false;
		}
		return true;
	}
	
	private boolean checkKCoreLayout() {
		if(dfs(networkPanel.getNetwork(), new HashSet<Vertex>(), networkPanel.getNetwork().getVertices().iterator().next()).size() < networkPanel.getNetwork().getVertexCount() ) {
			JOptionPane.showMessageDialog(networkPanel, "This layout currently supports only connected graphs", "Warning message", JOptionPane.WARNING_MESSAGE, null);
			return false;
		} 
		int answer = JOptionPane.showConfirmDialog(networkPanel, "This layout assigns colors to nodes. This operation cannot be undone. Proceed?", "Confirm layout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);
		if(answer == 0)
			return true;		
		return false;
	}
	
	private void layoutSelected(JRadioButtonMenuItem selected) {
		lastSelectedLayout = selected;
		selected.setSelected(true);	
		//networkPanel.setLayout(layoutMap.get(selected ));
		new SetLayoutWorker(layoutMap.get(selected ), networkPanel).execute();
	}

	@Override
	public void update(Subject o) {
		for(JRadioButtonMenuItem layoutButton : layoutMap.keySet() ) {
			if(layoutMap.get(layoutButton) == networkPanel.getCurrentLayout() ) {
				layoutButton.setSelected(true);
			}
		}
	}
	
	class SetLayoutWorker extends SwingWorker<Object, Object> {
		NetworkPanel networkPanel;
		String layoutName;
		public SetLayoutWorker(String layoutName, NetworkPanel networkPanel) {
			this.networkPanel = networkPanel;
			this.layoutName = layoutName;
		}
		@Override
		protected Object doInBackground() throws Exception {
			networkPanel.getStatusBar().setBusyMessage("Setting layout to " + layoutName, this);
			networkPanel.setLayout(layoutName);
			networkPanel.getStatusBar().setMessage("Done setting layout");
			return null;
		}
	}
}
