import server.ServerFacade;
import ui.PreloginUI;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("♕ 240 Chess Client");
        ServerFacade server = new ServerFacade("http://localhost:8080");
        new PreloginUI(server).run();
    }
}