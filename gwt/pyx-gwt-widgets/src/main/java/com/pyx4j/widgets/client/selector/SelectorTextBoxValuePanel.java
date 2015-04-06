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
 * Created on Sep 4, 2014
 * @author michaellif
 */
package com.pyx4j.widgets.client.selector;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.StringBox;
import com.pyx4j.widgets.client.event.shared.PasteEvent;
import com.pyx4j.widgets.client.event.shared.PasteHandler;

public class SelectorTextBoxValuePanel<E> extends StringBox implements ISelectorValuePanel {

    private final IFormatter<E, String> valueFormatter;

    public SelectorTextBoxValuePanel(IFormatter<E, String> valueFormatter) {
        this.valueFormatter = valueFormatter;

        addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                updateTextBox(getTextBoxWidget().getText(), true, null);
            }
        });

        addPasteHandler(new PasteHandler() {

            @Override
            public void onPaste(PasteEvent event) {
                updateTextBox(getTextBoxWidget().getText(), true, null);
            }
        });
    }

    public void showValue(E value) {
        setValue(valueFormatter.format(value));
    }

    @Override
    public String getQuery() {
        return getValue();
    }

}
