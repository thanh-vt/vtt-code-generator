package vn.thanhvt;

@FunctionalInterface
public interface RunnableWithError {

    void run() throws Exception;
}
