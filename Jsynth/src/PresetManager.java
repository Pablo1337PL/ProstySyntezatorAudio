import java.io.*;

public class PresetManager {
    // Zapis ustawień do pliku
    public static void saveSettings(SynthSettings settings, File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(settings);
        }
    }

    // Odczyt ustawień z pliku
    public static SynthSettings loadSettings(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (SynthSettings) ois.readObject();
        }
    }
}
