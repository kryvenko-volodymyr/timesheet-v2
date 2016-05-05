package ua.pravex.timesheet.persist.pools;

import ua.pravex.timesheet.persist.pools.Poolable;
import ua.pravex.timesheet.model.WorkType;

import java.util.Map;
import java.util.TreeMap;


public class WorkTypePool  {
    private Map<Integer, Poolable> pool = new TreeMap<>();

    public WorkType add (int id, String title) {
        if (!pool.containsKey(id)) {
            WorkType workType = new WorkType();
            workType.setId(id);
            workType.setTitle(title);
            pool.put(id, workType);
            return workType;
        }
        return (WorkType)pool.get(id);
    }

    public WorkType get (int id) {
        return (WorkType)pool.get(id);
    }
}
