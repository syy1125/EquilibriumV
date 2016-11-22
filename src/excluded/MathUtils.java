package excluded;

import java.math.BigInteger;

public class MathUtils
{
	public static long gcd(long num1, long num2)
	{
		if (num1 == 0) return num2;
		if (num2 == 0) return num1;

		do
		{
			// Keep num1 bigger
			if (num1 < num2)
			{
				long temp = num1;
				num1 = num2;
				num2 = temp;
			}
			
			num1 %= num2;
		}
		while (num1 != 0);
		
		return num2;
	}
	
	public static void main(String[] args)
	{
		System.out.println();
	}
}
