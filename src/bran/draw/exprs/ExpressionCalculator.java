package bran.draw.exprs;

import java.util.List;

public class ExpressionCalculator {

	private final List<Drawable> drawables;
	private final ExpressionViewer viewer;
	// private boolean calculating = true;

	public ExpressionCalculator(final List<Drawable> drawables, final ExpressionViewer viewer) {
		this.drawables = drawables;
		this.viewer = viewer;
	}

	public void calculate() {
		// drawables.stream().filter(Drawable::isDrawn).filter(d -> d.dataScale() > 1).forEach(d -> completeUpscale(d, d.dataScale()));
		for (Drawable drawable : drawables) {
			if (drawable.isDrawn()) {
				int dataScale = drawable.dataScale();
				if (dataScale > 1) {
					completeUpscale(drawable, dataScale);
					// calculating = true;
				}
			}
		}
	}

	public void upscale(final Drawable drawable, int dataScale) {
		if (dataScale == viewer.precision) { // the ends & mid
			viewer.x.setValue(viewer.xMin);
			drawable.refresh(0);
			viewer.x.setValue((viewer.xMin + viewer.xMax) / 2);
			drawable.refresh(viewer.precision / 2);
			viewer.x.setValue(viewer.xMax);
			drawable.refresh(viewer.precision);
		} else
			for (int i = dataScale / 2; i < viewer.precision; i += dataScale) {
				viewer.x.setValue((double) i / viewer.precision * viewer.xScale + viewer.xMin);
				drawable.refresh(i);
			}
	}

	/**
	 * upscale the children just enough so that they're data is precise enough to be used
	 */
	public void upscaleChildren(final Drawable drawable, final int targetScale) {
		for (Entry e : drawable.subEntries)
			if (e instanceof Declaration d) {
				upscaleChildren(d, targetScale);
				while (d.dataScale() >= targetScale) {
					upscale(d, d.dataScale());
					d.upScale();
				}
			}
	}

	private void completeUpscale(final Drawable drawable, int dataScale) {
		upscaleChildren(drawable, drawable.dataScale());
		upscale(drawable, dataScale);
		drawable.upScale();
	}

}
