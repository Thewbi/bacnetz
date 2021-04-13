package de.bacnetz.fatclient;

import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.Window;

import de.bacnetz.fatclient.buttonhandler.TestButtonHandler;

/**
 * This is the application class required by the Apache Pivot UI Framework.
 */
public class App extends Window implements Application, Bindable {

    private static Window window;

    @BXML
    private PushButton btToogleDoors;

    @Override
    public void startup(final Display display, final Map<String, String> properties) throws Exception {
        final BXMLSerializer bxmlSerializer = new BXMLSerializer();
        window = (Window) bxmlSerializer.readObject(getClass().getResourceAsStream("/main_window.bxml"));
        window.open(display);
    }

    @Override
    public boolean shutdown(final boolean optional) throws Exception {
        // Auto-generated method stub
        return false;
    }

    @Override
    public void suspend() throws Exception {
        // Auto-generated method stub

    }

    @Override
    public void resume() throws Exception {
        // Auto-generated method stub

    }

    @Override
    public void initialize(final Map<String, Object> namespace, final URL location, final Resources resources) {
        if (btToogleDoors != null) {
            ApplicationContextProvider.getApplicationContext().getBean("testButtonHandler", TestButtonHandler.class)
                    .connectToButton(btToogleDoors);
        }
    }

}
