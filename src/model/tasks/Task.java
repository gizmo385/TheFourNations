package model.tasks;
import java.io.Serializable;

import model.Civilization;
import model.map.Map;
import model.units.Unit;

/**
 * A task is an action performed on the map that makes some sort of meaningful change to the map
 * @author Christopher Chapline, James Fagan, Michelle Yung, Emile Leones
 *
 */
public abstract class Task implements Serializable
{
	private static final long serialVersionUID = -8217627590340674929L;
	private int remainingWorkRequirement; //amount of work required for this task
	private int locationOfWorker; //location where unit needs to be to work on this task
	protected int locationOfTask; //location where work is being done (i.e. where something is being built)
	protected Map map;
	protected Unit unit;
	protected boolean isDone;
	protected Civilization civ = Civilization.getInstance();
	
	/**
	 * Creates a task
	 * @param work The amount of work required to complete the task (ticks).
	 * @param locWorker The location that the worker will be located in when working on this task
	 * @param locTask The location that will be changed when the task is completed.
	 * @param map The map that this task is taking place on
	 */
	public Task(int work, int locWorker, int locTask, Map map )
	{
		remainingWorkRequirement = work;
		locationOfWorker = locWorker;
		locationOfTask = locTask;
		this.map = map;
		this.unit = null;
		this.isDone = false;
	}
	
	public final boolean decrement(int workDone)
	{
		remainingWorkRequirement -= workDone;
		if(remainingWorkRequirement <= 0)
		{
			performAction();
			return true;
		}
		return false;
	}
	
	public int getWorkerLocation()
	{
		return locationOfWorker;
	}
	
	public int getTaskLocation()
	{
		return locationOfTask;
	}
	
	public int getRemainingWorkRequirement() {
		return this.remainingWorkRequirement;
	}
	
	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	
	/**
	 * Performs the action for the call
	 */
	public abstract void performAction();

	public boolean isDone() {
		return isDone;
	}
}
