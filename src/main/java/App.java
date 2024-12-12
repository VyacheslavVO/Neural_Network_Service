import com.ThreadServer;

import java.io.IOException;

public class App {


    public static void main(String[] args) {
        new ThreadServer(3345).run();
    }
}
