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
 * Created on Jun 4, 2010
 * @author michaellif
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.widgets.client.richtext.RichTextEditor.RichTextFormat;
import com.pyx4j.widgets.client.richtext.RichTextEditor.RichTextParser;
import com.pyx4j.widgets.client.richtext.RichTextImageProvider;

public class CRichTextArea extends CTextComponent<String, NRichTextArea> {

    private IRichTextTidy tidy;

    private RichTextImageProvider imageProvider;

    public CRichTextArea() {
        setFormatter(new RichTextFormat());
        setParser(new RichTextParser());
        setNativeComponent(new NRichTextArea(this));
    }

    public void scrollToBottom() {
        getNativeComponent().scrollToBottom();
    }

    public void setTidy(IRichTextTidy tidy) {
        this.tidy = tidy;
    }

    public IRichTextTidy getTidy() {
        return tidy;
    }

    public void setImageProvider(RichTextImageProvider imageProvider) {
        this.imageProvider = imageProvider;
        if (getNativeComponent() != null) {
            getNativeComponent().setImageProvider(imageProvider);
        }
    }

    public RichTextImageProvider getImageProvider() {
        return imageProvider;
    }

    @Override
    public boolean isValueEmpty() {
        return super.isValueEmpty() || HtmlUtils.isEmpty(getValue());
    }

}