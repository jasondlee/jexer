package com.steeplesoft.jexer.examples.jexerimageviewer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

import com.steeplesoft.jexer.TAction;
import com.steeplesoft.jexer.TApplication;
import com.steeplesoft.jexer.TDesktop;
import com.steeplesoft.jexer.TDirectoryList;
import com.steeplesoft.jexer.TImage;
import com.steeplesoft.jexer.TKeypress;
import com.steeplesoft.jexer.backend.SwingTerminal;
import com.steeplesoft.jexer.bits.CellAttributes;
import com.steeplesoft.jexer.bits.GraphicsChars;
import com.steeplesoft.jexer.event.TKeypressEvent;
import com.steeplesoft.jexer.event.TResizeEvent;
import com.steeplesoft.jexer.ttree.TDirectoryTreeItem;
import com.steeplesoft.jexer.ttree.TTreeItem;
import com.steeplesoft.jexer.ttree.TTreeViewWidget;

/**
 * The desktop contains a tree view on the left, list of files on the top
 * right, and image view on the bottom right.
 */
public class ImageViewerDesktop extends TDesktop {

    /**
     * The left-side tree view pane.
     */
    private TTreeViewWidget treeView;

    /**
     * The data behind treeView.
     */
    private TDirectoryTreeItem treeViewRoot;

    /**
     * The top-right-side directory list pane.
     */
    private TDirectoryList directoryList;

    /**
     * The bottom-right-side image pane.
     */
    private TImage imageWidget;

