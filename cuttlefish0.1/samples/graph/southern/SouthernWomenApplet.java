/*
 * Created on Jan 8, 2004
 */
package samples.graph.southern;

import java.applet.Applet;
import java.io.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * @author danyelf
 */
public class SouthernWomenApplet extends Applet
{

	public void start()
	{
		System.out.println("Starting in applet mode.");
		InputStream is =
			this.getClass().getClassLoader().getResourceAsStream(
				"samples/datasets/southern_women_data.txt");
		Reader br = new InputStreamReader(is);

		TestSouthernWomenBipartite swb;
		try
		{
			swb = new TestSouthernWomenBipartite(br);
			add(new JLabel("Opening demo in new frame"));
			JFrame jf = new JFrame();
			jf.getContentPane().add(swb);
			jf.pack();
			jf.setVisible(true);
		}
		catch (IOException e)
		{
			add(new JLabel(e.toString()));
			e.printStackTrace();
		}
	}

}
