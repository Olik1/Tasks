public class Client  implements FSMonitor{

    @Override
    public void fireEvent(String fName, int eventType) {
        switch (eventType){
            case FSMonitor.CREATED : System.out.println(fName + "created"); break;
            case FSMonitor.REMOVED : System.out.println(fName + "removed"); break;
            default : System.out.println("Smth strange happend...");
        }
    }
}
