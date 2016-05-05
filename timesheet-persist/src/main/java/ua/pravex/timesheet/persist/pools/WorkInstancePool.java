package ua.pravex.timesheet.persist.pools;

import ua.pravex.timesheet.persist.pools.Poolable;
import ua.pravex.timesheet.model.WorkForm;
import ua.pravex.timesheet.model.WorkInstance;
import ua.pravex.timesheet.model.WorkTitle;

import java.util.Map;
import java.util.TreeMap;

public class WorkInstancePool {
    private Map<Integer, Poolable> pool = new TreeMap<>();

    public WorkInstance add(int id, String details, Poolable workTitle, Poolable workForm) {
        if (!pool.containsKey(id)) {
            WorkInstance workInstance = new WorkInstance();
            workInstance.setId(id);
            workInstance.setDetails(details);
            workInstance.setWorkTitle((WorkTitle)workTitle);
            workInstance.setWorkForm((WorkForm)workForm);
            pool.put(id, workInstance);
            return workInstance;
        }
        return (WorkInstance)pool.get(id);
    }

    public WorkInstance get(int id) {
        return (WorkInstance)pool.get(id);
    }

}
