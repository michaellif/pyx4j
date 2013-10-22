/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Nov 9, 2011
 * @author stanp
 * @version $Id$
 */
package com.pyx4j.tester.client.view.widget;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.widgets.client.dialog.CancelOption;
import com.pyx4j.widgets.client.dialog.Dialog;
import com.pyx4j.widgets.client.richtext.ExtendedRichTextArea;
import com.pyx4j.widgets.client.richtext.ImageGallery;
import com.pyx4j.widgets.client.richtext.RichTextImageProvider;

public class RichTextEditorViewImpl extends ScrollPanel implements RichTextEditorView {

    class RichTextImageDialog extends Dialog implements CancelOption, RichTextImageProvider {

        private final ImageGallery gallery;

        private AsyncCallback<String> selectionHandler;

        public RichTextImageDialog() {
            super("Image Picker");
            this.setDialogOptions(this);

            gallery = new ImageGallery() {
                @Override
                public void onSelectImage(Image image) {
                    selectionHandler.onSuccess(image.getUrl());
                    RichTextImageDialog.this.hide(false);
                }

                @Override
                public void onRemoveImage(Image image) {
                    removeImage(image);
                }
            };

            setBody(gallery);
        }

        @Override
        public void selectImage(AsyncCallback<String> callback) {
            selectionHandler = callback;
            layout();
        }

        public void addImage(String url) {
            gallery.addImage(url, url.substring(url.lastIndexOf('/') + 1));
        }

        @Override
        public boolean onClickCancel() {
            selectionHandler.onSuccess(null);
            return true;
        }
    }

    public RichTextEditorViewImpl() {
        setSize("100%", "100%");

        ExtendedRichTextArea editor = new ExtendedRichTextArea();
        editor.setSize("800px", "450px");
        editor.getElement().getStyle().setProperty("padding", "40px");
        add(editor);

        RichTextImageDialog imageProvider = new RichTextImageDialog();
        imageProvider.addImage("http://images.metmuseum.org/CRDImages/ma/web-thumb/1997.149.9.jpg");
        imageProvider.addImage("http://images.metmuseum.org/CRDImages/ma/web-thumb/DT1308.jpg");
        imageProvider.addImage("http://images.metmuseum.org/CRDImages/ma/web-thumb/DT2533.jpg");
        imageProvider.addImage("http://images.metmuseum.org/CRDImages/ma/web-thumb/DT7873.jpg");
        imageProvider.addImage("http://images.metmuseum.org/CRDImages/ma/web-thumb/DT6414.jpg");

        editor.setImageProvider(imageProvider);
    }

}
