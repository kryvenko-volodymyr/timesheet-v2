import javax.servlet.ServletContext;
import java.util.List;

public interface DomainObjectsManager {
    public List<WorkType> getWorkTypes ();

    void setServletContext(ServletContext servletContext);

    List<WorkTitle> getWorkTitles(String workTypeId);

    List<WorkForm> getWorkForms(String workTypeId);

    List<WorkInstance> getWorkInstances(String workTitleId, String workFormId);

    WorkInstance createWorkInstance(String workTitleId, String workFormId, String employeeAccount, String workInstanceDetails);
}
