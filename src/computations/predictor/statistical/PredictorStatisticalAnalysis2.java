package computations.predictor.statistical;

import java.util.List;

import computations.Constants;
import computations.predictor.Phase;
import computations.predictor.physics.HelperPhysics;
import computations.predictor.statistical.stats2.ConstantAccelerationModel;
import computations.predictor.statistical.stats2.RegressionManager;
import computations.utils.Helper;
import computations.wheel.Wheel;
import exceptions.SessionNotReadyException;
import logger.Logger;

public class PredictorStatisticalAnalysis2
{
	public int predict(List<Double> ballCumsumTimes, List<Double> wheelCumsumTimes) throws SessionNotReadyException
	{
		if (wheelCumsumTimes.size() < Constants.MIN_NUMBER_OF_WHEEL_TIMES_BEFORE_PREDICTION
				|| ballCumsumTimes.size() < Constants.MIN_NUMBER_OF_BALL_TIMES_BEFORE_PREDICTION)
		{
			throw new SessionNotReadyException();
		}

		double originTimeBall = Helper.head(ballCumsumTimes);
		ballCumsumTimes = Helper.normalize(ballCumsumTimes, originTimeBall);

		double originTimeWheel = Helper.head(wheelCumsumTimes);
		wheelCumsumTimes = Helper.normalize(wheelCumsumTimes, originTimeWheel);

		List<Double> ballDiffTimes = Helper.computeDiff(ballCumsumTimes);
		List<Double> wheelDiffTimes = Helper.computeDiff(wheelCumsumTimes);

		double constantWheelSpeed = Helper.getWheelSpeed(0, Helper.peek(wheelDiffTimes)); // trick

		ConstantAccelerationModel ballModel = RegressionManager.computeModel(ballDiffTimes, Constants.Type.BALL);

		double cutoffSpeed = Constants.CUTOFF_SPEED;
		double remainingTime = ballModel.estimateTime(ballDiffTimes.size(), cutoffSpeed);

		double lastTimeBallPassesInFrontOfRef = Helper.peek(ballCumsumTimes);
		double remainingDistance = Helper.estimateDistanceConstantSpeed(lastTimeBallPassesInFrontOfRef,
				lastTimeBallPassesInFrontOfRef + remainingTime, constantWheelSpeed);
		double wheelSpeedInFrontOfMark = constantWheelSpeed;
		double lastKnownSpeedWheel = constantWheelSpeed;

		Logger.traceDEBUG("Remaining distance computed = " + Helper.printDigit(remainingDistance) + " m");

		double angleAtCutOffTime = HelperPhysics.estimatePhaseAngleDegrees(remainingDistance, Constants.get_WHEEL_CIRCUMFERENCE());
		Logger.traceDEBUG("Angle of the wheel at cutoff time = " + Helper.printDigit(angleAtCutOffTime) + " degrees.");

		int shiftPhaseBetweenInitialTimeAndCutOff = HelperPhysics.estimateShiftWithAngle(angleAtCutOffTime);

		/**
		 * Comparing this value with the true value can be used to optimize the
		 * algorithm.
		 */
		double lastWheelLapTimeInFrontOfRef = Helper.getLastTimeWheelIsInFrontOfRef(wheelCumsumTimes, lastTimeBallPassesInFrontOfRef);
		int initialPhase = Phase.findPhaseNumberBetweenBallAndWheel(lastTimeBallPassesInFrontOfRef, lastWheelLapTimeInFrontOfRef,
				wheelSpeedInFrontOfMark, Constants.DEFAULT_WHEEL_WAY);
		/**
		 * Shift depends on the speed of the wheel. High speed means more travel
		 * on average.
		 */
		int adjustedInitialPhase = (int) (Constants.DEFAULT_SHIFT_PHASE * lastKnownSpeedWheel);
		int finalPredictedShift = shiftPhaseBetweenInitialTimeAndCutOff + adjustedInitialPhase;
		Logger.traceDEBUG("Number of pockets (computed from angle) = " + shiftPhaseBetweenInitialTimeAndCutOff);
		Logger.traceDEBUG("DEFAULT_SHIFT_PHASE = " + adjustedInitialPhase);

		int predictedNumber = Wheel.getNumberWithPhase(initialPhase, finalPredictedShift, Constants.DEFAULT_WHEEL_WAY);
		Logger.traceDEBUG(
				"Initial phase was = " + initialPhase + ", Total shift = " + finalPredictedShift + ", Predicted number is = " + predictedNumber);
		return predictedNumber;
	}
}
