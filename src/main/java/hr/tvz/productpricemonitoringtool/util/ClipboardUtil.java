package hr.tvz.productpricemonitoringtool.util;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * ClipboardUtil class.
 * Contains method for copying data to clipboard.
 */
public class ClipboardUtil {

    private ClipboardUtil() {}

    /**
     * Copy data to clipboard.
     * @param data Data to be copied to clipboard.
     */
    public static void copyToClipboard(String data) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(data);
        clipboard.setContent(content);
    }
}
