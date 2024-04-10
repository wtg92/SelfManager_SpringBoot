package manager.data.career;

import manager.system.SM;

import java.io.Serializable;
import java.util.List;

public class StatisticsList <T> implements Serializable {

    public long count;
    public List<T> items;
    public int maxLines = SM.MAX_LINES;
}
