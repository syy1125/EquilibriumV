package math;

public class ProbabilityCalculator
{
	public static final BigFraction[][] PROBABILITY_CHART = generateProbabilityChart();
	
	/**
	 * Calculates the probability that a will win at this point
	 *
	 * @param aPower A's power at this level
	 * @param bPower B's power at this level
	 * @return THe probability that A wins
	 */
	public static BigFraction probabilityWinA(int aPower, int bPower)
	{
		int diff = aPower < bPower ? bPower - aPower : aPower - bPower;
		int denominator = 30 + diff;
		int numerator = (5 - diff) * (6 - diff) / 2;
		if (aPower < bPower) numerator = denominator - numerator;
		
		return new BigFraction(numerator, denominator, false);
	}
	
	public static BigFraction probabilityWinB(int aPower, int bPower)
	{
		return probabilityWinA(bPower, aPower); // Use symmetry
	}
	
	private static void log(int a, int b, Object msg)
	{
//		System.out.println("(" + a + " " + b + "): " + msg);
	}
	
	public static BigFraction[][] generateProbabilityChart()
	{
		BigFraction[][] probabilityChart = new BigFraction[6][6];
		probabilityChart[5][5] = new BigFraction(1);
		probabilityChart[0][0] = null;
		
		for (int a = 5; a >= 0; a--)
		{
			for (int b = 5; b >= 0; b--)
			{
				log(a, b, "Calculating...");
				if (a == 5 && b == 5 || a == 0 && b == 0)
				{
					continue;
				}
				
				BigFraction chance = new BigFraction(0);
				if (a != 5 && b != 0)
				{
					chance = chance.add(probabilityChart[a + 1][b].multiply(probabilityWinB(a + 1, b)));
				}
				if (b != 5 && a != 0)
				{
					chance = chance.add(probabilityChart[a][b + 1].multiply(probabilityWinA(a, b + 1)));
				}
				
				probabilityChart[a][b] = chance;
				log(a, b, chance);
			}
		}
		
		return probabilityChart;
	}
	
	public static BigFraction[] calcWinProbabilities(int lifeCountA, int lifeCountB)
	{
		BigFraction[][] probabilityGraph = new BigFraction[6][6];
		probabilityGraph[lifeCountA][lifeCountB] = new BigFraction(1);
		
		for (int a = lifeCountA; a >= 0; a--)
		{
			for (int b = lifeCountB; b >= 0; b--)
			{
				log(a, b, "Calculating...");
				if (a == lifeCountA && b == lifeCountB || a == 0 && b == 0)
				{
					continue;
				}
				
				BigFraction chance = new BigFraction(0);
				if (a != lifeCountA && b != 0)
				{
					chance = chance.add(probabilityGraph[a + 1][b].multiply(probabilityWinB(a + 1, b)));
				}
				if (b != lifeCountB && a != 0)
				{
					chance = chance.add(probabilityGraph[a][b + 1].multiply(probabilityWinA(a, b + 1)));
				}
				
				probabilityGraph[a][b] = chance;
				log(a, b, chance);
			}
		}
		
		BigFraction winChanceA = new BigFraction(0);
		for (int a = lifeCountA; a > 0; a--)
		{
			winChanceA = winChanceA.add(probabilityGraph[a][0]);
		}
		
		BigFraction winChanceB = new BigFraction(0);
		for (int b = lifeCountB; b > 0; b--)
		{
			winChanceB = winChanceB.add(probabilityGraph[0][b]);
		}
		
		return new BigFraction[]{winChanceA, winChanceB};
	}
}
