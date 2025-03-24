package manager.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

public abstract class SelfXCollectionUtils {

    public static <T> ComparisonResult<T> compareLists(List<T> list1, List<T> list2, BiPredicate<T,T> checker) {
        if (list1 == null) {
            list1 = new ArrayList<>();
        }
        if (list2 == null) {
            list2 = new ArrayList<>();
        }

        Set<T> onlyInList2 = new HashSet<>();
        Set<T> inBoth = new HashSet<>();

        Set<T> onlyInList1 = new HashSet<>(list1);

        for (T elementList2 : list2) {
            boolean foundMatch = false;
            for (T elementList1 : list1) {
                if (checker.test(elementList1, elementList2)) {
                    inBoth.add(elementList1);
                    onlyInList1.remove(elementList1);
                    foundMatch = true;
                    break;
                }
            }
            if (!foundMatch) {
                onlyInList2.add(elementList2);
            }
        }

        return new ComparisonResult<>(new ArrayList<>(onlyInList1), new ArrayList<>(onlyInList2), new ArrayList<>(inBoth));
    }

    public static class ComparisonResult<T> {
        public final List<T> onlyInList1;
        public final List<T> onlyInList2;
        public final List<T> inBoth;

        public ComparisonResult(List<T> onlyInList1, List<T> onlyInList2, List<T> inBoth) {
            this.onlyInList1 = onlyInList1;
            this.onlyInList2 = onlyInList2;
            this.inBoth = inBoth;
        }

    }
}
