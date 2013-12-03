/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-03
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.datagrid;

import java.text.ParseException;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.forms.client.ui.IFormat;

public class ObjectEditCell<E> extends AbstractEditableCell<E, String> {

    public interface Template extends SafeHtmlTemplates {

        @Template("<input type=\"text\" value=\"{1}\" tabindex=\"-1\" class=\"{0}\"></input>")
        SafeHtml inputBox(String style, String formattedValue);
    }

    public enum Styles implements IStyleName {

        ObjectEditCell;

    }

    private final IFormat<E> format;

    private final Template template;

    private final String style;

    private final String onParseErrorMessage;

    /**
     * @param format
     *            Required. This will be used to parse input and format the value. And of course '<code>format.parse(format.format(value)) == value</code>' must
     *            always hold and never throw a <code>ParseException</code>.
     * @param style
     *            Optional. Will set class of the input cell, if <code>null</code> the class will be {@link Styles#ObjectEditCell}.
     * @param onParseErrorMessage
     *            if parsing fails will show this text as a suggestion what whet wrong and how to fix it. UNFORTUNATELY NOT YET IMPLEMENTED
     */
    public ObjectEditCell(IFormat<E> format, String style, String onParseErrorMessage) {
        super(BrowserEvents.BLUR);
        this.template = GWT.create(Template.class);
        this.style = (style != null) ? style : Styles.ObjectEditCell.name();
        this.format = format;
        this.onParseErrorMessage = onParseErrorMessage;
    }

    @Override
    public boolean isEditing(Context context, Element parent, E value) {
        return true;
    }

    @Override
    public void render(Context context, E value, SafeHtmlBuilder sb) {
        String formattedValue = format.format(value);
        sb.append(template.inputBox(this.style, formattedValue));
    }

    @Override
    public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, E value, NativeEvent event, ValueUpdater<E> valueUpdater) {
        EventTarget eventTarget = event.getEventTarget();
        if (Element.is(eventTarget)) {
            Element target = Element.as(eventTarget);
            if ("input".equals(target.getTagName().toLowerCase())) {
                InputElement input = (InputElement) parent.getFirstChild();
                String unparsedValue = input.getValue();
                E parsedValue = null;
                try {
                    parsedValue = format.parse(unparsedValue);
                } catch (ParseException parseException) {
                    // TODO show a popup with error 
                }
                String newFormatted = format.format(parsedValue != null ? parsedValue : value);
                input.setValue(newFormatted);
                if (parsedValue != null) {
                    valueUpdater.update(parsedValue);
                }
            }
        }
    }
}
