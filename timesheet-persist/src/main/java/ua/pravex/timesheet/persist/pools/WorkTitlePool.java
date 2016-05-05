package ua.pravex.timesheet.persist.pools;


import ua.pravex.timesheet.persist.pools.Poolable;
import ua.pravex.timesheet.model.WorkTitle;
import ua.pravex.timesheet.model.WorkType;

import java.util.Map;
import java.util.TreeMap;

public class WorkTitlePool {
    private Map<Integer, Poolable> pool = new TreeMap<>();

    public WorkTitle add(int id, String title, Poolable workType) {
        if (!pool.containsKey(id)) {
            WorkTitle workTitle = new WorkTitle();
            workTitle.setId(id);
            workTitle.setTitle(title);
            workTitle.setWorkType((WorkType)workType);
            pool.put(id, workTitle);
            return workTitle;
        }
        return (WorkTitle)pool.get(id);
    }

    public WorkTitle get(int id) {
        return (WorkTitle)pool.get(id);
    }
}
