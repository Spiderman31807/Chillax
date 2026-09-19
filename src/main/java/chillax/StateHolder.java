package chillax;

public interface StateHolder {
	abstract State getChillaxState();

	abstract void setChillaxState(State state);

	abstract float getSleepingAngle();

	abstract void setSleepingAngle(float angle);
}