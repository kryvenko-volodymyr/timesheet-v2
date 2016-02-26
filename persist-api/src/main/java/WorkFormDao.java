import javax.servlet.ServletContext;
import java.util.List;

public interface WorkFormDao {
    List<WorkForm> getWorkForms(String workTypeId);

    void setServletContext(ServletContext servletContext);
}
