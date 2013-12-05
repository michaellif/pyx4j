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
 * Created on Jan 11, 2010
 * @author Michael
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.commons.CommonsStringUtils;

public class CTextArea extends CTextComponent<String, NTextArea> {

    private int rows = 4;

    public CTextArea() {
        super();
        NTextArea nativeTextArea = new NTextArea(this);
        nativeTextArea.setVisibleLines(getRows());
        nativeTextArea.setWidth("100%");
        setNativeWidget(nativeTextArea);
    }

    @Override
    public boolean isValueEmpty() {
        return super.isValueEmpty() || CommonsStringUtils.isEmpty(getValue());
    }

    public void scrollToBottom() {
        getWidget().scrollToBottom();
    }

    public void setRows(int rows) {
        this.rows = rows;
        getWidget().setVisibleLines(rows);
    }

    public int getRows() {
        return rows;
    }

}
