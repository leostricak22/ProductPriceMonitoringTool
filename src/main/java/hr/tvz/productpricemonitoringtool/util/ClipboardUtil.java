package hr.tvz.productpricemonitoringtool.util;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ClipboardUtil {

    private ClipboardUtil() {}

    public static void copyToClipboard(String data) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(data);
        clipboard.setContent(content);
    }
}
