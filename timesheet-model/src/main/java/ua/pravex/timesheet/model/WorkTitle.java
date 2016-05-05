package ua.pravex.timesheet.model;

import ua.pravex.timesheet.persist.pools.Poolable;

public class WorkTitle implements Poolable {
    private int id;
    private String title;
    private WorkType workType;

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public WorkType getWorkType() {
        return workType;
    }

    public void setWorkType(WorkType workType) {
        this.workType = workType;
    }

	@Override
	public int getId() {
		return id;
	}
}
