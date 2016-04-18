/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Apr 18, 2016
 * @author vlads
 */
package com.pyx4j.gwt.commons.ui;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Clear;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.ListStyleType;
import com.google.gwt.dom.client.Style.OutlineStyle;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TableLayout;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.dom.client.Style.TextJustify;
import com.google.gwt.dom.client.Style.TextOverflow;
import com.google.gwt.dom.client.Style.TextTransform;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.dom.client.Style.WhiteSpace;

/**
 * Hosted mode safe style.<br/>
 *
 * Avoid the usage of 'com.google.gwt.dom.' in application code.<br/>
 *
 * Solution for classic problems 'hot code replace breaks'<br/>
 *
 * https://code.google.com/archive/p/google-web-toolkit/issues/5252<br/>
 * https://github.com/gwtproject/gwt/issues/5261<br/>
 *
 * usage of Widget.getElement().getStyle() throws exception java.lang.IncompatibleClassChangeError in hosted mode
 *
 */
public class Style {

    private final com.google.gwt.dom.client.Style style;

    Style(com.google.gwt.dom.client.Style style) {
        this.style = style;
    }

    public String toSource() {
        return style.toSource();
    }

    @Override
    public final String toString() {
        return style.toString();
    }

    public final void clearBackgroundColor() {
        style.clearBackgroundColor();
    }

    public final void clearBackgroundImage() {
        style.clearBackgroundImage();
    }

    public final void clearBorderColor() {
        style.clearBorderColor();
    }

    public final void clearBorderStyle() {
        style.clearBorderStyle();
    }

    public final void clearBorderWidth() {
        style.clearBorderWidth();
    }

    public final void clearBottom() {
        style.clearBottom();
    }

    public final void clearClear() {
        style.clearClear();
    }

    public final void clearColor() {
        style.clearColor();
    }

    public final void clearCursor() {
        style.clearCursor();
    }

    public final void clearDisplay() {
        style.clearDisplay();
    }

    public final void clearFloat() {
        style.clearFloat();
    }

    public final void clearFontSize() {
        style.clearFontSize();
    }

    public final void clearFontStyle() {
        style.clearFontStyle();
    }

    public final void clearFontWeight() {
        style.clearFontWeight();
    }

    public final void clearHeight() {
        style.clearHeight();
    }

    public final void clearLeft() {
        style.clearLeft();
    }

    public final void clearLineHeight() {
        style.clearLineHeight();
    }

    public final void clearListStyleType() {
        style.clearListStyleType();
    }

    public final void clearMargin() {
        style.clearMargin();
    }

    public final void clearMarginBottom() {
        style.clearMarginBottom();
    }

    public final void clearMarginLeft() {
        style.clearMarginLeft();
    }

    public final void clearMarginRight() {
        style.clearMarginRight();
    }

    public final void clearMarginTop() {
        style.clearMarginTop();
    }

    public final void clearOpacity() {
        style.clearOpacity();
    }

    public final void clearOutlineColor() {
        style.clearOutlineColor();
    }

    public final void clearOutlineStyle() {
        style.clearOutlineStyle();
    }

    public final void clearOutlineWidth() {
        style.clearOutlineWidth();
    }

    public final void clearOverflow() {
        style.clearOverflow();
    }

    public final void clearOverflowX() {
        style.clearOverflowX();
    }

    public final void clearOverflowY() {
        style.clearOverflowY();
    }

    public final void clearPadding() {
        style.clearPadding();
    }

    public final void clearPaddingBottom() {
        style.clearPaddingBottom();
    }

    public final void clearPaddingLeft() {
        style.clearPaddingLeft();
    }

    public final void clearPaddingRight() {
        style.clearPaddingRight();
    }

    public final void clearPaddingTop() {
        style.clearPaddingTop();
    }

    public final void clearPosition() {
        style.clearPosition();
    }

    public final void clearProperty(String name) {
        style.clearProperty(name);
    }

    public final void clearRight() {
        style.clearRight();
    }

    public final void clearTableLayout() {
        style.clearTableLayout();
    }

    public final void clearTextAlign() {
        style.clearTextAlign();
    }

    public final void clearTextDecoration() {
        style.clearTextDecoration();
    }

    public final void clearTextIndent() {
        style.clearTextIndent();
    }

    public final void clearTextJustify() {
        style.clearTextJustify();
    }

