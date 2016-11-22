package ui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;

public abstract class InitializableGUI
		extends JFrame
{
	protected JPanel initPanel;
	/** All subclasses will initialize mainPanel during the init method. **/
	protected JPanel mainPanel;
	
	public InitializableGUI(String title)
			throws HeadlessException
	{
		super(title);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		
		preInit();
		init();
		postInit();
	}
	
	protected void preInit()
	{
		// Initializing panel
		initPanel = new JPanel();
		initPanel.setLayout(new GridBagLayout());
		
		JLabel initLabel = new JLabel("Initializing...");
		initLabel.setFont(new Font("Arial", Font.PLAIN, 48));
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		
		initPanel.add(initLabel, gbc);
		add(initPanel, BorderLayout.CENTER);
		initPanel.setVisible(true);
		pack();
		setSize(getDimensions());
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
	}
	
	protected abstract void init();
	
	protected abstract Dimension getDimensions();
	
	protected void postInit()
	{
		remove(initPanel);
		add(mainPanel, BorderLayout.CENTER);
		setResizable(true);
		setVisible(true);
	}
}