    /**
     * Public constructor.
     *
     * @param application the TApplication that manages this window
     * @param path        path of selected file
     * @param filters     a list of strings that files must match to be displayed
     * @throws IOException of a java.io operation throws
     */
    public ImageViewerDesktop(final TApplication application, final String path,
                              final List<String> filters) throws IOException {

        super(application);
        setActive(true);

        // Add directory treeView
        treeView = addTreeViewWidget(0, 0, getWidth() / 2, getHeight(),
                new TAction() {
                    public void DO() {
                        TTreeItem item = treeView.getSelected();
                        File selectedDir = ((TDirectoryTreeItem) item).getFile();
                        try {
                            directoryList.setPath(selectedDir.getCanonicalPath());
                            if (directoryList.getList().size() > 0) {
                                setThumbnail(directoryList.getPath());
                            } else {
                                if (imageWidget != null) {
                                    getChildren().remove(imageWidget);
                                }
                                imageWidget = null;
                            }
                            activate(treeView);
                        } catch (IOException e) {
                            // If the backend is Swing, we can emit the stack
                            // trace to stderr.  Otherwise, just squash it.
                            if (getScreen() instanceof SwingTerminal) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        );
        treeViewRoot = new TDirectoryTreeItem(treeView, path, true);

        // Add directory files list
        directoryList = addDirectoryList(path, getWidth() / 2 + 1, 0,
                getWidth() / 2 - 1, getHeight() / 2,

                new TAction() {
                    public void DO() {
                        setThumbnail(directoryList.getPath());
                    }
                },
                new TAction() {

                    public void DO() {
                        setThumbnail(directoryList.getPath());
                    }
                },
                filters);

        if (directoryList.getList().size() > 0) {
            activate(directoryList);
            setThumbnail(directoryList.getPath());
        } else {
            activate(treeView);
        }
    }

    /**
     * Handle window/screen resize events.
     *
     * @param event resize event
     */
    @Override
    public void onResize(final TResizeEvent event) {

        // Resize the tree and list
        treeView.setY(1);
        treeView.setWidth(getWidth() / 2);
        treeView.setHeight(getHeight() - 1);
        treeView.onResize(new TResizeEvent(getApplication().getBackend(),
                TResizeEvent.Type.WIDGET,
                treeView.getWidth(),
                treeView.getHeight()));
        treeView.getTreeView().onResize(new TResizeEvent(getApplication().getBackend(),
                TResizeEvent.Type.WIDGET,
                treeView.getWidth() - 1,
                treeView.getHeight() - 1));
        directoryList.setX(getWidth() / 2 + 1);
        directoryList.setY(1);
        directoryList.setWidth(getWidth() / 2 - 1);
        directoryList.setHeight(getHeight() / 2 - 1);
        directoryList.onResize(new TResizeEvent(getApplication().getBackend(),
                TResizeEvent.Type.WIDGET,
                directoryList.getWidth(),
                directoryList.getHeight()));

        // Recreate the image
        if (imageWidget != null) {
            getChildren().remove(imageWidget);
        }
        imageWidget = null;
        if (directoryList.getList().size() > 0) {
            activate(directoryList);
            setThumbnail(directoryList.getPath());
        } else {
            activate(treeView);
        }
    }

    /**
     * Handle keystrokes.
     *
     * @param keypress keystroke event
     */
    @Override
    public void onKeypress(final TKeypressEvent keypress) {

        if (treeView.isActive() || directoryList.isActive()) {
            if ((keypress.equals(TKeypress.kbEnter))
                    || (keypress.equals(TKeypress.kbUp))
                    || (keypress.equals(TKeypress.kbDown))
                    || (keypress.equals(TKeypress.kbPgUp))
                    || (keypress.equals(TKeypress.kbPgDn))
                    || (keypress.equals(TKeypress.kbHome))
                    || (keypress.equals(TKeypress.kbEnd))
            ) {
                // Tree view will be changing, update the directory list.
                super.onKeypress(keypress);

                // This is the same action as treeView's enter.
                TTreeItem item = treeView.getSelected();
                File selectedDir = ((TDirectoryTreeItem) item).getFile();
                try {
                    if (treeView.isActive()) {
                        directoryList.setPath(selectedDir.getCanonicalPath());
                    }
                    if (directoryList.getList().size() > 0) {
                        activate(directoryList);
                        setThumbnail(directoryList.getPath());
                    } else {
                        if (imageWidget != null) {
                            getChildren().remove(imageWidget);
                        }
                        imageWidget = null;
                        activate(treeView);
                    }
                } catch (IOException e) {
                    // If the backend is Swing, we can emit the stack trace
                    // to stderr.  Otherwise, just squash it.
                    if (getScreen() instanceof SwingTerminal) {
                        e.printStackTrace();
                    }
                }
                return;
            }
        }

        // Pass to my parent
        super.onKeypress(keypress);
    }

    /**
     * Draw me on screen.
     */
    @Override
    public void draw() {
        CellAttributes background = getTheme().getColor("tdesktop.background");
        putAll(' ', background);

        vLineXY(getWidth() / 2, 0, getHeight(),
                GraphicsChars.WINDOW_SIDE, getBackground());

        hLineXY(getWidth() / 2, getHeight() / 2, (getWidth() + 1) / 2,
                GraphicsChars.WINDOW_TOP, getBackground());

        putCharXY(getWidth() / 2, getHeight() / 2,
                GraphicsChars.WINDOW_LEFT_TEE, getBackground());
    }

    /**
     * Set the image thumbnail.
     *
     * @param file the image file
     */
    private void setThumbnail(final File file) {
        if (file == null) {
            return;
        }
        if (!file.exists() || !file.isFile()) {
            return;
        }

        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
        } catch (IOException e) {
            // If the backend is Swing, we can emit the stack trace to
            // stderr.  Otherwise, just squash it.
            if (getScreen() instanceof SwingTerminal) {
                e.printStackTrace();
            }
            return;
        }

        if (imageWidget != null) {
            getChildren().remove(imageWidget);
        }
        int width = getWidth() / 2 - 1;
        int height = getHeight() / 2 - 1;

        imageWidget = new TImage(this, getWidth() - width,
                getHeight() - height, width, height, image, 0, 0, null);

        // Resize the image to fit within the pane.
        imageWidget.setScaleType(TImage.Scale.SCALE);

        imageWidget.setActive(false);
        activate(directoryList);
    }

}
