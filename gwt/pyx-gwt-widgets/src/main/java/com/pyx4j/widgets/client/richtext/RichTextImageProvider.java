package com.pyx4j.widgets.client.richtext;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RichTextImageProvider {
    void selectImage(AsyncCallback<String> callback);
}
