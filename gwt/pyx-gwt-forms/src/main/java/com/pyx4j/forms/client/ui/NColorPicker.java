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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.css.ColorUtil;
import com.pyx4j.forms.client.ui.NColorPicker.ColorButton;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.IFocusWidget;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;
import com.pyx4j.svg.basic.Group;
import com.pyx4j.svg.basic.SvgFactory;
import com.pyx4j.svg.gwt.basic.SvgFactoryForGwt;
import com.pyx4j.svg.basic.SvgRoot;
import com.pyx4j.svg.gwt.ColorPicker;

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
                String header = getCComponent().isHueOnly()?"Hue Picker":"Color Picker";
                   new ColorPickerDialog(header) {
                        @Override
                        public boolean onClickOk() {
                            setValue(colorPicker.getColor());
                            return true;
                        }
                    }.show();
                }
            });
        }

        public Integer getValue() {
            return color;
        }

        public void setValue(Integer color) {

         	String textString;
         	String colorString;
        	this.color = color;
        	float[] hsb;
            if (color == null) {
                colorString = "#fff";
                textString = "";
            } else {
                if (getCComponent().isHueOnly()) {
                    colorString = ColorUtil.rgbToHex(color);
               	    hsb = ColorUtil.rgbToHsb(color);
               	    textString = Integer.toString((int)Math.round((hsb[0]*360)));
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

    	ColorPicker colorPicker;
    	
        public ColorPickerDialog(String header) {
         	super(header);
             
           SimplePanel content = new SimplePanel();
           SvgFactory svgFactory = new SvgFactoryForGwt();
           
           SvgRoot svgroot = svgFactory.getSvgRoot();
           ((Widget) svgroot).setSize("230px", "240px");
           
           int hue, pickerColor;
           float[] hsb;
           Integer color = getEditor().color;          
           if (color == null) {
        	   hue = 0;
        	   pickerColor = 0;
           } else {
         	   hsb = ColorUtil.rgbToHsb(color);
        	   hue = (int) Math.round((hsb[0]*360));
        	   pickerColor = color.intValue();
           }

           if(header.equals("Hue Picker")) {
               colorPicker = new ColorPicker(svgFactory, (Widget) svgroot, ColorPicker.PickerType.Hue, 90, hue);        	   
           } else {
               colorPicker = new ColorPicker(svgFactory, (Widget) svgroot, ColorPicker.PickerType.Color, 90, pickerColor);        	   
           }
        	   
            Group g = svgFactory.createGroup();
            g.add(colorPicker);
            svgroot.add(g);
            content.setWidget((Widget) svgroot);      
            
            setBody(content);
        }

    }
}