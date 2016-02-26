public class WorkInstance {
    private int id;
    private WorkTitle workTitle;
    private WorkForm workForm;
    private String details;

    public void setId(int id) {
        this.id = id;
    }

    public WorkTitle getWorkTitle() {
        return workTitle;
    }

    public void setWorkTitle(WorkTitle workTitle) {
        this.workTitle = workTitle;
    }

    public WorkForm getWorkForm() {
        return workForm;
    }

    public void setWorkForm(WorkForm workForm) {
        this.workForm = workForm;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorkInstance)) return false;

        WorkInstance that = (WorkInstance) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
