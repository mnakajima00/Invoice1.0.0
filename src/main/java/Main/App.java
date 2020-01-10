package Main;

import javax.swing.*;

public class App {
    public App(){

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CustomerFrame mf = new CustomerFrame(); //Initializes JFrame
            }
        });

    }

    public static void main(String[] args){
        App invoice = new App();
    }
}
