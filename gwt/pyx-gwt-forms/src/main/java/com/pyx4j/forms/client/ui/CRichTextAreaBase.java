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
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.HtmlUtils;

//TODO add support to NativeRichTextArea
public abstract class CRichTextAreaBase<WIDGET_TYPE extends Widget & INativeRichTextComponent<String>> extends CTextComponent<String, WIDGET_TYPE> {

    private final int rows = 4;

    private IRichTextTidy tidy;

    public CRichTextAreaBase() {
        super();
        setWidth("100%");
    }

    public CRichTextAreaBase(String title) {
        super(title);
        setWidth("100%");
    }

    public void setTidy(IRichTextTidy tidy) {
        this.tidy = tidy;
    }

    public IRichTextTidy getTidy() {
        return tidy;
    }

    @Override
    public boolean isValueEmpty() {
        return super.isValueEmpty() || HtmlUtils.isEmpty(getValue());
    }

    public int getRows() {
        return rows;
    }

}