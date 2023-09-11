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

/**
 * SessionInfo is used to store per-session properties that are determined at
 * different layers of the communication stack.
 */
public interface SessionInfo {

    /**
     * Get the time this session was started.
     *
     * @return the number of millis since midnight, January 1, 1970 UTC
     */
    public long getStartTime();

    /**
     * Get the time this session was idle.
     *
     * @return the number of seconds since the last user input event from
     * this session
     */
    public int getIdleTime();

    /**
     * Set the time this session was idle.
     *
     * @param seconds the number of seconds since the last user input event
     * from this session
     */
    public void setIdleTime(final int seconds);

    /**
     * Username getter.
     *
     * @return the username
     */
    public String getUsername();

    /**
     * Username setter.
     *
     * @param username the value
     */
    public void setUsername(String username);

    /**
     * Language getter.
     *
     * @return the language
     */
    public String getLanguage();

    /**
     * Language setter.
     *
     * @param language the value
     */
    public void setLanguage(String language);

    /**
     * Text window width getter.
     *
     * @return the window width
     */
    public int getWindowWidth();

    /**
     * Text window height getter.
     *
     * @return the window height
     */
    public int getWindowHeight();

    /**
     * Re-query the text window size.
     */
    public void queryWindowSize();
}
