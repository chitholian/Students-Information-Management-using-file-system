package team7.cu.sim;

/**
 * Created by Chitholian on 6/23/2017.
 */
public final class DataChangeOption {
    public static final int DEPARTMENT = 1;
    public static final int STUDENTS = 2;
    public long nid;
    public int opt;

    public DataChangeOption(int option, long nid) {
        opt = option;
        this.nid = nid;
    }
}
