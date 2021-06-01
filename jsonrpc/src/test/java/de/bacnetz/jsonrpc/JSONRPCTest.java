package de.bacnetz.jsonrpc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import com.geze.gmu.services.bacnet.BACnetObjectIdentifier;
import com.geze.gmu.services.bacnet.BACnetObjectType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * tail -f /opt/gmu/logs/error-bacapp-ip.txt
 */
public class JSONRPCTest {

//    private static final String REMOTE_IP = "192.168.2.2";
    private static final String REMOTE_IP = "192.168.2.3";
//	private static final String REMOTE_IP = "192.168.0.207";
//	private static final String REMOTE_IP = "192.168.0.248";
//    private static final String REMOTE_IP = "169.254.254.254";

    private static final int REMOTE_PORT = 1234;

    private static int sequenceId = 0;

    final byte[] getBytes(final String requestStr) {
        try {
            return requestStr.getBytes(StandardCharsets.UTF_8.name());
        } catch (final UnsupportedEncodingException e) {
            // should not happen
            return requestStr.getBytes();
        }
    }

    public static String bacnetObjectIdentifierToString(final BACnetObjectIdentifier objectIdentifier) {
        return bacnetObjectIdentifierToString(objectIdentifier, false);
    }

    public static String bacnetObjectIdentifierToString(final BACnetObjectIdentifier objectIdentifier,
            final boolean useText) {
        final BACnetObjectType objectType = objectIdentifier.getObjectType();
        return '(' + (useText ? objectType.getText() : String.valueOf(objectType.getCode())) + ','
                + objectIdentifier.getInstanceId() + ')';
    }

    @Test
    public void testGetAppInfo() throws UnknownHostException, IOException {

        try (Socket socket = new Socket(REMOTE_IP, REMOTE_PORT)) {

            new Thread(new Runnable() {

                @Override
                public void run() {

                    System.out.println("Reading ...");

                    final String str;
                    try {
                        final InputStream inputStream = socket.getInputStream();
                        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                        int totalLength = -1;
                        String totalValue = "";

                        while (true) {
//                            System.out.println("Reading ...");
                            final char[] buffer = new char[1024];
                            final int bytesRead = inputStreamReader.read(buffer);

//                            System.out.println("bytesRead: " + bytesRead);
                            final String value = new String(buffer);
//                            System.out.println("Value: " + value);

                            if (totalLength < 0) {
                                if (value.contains("{")) {
                                    final String dataSplit[] = value.split("\\{", 2);
                                    final String segmentLengthAsString = dataSplit[0];
                                    totalLength = Integer.parseInt(segmentLengthAsString);
                                    totalLength -= bytesRead;
                                    totalValue += "{" + dataSplit[1];
                                } else {
//                                    System.out.println(value);
                                    totalLength = Integer.parseInt(value.trim());
                                    totalLength -= bytesRead;
                                }
                            } else {
//                                final String dataSplit[] = value.split("\\{", 2);
//                                final String segmentLengthAsString = dataSplit[0];
//                                final int segmentLength = Integer.parseInt(segmentLengthAsString);
                                final int segmentLength = bytesRead;
                                totalLength -= segmentLength;

//                                totalValue += "{" + dataSplit[1];
                                totalValue += value;

                            }

                            if (totalLength <= 0) {
                                totalValue = totalValue.trim();
//                                System.out.println(totalValue);

                                final Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
                                final JsonParser jp = new JsonParser();
                                final JsonElement je = jp.parse(totalValue);
                                final String prettyJsonString = gson.toJson(je);

                                System.out.println(prettyJsonString);

                                totalLength = -1;
                                totalValue = "";
                            }
                        }

//                        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                        str = bufferedReader.readLine();
//                        System.out.println(str);
//                        System.out.println("Reading line done.");

//                        while ((str = bufferedReader.readLine()) != null) {
//                            System.out.println(str);
//                        }

//                        System.out.println("Reading line done.");
                    } catch (final IOException e) {
                        // e.printStackTrace();
                        System.out.println(e.getMessage());
                    }

                }

            }).start();

            try {
                Thread.sleep(2000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }

            final OutputStream outputStream = socket.getOutputStream();
            final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");

            // get app info
//            final JSONObject request = getAppInfo();

//            // search devices
//            final JSONObject request = searchDevices();

            // get devices
//            final JSONObject request = getDevices();

            // METHOD_GET_PROPERTIES
//            final BACnetObjectType objectType = BACnetObjectType.DEVICE;
//            final int instanceId = 2;
//            final BACnetObjectIdentifier objectIdentifier = new BACnetObjectIdentifier(objectType, instanceId);
//            final JSONObject request = getProperties(objectIdentifier);

            // METHOD_GET_PROPERTY
            final BACnetObjectType objectType = BACnetObjectType.DEVICE;
            final int instanceId = 2;
            final BACnetObjectIdentifier objectIdentifier = new BACnetObjectIdentifier(objectType, instanceId);
            final JSONObject request = getProperty(objectIdentifier, BACnetPropertyIdentifier.VENDOR_IDENTIFIER);

            final String data = request.toString();
            final String dataWithSize = data.length() + data;
            System.out.println("Sending " + dataWithSize + " to " + REMOTE_IP + ":" + REMOTE_PORT);
            outputStreamWriter.write(dataWithSize, 0, dataWithSize.length());
            outputStreamWriter.flush();

//            final String test = "abc";
//            outputStreamWriter.write(test, 0, test.length());
//            outputStreamWriter.flush();

//            // Send JSON Request
//            final String requestStr = data;
//            final byte[] requestBytes = getBytes(requestStr);
//            final byte[] lengthBytes = String.valueOf(requestBytes.length).getBytes();
//            outputStream.write(lengthBytes);
//            outputStream.write(requestBytes);
//            outputStream.flush();

            System.out.println("Waiting for response ...");

            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }

        }

    }

