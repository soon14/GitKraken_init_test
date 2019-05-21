package cn.rf.hz.web.utils;

/**
 * 两条交叉线，交点的坐标
 * 
 * @author 程依寿 2015年6月11日 下午2:35:45
 */
public class Line
{

	/**
	 * 两条线的X,Y轴坐标
	 * 
	 * 
	 */
	public static double[] getCrossedPoint(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4)
	{
		double[] ret = new double[2];
		double x;
		double y;

		double k1 = Float.MAX_VALUE;
		double k2 = Float.MAX_VALUE;
		boolean flag1 = false;
		boolean flag2 = false;
		if ((x1 - x2) == 0)
			flag1 = true;
		if ((x3 - x4) == 0)
			flag2 = true;
		if (!flag1)
			k1 = (y1 - y2) / (x1 - x2);
		if (!flag2)
			k2 = (y3 - y4) / (x3 - x4);
		if (k1 == k2)
			return null;
		if (flag1)
		{
			if (flag2)
				return null;
			x = x1;
			if (k2 == 0)
			{
				y = y3;
			} else
			{
				y = k2 * (x - x4) + y4;
			}
		} else if (flag2)
		{
			x = x3;
			if (k1 == 0)
			{
				y = y1;
			} else
			{
				y = k1 * (x - x2) + y2;
			}
		} else
		{
			if (k1 == 0)
			{
				y = y1;
				x = (y - y4) / k2 + x4;
			} else if (k2 == 0)
			{
				y = y3;
				x = (y - y2) / k1 + x2;
			} else
			{
				x = (k1 * x2 - k2 * x4 + y4 - y2) / (k1 - k2);
				y = k1 * (x - x2) + y2;
			}
		}
		if (between(x1, x2, x) && between(y1, y2, y) && between(x3, x4, x) && between(y3, y4, y))
		{
			ret[0] = x;
			ret[1] = y;

			return ret;
		} else
		{
			return null;
		}
	}

	public static boolean between(double a, double b, double target)
	{
		if (target >= a - 0.01 && target <= b + 0.01 || target <= a + 0.01 && target >= b - 0.01)
			return true;
		else
			return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		// double[] crossPoint = getCrossedPoint(line1, line2);
		// if (crossPoint != null)
		// {
		// System.out.println("Crossed point is: X:Y ---" + crossPoint[0] + ":"
		// + crossPoint[1]);
		// } else
		// {
		// System.out.println("No crossed point");
		// }

	}
}