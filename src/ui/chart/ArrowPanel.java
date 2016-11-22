package ui.chart;

import javafx.scene.shape.TriangleMesh;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class ArrowPanel
		extends JPanel
{
	private static final int ARROW_WIDTH = 10;
	private static final int MARGIN = 10;
	
	private boolean vertical;
	
	public ArrowPanel(boolean vertical)
	{
		super();
		this.vertical = vertical;
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		
		if (vertical)
		{
			g2d.draw(new Line2D.Float(getWidth() / 2F, MARGIN, getWidth() / 2F, getHeight() - MARGIN - 1));
			g2d.fillPolygon(new int[]{getWidth() / 2, (getWidth() + ARROW_WIDTH) / 2, (getWidth() - ARROW_WIDTH) / 2},
							new int[]{getHeight() - MARGIN, getHeight() - MARGIN - ARROW_WIDTH, getHeight() - MARGIN - ARROW_WIDTH}, 3);
		}
		else
		{
			// Draw horizontal arrow
			g2d.draw(new Line2D.Float(MARGIN, getHeight() / 2F, getWidth() - MARGIN - 1, getHeight() / 2F));
			g2d.fillPolygon(new int[]{getWidth() - MARGIN, getWidth() - MARGIN - ARROW_WIDTH, getWidth() - MARGIN - ARROW_WIDTH},
							new int[]{getHeight() / 2, (getHeight() + ARROW_WIDTH) / 2, (getHeight() - ARROW_WIDTH) / 2}, 3);
		}
	}
}
