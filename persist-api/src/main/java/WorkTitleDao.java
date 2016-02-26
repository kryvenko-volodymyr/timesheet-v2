import javax.servlet.ServletContext;
import java.util.List;

public interface WorkTitleDao {
    void setServletContext(ServletContext servletContext);

    List<WorkTitle> getWorkTitles(String workTypeId);
}
