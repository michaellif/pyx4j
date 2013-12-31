/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-02
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.datagrid;

import java.text.ParseException;

import com.google.gwt.cell.client.AbstractInputCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.pyx4j.commons.IFormat;
import com.pyx4j.commons.css.IStyleName;

public class ObjectSelectionCell<E> extends AbstractInputCell<ObjectSelectionState<E>, ObjectSelectionState<E>> {

    public enum StyleNames implements IStyleName {
        ObjectSelectionCell;
    }

    public interface Template extends SafeHtmlTemplates {

        @Template("<option value=\"{0}\">{0}</option>")
        SafeHtml deselected(String option);

        @Template("<option value=\"{0}\" selected=\"selected\">{0}</option>")
        SafeHtml selected(String option);

    }

    private static Template template = null;

    private IFormat<E> format;

    public ObjectSelectionCell(IFormat<E> format) {
        super(BrowserEvents.CHANGE);
        if (template == null) {
            template = GWT.create(Template.class);
        }
        this.format = format != null ? format : new IFormat<E>() {
            @Override
            public String format(E value) {
                return value.toString();
            }

            @Override
            public E parse(String string) throws ParseException {
                return null; // not used in this
            }
        };
    }

    @Override
    public void render(Context context, ObjectSelectionState<E> value, SafeHtmlBuilder sb) {
        Object key = context.getKey();
        ObjectSelectionState<E> viewData = getViewData(key);
        if (viewData != null && viewData.equals(value)) {
            clearViewData(key);
            viewData = null;
        }

        sb.appendHtmlConstant("<select tabindex=\"-1\" class=\"" + StyleNames.ObjectSelectionCell.name() + "\">");
        E selectedOption = value.getSelectedOption();

        for (E option : value.getOptions()) {
            String formattedOption = format.format(option);
            if (selectedOption != null && selectedOption.equals(option)) {
                sb.append(template.selected(formattedOption));
            } else {
                sb.append(template.deselected(formattedOption));
            }
        }
        sb.appendHtmlConstant("</select>");

    }

    @Override
    public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, ObjectSelectionState<E> value, NativeEvent event,
            ValueUpdater<ObjectSelectionState<E>> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);
        String type = event.getType();
        if (BrowserEvents.CHANGE.equals(type)) {
            Object key = context.getKey();
            SelectElement select = parent.getFirstChild().cast();

            int selectedIndex = select.getSelectedIndex();
            E newSelection = selectedIndex != -1 ? value.getOptions().get(selectedIndex) : null;

            ObjectSelectionState<E> newSelectionState = value.updatedSelection(newSelection);
            setViewData(key, newSelectionState);
            finishEditing(parent, newSelectionState, key, valueUpdater);
            if (valueUpdater != null) {
                valueUpdater.update(newSelectionState);
            }
        }
    }

}
