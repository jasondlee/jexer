/*
 * Jexer - Java Text User Interface
 *
 * The MIT License (MIT)
 *
 * Copyright (C) 2021 Autumn Lamonte
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 * @author Autumn Lamonte [AutumnWalksTheLake@gmail.com]
 * @version 1
 */
package com.steeplesoft.jexer.backend;

import java.util.List;

import com.steeplesoft.jexer.TCommand;
import com.steeplesoft.jexer.event.TCommandEvent;
import com.steeplesoft.jexer.event.TInputEvent;

/**
 * This abstract class provides a screen, keyboard, and mouse to
 * TApplication.  It also exposes session information as gleaned from lower
 * levels of the communication stack.
 */
public abstract class GenericBackend implements Backend {

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * The session information.
     */
    protected SessionInfo sessionInfo;

    /**
     * The screen to draw on.
     */
    protected Screen screen;

    /**
     * Input events are processed by this Terminal.
     */
    protected TerminalReader terminal;

    /**
     * By default, GenericBackend adds a cmAbort after it sees
     * cmBackendDisconnect, so that TApplication will exit when the user
     * closes the Swing window or disconnects the ECMA48 streams.  But
     * MultiBackend wraps multiple Backends, and needs to decide when to send
     * cmAbort differently.  Setting this to false is how it manages that.
     * Note package private access.
     */
    boolean abortOnDisconnect = true;

    /**
     * The last time user input (mouse or keyboard) was received.
     */
    protected long lastUserInputTime = System.currentTimeMillis();

    /**
     * Whether or not this backend is read-only.
     */
    protected boolean readOnly = false;

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Backend ----------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Getter for sessionInfo.
     *
     * @return the SessionInfo
     */
    public final SessionInfo getSessionInfo() {
        return sessionInfo;
    }

    /**
     * Getter for screen.
     *
     * @return the Screen
     */
    public final Screen getScreen() {
        return screen;
    }

    /**
     * Sync the logical screen to the physical device.
     */
    public void flushScreen() {
        screen.flushPhysical();
    }

    /**
     * Check if there are events in the queue.
     *
     * @return if true, getEvents() has something to return to the application
     */
    public boolean hasEvents() {
        if (terminal.hasEvents()) {
            return true;
        }
        long now = System.currentTimeMillis();
        sessionInfo.setIdleTime((int) (now - lastUserInputTime) / 1000);
        /*
        System.err.println(sessionInfo + " idle " +
            sessionInfo.getIdleTime());
         */
        return false;
    }

    /**
     * Get keyboard, mouse, and screen resize events.
     *
     * @param queue list to append new events to
     */
    public void getEvents(final List<TInputEvent> queue) {
        if (terminal.hasEvents()) {
            terminal.getEvents(queue);

            long now = System.currentTimeMillis();
            TCommandEvent backendDisconnect = null;
            boolean disconnect = false;

            if (queue.size() > 0) {
                lastUserInputTime = now;

                TInputEvent event = queue.get(queue.size() - 1);
                if (event instanceof TCommandEvent) {
                    TCommandEvent command = (TCommandEvent) event;
                    if (command.equals(TCommand.cmBackendDisconnect)) {
                        backendDisconnect = command;
                        // This default backend assumes a single user, and if
                        // that user becomes disconnected we should terminate
                        // the application.
                        if (abortOnDisconnect == true) {
                            disconnect = true;
                        }
                    }
                }
            }

            sessionInfo.setIdleTime((int) (now - lastUserInputTime) / 1000);

            if (readOnly) {
                queue.clear();
                if (backendDisconnect != null) {
                    queue.add(backendDisconnect);
                }
            }

            if (disconnect) {
                assert (backendDisconnect != null);
                assert (queue.size() > 0);
                assert (queue.get(queue.size() - 1).equals(backendDisconnect));
                queue.add(new TCommandEvent(backendDisconnect.getBackend(),
                        TCommand.cmAbort));
            }
        }
    }

    /**
     * Close the I/O, restore the console, etc.
     */
    public void shutdown() {
        terminal.closeTerminal();
    }

    /**
     * Set the window title.
     *
     * @param title the new title
     */
    public void setTitle(final String title) {
        screen.setTitle(title);
    }

    /**
     * Set listener to a different Object.
     *
     * @param listener the new listening object that run() wakes up on new
     * input
     */
    public void setListener(final Object listener) {
        terminal.setListener(listener);
    }

    /**
     * Reload backend options from System properties.
     */
    public void reloadOptions() {
        terminal.reloadOptions();
    }

    /**
     * Check if backend is read-only.
     *
     * @return true if user input events from the backend are discarded
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Set read-only flag.
     *
     * @param readOnly if true, then input events will be discarded
     */
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }

}
