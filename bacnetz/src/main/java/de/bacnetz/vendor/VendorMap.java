package de.bacnetz.vendor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

import de.bacnetz.controller.DefaultMessageController;

public class VendorMap {
	
	private static final Logger LOG = LogManager.getLogger(VendorMap.class);
	
    public static Map<Integer, String> processVendorMap() throws FileNotFoundException, IOException {
    	
        // from file from eclipse
//        final String filename = "src/main/resources/BACnetVendors.csv";
//      final BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
    	
    	// for the fat runnable .jar
    	final String filename = "BACnetVendors.csv";
    	
    	LOG.info("Loading from classpath! filename=" + filename);
    	
    	ClassPathResource classPathResource = new ClassPathResource(filename);
    	LOG.info("classPathResource: " + classPathResource);
    	
    	InputStreamReader inputStreamReader = new InputStreamReader(classPathResource.getInputStream(), "UTF-8");
    	LOG.info("inputStreamReader: " + inputStreamReader);

    	final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    	LOG.info("bufferedReader: " + bufferedReader);

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
