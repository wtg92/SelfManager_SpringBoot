package manager.data.career;

import manager.system.SM;

import java.io.Serializable;
import java.util.List;

public class MultipleItemsResult<T> implements Serializable {

    public long count;
    public List<T> items;
    public int maxLines = SM.MAX_DB_LINES_IN_ONE_SELECTS;
}
