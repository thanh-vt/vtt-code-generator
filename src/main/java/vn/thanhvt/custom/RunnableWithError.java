package vn.thanhvt.custom;

@FunctionalInterface
public interface RunnableWithError {

    void run() throws Exception;
}
