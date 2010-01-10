/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
