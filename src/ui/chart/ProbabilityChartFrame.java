package ui.chart;

import math.BigFraction;
import math.ProbabilityCalculator;
import ui.Config;
import ui.InitializableGUI;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProbabilityChartFrame
		extends InitializableGUI
{
	private static final BigFraction[][] PROBABILITY_CHART = ProbabilityCalculator.generateProbabilityChart();
	
	private static final Integer POPUP_Z_INDEX = 100;
	
	private JPanel[][] gridPanels;
	
	public ProbabilityChartFrame()
			throws HeadlessException
	{
		super(Config.PROBABILITY_CHART_NAME);
	}
	
	protected void init()
	{
		// Initialize header
		initHeader();
		
		// Initialize wrapper panels
		initWrapperPanels();
		
		// Initialize state labels
		initStateLabels();
		
		// Initialize state transition arrows
		initArrows();
		
		setMinimumSize(new Dimension(800, 600));
	}
	
	@Override
	protected Dimension getDimensions()
	{
		return new Dimension(1000, 750);
	}
	
	private void initHeader()
	{
		JLabel header = new JLabel("Hover over any item to see relevant information.");
		header.setFont(new Font("Arial", Font.BOLD, 30));
		header.setBorder(new EmptyBorder(10, 10, 20, 10));
		mainPanel.add(header, BorderLayout.NORTH);
	}
	
	private void initWrapperPanels()
	{
		// Main panel
		JPanel chartPanel = new JPanel();
		chartPanel.setLayout(new GridLayout(11, 11));
		
		gridPanels = new JPanel[11][11];
		
		for (int x = 0; x < 11; x++)
		{
			for (int y = 0; y < 11; y++)
			{
				gridPanels[x][y] = new JPanel();
				chartPanel.add(gridPanels[x][y]);
			}
		}
		
		mainPanel.add(chartPanel, BorderLayout.CENTER);
	}
	
	private void initStateLabels()
	{
		for (int lifeCountA = 0; lifeCountA < 6; lifeCountA++)
		{
			for (int lifeCountB = 0; lifeCountB < 6; lifeCountB++)
			{
				if (lifeCountA == 0 && lifeCountB == 0)
				{
					continue;
				}
				
				createAndAddLabelFor(lifeCountA, lifeCountB);
			}
		}
	}
	
	private void initArrows()
	{
		for (int lifeCountA = 1; lifeCountA < 6; lifeCountA++)
		{
			for (int lifeCountB = 1; lifeCountB < 6; lifeCountB++)
			{
				createAndAddOutboundTransition(lifeCountA, lifeCountB);
			}
		}
	}
	
	private void createAndAddLabelFor(int lifeCountA, int lifeCountB)
	{
		// Load the wrapper
		JPanel wrapperPanel = gridPanels[10 - lifeCountB * 2][10 - lifeCountA * 2];
		
		JPanel statePanel = new JPanel();
		statePanel.setLayout(new FlowLayout());
		
		// Use colored text to indicate the lives of players
		JLabel coloredLabel;
		final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 24);
		coloredLabel = new JLabel("(");
		coloredLabel.setFont(LABEL_FONT);
		statePanel.add(coloredLabel);
		coloredLabel = new JLabel(String.valueOf(lifeCountA));
		coloredLabel.setForeground(Config.PLAYER_A_COLOR);
		coloredLabel.setFont(LABEL_FONT);
		statePanel.add(coloredLabel);
		coloredLabel = new JLabel(",");
		coloredLabel.setFont(LABEL_FONT);
		statePanel.add(coloredLabel);
		coloredLabel = new JLabel(String.valueOf(lifeCountB));
		coloredLabel.setForeground(Config.PLAYER_B_COLOR);
		coloredLabel.setFont(LABEL_FONT);
		statePanel.add(coloredLabel);
		coloredLabel = new JLabel(")");
		coloredLabel.setFont(LABEL_FONT);
		statePanel.add(coloredLabel);
		
		JPanel popup = createPopupPanel(lifeCountA, lifeCountB);
		
		PopupPanelListener listener = new PopupPanelListener(popup);
		statePanel.addMouseListener(listener);
		statePanel.addMouseMotionListener(listener);
		
		wrapperPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		wrapperPanel.add(statePanel, gbc);
	}
	
	private JPanel createPopupPanel(int lifeCountA, int lifeCountB)
	{
		// Make the JPanel popup
		final Font POPUP_FONT = new Font("Arial", Font.BOLD, 24);
		JPanel popup = new JPanel();
		
		JPanel popupDisplay = new JPanel();
		popupDisplay.setLayout(new BoxLayout(popupDisplay, BoxLayout.PAGE_AXIS));
		
		JLabel lifeLabelA = new JLabel("Player A lives remaining: " + lifeCountA);
		lifeLabelA.setFont(POPUP_FONT);
		lifeLabelA.setForeground(Config.PLAYER_A_COLOR);
		lifeLabelA.setAlignmentX(0);
		popupDisplay.add(lifeLabelA);
		
		JLabel lifeLabelB = new JLabel("Player B lives remaining: " + lifeCountB);
		lifeLabelB.setFont(POPUP_FONT);
		lifeLabelB.setForeground(Config.PLAYER_B_COLOR);
		lifeLabelB.setAlignmentX(0);
		popupDisplay.add(lifeLabelB);
		
		// Add state probability
		BigFraction probability = PROBABILITY_CHART[lifeCountA][lifeCountB];
		JLabel chanceTag = new JLabel("Probability of this state occurring in a game: ");
		chanceTag.setFont(new Font("Arial", Font.PLAIN, 20));
		popupDisplay.add(chanceTag);
		JLabel chanceFraction = new JLabel(String.valueOf(probability));
		chanceFraction.setFont(new Font("Arial", Font.PLAIN, 20));
		popupDisplay.add(chanceFraction);
		
		JLabel chanceEstimate = new JLabel("(approx. " + formatProbability(probability) + ")");
		chanceEstimate.setFont(new Font("Arial", Font.ITALIC, 20));
		popupDisplay.add(chanceEstimate);
		
		final Font WIN_PROB_FONT = new Font("Arial", Font.ITALIC, 18);
		// If the player won, give the winning probability.
		if (lifeCountA == 0)
		{
			JLabel winLabelB = new JLabel(
					"Probability that the winning player has " + lifeCountB + (lifeCountB == 1 ? " life" : " lives") + " left:");
			winLabelB.setForeground(Config.PLAYER_B_COLOR);
			winLabelB.setFont(WIN_PROB_FONT);
			popupDisplay.add(winLabelB);
			
			JLabel winChanceB = new JLabel(formatProbability(probability.multiply(new BigFraction(2))));
			winChanceB.setForeground(Config.PLAYER_B_COLOR);
			winChanceB.setFont(WIN_PROB_FONT);
			popupDisplay.add(winChanceB);
		}
		else if (lifeCountB == 0)
		{
			JLabel winLabelA = new JLabel(
					"Probability that the winning player has " + lifeCountA + (lifeCountA == 1 ? " life" : " lives") + " left:");
			winLabelA.setForeground(Config.PLAYER_A_COLOR);
			winLabelA.setFont(WIN_PROB_FONT);
			popupDisplay.add(winLabelA);
			
			JLabel winChanceA = new JLabel(formatProbability(probability.multiply(new BigFraction(2))));
			winChanceA.setForeground(Config.PLAYER_A_COLOR);
			winChanceA.setFont(WIN_PROB_FONT);
			popupDisplay.add(winChanceA);
		}
		else
		{
			BigFraction[] winChances = ProbabilityCalculator.calcWinProbabilities(lifeCountA, lifeCountB);
			
			JLabel winLabelA = new JLabel("Here, probability that player A will win is");
			winLabelA.setForeground(Config.PLAYER_A_COLOR);
			winLabelA.setFont(WIN_PROB_FONT.deriveFont(Font.PLAIN));
			popupDisplay.add(winLabelA);
			
			JLabel winChanceA = new JLabel("<html>" + String.valueOf(winChances[0]) + " <em>(approx. " + formatProbability(winChances[0]) + ")</em></html>");
			winChanceA.setForeground(Config.PLAYER_A_COLOR);
			winChanceA.setFont(WIN_PROB_FONT.deriveFont(Font.PLAIN));
			popupDisplay.add(winChanceA);
			
			JLabel winLabelB = new JLabel("Here, probability that player B will win is");
			winLabelB.setForeground(Config.PLAYER_B_COLOR);
			winLabelB.setFont(WIN_PROB_FONT.deriveFont(Font.PLAIN));
			popupDisplay.add(winLabelB);
			
			JLabel winChanceB = new JLabel("<html>" + String.valueOf(winChances[1]) + " <em>(approx. " + formatProbability(winChances[1]) + ")</em></html>");
			winChanceB.setForeground(Config.PLAYER_B_COLOR);
			winChanceB.setFont(WIN_PROB_FONT.deriveFont(Font.PLAIN));
			popupDisplay.add(winChanceB);
		}
		
		popup.add(popupDisplay, BorderLayout.CENTER);
		popup.setBounds(0, 0, 0, 0);
		
		return popup;
	}
	
	private void createAndAddOutboundTransition(int lifeCountA, int lifeCountB)
	{
		JPanel rightPanel = gridPanels[10 - lifeCountB * 2][11 - lifeCountA * 2];
		rightPanel.setLayout(new GridLayout(3, 1));
		JPanel rightArrow = new ArrowPanel(false);
		
		rightPanel.add(new JPanel());
		rightPanel.add(rightArrow);
		rightPanel.add(new JPanel());
		
		PopupPanelListener rightListener = new PopupPanelListener(createTransitionPopup(lifeCountA, lifeCountB, false));
		rightArrow.addMouseListener(rightListener);
		rightArrow.addMouseMotionListener(rightListener);
		
		JPanel downPanel = gridPanels[11 - lifeCountB * 2][10 - lifeCountA * 2];
		downPanel.setLayout(new GridLayout(1, 3));
		JPanel downArrow = new ArrowPanel(true);
		
		downPanel.add(new JPanel());
		downPanel.add(downArrow);
		downPanel.add(new JPanel());
		
		PopupPanelListener downListener = new PopupPanelListener(createTransitionPopup(lifeCountA, lifeCountB, true));
		downArrow.addMouseListener(downListener);
		downArrow.addMouseMotionListener(downListener);
	}
	
	private JPanel createTransitionPopup(int lifeCountA, int lifeCountB, boolean down)
	{
		final Font POPUP_FONT = new Font("Arial", Font.PLAIN, 20);
		JPanel popupWrapper = new JPanel();
		popupWrapper.setLayout(new BoxLayout(popupWrapper, BoxLayout.PAGE_AXIS));
		popupWrapper.setBorder(new BevelBorder(BevelBorder.RAISED));
		
		JLabel transitionDescription = new JLabel("Probability that " + (down ? "A" : "B") + " wins the round");
		transitionDescription.setFont(POPUP_FONT.deriveFont(24F));
		transitionDescription.setForeground(down ? Config.PLAYER_A_COLOR : Config.PLAYER_B_COLOR);
		popupWrapper.add(transitionDescription);
		
		BigFraction probability = down ? ProbabilityCalculator
				.probabilityWinA(lifeCountA, lifeCountB) : ProbabilityCalculator
				.probabilityWinB(lifeCountA, lifeCountB);
		JLabel chanceLabel = new JLabel("is " + probability);
		chanceLabel.setFont(POPUP_FONT.deriveFont(24F));
		chanceLabel.setForeground(down ? Config.PLAYER_A_COLOR : Config.PLAYER_B_COLOR);
		popupWrapper.add(chanceLabel);
		
		JLabel chanceEstimateLabel = new JLabel("(approx. " + formatProbability(probability) + ")");
		chanceEstimateLabel.setFont(POPUP_FONT.deriveFont(Font.ITALIC));
		popupWrapper.add(chanceEstimateLabel);
		
		JLabel playerALabel = new JLabel("<html>when <font color='" + toHTML(
				Config.PLAYER_A_COLOR) + "'>player A has " + lifeCountA + (lifeCountA == 1 ? " life " : " lives ") + "left</font></html>");
		playerALabel.setFont(POPUP_FONT);
		popupWrapper.add(playerALabel);
		
		JLabel playerBLabel = new JLabel("<html>and <font color='" + toHTML(
				Config.PLAYER_B_COLOR) + "'>player B has " + lifeCountB + (lifeCountB == 1 ? " life " : " lives ") + "left</font></html>");
		playerBLabel.setFont(POPUP_FONT);
		popupWrapper.add(playerBLabel);
		
		popupWrapper.setBorder(new EmptyBorder(0, 0, 0, 0));
		JPanel popup = new JPanel();
		popup.add(popupWrapper);
		return popup;
	}
	
	private String toHTML(Color c)
	{
		return "rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")";
	}
	
	private String formatProbability(BigFraction probability)
	{
		BigDecimal prob = new BigDecimal(probability.doubleValue());
		
		int shift = 0;
		BigDecimal target = new BigDecimal("1000");
		while (prob.compareTo(target) < 0)
		{
			shift++;
			prob = prob.movePointRight(1);
		}
		
		prob = prob.setScale(0, RoundingMode.HALF_EVEN).movePointLeft(shift);
		
		return prob.toString();
	}
	
	
	private void movePanelToMouse(JPanel panel, MouseEvent e)
	{
		final int MOUSE_SIZE = 15;
		
		Dimension preferredSize = panel.getPreferredSize();
		panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		
		int xPos = e.getXOnScreen() - ProbabilityChartFrame.this.getLayeredPane().getLocationOnScreen().x;
		if (ProbabilityChartFrame.this.getLayeredPane().getWidth() - xPos - MOUSE_SIZE < preferredSize.width)
		{
			xPos -= preferredSize.width;
		}
		else
		{
			xPos += MOUSE_SIZE;
		}
		
		int yPos = e.getYOnScreen() - ProbabilityChartFrame.this.getLayeredPane().getLocationOnScreen().y;
		if (ProbabilityChartFrame.this.getLayeredPane().getHeight() - yPos - MOUSE_SIZE < preferredSize.height)
		{
			yPos -= preferredSize.height;
		}
		else
		{
			yPos += MOUSE_SIZE;
		}
		
		panel.setBounds(xPos, yPos, preferredSize.width, panel.getPreferredSize().height);
	}
	
	private class PopupPanelListener
			extends MouseAdapter
	{
		private JPanel popup;
		
		public PopupPanelListener(JPanel popup)
		{
			super();
			this.popup = popup;
			getLayeredPane().add(popup, POPUP_Z_INDEX);
			popup.setVisible(false);
		}
		
		@Override
		public void mouseEntered(MouseEvent e)
		{
			movePanelToMouse(popup, e);
			popup.setVisible(true);
		}
		
		@Override
		public void mouseMoved(MouseEvent e)
		{
			movePanelToMouse(popup, e);
		}
		
		@Override
		public void mouseExited(MouseEvent e)
		{
			popup.setVisible(false);
		}
	}
}
