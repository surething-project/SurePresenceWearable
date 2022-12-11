package pt.ulisboa.tecnico.surething.wearable.utils;

import java.util.HashMap;

public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String TEST = "0000bbbb-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributes.put("0000ffff-0000-1000-8000-00805f9b34fb", "test_service");
        // Sample Characteristics.
        attributes.put(TEST, "test_char");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
