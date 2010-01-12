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
package com.pyx4j.forms.client.gwt;

import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;

import com.pyx4j.forms.client.ui.CSuggestBox;
import com.pyx4j.forms.client.ui.INativeEditableComponent;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public class NativeSuggestBox extends NativeTriggerComponent<Object> implements INativeEditableComponent<Object> {

    private final SuggestBox suggestBox;

    private final SuggestTextBox suggestTextBox;

    private final MultiWordSuggestOracle oracle;

    private final CSuggestBox csuggestBox;

    public NativeSuggestBox(CSuggestBox csuggestBox) {
        super();
        this.csuggestBox = csuggestBox;
        suggestTextBox = new SuggestTextBox(csuggestBox);
        oracle = new MultiWordSuggestOracle();
        suggestBox = new SuggestBox(oracle, suggestTextBox);

        construct(suggestBox, suggestTextBox);

        setWidth(csuggestBox.getWidth());
        setHeight(csuggestBox.getHeight());

        setTabIndex(csuggestBox.getTabIndex());

    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        suggestTextBox.setEnabled(enabled);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setEnabled(!readOnly);
        suggestTextBox.setReadOnly(readOnly);
    }

    @Override
    public boolean isEnabled() {
        return suggestTextBox.isEnabled();
    }

    @Override
    protected void onTrigger(boolean show) {
        MessageDialog.info("Under Construction", "Under Construction");
    }

    public void addItem(String optionName) {
        oracle.add(optionName);
    }

    public void removeAllItems() {
        oracle.clear();
    }

    public void setNativeValue(Object value) {
        // TODO Auto-generated method stub

    }

    public CSuggestBox getCComponent() {
        return csuggestBox;
    }

    @Override
    public boolean isReadOnly() {
        return suggestTextBox.isReadOnly();
    }

}

class SuggestTextBox extends com.google.gwt.user.client.ui.TextBox {

    private final CSuggestBox suggestBox;

    public SuggestTextBox(CSuggestBox suggestBox) {
        super();
        this.suggestBox = suggestBox;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
    }

}
