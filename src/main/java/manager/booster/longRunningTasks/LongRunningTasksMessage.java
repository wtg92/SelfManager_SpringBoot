package manager.booster.longRunningTasks;

import manager.exception.SMException;

import java.io.Serializable;

public class LongRunningTasksMessage implements Serializable {

    public Integer status;
    public Object[] params;

    public SMException error;

    public Integer type;

    public long startUtc;
    public long endUtc;

}
