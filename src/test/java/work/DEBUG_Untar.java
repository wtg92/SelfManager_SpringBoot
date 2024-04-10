package work;

import org.junit.Test;

public class DEBUG_Untar {
    @Test
    public void test1() throws Exception{
        UnTar.extractTarGz("test.tar","C:\\20240410\\test3",0);
    }
}
