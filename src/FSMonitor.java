public interface FSMonitor {
    public final int CREATED = 1;
    public final int REMOVED = 2;

    public void fireEvent(String fName, int eventType);
}
