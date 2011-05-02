package ch.ethz.sg.cuttlefish.gui2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JSlider;

public class ZoomToolbar extends AbstractToolbar {

	private JButton zoomInButton;
	private JButton zoomOutButton;
	//private JSlider zoomSlider;
	//private int sliderPosition;

	private static String zoomInIconFile = "src/ch/ethz/sg/cuttlefish/gui2/icons/zoom-in.png";
	private static String zoomOutIconFile = "src/ch/ethz/sg/cuttlefish/gui2/icons/zoom-out.png";

	public ZoomToolbar(NetworkPanel networkPanel) {
		super(networkPanel);
		initialize();
		//sliderPosition = zoomSlider.getValue();
	}

	private void initialize() {
		zoomInButton = new JButton(new ImageIcon(zoomInIconFile));
		zoomOutButton = new JButton(new ImageIcon(zoomOutIconFile));

		this.add(zoomInButton);
		this.add(zoomOutButton);
		//this.add(getScopeSlider());
		
		zoomInButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				networkPanel.getMouse().mouseWheelMoved(new MouseWheelEvent(networkPanel.getVisualizationViewer(), CENTER, 0, 0, networkPanel.getVisualizationViewer().getWidth()/2, networkPanel.getVisualizationViewer().getHeight()/2, 1, false, 1, 10, 1) );
			}
		});
		
		zoomOutButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				networkPanel.getMouse().mouseWheelMoved(new MouseWheelEvent(networkPanel.getVisualizationViewer(), CENTER, 0, 0, networkPanel.getVisualizationViewer().getWidth()/2, networkPanel.getVisualizationViewer().getHeight()/2, 1, false, 1, -1, -1) );				
			}
		});
	}

//	private JSlider getScopeSlider() {
//		if (zoomSlider == null) {
//			zoomSlider = new JSlider();
//			zoomSlider.addChangeListener(new javax.swing.event.ChangeListener() {
//				public void stateChanged(javax.swing.event.ChangeEvent e) {
//					int oldSliderPosition = sliderPosition;
//					sliderPosition = zoomSlider.getValue();
//					int diff = oldSliderPosition - sliderPosition;
//					networkPanel.getMouse().mouseWheelMoved(new MouseWheelEvent(networkPanel.getVisualizationViewer(), CENTER, 0, 0, networkPanel.getVisualizationViewer().getWidth()/2, networkPanel.getVisualizationViewer().getHeight()/2, 1, false, 1, diff, diff) );
//				}
//			});
//		}
//		zoomSlider.setSize(100, 10);
//		return zoomSlider;
//	}

}
