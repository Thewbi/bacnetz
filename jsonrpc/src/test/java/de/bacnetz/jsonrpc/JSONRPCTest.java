package de.bacnetz.jsonrpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.jupiter.api.Test;

public class JSONRPCTest {

//	private static final String REMOTE_IP = "192.168.0.207";
	private static final String REMOTE_IP = "192.168.0.248";

	private static final int REMOTE_PORT = 1234;

	@Test
	public void testGetAppInfo() throws UnknownHostException, IOException {

		String data = "{\"ID\":12,\"method\":\"getAppInfo\"}";
		data = data.length() + data;

		System.out.println("Sending " + data + " to " + REMOTE_IP + ":" + REMOTE_PORT);
		try (Socket socket = new Socket(REMOTE_IP, REMOTE_PORT)) {

			new Thread(new Runnable() {

				@Override
				public void run() {

					String str;
					try {
						final BufferedReader bufferedReader = new BufferedReader(
								new InputStreamReader(socket.getInputStream()));

						System.out.println("Reading line ...");
						str = bufferedReader.readLine();
						System.out.println(str);
						System.out.println("Reading line done.");

						while ((str = bufferedReader.readLine()) != null) {

							System.out.println(str);
//						responseString.append(str);
						}
					} catch (final IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}).start();

			try {
				Thread.sleep(2000);
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			final OutputStream outputStream = socket.getOutputStream();
//			PrintWriter printWriter = new PrintWriter(outputStream);
//			printWriter.print(data);

			final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
			outputStreamWriter.write(data, 0, data.length());
			outputStreamWriter.flush();

			System.out.println("Waiting for response ...");

			try {
				Thread.sleep(20000);
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}
