package ui;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import java.awt.Font;
import java.awt.HeadlessException;

public class RulesFrame
		extends JFrame
{
	public RulesFrame()
			throws HeadlessException
	{
		super(Config.RULES_NAME);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		//language=HTML
		String rules = "<font style='font-size: 20px;' face='Arial'>" +
				"In EquilibriumV, each player starts with 5 lives<br><br>" +
				"When playing, each player tosses a fair six-sided die<br><br>" +
				"The score of a player is the sum of the number of lives that player has and the roll on the dice<br><br>" +
				"The player with the <b><em><u>lower</u></em></b> score wins the round<br><br>" +
				"If there is a tie, the dice are re-rolled and scores are re-calculated<br><br>" +
				"The player who lost the round will lose 1 life<br><br>" +
				"The first player who reaches 0 lives loses the game" +
				"</font>";
		
		JTextPane rulesPane = new JTextPane();
		rulesPane.setEditable(false);
		rulesPane.setContentType("text/html");
		rulesPane.setText(rules);
		rulesPane.setBorder(new EmptyBorder(30, 30, 30, 30));
		
		add(rulesPane);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
}
