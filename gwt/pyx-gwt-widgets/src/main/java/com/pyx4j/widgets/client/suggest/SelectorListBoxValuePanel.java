/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on Sep 9, 2014
 * @author arminea
 * @version $Id$
 */
package com.pyx4j.widgets.client.suggest;

import java.util.Collection;

import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.IFormatter;
import com.pyx4j.widgets.client.TextBox;
import com.pyx4j.widgets.client.WatermarkComponent;
import com.pyx4j.widgets.client.event.shared.PasteHandler;

public class SelectorListBoxValuePanel<E> extends FlowPanel implements ISelectorValuePanel, WatermarkComponent {

    private final IFormatter<E, String> valueFormatter;

    private final  TextBox textBox;

    public SelectorListBoxValuePanel(IFormatter<E, String> valueFormatter) {
        this.valueFormatter = valueFormatter;

        textBox = new TextBox();
        add(textBox);
    }


    public void showValue(Collection<E> value){
        StringBuffer textBoxValue = new StringBuffer();

        //TODO: check if is enabled

        for(E val : value){
            if(textBoxValue.length()==0){
                textBoxValue.append(valueFormatter.format(val));
            }else{
                textBoxValue.append(",").append(valueFormatter.format(val));
            }
        }
        textBox.setText(textBoxValue.toString());

    }

    @Override
    public String getQuery() {
        return textBox.getText();
    }


    @Override
    public void setEnabled(boolean enabled) {
        // TODO Auto-generated method stub

    }


    @Override
    public boolean isEnabled() {
        return textBox.isEnabled();
    }


    @Override
    public void setEditable(boolean editable) {
        textBox.setEditable(editable);

    }


    @Override
    public boolean isEditable() {
        return textBox.isEditable();
    }


    @Override
    public void setDebugId(IDebugId debugId) {
        textBox.setDebugId(debugId);
    }


    @Override
    public int getTabIndex() {
        return textBox.getTabIndex();
    }


    @Override
    public void setAccessKey(char key) {
        textBox.setAccessKey(key);

    }


    @Override
    public void setFocus(boolean focused) {
        textBox.setFocus(focused);
    }


    @Override
    public void setTabIndex(int index) {
        textBox.setTabIndex(index);

    }


    @Override
    public HandlerRegistration addFocusHandler(FocusHandler handler) {
        return textBox.addFocusHandler(handler);
    }


    @Override
    public HandlerRegistration addBlurHandler(BlurHandler handler) {
        return textBox.addBlurHandler(handler);
    }


    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
        return textBox.addKeyUpHandler(handler);
    }


    @Override
    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return textBox.addKeyDownHandler(handler);
    }


    @Override
    public HandlerRegistration addKeyPressHandler(KeyPressHandler handler) {
        return textBox.addKeyPressHandler(handler);
    }


    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler) {
        return textBox.addValueChangeHandler(handler);
    }


    @Override
    public void setWatermark(String watermark) {
        textBox.setWatermark(watermark);

    }


    @Override
    public String getWatermark() {
        return textBox.getWatermark();
    }



    public HandlerRegistration addPasteHandler(PasteHandler handler) {
        return null;
    }

}