    private JSONObject getAppInfo() {
        final JSONObject request = new JSONObject();
        request.put(Constants.MESSAGE_METHOD, "getAppInfo");
        request.put(Constants.MESSAGE_ID, getNextSequenceId());
        return request;
    }

    private JSONObject searchDevices() {
        final JSONObject request = new JSONObject();
        request.put(Constants.MESSAGE_METHOD, Constants.METHOD_SEARCH_DEVICES);
        request.put(Constants.MESSAGE_ID, getNextSequenceId());

        return request;
    }

    private JSONObject getDevices() {
        final JSONObject request = new JSONObject();
        request.put(Constants.MESSAGE_METHOD, Constants.METHOD_GET_DEVICES);
        request.put(Constants.MESSAGE_ID, getNextSequenceId());
        return request;
    }

    private JSONObject getProperty(final BACnetObjectIdentifier objectIdentifier,
            final BACnetPropertyIdentifier bacnetPropertyIdentifier) {
        final JSONObject params = new JSONObject();
        params.put(Constants.PARAM_DEVICE_INSTANCE, 2);

        final JSONObject paramAllProperties = new JSONObject();
        paramAllProperties.put(Constants.PARAM_OBJECT_ID, bacnetObjectIdentifierToString(objectIdentifier));
        paramAllProperties.put(Constants.PARAM_PROPERTY_ID, bacnetPropertyIdentifier.getCode());
        params.put(Constants.PARAM_OBJECT_PROPERTIES, new JSONArray().put(paramAllProperties));

        final JSONObject request = new JSONObject();
        request.put(Constants.MESSAGE_METHOD, Constants.METHOD_GET_PROPERTIES);
        if (params != null) {
            request.put(Constants.MESSAGE_PARAMS, params);
        }
        request.put(Constants.MESSAGE_ID, getNextSequenceId());
        return request;
    }

    private JSONObject getProperties(final BACnetObjectIdentifier objectIdentifier) {

        final JSONObject params = new JSONObject();
        params.put(Constants.PARAM_DEVICE_INSTANCE, 2);

        final JSONObject paramAllProperties = new JSONObject();
        paramAllProperties.put(Constants.PARAM_OBJECT_ID, bacnetObjectIdentifierToString(objectIdentifier));
        paramAllProperties.put(Constants.PARAM_PROPERTY_ID, BACnetPropertyIdentifier.ALL.getCode());
        params.put(Constants.PARAM_OBJECT_PROPERTIES, new JSONArray().put(paramAllProperties));

        final JSONObject request = new JSONObject();
        request.put(Constants.MESSAGE_METHOD, Constants.METHOD_GET_PROPERTIES);
        if (params != null) {
            request.put(Constants.MESSAGE_PARAMS, params);
        }
        request.put(Constants.MESSAGE_ID, getNextSequenceId());
        return request;
    }

    private static int getNextSequenceId() {
        sequenceId++;
        return sequenceId;
    }

}
