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
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.IFormat;
import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.widgets.client.Label;

public class ObjectEditCell<E> extends AbstractEditableCell<E, ValidationErrors> {

    public interface Template extends SafeHtmlTemplates {

        @Template("<input type=\"text\" value=\"{1}\" tabindex=\"-1\" class=\"{0}\"></input>")
        SafeHtml inputBox(String appliedStyles, String formattedValue);

    }

    public enum StyleNames implements IStyleName {

        ObjectEditCell, ObjectEditCellPendingValidation, ObjectEditCellFailedValidation, ObjectEditCellValidationPopup;

    }

    public interface Style {

        String objectEditCell();

        String objectEditCellPendingValidation();

        String objectEditCellFailedValidation();

        String objectEditCellValidationPopup();

    }

    public static class DefaultStyle implements Style {//@formatter:off
        @Override public String objectEditCellValidationPopup() { return  StyleNames.ObjectEditCellValidationPopup.name(); }
        @Override public String objectEditCellPendingValidation() { return StyleNames.ObjectEditCellPendingValidation.name();} 
        @Override public String objectEditCellFailedValidation() { return StyleNames.ObjectEditCellFailedValidation.name();}            
        @Override public String objectEditCell() { return StyleNames.ObjectEditCell.name();};
    };//@formatter:on

    private class ValidationErrorsPopup extends PopupPanel {

        public ValidationErrorsPopup(ValidationErrors validationErrors) {
            VerticalPanel errorsPanel = new VerticalPanel();
            for (String errorMessage : validationErrors.getValidationErrorMessages()) {
                errorsPanel.add(new Label(errorMessage));
            }
            setWidget(errorsPanel);
            addStyleName(ObjectEditCell.this.style.objectEditCellValidationPopup());
        }

    }

    protected final IFormat<E> format;

    private final Template template;

    private final Style style;

    private ValidationErrorsPopup displayedPopup;

    /**
     * Let's edit any data in form of string. The <code>IFormat</code> passed in constructor will be responsible for parsing and formatting the values.
     * If parsing fails {@link #onParsingFailed} will be launched, else value updater will be launched, and value in input field will be replaced with a
     * formatted value.
     * 
     * @param format
     *            Required. This will be used to parse input and format the value. And of course '<code>format.parse(format.format(value)) == value</code>' must
     *            always hold and never throw a <code>ParseException</code>.
     * @param style
     *            Optional. sets styles for the input cell
     */
    public ObjectEditCell(IFormat<E> format, Style style) {
        super(BrowserEvents.BLUR, BrowserEvents.MOUSEOVER, BrowserEvents.MOUSEOUT);
        this.template = GWT.create(Template.class);
        this.style = (style != null) ? style : new DefaultStyle();
        this.format = format;
    }

    @Override
    public boolean isEditing(Context context, Element parent, E value) {
        return true;
    }

    @Override
    public void render(Context context, E value, SafeHtmlBuilder sb) {
        String formattedValue = format.format(value);

        ValidationErrors validationErrors = getViewData(context.getKey());
        String appliedStyleNames = this.style.objectEditCell();
        if (validationErrors != null) {
            appliedStyleNames += " "
                    + ((validationErrors.isPending()) ? this.style.objectEditCellPendingValidation() : this.style.objectEditCellFailedValidation());
        }
        sb.append(template.inputBox(appliedStyleNames, formattedValue));
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, E value, NativeEvent event, ValueUpdater<E> valueUpdater) {
        EventTarget eventTarget = event.getEventTarget();
        if (Element.is(eventTarget)) {
            if (event.getType().equals(BrowserEvents.BLUR)) {
                Element target = Element.as(eventTarget);
                if (InputElement.TAG.equals(target.getTagName().toLowerCase())) {
                    InputElement input = (InputElement) parent.getFirstChild();
                    String unparsedValue = input.getValue();
                    E parsedValue = null;
                    try {
                        parsedValue = format.parse(unparsedValue);
                    } catch (ParseException parseException) {
                        // TODO show a popup with error 
                    }
                    if (parsedValue != null) {
                        String newFormatted = format.format(parsedValue);
                        input.setValue(newFormatted);
                        if (valueUpdater != null) {
                            valueUpdater.update(parsedValue);
                        }
                    } else {
                        onParsingFailed(context, input, value, valueUpdater);
                    }
                }
            } else if (event.getType().equals(BrowserEvents.MOUSEOVER)) {
                if (displayedPopup != null) {
                    displayedPopup.hide();
                    displayedPopup = null;
                }
                ValidationErrors validationErrors = getViewData(context.getKey());
                if (validationErrors != null) {
                    // TODO set popup position properly (top bottom, left or right, not just bottom);
                    displayedPopup = new ValidationErrorsPopup(validationErrors);
                    final int left = parent.getAbsoluteLeft();
                    final int top = parent.getAbsoluteBottom() + 1;
                    displayedPopup.setPopupPositionAndShow(new PositionCallback() {
                        @Override
                        public void setPosition(int offsetWidth, int offsetHeight) {
                            displayedPopup.setPopupPosition(left, top);
                        }
                    });
                }
            } else if (event.getType().equals(BrowserEvents.MOUSEOUT) && displayedPopup != null) {
                displayedPopup.hide();
                displayedPopup = null;
            }

        }

    }

    /**
     * Called when parsing of input field fails. The default implementation fills the input field with a formatted value of the cell.
     * Params same as in onBrowserEvent
     * 
     * @param context
     * @param input
     * @param value
     * @param valueUpdater
     */
    protected void onParsingFailed(Context context, InputElement input, E value, ValueUpdater<E> valueUpdater) {
        input.setValue(format.format(value));
    }
}
