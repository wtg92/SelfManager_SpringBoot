package manager.data.general;

public class FinalIntegerCounter {

    public int value = 0;

    public FinalIntegerCounter(int initialVal){
        this.value = initialVal;
    }

    public int getAndIncrementOne() {
        int rlt = value;
        value ++;
        return rlt;
    }
}
