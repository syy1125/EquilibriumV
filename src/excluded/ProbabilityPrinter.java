package excluded;

import math.BigFraction;
import math.ProbabilityCalculator;

public class ProbabilityPrinter
{
	private static void tabularPrint(Object[][] table)
	{
		int maxLength = 0;
		
		for (Object[] row : table)
		{
			for (Object item : row)
			{
				int length = String.valueOf(item).length();
				if (maxLength < length)
				{
					maxLength = length;
				}
			}
		}
		
		for (Object[] row : table)
		{
			for (Object item : row)
			{
				int length = String.valueOf(item).length();
				int i = 0;
				for (; i < (maxLength - length) / 2; i++)
				{
					System.out.print(' ');
				}
				System.out.print(item);
				for (; i <= maxLength - length; i++)
				{
					System.out.print(' ');
				}
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args)
	{
		BigFraction[][] probabilityGraph = ProbabilityCalculator.generateProbabilityGraph();
		for (BigFraction[] row : probabilityGraph) {
			for (int i = 0; i < row.length; i++)
			{
				row[i] = row[i] == null ? null :row[i].multiply(new BigFraction(2));
			}
		}
		
		double[][] roundedGraph = new double[6][6];
		
		int maxLength = 0;
		
		for (Object[] row : probabilityGraph)
		{
			for (Object item : row)
			{
				int length = String.valueOf(item).length();
				if (maxLength < length)
				{
					maxLength = length;
				}
			}
		}
		
		for (int a = 5; a >= 0; a--)
		{
			BigFraction[] row = probabilityGraph[a];
			for (int b = 5; b >= 0; b--)
			{
				BigFraction item = row[b];
				roundedGraph[a][b] = item != null ? item.doubleValue() : 0;
				int length = String.valueOf(item).length();
				int i = 0;
				for (; i < (maxLength - length) / 2; i++)
				{
					System.out.print(' ');
				}
				System.out.print(item);
				for (; i <= maxLength - length; i++)
				{
					System.out.print(' ');
				}
			}
			System.out.println();
		}
		
		System.out.println();
		
		for (double[] row : roundedGraph)
		{
			for (double item : row)
			{
				int length = String.valueOf(item).length();
				if (maxLength < length)
				{
					maxLength = length;
				}
			}
		}
		
		for (int a = 5; a >= 0; a--)
		{
			double[] row = roundedGraph[a];
			for (int b = 5; b >= 0; b--)
			{
				double item = row[b];
				int length = String.valueOf(item).length();
				int i = 0;
				for (; i < (maxLength - length) / 2; i++)
				{
					System.out.print(' ');
				}
				System.out.print(item);
				for (; i <= maxLength - length; i++)
				{
					System.out.print(' ');
				}
			}
			System.out.println();
		}
		
//		System.out.println(probabilityGraph[5][0].add(probabilityGraph[4][0]).add(probabilityGraph[3][0]).add(probabilityGraph[2][0]).add(probabilityGraph[1][0]));
	}
}
