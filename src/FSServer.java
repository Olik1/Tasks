import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

public class FSServer {
    private String dirPath;
    private ArrayList<FSMonitor> clients;
    private volatile boolean canWork;//флаг проверки работы, => используем volatile запрещающий любую оптимизацию

    public FSServer(String dirPath) {
        this.dirPath = dirPath;
        clients = new ArrayList<>();
        canWork = true;
    }

    public void addClient(FSMonitor client){
        clients.add(client);
    }

    public void removeClient(FSMonitor client){
        clients.remove(client);
    }

    public void start(){
        canWork = true;
        run();//! delite later
    }
    public void stop(){
        canWork = false;
    }

    public void run(){
        try{
            //нам нужно следя за директорией получать оповещение о том, создан или нет файл, обращаемся к наблюдателю
            WatchService watch = FileSystems.getDefault().newWatchService();
            //чтобы привязать наблюдателя к конкретной директории мы регистрируем его по указанному пути ниже
            Paths.get(dirPath).register(watch,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE);
            //чтобы получать несколько событий мы создаем цикл и будем получать из Наблюдателя
            while (canWork){
                WatchKey key = watch.take();
                // блокирующий вызов, метод take будет останавливать выполнение кода,
                // пока не произойдет какого-либо события и мы его не получим
                for (WatchEvent<?> pollEvent: key.pollEvents()) { //? дженерик не используем
                    //создали цикл по списку событий
                   String fName = pollEvent.context().toString();
                   //контекст спец.данные вокруг данных (в данном случае имя файла)
                   //метод контекст вернет имя файла ввиде object
                   int type; //тип произошедшего события
                    if(pollEvent.kind() == StandardWatchEventKinds.ENTRY_CREATE)
                        type = FSMonitor.CREATED;
                    else type = FSMonitor.REMOVED;
                    for (FSMonitor client: clients) {
                        client.fireEvent(fName, type);
                    }
                }

                key.reset();//для получение нескольких событий и надо сбрасывать событие для перехода
            }
            watch.close();
        }catch (IOException ex){
            System.out.println(ex.getMessage());
        } catch (InterruptedException ex){
            //..когда будет подсоединять потоки настроить
        }

     }

}
