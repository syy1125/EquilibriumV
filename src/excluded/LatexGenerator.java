package excluded;

import math.BigFraction;
import math.ProbabilityCalculator;

public class LatexGenerator
{
	public static void main(String[] args)
	{
		System.out.println("% Horizontal");
		for (int a = 0; a < 5; a++)
		{
			for (int b = 0; b < 5; b++)
			{
				BigFraction prob = ProbabilityCalculator.probabilityWinA(a, b);
				System.out.println("\\node[below] at (" + (a*4+2) + "," + (10-b*2) + ") {$\\frac{" + prob.getNumerator() + "}{" + prob.getDenominator() + "}$};");
			}
		}
		
		System.out.println("% Vertical");
		for (int a = 0; a < 5; a++)
		{
			for (int b = 0; b < 5; b++)
			{
				BigFraction prob = ProbabilityCalculator.probabilityWinB(a,b);
				System.out.println("\\node[right] at (" + (a*4) + "," + (9-b*2) + ") {$\\frac{" + prob.getNumerator() + "}{" + prob.getDenominator() + "}$};");
			}
		}
	}
}