    public final void clearTextOverflow() {
        style.clearTextOverflow();
    }

    public final void clearTextTransform() {
        style.clearTextTransform();
    }

    public final void clearTop() {
        style.clearTop();
    }

    public final void clearVisibility() {
        style.clearVisibility();
    }

    public final void clearWhiteSpace() {
        style.clearWhiteSpace();
    }

    public final void clearWidth() {
        style.clearWidth();
    }

    public final void clearZIndex() {
        style.clearZIndex();
    }

    public final String getBackgroundColor() {
        return style.getBackgroundColor();
    }

    public final String getBackgroundImage() {
        return style.getBackgroundImage();
    }

    public final String getBorderColor() {
        return style.getBorderColor();
    }

    public final String getBorderStyle() {
        return style.getBorderStyle();
    }

    public final String getBorderWidth() {
        return style.getBorderWidth();
    }

    public final String getBottom() {
        return style.getBottom();
    }

    public final String getClear() {
        return style.getClear();
    }

    public final String getColor() {
        return style.getColor();
    }

    public final String getCursor() {
        return style.getCursor();
    }

    public final String getDisplay() {
        return style.getDisplay();
    }

    public final String getFontSize() {
        return style.getFontSize();
    }

    public final String getFontStyle() {
        return style.getFontStyle();
    }

    public final String getFontWeight() {
        return style.getFontWeight();
    }

    public final String getHeight() {
        return style.getHeight();
    }

    public final String getLeft() {
        return style.getLeft();
    }

    public final String getLineHeight() {
        return style.getLineHeight();
    }

    public final String getListStyleType() {
        return style.getListStyleType();
    }

    public final String getMargin() {
        return style.getMargin();
    }

    public final String getMarginBottom() {
        return style.getMarginBottom();
    }

    public final String getMarginLeft() {
        return style.getMarginLeft();
    }

    public final String getMarginRight() {
        return style.getMarginRight();
    }

    public final String getMarginTop() {
        return style.getMarginTop();
    }

    public final String getOpacity() {
        return style.getOpacity();
    }

    public final String getOverflow() {
        return style.getOverflow();
    }

    public final String getOverflowX() {
        return style.getOverflowX();
    }

    public final String getOverflowY() {
        return style.getOverflowY();
    }

    public final String getPadding() {
        return style.getPadding();
    }

    public final String getPaddingBottom() {
        return style.getPaddingBottom();
    }

    public final String getPaddingLeft() {
        return style.getPaddingLeft();
    }

    public final String getPaddingRight() {
        return style.getPaddingRight();
    }

    public final String getPaddingTop() {
        return style.getPaddingTop();
    }

    public final String getPosition() {
        return style.getPosition();
    }

    public final String getProperty(String name) {
        return style.getProperty(name);
    }

    public final String getRight() {
        return style.getRight();
    }

    public final String getTableLayout() {
        return style.getTableLayout();
    }

    public final String getTextAlign() {
        return style.getTextAlign();
    }

    public final String getTextDecoration() {
        return style.getTextDecoration();
    }

    public final String getTextIndent() {
        return style.getTextIndent();
    }

    public final String getTextJustify() {
        return style.getTextJustify();
    }

    public final String getTextOverflow() {
        return style.getTextOverflow();
    }

    public final String getTextTransform() {
        return style.getTextTransform();
    }

    public final String getTop() {
        return style.getTop();
    }

    public final String getVerticalAlign() {
        return style.getVerticalAlign();
    }

    public final String getVisibility() {
        return style.getVisibility();
    }

    public final String getWhiteSpace() {
        return style.getWhiteSpace();
    }

    public final String getWidth() {
        return style.getWidth();
    }

    public final String getZIndex() {
        return style.getZIndex();
    }

    public final void setBackgroundColor(String value) {
        style.setBackgroundColor(value);
    }

    public final void setBackgroundImage(String value) {
        style.setBackgroundImage(value);
    }

    public final void setBorderColor(String value) {
        style.setBorderColor(value);
    }

    public final void setBorderStyle(BorderStyle value) {
        style.setBorderStyle(value);
    }

    public final void setBorderWidth(double value, Unit unit) {
        style.setBorderWidth(value, unit);
    }

    public final void setBottom(double value, Unit unit) {
        style.setBottom(value, unit);
    }

    public final void setClear(Clear value) {
        style.setClear(value);
    }

    public final void setColor(String value) {
        style.setColor(value);
    }

