package computations;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import computations.Wheel.WheelWay;
import computations.predictor.Phase;
import computations.utils.Helper;

public class TestWheel
{
	@Test
	public void testWheel()
	{
		Assert.assertEquals(0, Wheel.findIndexOfNumber(0), 0);
		Assert.assertEquals(Wheel.NUMBERS.length - 1, Wheel.findIndexOfNumber(26));
		Assert.assertEquals(2, Wheel.findIndexOfNumber(15));

		// 26, 0, 32
		Assert.assertArrayEquals(new int[] { 26, 0, 32 }, Wheel.getNearbyNumbers(0, 1));

		// 2, 25, 17, 34, 6, 27
		Assert.assertArrayEquals(new int[] { 2, 25, 17, 34, 6 }, Wheel.getNearbyNumbers(17, 2));

		Assert.assertArrayEquals(new int[] { 2, 25, 17, 34, 6 }, Wheel.getNearbyNumbers(17, 2));

		Assert.assertEquals(26, Wheel.getNumberWithPhase(0, 1, WheelWay.CLOCKWISE));
		Assert.assertEquals(32, Wheel.getNumberWithPhase(0, 1, WheelWay.ANTICLOCKWISE));

		Assert.assertEquals(2, Wheel.getNumberWithPhase(17, 2, WheelWay.CLOCKWISE));
		Assert.assertEquals(6, Wheel.getNumberWithPhase(17, 2, WheelWay.ANTICLOCKWISE));
	}

	@Test
	public void testHelperSpeed()
	{
		// In meter/second
		Assert.assertEquals(Constants.get_BALL_CIRCUMFERENCE(), Helper.getBallSpeed(0, 1), 0.01);
		Assert.assertEquals(Constants.get_WHEEL_CIRCUMFERENCE(), Helper.getWheelSpeed(0, 1), 0.01);
	}

	@Test
	public void testHelperStrategy2()
	{
		// Let assume we are at 5000.
		double timeOfBallInFrontOfMark = 5456 * 0.001;
		double timeOfWheelInFrontOfMark = 6168 * 0.001;
		double lastWheelSpeed = Helper.getWheelSpeed(2600 * 0.001, 6168 * 0.001);

		// We want to find the number of the wheel where the ball passes in
		// front of the mark.
		int phaseNumber = Phase.findPhaseNumberBetweenBallAndWheel(timeOfBallInFrontOfMark, timeOfWheelInFrontOfMark, lastWheelSpeed,
				WheelWay.ANTICLOCKWISE);
		Assert.assertEquals(29, phaseNumber); // 29 is correct
	}

	// https://www.youtube.com/watch?v=nztuIbJhq6o
	// Second part of video. CLOCKWISE
	/*
	 * REAL MEASURES
	 * 
	 * BALLS = 28184 28714 29323 29957 30832 31748 32825 34014 35326 36727 38277
	 * 39924 41692 43643 45756
	 * 
	 * WHEELS = 27935 31866 35749 39723 43724 47751 51836
	 * 
	 */
	@Test
	public void testHelperStrategy3()
	{
		// Let assume we are at 35000.
		double timeOfBallInFrontOfMark = 35326 * 0.001;
		double timeOfWheelInFrontOfMark = 35749 * 0.001;
		double lastWheelSpeed = Helper.getWheelSpeed(31866 * 0.001, 35749 * 0.001);

		int phaseNumber = Phase.findPhaseNumberBetweenBallAndWheel(timeOfBallInFrontOfMark, timeOfWheelInFrontOfMark, lastWheelSpeed,
				WheelWay.CLOCKWISE);
		Assert.assertEquals(4, phaseNumber); // 4 is correct
	}

	@Test
	public void testHelperStrategy4()
	{
		// Let assume we are at 57000.
		double timeOfBallInFrontOfMark = 61564 * 0.001;
		double timeOfWheelInFrontOfMark = 60882 * 0.001;
		double lastWheelSpeed = Helper.getWheelSpeed(57196 * 0.001, 60882 * 0.001);

		int phaseNumber = Phase.findPhaseNumberBetweenBallAndWheel(timeOfBallInFrontOfMark, timeOfWheelInFrontOfMark, lastWheelSpeed,
				WheelWay.ANTICLOCKWISE);
		Assert.assertEquals(2, phaseNumber); // 2 is correct
	}

	// Can predict the ball before the wheel passes by.
	@Test
	public void testHelperStrategy5()
	{
		// Let assume we are at 57000.
		double timeOfBallInFrontOfMark = 58547 * 0.001;
		double timeOfWheelInFrontOfMark = 60882 * 0.001;
		double lastWheelSpeed = Helper.getWheelSpeed(57196 * 0.001, 60882 * 0.001);

		int phaseNumber = Phase.findPhaseNumberBetweenBallAndWheel(timeOfBallInFrontOfMark, timeOfWheelInFrontOfMark, lastWheelSpeed,
				WheelWay.ANTICLOCKWISE);
		Assert.assertEquals(11, phaseNumber); // 11 is correct
	}

	/*
	 * 0, 32, 15, 19, 4, 21, 2, 25, 17, 34, 6, 27, 13, 36, 11, 30, 8, 23, 10, 5,
	 * 24, 16, 33, 1, 20, 14, 31, 9, 22, 18, 29, 7, 28, 12, 35, 3, 26 };
	 */
	@Test
	public void unitTests()
	{
		Assert.assertEquals(1, Wheel.distanceBetweenNumbers(17, 34));
		Assert.assertEquals(1, Wheel.distanceBetweenNumbers(26, 0));
		Assert.assertEquals(1, Wheel.distanceBetweenNumbers(0, 26));
		Assert.assertEquals(Wheel.distanceBetweenNumbers(18, 34), Wheel.distanceBetweenNumbers(34, 18));
	}

	@Test
	public void distanceTwoNumbers()
	{
		Random r = new Random();
		double dist = 0;
		int N = 1000000;
		for (int i = 0; i < N; i++)
		{
			dist += Wheel.distanceBetweenNumbers(r.nextInt(37), r.nextInt(37));
		}
		dist /= N;
		Assert.assertTrue(dist > 9.2 && dist < 9.3);

	}
}
