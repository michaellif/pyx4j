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
 * Created on Aug 23, 2012
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.commons.css.ColorUtil;
import com.pyx4j.forms.client.ui.NColorPicker.ColorButton;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.IFocusWidget;
import com.pyx4j.widgets.client.TextBox;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

public class NColorPicker extends NFocusComponent<Integer, ColorButton, CColorPicker, ColorButton> implements INativeFocusComponent<Integer> {

    public NColorPicker(final CColorPicker colorPicker) {
        super(colorPicker);
    }

    @Override
    protected ColorButton createEditor() {
        return new ColorButton();
    }

    @Override
    protected ColorButton createViewer() {
        ColorButton button = new ColorButton();
        button.setEnabled(false);
        return button;
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        setTabIndex(getCComponent().getTabIndex());
    }

    @Override
    public void setNativeValue(Integer value) {
        ColorButton button;
        if (isViewable()) {
            button = getViewer();
        } else {
            button = getEditor();
        }
        button.setValue(value);

    }

    @Override
    public Integer getNativeValue() {
        if (!isViewable()) {
            return getEditor().getValue();
        } else {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
        }
        return null;
    }

    static void testRGBColorValueRange(int r, int g, int b, int a) {
        boolean rangeError = false;
        StringBuilder badComponents = new StringBuilder();

        if (a < 0 || a > 255) {
            rangeError = true;
            badComponents.append(" Alpha");
        }
        if (r < 0 || r > 255) {
            rangeError = true;
            badComponents.append(" Red");
        }
        if (g < 0 || g > 255) {
            rangeError = true;
            badComponents.append(" Green");
        }
        if (b < 0 || b > 255) {
            rangeError = true;
            badComponents.append(" Blue");
        }
        if (rangeError == true) {
            throw new IllegalArgumentException("Color parameter outside of expected range:" + badComponents);
        }
    }

    static void testHSBColorValueRange(int h, int s, int b) {
        boolean rangeError = false;
        StringBuilder badComponents = new StringBuilder();

        if (h < 0 || h > 360) {
            rangeError = true;
            badComponents.append(" Hue");
        }
        if (s < 0 || s > 100) {
            rangeError = true;
            badComponents.append(" Saturation");
        }
        if (b < 0 || b > 100) {
            rangeError = true;
            badComponents.append(" Brightness");
        }
        if (rangeError == true) {
            throw new IllegalArgumentException("Color parameter outside of expected range:" + badComponents);
        }
    }

    public class ColorButton extends Button implements IFocusWidget {

        private Integer color;

        public ColorButton() {
            super("");

            //TODO move to style
            getTextLabelComponent().getElement().getStyle().setProperty("margin", "2px 5px");
            getTextLabelComponent().getElement().getStyle().setProperty("padding", "0 2px");
            getTextLabelComponent().getElement().getStyle().setProperty("textAlign", "center");
            getTextLabelComponent().getElement().getStyle().setProperty("width", "60px");
            getTextLabelComponent().getElement().getStyle().setProperty("height", "1.5em");
            getTextLabelComponent().getElement().getStyle().setProperty("border", "1px inset #C2C2C2");
            getElement().getStyle().setProperty("opacity", "1");

            addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    new ColorPickerDialog() {
                        @Override
                        public boolean onClickOk() {
                            setValue(Integer.parseInt(valueBox.getText()));
                            return true;
                        }
                    }.show();
                }
            });
        }

        private void setRGB(int r, int g, int b, int a) {
            testRGBColorValueRange(r, g, b, a);
            color = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
        }

        private void setHSB(int h, int s, int b) {
            testHSBColorValueRange(h, s, b);
            color = ColorUtil.hsbToRgb(h, s, b);
        }

        public Integer getValue() {
            return color;
        }

        public void setValue(Integer color) {

            this.color = color;

            String colorString;
            String textString;
            if (color == null) {
                colorString = "#fff";
                textString = "";
            } else {
                if (getCComponent().isHueOnly()) {
                    colorString = ColorUtil.rgbToHex(ColorUtil.hsbToRgb((float) color / 360, 1, 1));
                    textString = color.toString();
                } else {
                    colorString = ColorUtil.rgbToHex(color);
                    textString = colorString;
                }
            }

            setButtonLabel(colorString, textString);
        }

        public void setButtonLabel(String color, String text) {
            getTextLabelComponent().getElement().getStyle().setBackgroundColor(color);
            setTextLabel(text);
        }

        @Override
        public void setEditable(boolean editable) {
        }

        @Override
        public boolean isEditable() {
            return false;
        }

    }

    abstract class ColorPickerDialog extends OkCancelDialog {

        final TextBox valueBox;

        public ColorPickerDialog() {
            super("Color Picker");
            valueBox = new TextBox();
            setBody(valueBox);
        }

    }
}