    public final void setCursor(Cursor value) {
        style.setCursor(value);
    }

    public final void setDisplay(Display value) {
        style.setDisplay(value);
    }

    public final void setFloat(Float value) {
        style.setFloat(value);
    }

    public final void setFontSize(double value, Unit unit) {
        style.setFontSize(value, unit);
    }

    public final void setFontStyle(FontStyle value) {
        style.setFontStyle(value);
    }

    public final void setFontWeight(FontWeight value) {
        style.setFontWeight(value);
    }

    public final void setHeight(double value, Unit unit) {
        style.setHeight(value, unit);
    }

    public final void setLeft(double value, Unit unit) {
        style.setLeft(value, unit);
    }

    public final void setLineHeight(double value, Unit unit) {
        style.setLineHeight(value, unit);
    }

    public final void setListStyleType(ListStyleType value) {
        style.setListStyleType(value);
    }

    public final void setMargin(double value, Unit unit) {
        style.setMargin(value, unit);
    }

    public final void setMarginBottom(double value, Unit unit) {
        style.setMarginBottom(value, unit);
    }

    public final void setMarginLeft(double value, Unit unit) {
        style.setMarginLeft(value, unit);
    }

    public final void setMarginRight(double value, Unit unit) {
        style.setMarginRight(value, unit);
    }

    public final void setMarginTop(double value, Unit unit) {
        style.setMarginTop(value, unit);
    }

    public final void setOpacity(double value) {
        style.setOpacity(value);
    }

    public final void setOutlineColor(String value) {
        style.setOutlineColor(value);
    }

    public final void setOutlineStyle(OutlineStyle value) {
        style.setOutlineStyle(value);
    }

    public final void setOutlineWidth(double value, Unit unit) {
        style.setOutlineWidth(value, unit);
    }

    public final void setOverflow(Overflow value) {
        style.setOverflow(value);
    }

    public final void setOverflowX(Overflow value) {
        style.setOverflowX(value);
    }

    public final void setOverflowY(Overflow value) {
        style.setOverflowY(value);
    }

    public final void setPadding(double value, Unit unit) {
        style.setPadding(value, unit);
    }

    public final void setPaddingBottom(double value, Unit unit) {
        style.setPaddingBottom(value, unit);
    }

    public final void setPaddingLeft(double value, Unit unit) {
        style.setPaddingLeft(value, unit);
    }

    public final void setPaddingRight(double value, Unit unit) {
        style.setPaddingRight(value, unit);
    }

    public final void setPaddingTop(double value, Unit unit) {
        style.setPaddingTop(value, unit);
    }

    public final void setPosition(Position value) {
        style.setPosition(value);
    }

    public final void setProperty(String name, String value) {
        style.setProperty(name, value);
    }

    public final void setProperty(String name, double value, Unit unit) {
        style.setProperty(name, value, unit);
    }

    public final void setPropertyPx(String name, int value) {
        style.setPropertyPx(name, value);
    }

    public final void setRight(double value, Unit unit) {
        style.setRight(value, unit);
    }

    public final void setTableLayout(TableLayout value) {
        style.setTableLayout(value);
    }

    public final void setTextAlign(TextAlign value) {
        style.setTextAlign(value);
    }

    public final void setTextDecoration(TextDecoration value) {
        style.setTextDecoration(value);
    }

    public final void setTextIndent(double value, Unit unit) {
        style.setTextIndent(value, unit);
    }

    public final void setTextJustify(TextJustify value) {
        style.setTextJustify(value);
    }

    public final void setTextOverflow(TextOverflow value) {
        style.setTextOverflow(value);
    }

    public final void setTextTransform(TextTransform value) {
        style.setTextTransform(value);
    }

    public final void setTop(double value, Unit unit) {
        style.setTop(value, unit);
    }

    public final void setVerticalAlign(VerticalAlign value) {
        style.setVerticalAlign(value);
    }

    public final void setVerticalAlign(double value, Unit unit) {
        style.setVerticalAlign(value, unit);
    }

    public final void setVisibility(Visibility value) {
        style.setVisibility(value);
    }

    public final void setWhiteSpace(WhiteSpace value) {
        style.setWhiteSpace(value);
    }

    public final void setWidth(double value, Unit unit) {
        style.setWidth(value, unit);
    }

    public final void setZIndex(int value) {
        style.setZIndex(value);
    }

}
