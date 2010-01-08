/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 25, 2009
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.widgets.client.richtext.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface RichTextImages extends ClientBundle {

    @Source("text_bold.png")
    ImageResource bold();

    @Source("drive_link.png")
    ImageResource createLink();

    @Source("17384.png")
    ImageResource hr();

    @Source("text_indent.png")
    ImageResource indent();

    @Source("44753.png")
    ImageResource insertImage();

    @Source("text_italic.png")
    ImageResource italic();

    @Source("text_align_center.png")
    ImageResource justifyCenter();

    @Source("text_align_left.png")
    ImageResource justifyLeft();

    @Source("text_align_right.png")
    ImageResource justifyRight();

    @Source("22397.png")
    ImageResource ol();

    @Source("text_indent_remove.png")
    ImageResource outdent();

    @Source("29083.png")
    ImageResource removeFormat();

    @Source("image.png")
    ImageResource removeLink();

    @Source("text_strikethrough.png")
    ImageResource strikeThrough();

    @Source("text_subscript.png")
    ImageResource subscript();

    @Source("text_superscript.png")
    ImageResource superscript();

    @Source("10381.png")
    ImageResource ul();

    @Source("text_underline.png")
    ImageResource underline();
}
