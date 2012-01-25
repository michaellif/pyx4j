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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;

import com.pyx4j.widgets.client.richtext.ExtendedRichTextArea;
import com.pyx4j.widgets.client.richtext.ImageGallery;

public class RichTextEditorViewImpl extends ScrollPanel implements RichTextEditorView {

    public RichTextEditorViewImpl() {
        setSize("100%", "100%");

        ExtendedRichTextArea editor = new ExtendedRichTextArea();
        editor.setSize("800px", "450px");
        editor.getElement().getStyle().setProperty("padding", "40px");
        add(editor);

        List<Image> images = new ArrayList<Image>();
        Image image = new Image("http://images.metmuseum.org/CRDImages/ma/web-thumb/1997.149.9.jpg");
        image.setTitle("Reclining Nude");
        images.add(image);
        image = new Image("http://images.metmuseum.org/CRDImages/ma/web-thumb/DT1308.jpg");
        image.setTitle("The Mountain");
        images.add(image);
        image = new Image("http://images.metmuseum.org/CRDImages/ma/web-thumb/DT2533.jpg");
        image.setTitle("Juan Gris");
        images.add(image);
        image = new Image("http://images.metmuseum.org/CRDImages/ma/web-thumb/DT7873.jpg");
        image.setTitle("The Marketplace, Vitebsk");
        images.add(image);
        image = new Image("http://images.metmuseum.org/CRDImages/ma/web-thumb/DT6414.jpg");
        image.setTitle("Self-Portrait");
        images.add(image);

        ImageGallery imageProvider = new ImageGallery();
        imageProvider.setImages(images);

        editor.setImageProvider(imageProvider);
    }

}
