import javax.servlet.ServletContext;
import java.util.List;

public interface WorkInstanceDao {
    void setServletContext(ServletContext servletContext);

    List<WorkInstance> getWorkInstances(String workTitleId, String workFormId);

    WorkInstance createWorkInstance(String workTitleId, String workFormId, String employeeAccount, String workInstanceDetails);
}
