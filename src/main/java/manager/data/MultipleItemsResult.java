package manager.data;

import manager.system.SelfX;

import java.io.Serializable;
import java.util.List;

public class MultipleItemsResult<T> implements Serializable {

    public long count;
    public List<T> items;
    public int maxLines = SelfX.MAX_DB_LINES_IN_ONE_SELECTS;
}
