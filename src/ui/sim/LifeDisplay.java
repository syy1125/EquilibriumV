package ui.sim;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class LifeDisplay
		extends JPanel
{
	private static final int CIRCLE_DIM = 20;
	private static final int BOX_DIM = CIRCLE_DIM + 10;
	
	private int alignment;
	private int maxHealth;
	private int currentHealth;
	private Color aliveColor;
	private Color deadColor;
	
	public LifeDisplay(Color aliveColor, Color deadColor, int maxHealth, int alignment)
	{
		this.aliveColor = aliveColor;
		this.deadColor = deadColor;
		this.alignment = alignment;
		this.maxHealth = maxHealth;
		this.currentHealth = maxHealth;
	}
	
	public void setHealth(int health)
	{
		currentHealth = health;
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		
		if (alignment == JLabel.LEFT)
		{
			int i = 0;
			for (; i < currentHealth; i++)
			{
				g2d.setColor(aliveColor);
				g2d.fillOval(i * BOX_DIM + 5, 5, CIRCLE_DIM, CIRCLE_DIM);
				g2d.setColor(Color.BLACK);
				g2d.drawOval(i * BOX_DIM + 5, 5, CIRCLE_DIM, CIRCLE_DIM);
			}
			for (; i < maxHealth; i++)
			{
				g2d.setColor(deadColor);
				g2d.fillOval(i * BOX_DIM + 5, 5, CIRCLE_DIM, CIRCLE_DIM);
				g2d.setColor(Color.BLACK);
				g2d.drawOval(i * BOX_DIM + 5, 5, CIRCLE_DIM, CIRCLE_DIM);
			}
		}
		else if (alignment == JLabel.RIGHT)
		{
			int i = 0;
			for (; i < currentHealth; i++)
			{
				g2d.setColor(aliveColor);
				g2d.fillOval(getWidth() - i * BOX_DIM - BOX_DIM + 5, 5, CIRCLE_DIM, CIRCLE_DIM);
				g2d.setColor(Color.BLACK);
				g2d.drawOval(getWidth() - i * BOX_DIM - BOX_DIM + 5, 5, CIRCLE_DIM, CIRCLE_DIM);
			}
			for (; i < maxHealth; i++)
			{
				g2d.setColor(deadColor);
				g2d.fillOval(getWidth() - i * BOX_DIM - BOX_DIM + 5, 5, CIRCLE_DIM, CIRCLE_DIM);
				g2d.setColor(Color.BLACK);
				g2d.drawOval(getWidth() - i * BOX_DIM - BOX_DIM + 5, 5, CIRCLE_DIM, CIRCLE_DIM);
			}
			
		}
	}
}
