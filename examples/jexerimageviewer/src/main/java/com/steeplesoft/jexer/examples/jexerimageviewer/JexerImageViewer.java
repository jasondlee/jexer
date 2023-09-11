package com.steeplesoft.jexer.examples.jexerimageviewer;

import java.util.ArrayList;
import java.util.List;

import com.steeplesoft.jexer.TApplication;
import com.steeplesoft.jexer.menu.TMenu;

/**
 * Implements a simple image thumbnail file viewer.  Much of this code was
 * stripped down from TFileOpenBox.
 */
public class JexerImageViewer extends TApplication {

    /**
     * Main entry point.
     */
    public static void main(String [] args) throws Exception {
        JexerImageViewer app = new JexerImageViewer();
        (new Thread(app)).start();
    }

    /**
     * Public constructor chooses the ECMA-48 / Xterm backend.
     */
    public JexerImageViewer() throws Exception {
        super(BackendType.XTERM);

        // The stock tool menu has items for redrawing the screen, opening
        // images, and (when using the Swing backend) setting the font.
        addToolMenu();

        // We will have one menu containing a mix of new and stock commands
        TMenu fileMenu = addMenu("&File");

        // Stock commands: a new shell, exit program.
        fileMenu.addDefaultItem(TMenu.MID_SHELL);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);

        // Filter the files list to support image suffixes only.
        List<String> filters = new ArrayList<String>();
        filters.add("^.*\\.[Jj][Pp][Gg]$");
        filters.add("^.*\\.[Jj][Pp][Ee][Gg]$");
        filters.add("^.*\\.[Pp][Nn][Gg]$");
        filters.add("^.*\\.[Gg][Ii][Ff]$");
        filters.add("^.*\\.[Bb][Mm][Pp]$");
        setDesktop(new ImageViewerDesktop(this, ".", filters));
    }

}
