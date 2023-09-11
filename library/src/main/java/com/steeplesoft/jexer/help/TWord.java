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
package com.steeplesoft.jexer.help;

import com.steeplesoft.jexer.THelpWindow;
import com.steeplesoft.jexer.TKeypress;
import com.steeplesoft.jexer.TWidget;
import com.steeplesoft.jexer.bits.CellAttributes;
import com.steeplesoft.jexer.bits.StringUtils;
import com.steeplesoft.jexer.event.TKeypressEvent;
import com.steeplesoft.jexer.event.TMouseEvent;

/**
 * TWord contains either a string to display or a clickable link.
 */
public class TWord extends TWidget {

    // ------------------------------------------------------------------------
    // Constants --------------------------------------------------------------
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Variables --------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * The word(s) to display.
     */
    private String words;

    /**
     * Link to another Topic.
     */
    private Link link;

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Public constructor.
     *
     * @param words the words to display
     * @param link link to other topic, or null
     */
    public TWord(final String words, final Link link) {

        // TWord is created by THelpText before the TParagraph is belongs to
        // is created, so pass null as parent for now.
        super(null, 0, 0, StringUtils.width(words), 1);

        this.words = words;
        this.link = link;

        // Don't make text-only words "active".
        if (link == null) {
            setEnabled(false);
        }
    }

    // ------------------------------------------------------------------------
    // Event handlers ---------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Handle mouse press events.
     *
     * @param mouse mouse button press event
     */
    @Override
    public void onMouseDown(final TMouseEvent mouse) {
        if (mouse.isMouse1()) {
            if (link != null) {
                ((THelpWindow) getWindow()).setHelpTopic(link.getTopic());
            }
        }
    }

    /**
     * Handle keystrokes.
     *
     * @param keypress keystroke event
     */
    @Override
    public void onKeypress(final TKeypressEvent keypress) {
        if (keypress.equals(TKeypress.kbEnter)) {
            if (link != null) {
                ((THelpWindow) getWindow()).setHelpTopic(link.getTopic());
            }
        }
    }

    // ------------------------------------------------------------------------
    // TWidget ----------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Draw the words.
     */
    @Override
    public void draw() {
        CellAttributes color = getTheme().getColor("thelpwindow.text");
        if (link != null) {
            if (isAbsoluteActive()) {
                color = getTheme().getColor("thelpwindow.link.active");
            } else {
                color = getTheme().getColor("thelpwindow.link");
            }
        }
        putStringXY(0, 0, words, color);
    }

    // ------------------------------------------------------------------------
    // TWord ------------------------------------------------------------------
    // ------------------------------------------------------------------------

}
