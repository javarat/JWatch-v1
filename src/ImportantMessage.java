import javax.swing.JOptionPane;

public class ImportantMessage{

public static void infoBox(String infoMessage, String location)
    {
        JOptionPane.showMessageDialog(null, infoMessage, "Important Notice: " + location, JOptionPane.INFORMATION_MESSAGE);
    }
}