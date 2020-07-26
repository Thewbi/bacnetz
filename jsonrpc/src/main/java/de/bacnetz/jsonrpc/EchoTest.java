package de.bacnetz.jsonrpc;

import java.io.DataInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class EchoTest {

	public static void main(final String[] args) {

		try {

			final Socket echoSocket = new Socket("192.168.0.207", 1234);
			final OutputStream os = echoSocket.getOutputStream();
			final DataInputStream is = new DataInputStream(echoSocket.getInputStream());

			final int c;
			String responseLine;

//			while ((c = System.in.read()) != -1) {
//				os.write((byte) c);
//				if (c == '\n') {
//					os.flush();
//					responseLine = is.readLine();
//					System.out.println("echo: " + responseLine);
//				}
//			}

			String data = "{\"ID\":12,\"method\":\"getAppInfo\"}";
			data = data.length() + data;

			os.write(data.getBytes());
			os.flush();

			System.out.println("Reading ...");
			responseLine = is.readLine();
			System.out.println("echo: " + responseLine);

			os.close();
			is.close();
			echoSocket.close();
		} catch (final Exception e) {
			System.err.println("Exception:  " + e);
		}
	}
}
