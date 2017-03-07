Jexer - Java Text User Interface library
========================================

This library implements a text-based windowing system reminiscient of
Borland's [Turbo Vision](http://en.wikipedia.org/wiki/Turbo_Vision)
system.  (For those wishing to use the actual C++ Turbo Vision
library, see [Sergio Sigala's C++ version based on the public domain
sources released by Borland.](http://tvision.sourceforge.net/) )

Jexer currently supports three backends:

* System.in/out to a command-line ECMA-48 / ANSI X3.64 type terminal
  (tested on Linux + xterm).  I/O is handled through terminal escape
  sequences generated by the library itself: ncurses is not required
  or linked to.  xterm mouse tracking using UTF8 and SGR coordinates
  are supported.  For the demo application, this is the default
  backend on non-Windows platforms.

* The same command-line ECMA-48 / ANSI X3.64 type terminal as above,
  but to any general InputStream/OutputStream or Reader/Writer.  See
  the file jexer.demos.Demo2 for an example of running the demo over a
  TCP socket.  jexer.demos.Demo3 demonstrates how one might use a
  character encoding than the default UTF-8.

* Java Swing UI.  This backend can be selected by setting
  jexer.Swing=true.  The default window size for Swing is 132x40,
  which is set in jexer.session.SwingSession.  For the demo
  application, this is the default backend on Windows platforms.

Additional backends can be created by subclassing
jexer.backend.Backend and passing it into the TApplication
constructor.



License
-------

This project is licensed under the MIT License.  See the file LICENSE
for the full license text.



Acknowledgements
----------------

Jexer makes use of the Terminus TrueType font [made available
here](http://files.ax86.net/terminus-ttf/) .



Usage
-----

Simply subclass TApplication and then run it in a new thread:

```Java
import jexer.*;

class MyApplication extends TApplication {

    public MyApplication() throws Exception {
        super(BackendType.SWING); // Could also use BackendType.XTERM

        // Create standard menus for File and Window
        addFileMenu();
        addWindowMenu();

        // Add a custom window, see below for its code.
        addWindow(new MyWindow(this));
    }

    public static void main(String [] args) {
        try {
            MyApplication app = new MyApplication();
            (new Thread(app)).start();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
```

Similarly, subclass TWindow and add some widgets:

```Java
class MyWindow extends TWindow {

    public MyWindow(TApplication application) {
        // See TWindow's API for several constructors.  This one uses the
        // application, title, width, and height.  Note that the window width
        // and height include the borders.  The widgets inside the window
        // will see (0, 0) as the top-left corner inside the borders,
        // i.e. what the window would see as (1, 1).
        super(application, "My Window", 30, 20);

        // See TWidget's API for convenience methods to add various kinds of
        // widgets.  Note that ANY widget can be a container for other
        // widgets: TRadioGroup for example has TRadioButtons as child
        // widgets.

        // We will add a basic label, text entry field, and button.
        addLabel("This is a label", 5, 3);
        addField(5, 5, 20, false, "enter text here");
        // For the button, we will pop up a message box if the user presses
        // it.
        addButton("Press &Me!", 5, 8, new TAction() {
            public void DO() {
                MyWindow.this.messageBox("Box Title", "You pressed me, yay!");
            }
        } );
    }
}
```

Put these into a file, compile it with jexer.jar in the classpath, run
it and you'll see an application like this:

![The Example Code Above](/screenshots/readme_application.png?raw=true "The application in the text of README.md")

See the files in jexer.demos for many more detailed examples showing
all of the existing UI controls.  The demo can be run in three
different ways:

  * 'java -jar jexer.jar' .  This will use System.in/out with
    xterm-like sequences on non-Windows platforms.  On Windows it will
    use a Swing JFrame.

  * 'java -Djexer.Swing=true -jar jexer.jar' .  This will always use
    Swing on any platform.

  * 'java -cp jexer.jar jexer.demos.Demo2 PORT' (where PORT is a
    number to run the TCP daemon on).  This will use the telnet
    protocol to establish an 8-bit clean channel and be aware of
    screen size changes.



More Screenshots
----------------

![Several Windows Open Including A Terminal](/screenshots/screenshot1.png?raw=true "Several Windows Open Including A Terminal")

![Yo Dawg...](/screenshots/yodawg.png?raw=true "Yo Dawg, I heard you like text windowing systems, so I ran a text windowing system inside your text windowing system so you can have a terminal in your terminal.")



System Properties
-----------------

The following properties control features of Jexer:

  jexer.Swing
  -----------

  Used only by jexer.demos.Demo1.  If true, use the Swing interface
  for the demo application.  Default: true on Windows platforms
  (os.name starts with "Windows"), false on non-Windows platforms.

  jexer.Swing.cursorStyle
  -----------------------

  Used by jexer.io.SwingScreen.  Selects the cursor style to draw.
  Valid values are: underline, block, outline.  Default: underline.



Known Issues / Arbitrary Decisions
----------------------------------

Some arbitrary design decisions had to be made when either the
obviously expected behavior did not happen or when a specification was
ambiguous.  This section describes such issues.

  - TTerminalWindow will hang on input from the remote if the
    TApplication is exited before the TTerminalWindow's process has
    closed on its own.  This is due to a Java limitation/interaction
    between blocking reads (which is necessary to get UTF8 translation
    correct) and file streams.

  - See jexer.tterminal.ECMA48 for more specifics of terminal
    emulation limitations.

  - TTerminalWindow uses cmd.exe on Windows.  Output will not be seen
    until enter is pressed, due to cmd.exe's use of line-oriented
    input (see the ENABLE_LINE_INPUT flag for GetConsoleMode() and
    SetConsoleMode()).

  - TTerminalWindow launches 'script -fqe /dev/null' on non-Windows
    platforms.  This is a workaround for the C library behavior of
    checking for a tty: script launches $SHELL in a pseudo-tty.  This
    works on Linux but might not on other Posix-y platforms.

  - Java's InputStreamReader as used by the ECMA48 backend requires a
    valid UTF-8 stream.  The default X10 encoding for mouse
    coordinates outside (160,94) can corrupt that stream, at best
    putting garbage keyboard events in the input queue but at worst
    causing the backend reader thread to throw an Exception and exit
    and make the entire UI unusable.  Mouse support therefore requires
    a terminal that can deliver either UTF-8 coordinates (1005 mode)
    or SGR coordinates (1006 mode).  Most modern terminals can do
    this.

  - jexer.session.TTYSession calls 'stty size' once every second to
    check the current window size, performing the same function as
    ioctl(TIOCGWINSZ) but without requiring a native library.

  - jexer.io.ECMA48Terminal calls 'stty' to perform the equivalent of
    cfmakeraw() when using System.in/out.  System.out is also
    (blindly!)  put in 'stty sane cooked' mode when exiting.



Roadmap
-------

Many tasks remain before calling this version 1.0:

0.0.4

- TStatusBar
- TEditor
- TWindow
  - "Smart placement" for new windows

0.0.5: BUG HUNT

- Swing performance is better, triple-buffering appears to have helped.

0.1.0: BETA RELEASE

- TSpinner
- TComboBox
- TCalendar

Wishlist features (2.0):

- TTerminal
  - Handle resize events (pass to child process)
- Screen
  - Allow complex characters in putCharXY() and detect them in putStringXY().
- Drag and drop
  - TEditor
  - TField
  - TText
  - TTerminal
  - TComboBox
