package ua.pravex.timesheet.model;

import ua.pravex.timesheet.persist.pools.Poolable;

public class WorkType implements Poolable {
    private int id;
    private String title;

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

	@Override
	public int getId() {
		return id;
	}

}
