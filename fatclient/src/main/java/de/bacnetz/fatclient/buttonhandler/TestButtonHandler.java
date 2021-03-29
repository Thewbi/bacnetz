package de.bacnetz.fatclient.buttonhandler;

import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.PushButton;

public class TestButtonHandler implements ButtonPressListener {

    private PushButton btPushButton;

    private String label = "TestButtonHandler";

    public TestButtonHandler() {
        btPushButton = null;
    }

    public TestButtonHandler(final PushButton btToogleDoors) {
        connectToButton(btToogleDoors);
    }

    public TestButtonHandler(final String label) {
        this.label = label;
    }

    public void connectToButton(final PushButton btPushButton) {
        if (this.btPushButton != null) {
            disconnectFromButton();
        }
        this.btPushButton = btPushButton;
        this.btPushButton.getButtonPressListeners().add(this);
    }

    private void disconnectFromButton() {
        btPushButton.getButtonPressListeners().remove(this);
        btPushButton = null;
    }

    @Override
    public void buttonPressed(final Button button) {
        System.out.println(label);
    }

}
