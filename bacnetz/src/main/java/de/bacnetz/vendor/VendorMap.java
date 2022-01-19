package de.bacnetz.vendor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class VendorMap {
	
    public static Map<Integer, String> processVendorMap() throws FileNotFoundException, IOException {
    	
        // from file from eclipse
//        final String filename = "src/main/resources/BACnetVendors.csv";
    	
    	// for the fat runnable .jar
    	final String filename = "BACnetVendors.csv";
        
        final BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));

        return readVendorMap(bufferedReader);
    }

    private static Map<Integer, String> readVendorMap(final BufferedReader bufferedReader) throws IOException {

        final Map<Integer, String> map = new HashMap<>();

        try {

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {

                line = StringUtils.trim(line);

                final String[] split = line.split(";");

                final int vendorId = Integer.parseInt(split[0]);
                String vendorName = split[1];

                if (StringUtils.isBlank(vendorName)) {
                    vendorName = "";
                    for (int i = 2; i < split.length; i++) {
                        if (i > 2) {
                            vendorName += " ";
                        }
                        vendorName += split[i];
                    }
                }

                map.put(vendorId, vendorName);
            }

        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        return map;
    }
}
