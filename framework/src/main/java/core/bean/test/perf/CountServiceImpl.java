package core.bean.test.perf;

/**
 * Created by ChaoChao on 12/12/2017.
 */
public class CountServiceImpl implements CountService {

    private int count = 0;

    public int count() {
        return count ++;
    }
}