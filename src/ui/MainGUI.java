package ui;

import ui.chart.ProbabilityChartFrame;
import ui.sim.SimulationFrame;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainGUI
{
	private static final LayoutManager BUTTON_CONTAINER_LAYOUT = new BorderLayout(25, 25);
	private static final Border CONTAINER_BORDER = new EmptyBorder(10, 10, 10, 10);
	
	private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 30);
	
	private static void openSim()
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				new SimulationFrame();
			}
		});
		
		thread.start();
	}
	
	private static void openProbChart()
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				new ProbabilityChartFrame();
			}
		});
		
		thread.start();
	}
	
	private static void openRules()
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				new RulesFrame();
			}
		});
		
		thread.start();
	}
	
	private static JPanel newContainerPanel()
	{
		JPanel containerPanel = new JPanel();
		containerPanel.setBorder(CONTAINER_BORDER);
		containerPanel.setLayout(new BorderLayout());
		containerPanel.setPreferredSize(new Dimension(500,150));
		return containerPanel;
	}
	
	public static void main(String[] args)
	{
		JFrame mainFrame = new JFrame(Config.LAUNCHER_NAME);
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(0, 1));
		mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		// Setup buttons
		JPanel buttonContainer;
		
		JButton openRules = new JButton("Basic Rules");
		openRules.setFont(BUTTON_FONT);
		openRules.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				openRules();
			}
		});
		
		buttonContainer = newContainerPanel();
		buttonContainer.add(openRules);
		mainPanel.add(buttonContainer);
		
		JButton openSim = new JButton("Open Simulator");
		openSim.setFont(BUTTON_FONT);
		openSim.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				openSim();
			}
		});
		
		buttonContainer = newContainerPanel();
		buttonContainer.add(openSim);
		mainPanel.add(buttonContainer, BorderLayout.CENTER);
		
		JButton openProbGraph = new JButton("Open Probability Flowchart");
		openProbGraph.setFont(BUTTON_FONT);
		openProbGraph.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				openProbChart();
			}
		});
		
		buttonContainer = newContainerPanel();
		buttonContainer.add(openProbGraph);
		mainPanel.add(buttonContainer, BorderLayout.CENTER);
		
		mainFrame.add(mainPanel, BorderLayout.CENTER);
		mainFrame.pack();
		
		// Render
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}
}
