package math;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class BigFraction
{
	private static final boolean DEFUALT_CANCEL = true;
	
	private static final BigInteger COMMA_BREAKPOINT = BigInteger.valueOf(1000000);
	private static final DecimalFormat FORMATTER;
	
	static
	{
		FORMATTER = (DecimalFormat) NumberFormat.getInstance(Locale.US);
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(',');
		FORMATTER.setDecimalFormatSymbols(symbols);
		FORMATTER.setGroupingUsed(true);
	}
	
	private BigInteger numerator;
	private BigInteger denominator;
	
	public BigFraction(long integer)
	{
		this(integer, 1, false);
	}
	
	public BigFraction(BigInteger integer)
	{
		this(integer, BigInteger.ONE, false);
	}
	
	public BigFraction(long numerator, long denominator)
	{
		this(numerator, denominator, DEFUALT_CANCEL);
	}
	
	public BigFraction(BigInteger numerator, BigInteger denominator)
	{
		this(numerator, denominator, DEFUALT_CANCEL);
	}
	
	public BigFraction(long numerator, long denominator, boolean cancel)
	{
		this(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator), cancel);
	}
	
	public BigFraction(BigInteger numerator, BigInteger denominator, boolean cancel)
	{
		if (denominator.equals(BigInteger.ZERO))
		{
			throw new ArithmeticException("Denominator cannot be zero!");
		}
		
		this.numerator = numerator;
		this.denominator = denominator;
		
		if (cancel)
		{
			cancel();
		}
	}
	
	private void cancel()
	{
		BigInteger gcd = numerator.gcd(denominator);
		numerator = numerator.divide(gcd);
		denominator = denominator.divide(gcd);
	}
	
	public BigFraction add(BigFraction other)
	{
		return add(other, DEFUALT_CANCEL);
	}
	
	public BigFraction add(BigFraction other, boolean cancel)
	{
		BigInteger numer = this.numerator.multiply(other.denominator).add(this.denominator.multiply(other.numerator));
		BigInteger denom = this.denominator.multiply(other.denominator);
		return new BigFraction(numer, denom, cancel);
	}
	
	public BigFraction multiply(BigFraction other)
	{
		return multiply(other, DEFUALT_CANCEL);
	}
	
	public BigFraction multiply(BigFraction other, boolean cancel)
	{
		return new BigFraction(this.numerator.multiply(other.numerator), this.denominator.multiply(other.denominator), cancel);
	}
	
	public BigFraction divide(BigFraction other)
	{
		return divide(other, DEFUALT_CANCEL);
	}
	
	public BigFraction divide(BigFraction other, boolean cancel)
	{
		return new BigFraction(this.numerator.multiply(other.denominator), this.denominator.multiply(other.numerator), cancel);
	}
	
	public double doubleValue()
	{
		return numerator.doubleValue() / denominator.doubleValue();
	}
	
	public BigInteger getNumerator()
	{
		return numerator;
	}
	
	public BigInteger getDenominator()
	{
		return denominator;
	}
	
	public BigFraction toCancelledForm()
	{
		return new BigFraction(numerator, denominator, true);
	}
	
	@Override
	public String toString()
	{
		return toString(numerator.compareTo(COMMA_BREAKPOINT) > 0 || denominator.compareTo(COMMA_BREAKPOINT) > 0);
	}
	
	public String toString(boolean comma)
	{
		return comma ? FORMATTER.format(numerator.longValue()) + "/" + FORMATTER
				.format(denominator.longValue()) : numerator + "/" + denominator;
	}
}
