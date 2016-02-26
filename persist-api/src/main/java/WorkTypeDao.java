import javax.servlet.ServletContext;
import java.util.List;

public interface WorkTypeDao {
    void setServletContext(ServletContext servletContext);

    List<WorkType> getWorkTypes();
}
