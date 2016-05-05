package Service.Impl;

import Dao.WorkFormDao;
import Dao.WorkInstanceDao;
import Dao.WorkTitleDao;
import Dao.WorkTypeDao;
import Domain.WorkForm;
import Domain.WorkInstance;
import Domain.WorkTitle;
import Domain.WorkType;
import Service.DomainObjectsManager;

import javax.servlet.ServletContext;
import java.util.List;

public class DomainObjectsManagerImpl implements DomainObjectsManager {
    private ServletContext servletContext;

    @Override
    public List<WorkType> getWorkTypes() {
        WorkTypeDao workTypeDao = (WorkTypeDao)servletContext.getAttribute("workTypeDao");
        return workTypeDao.getWorkTypes();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Override
    public List<WorkTitle> getWorkTitles(String workTypeId) {
        WorkTitleDao workTitleDao = (WorkTitleDao)servletContext.getAttribute("workTitleDao");
        return workTitleDao.getWorkTitles(workTypeId);
    }

    @Override
    public List<WorkForm> getWorkForms(String workTypeId) {
        WorkFormDao workFormDao = (WorkFormDao)servletContext.getAttribute("workFormDao");
        return workFormDao.getWorkForms(workTypeId);
    }

    @Override
    public List<WorkInstance> getWorkInstances(String workTitleId, String workFormId) {
        WorkInstanceDao workInstanceDao = (WorkInstanceDao)servletContext.getAttribute("workInstanceDao");
        return workInstanceDao.getWorkInstances(workTitleId, workFormId);
    }

    @Override
    public WorkInstance createWorkInstance(String workTitleId, String workFormId, String employeeAccount, String workInstanceDetails) {
        WorkInstanceDao workInstanceDao = (WorkInstanceDao)servletContext.getAttribute("workInstanceDao");
        return workInstanceDao.createWorkInstance(workTitleId, workFormId, employeeAccount, workInstanceDetails);
    }
}
