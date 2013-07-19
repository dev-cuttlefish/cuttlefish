package ch.ethz.sg.cuttlefish.exporter;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.gephi.io.exporter.spi.ByteExporter;
import org.gephi.io.exporter.spi.VectorExporter;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewProperties;
import org.gephi.preview.api.PreviewProperty;
import org.gephi.preview.api.ProcessingTarget;
import org.gephi.preview.api.RenderTarget;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

import processing.core.PGraphicsJava2D;

public class JPEGExporter implements VectorExporter, ByteExporter {

	private Workspace workspace;
	private OutputStream outputStream;

	// TODO ilias: Do something with the dimensions!
	private int width = 800;
	private int height = 600;
	private float margin = 4;

	@Override
	public boolean execute() {
		try {
			exportData();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	@Override
	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;
	}

	@Override
	public Workspace getWorkspace() {
		return workspace;
	}

	@Override
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setMargin(float margin) {
		this.margin = margin;
	}

	private void exportData() throws IOException {
		PreviewController controller = Lookup.getDefault().lookup(
				PreviewController.class);
		PreviewProperties props = controller.getModel(workspace)
				.getProperties();

		props.putValue(PreviewProperty.VISIBILITY_RATIO, 1.0);
		props.putValue("width", width);
		props.putValue("height", height);
		props.putValue(PreviewProperty.MARGIN, margin);
		// props.putValue(PreviewProperty.EDGE_CURVED, false);
		
		controller.refreshPreview(workspace);

		ProcessingTarget target = (ProcessingTarget) controller
				.getRenderTarget(RenderTarget.PROCESSING_TARGET, workspace);

		target.refresh();

		PGraphicsJava2D pg2 = (PGraphicsJava2D) target.getGraphics();
		BufferedImage img = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		img.setRGB(0, 0, width, height, pg2.pixels, 0, width);
		ImageIO.write(img, "jpg", outputStream);

		outputStream.close();
		props.removeSimpleValue(PreviewProperty.VISIBILITY_RATIO);
		props.removeSimpleValue("width");
		props.removeSimpleValue("height");
		props.removeSimpleValue(PreviewProperty.MARGIN);
	}

}
