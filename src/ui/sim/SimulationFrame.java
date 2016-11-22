package ui.sim;

import com.sun.istack.internal.Nullable;
import ui.Config;
import ui.ImageUtils;
import ui.InitializableGUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class SimulationFrame
		extends InitializableGUI
{
	// Class fields
	private static final Random RNG = new Random();
	private static final ImageIcon[] DICE_ICONS = new ImageIcon[6];
	private static final Color TRANSPARENT = new Color(255, 255, 255, 0);
	
	private static final Border DEBUG_BORDER = new LineBorder(Color.BLACK);
	
	// Configuration
	private static boolean dramaticRoll = true;
	
	static
	{
		for (int i = 0; i < DICE_ICONS.length; i++)
		{
			DICE_ICONS[i] = ImageUtils.loadImage("dice_" + (i + 1) + ".png");
		}
	}
	
	private JButton stepButton;
	private JButton turnButton;
	private JButton fullGameButton;
	private JButton resetButton;
	private JButton optionsButton;
	
	private int lifeCountA;
	private JLabel lifeLabelA;
	private LifeDisplay lifePointPanelA;
	private JLabel rollLabelA;
	private JTextArea scoreTextA;
	
	private int lifeCountB;
	private JLabel lifeLabelB;
	private LifeDisplay lifePointPanelB;
	private JLabel rollLabelB;
	private JTextArea scoreTextB;
	
	private JLabel statusDisp;
	
	private Thread currentTask;
	
	public SimulationFrame()
			throws HeadlessException
	{
		super(Config.SIMULATOR_NAME);
	}
	
	protected void init()
	{
		// Setup status display
		initStatusDisp();
		
		// Set up control panel
		initControlPanel();
		
		// Set up player panels
		initPlayerPanels();
		
		// Initialize the game
		initGame();
	}
	
	@Override
	protected Dimension getDimensions()
	{
		return new Dimension(1000, 750);
	}
	
	private void initStatusDisp()
	{
		statusDisp = new JLabel("Initializing...");
		statusDisp.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 32));
		JPanel statusWrapper = new JPanel();
		statusWrapper.add(statusDisp);
		mainPanel.add(statusWrapper, BorderLayout.NORTH);
	}
	
	@SuppressWarnings("ConstantConditions")
	private void initControlPanel()
	{
		final Dimension CP_BUTTON_SIZE = new Dimension(100, 30);
		
		// Initialize object
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(1, 0, 5, 5));
		
		// Step button
		stepButton = new JButton("Step", ImageUtils.loadImage("StepForward24.gif"));
		stepButton.setPreferredSize(CP_BUTTON_SIZE);
		stepButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				SimulationFrame.this.doStep();
			}
		});
		stepButton.setToolTipText("Advance once (will stop even if there is a tie)");
		controlPanel.add(stepButton);
		
		// Play button
		turnButton = new JButton("Turn", ImageUtils.loadImage("Play24.gif"));
		turnButton.setPreferredSize(CP_BUTTON_SIZE);
		turnButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				SimulationFrame.this.doTurn();
			}
		});
		turnButton.setToolTipText("Advance until a player wins this turn");
		controlPanel.add(turnButton);
		
		// Play to game end
		fullGameButton = new JButton("Game", ImageUtils.loadImage("FastForward24.gif"));
		fullGameButton.setPreferredSize(CP_BUTTON_SIZE);
		fullGameButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				SimulationFrame.this.doFullGame();
			}
		});
		fullGameButton.setToolTipText("Advance until a player wins the game");
		controlPanel.add(fullGameButton);
		
		// Reset
		resetButton = new JButton("Reset", ImageUtils.loadImage("Refresh24.gif"));
		resetButton.setPreferredSize(CP_BUTTON_SIZE);
		resetButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				SimulationFrame.this.reset();
			}
		});
		resetButton.setToolTipText("Reset the game to its initial state");
		controlPanel.add(resetButton);
		
		// Options
		optionsButton = new JButton("Options", ImageUtils.loadImage("Preferences24.gif"));
		optionsButton.setPreferredSize(new Dimension(120, 30));
		optionsButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				SimulationFrame.this.openOptions();
			}
		});
		controlPanel.add(optionsButton);
		
		// Add control panel to the frame
		JPanel controlPanelWrapper = new JPanel();
		controlPanelWrapper.add(controlPanel);
		controlPanelWrapper.setAlignmentX(0.5F);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.PAGE_AXIS));
		JLabel controlLabel = new JLabel("Game Controls");
		controlLabel.setFont(new Font("Arial", Font.BOLD, 20));
		controlLabel.setAlignmentX(0.5F);
		
		bottomPanel.add(controlLabel);
		bottomPanel.add(controlPanelWrapper);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
	}
	
	private void initPlayerPanels()
	{
		final Font STATES_FONT = new Font("Arial", Font.PLAIN, 24);
		final Font PLAYER_FONT = new Font("Arial", Font.BOLD, 30);
		
		JPanel playerPanel;
		JLabel playerNameLabel;
		
		// Initialize player A
		playerPanel = buildPlayerPanel();
		
		// Add items
		playerNameLabel = new JLabel("Player A");
		playerNameLabel.setFont(PLAYER_FONT);
		playerNameLabel.setForeground(Config.PLAYER_A_COLOR);
		playerPanel.add(playerNameLabel);
		
		lifeLabelA = new JLabel();
		lifeLabelA.setFont(STATES_FONT);
		lifeLabelA.setForeground(Config.PLAYER_A_COLOR);
		
		lifePointPanelA = new LifeDisplay(Config.PLAYER_A_COLOR, Color.GRAY, 5, JLabel.LEFT);
		JPanel lifeIndicatorPanelA = new JPanel();
		
		lifeIndicatorPanelA.setLayout(new BoxLayout(lifeIndicatorPanelA, BoxLayout.PAGE_AXIS));
		lifeLabelA.setAlignmentX(0F);
		lifeIndicatorPanelA.add(lifeLabelA);
		lifeIndicatorPanelA.add(lifePointPanelA);
		lifeIndicatorPanelA.setAlignmentX(0F);
		
		playerPanel.add(lifeIndicatorPanelA);
		
		rollLabelA = new JLabel();
		playerPanel.add(rollLabelA);
		
		scoreTextA = buildScoreArea();
		scoreTextA.setAlignmentX(0F);
		
		scoreTextA.setFont(STATES_FONT.deriveFont(18F));
		scoreTextA.setForeground(Config.PLAYER_A_COLOR);
		
		JPanel scoreWrapperA = new JPanel();
		scoreWrapperA.setLayout(new BoxLayout(scoreWrapperA, BoxLayout.PAGE_AXIS));
		scoreWrapperA.add(Box.createVerticalGlue());
		scoreWrapperA.add(scoreTextA);
		scoreWrapperA.add(Box.createVerticalGlue());
		
		playerPanel.add(scoreWrapperA);
		
		// Add to frame
		mainPanel.add(playerPanel, BorderLayout.WEST);
		
		// Initialize player B
		playerPanel = buildPlayerPanel();
		
		// Add items
		playerNameLabel = new JLabel("Player B");
		playerNameLabel.setFont(PLAYER_FONT);
		playerNameLabel.setForeground(Config.PLAYER_B_COLOR);
		playerNameLabel.setHorizontalAlignment(JLabel.RIGHT);
		playerPanel.add(playerNameLabel);
		
		lifeLabelB = new JLabel();
		lifeLabelB.setFont(STATES_FONT);
		lifeLabelB.setForeground(Config.PLAYER_B_COLOR);
		
		lifePointPanelB = new LifeDisplay(Config.PLAYER_B_COLOR, Color.GRAY, 5, JLabel.RIGHT);
		JPanel lifeIndicatorPanelB = new JPanel();
		
		lifeIndicatorPanelB.setLayout(new BoxLayout(lifeIndicatorPanelB, BoxLayout.PAGE_AXIS));
		lifeLabelB.setAlignmentX(1F);
		lifeIndicatorPanelB.add(lifeLabelB);
		lifeIndicatorPanelB.add(lifePointPanelB);
		lifeIndicatorPanelB.setAlignmentX(1F);
		
		playerPanel.add(lifeIndicatorPanelB);
		
		rollLabelB = new JLabel();
		rollLabelB.setHorizontalAlignment(JLabel.RIGHT);
		playerPanel.add(rollLabelB);
		
		scoreTextB = buildScoreArea();
		scoreTextB.setAlignmentX(1F);
		
		scoreTextB.setFont(STATES_FONT.deriveFont(18F));
		scoreTextB.setForeground(Config.PLAYER_B_COLOR);
		
		JPanel scoreWrapperB = new JPanel();
		scoreWrapperB.setLayout(new BoxLayout(scoreWrapperB, BoxLayout.PAGE_AXIS));
		scoreWrapperB.add(Box.createVerticalGlue());
		scoreWrapperB.add(scoreTextB);
		scoreWrapperB.add(Box.createVerticalGlue());
		playerPanel.add(scoreWrapperB);
		
		// Add to frame
		mainPanel.add(playerPanel, BorderLayout.EAST);
	}
	
	private void initGame()
	{
		setLifeCountA(5);
		setLifeCountB(5);
		setStatus("Idle");
	}
	
	private JPanel buildPlayerPanel()
	{
		final Dimension PLAYER_PANEL_SIZE = new Dimension(300, 400);
		final Border PLAYER_PANEL_BORDER = new EmptyBorder(20, 20, 20, 20);
		
		JPanel playerPanel = new JPanel();
		playerPanel.setLayout(new GridLayout(0, 1));
		playerPanel.setPreferredSize(PLAYER_PANEL_SIZE);
		playerPanel.setBorder(PLAYER_PANEL_BORDER);
		
		return playerPanel;
	}
	
	private JTextArea buildScoreArea()
	{
		JTextArea scoreArea = new JTextArea();
		scoreArea.setEditable(false);
		
		scoreArea.setBackground(getBackground());
		scoreArea.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		return scoreArea;
	}
	
	private void disableButtons()
	{
		// Disable all the buttons
		setButtonsEnabled(false);
	}
	
	private void enableButtons()
	{
		// Enable all the buttons
		setButtonsEnabled(true);
	}
	
	private void setButtonsEnabled(boolean enabled)
	{
		if (!checkGameEnd())
		{
			stepButton.setEnabled(enabled);
			turnButton.setEnabled(enabled);
			fullGameButton.setEnabled(enabled);
		}
		resetButton.setEnabled(enabled);
		optionsButton.setEnabled(enabled);
	}
	
	private void setStatus(String status)
	{
		setStatus(status, Color.BLACK);
	}
	
	private void setStatus(String status, Color c)
	{
		statusDisp.setText(status);
		statusDisp.setForeground(c);
	}
	
	private void setLifeCountA(int lifeCountA)
	{
		this.lifeCountA = lifeCountA;
		lifeLabelA.setText(lifeCountA + (lifeCountA == 1 ? " life remaining" : " lives remaining"));
		lifePointPanelA.setHealth(lifeCountA);
		repaint();
	}
	
	private void setLifeCountB(int lifeCountB)
	{
		this.lifeCountB = lifeCountB;
		lifeLabelB.setText(lifeCountB + (lifeCountB == 1 ? " life remaining" : " lives remaining"));
		lifePointPanelB.setHealth(lifeCountB);
		repaint();
	}
	
	/**
	 * @return true if the game ended
	 */
	private boolean checkGameEnd()
	{
		if (lifeCountA <= 0)
		{
			setStatus("B has won the game!", Config.PLAYER_B_COLOR);
			// Disable all play related buttons
			stepButton.setEnabled(false);
			turnButton.setEnabled(false);
			fullGameButton.setEnabled(false);
			return true;
		}
		else if (lifeCountB <= 0)
		{
			setStatus("A has won the game!", Config.PLAYER_A_COLOR);
			// Disable all play related buttons
			stepButton.setEnabled(false);
			turnButton.setEnabled(false);
			fullGameButton.setEnabled(false);
			return true;
		}
		return false;
	}
	
	private boolean step()
	{
		setStatus("Rolling...");
		float delay = 10F;
		
		if (dramaticRoll)
		{
			for (int i = 0; i < 10; i++)
			{
				rollLabelA.setIcon(DICE_ICONS[rollDiceZeroIndex()]);
				rollLabelB.setIcon(DICE_ICONS[rollDiceZeroIndex()]);
				try
				{
					Thread.sleep((int) delay);
				}
				catch (InterruptedException e)
				{
					System.err.println("Unexpected exception:");
					e.printStackTrace();
				}
				delay *= 1.5;
			}
		}
		
		int rollA = rollDiceZeroIndex();
		int rollB = rollDiceZeroIndex();
		int aScore = lifeCountA + rollA;
		int bScore = lifeCountB + rollB;
		
		rollLabelA.setIcon(DICE_ICONS[rollA]);
		scoreTextA.setText("Score: " + lifeCountA + (lifeCountA == 1 ? " life " : " lives ") + "+ " + (rollA + 1) + " dice roll = " + (aScore + 1));
		rollLabelB.setIcon(DICE_ICONS[rollB]);
		scoreTextB.setText("Score: " + lifeCountB + (lifeCountB == 1 ? " life " : " lives ") + "+ " + (rollB + 1) + " dice roll = " + (bScore + 1));
		
		if (dramaticRoll)
		{
			try
			{
				Thread.sleep((int) delay);
			}
			catch (InterruptedException e)
			{
				System.err.println("Unexpected exception:");
				e.printStackTrace();
			}
		}
		
		if (aScore > bScore)
		{
			setLifeCountA(lifeCountA - 1);
			setStatus("Player B won this turn!", Config.PLAYER_B_COLOR);
			return true;
		}
		else if (aScore < bScore)
		{
			setLifeCountB(lifeCountB - 1);
			setStatus("Player A won this turn!", Config.PLAYER_A_COLOR);
			return true;
		}
		else
		{
			setStatus("Tie! Please reroll.");
		}
		return false;
	}
	
	private void finishLastTask()
	{
		if (currentTask != null)
		{
			try
			{
				currentTask.join();
			}
			catch (InterruptedException e)
			{
				System.err.println("Unexpected error:");
				e.printStackTrace();
			}
		}
	}
	
	private void doStep()
	{
		finishLastTask();
		
		currentTask = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				SimulationFrame.this.disableButtons();
				SimulationFrame.this.step();
				SimulationFrame.this.enableButtons();
				SimulationFrame.this.checkGameEnd();
			}
		});
		currentTask.start();
	}
	
	private void doTurn()
	{
		finishLastTask();
		
		currentTask = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				SimulationFrame.this.disableButtons();
				while (!SimulationFrame.this.step()) ;
				SimulationFrame.this.enableButtons();
				SimulationFrame.this.checkGameEnd();
			}
		});
		currentTask.start();
	}
	
	private void doFullGame()
	{
		finishLastTask();
		
		currentTask = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				SimulationFrame.this.disableButtons();
				while (!SimulationFrame.this.checkGameEnd())
				{
					SimulationFrame.this.step();
				}
				SimulationFrame.this.enableButtons();
				SimulationFrame.this.checkGameEnd();
			}
		});
		
		currentTask.start();
	}
	
	private void reset()
	{
		disableButtons();
		initGame();
		enableButtons();
		
		// Clear icons
		rollLabelA.setIcon(null);
		scoreTextA.setText(null);
		rollLabelB.setIcon(null);
		scoreTextB.setText(null);
	}
	
	private void openOptions()
	{
		disableButtons();
		new OptionsFrame().setVisible(true);
	}
	
	private int rollDiceZeroIndex()
	{
		return RNG.nextInt(6);
	}
	
	private class OptionsFrame
			extends JFrame
	{
		JPanel mainPanel;
		
		private boolean newDramaticRoll;
		
		public OptionsFrame()
				throws HeadlessException
		{
			super("Game Options");
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			addWindowListener(new CloseListener());
			
			loadOptions();
			addOptions();
			
			// Render
			pack();
			setLocationRelativeTo(null);
		}
		
		private void loadOptions()
		{
			newDramaticRoll = dramaticRoll;
		}
		
		private void addOptions()
		{
			mainPanel = new JPanel();
			mainPanel.setLayout(new GridLayout(0, 1));
			mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
			
			final JButton dramaticRollToggle = new JButton(newDramaticRoll ? "Yes" : "No");
			dramaticRollToggle.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					newDramaticRoll = !newDramaticRoll;
					dramaticRollToggle.setText(newDramaticRoll ? "Yes" : "No");
				}
			});
			addOptionRow("Use dramatic dice diceroll?", "Toggles whether the dice rolling takes time",
						 dramaticRollToggle);
			
			add(mainPanel, BorderLayout.CENTER);
			
			// Save and cancel
			JPanel buttonContainers = new JPanel();
			buttonContainers.setLayout(new FlowLayout());
			buttonContainers.setBorder(new EmptyBorder(0, 0, 0, 0));
			
			JButton save = new JButton("Save");
			save.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					OptionsFrame.this.saveOptions();
				}
			});
			buttonContainers.add(save);
			
			JButton saveAndClose = new JButton("Save And Close");
			saveAndClose.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					OptionsFrame.this.saveOptions();
					OptionsFrame.this.dispatchEvent(new WindowEvent(OptionsFrame.this, WindowEvent.WINDOW_CLOSING));
				}
			});
			buttonContainers.add(saveAndClose);
			
			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent e)
				{
					OptionsFrame.this.dispatchEvent(new WindowEvent(OptionsFrame.this, WindowEvent.WINDOW_CLOSING));
				}
			});
			buttonContainers.add(cancel);
			
			add(buttonContainers, BorderLayout.SOUTH);
		}
		
		private void addOptionRow(String description, JButton button)
		{
			addOptionRow(description, null, button);
		}
		
		private void addOptionRow(String description, @Nullable String tooltip, JButton button)
		{
			JPanel optionPanel = new JPanel();
			optionPanel.setLayout(new BorderLayout());
			optionPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			optionPanel.setPreferredSize(new Dimension(400, 50));
			
			JLabel descriptionLabel = new JLabel(description);
			descriptionLabel.setToolTipText(tooltip);
			JPanel buttonWrapper = new JPanel();
			buttonWrapper.setLayout(new BoxLayout(buttonWrapper, BoxLayout.LINE_AXIS));
			buttonWrapper.add(button);
			
			optionPanel.add(descriptionLabel, BorderLayout.WEST);
			optionPanel.add(new JPanel(), BorderLayout.CENTER);
			optionPanel.add(buttonWrapper, BorderLayout.EAST);
			
			mainPanel.add(optionPanel);
		}
		
		private void saveOptions()
		{
			dramaticRoll = newDramaticRoll;
		}
		
		private class CloseListener
				extends WindowAdapter
		{
			@Override
			public void windowClosed(WindowEvent e)
			{
				enableButtons();
			}
		}
	}
}
