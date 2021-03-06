package ua.pravex.timesheet.model;

import ua.pravex.timesheet.persist.pools.Poolable;

public class WorkForm implements Poolable {
    private int id;
    private String title;

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkForm)) return false;

        WorkForm workForm = (WorkForm) o;

        return id == workForm.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

	@Override
	public int getId() {
		return id;
	}
}
