/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-28
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.datagrid;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.tools.common.datagrid.SelectionPresetModel.MultiSelectorCellModelFactory;

public class MultiSelectorCell extends AbstractEditableCell<SelectionPresetModel, SelectionPresetModel> {

    private static final I18n i18n = I18n.get(MultiSelectorCell.class);

    public enum Styles implements IStyleName {

        MultiSelectorCheckbox, MultiSelectorPresetSelector, MultiSlectorPresetMenu

    }

    public interface Templates extends SafeHtmlTemplates {

        public static Templates INSTANCE = GWT.create(Templates.class);

        @Template("<div><input type=\"checkbox\" class=\"{0}\" checked><span class=\"{1}\">\u25BC</span></div>")
        SafeHtml renderAllCheckedSelectorCell(String checkboxStyle, String presetSelectorStyle);

        @Template("<div><input type=\"checkbox\" class=\"{0}\"></span><span class=\"{1}\">\u25BC</span></div>")
        SafeHtml renderSomeCheckedSelectorCell(String someStyle, String checkboxStyle, String presetSelectorStyle);

        @Template("<div><input type=\"checkbox\" class=\"{0}\"><span class=\"{1}\">\u25BC</span></div>")
        SafeHtml renderNonCheckedSelectorCell(String checkboxStyle, String presetSelectorStyle);

    }

    public static class PresetSelectorPopup extends PopupPanel {

        public PresetSelectorPopup(final SelectionPresetModel.MultiSelectorCellModelFactory factory, final ValueUpdater<SelectionPresetModel> valueUpdater) {
            super(true);
            setStyleName(Styles.MultiSlectorPresetMenu.name());
            VerticalPanel panel = new VerticalPanel();
            panel.add(new Button(i18n.tr("All"), makePresetCommand(factory.makeAll(), valueUpdater)));
            panel.add(new Button(i18n.tr("None"), makePresetCommand(factory.makeNone(), valueUpdater)));
            for (final Object p : factory.makeNone().presets()) {
                panel.add(new Button(p.toString(), makePresetCommand(factory.makePreset(p), valueUpdater)));
            }
            setWidget(panel);
        }

        private Command makePresetCommand(final SelectionPresetModel m, final ValueUpdater<SelectionPresetModel> valueUpdater) {
            return new Command() {
                @Override
                public void execute() {
                    valueUpdater.update(m);
                    PresetSelectorPopup.this.hide();
                }
            };
        }
    }

    private final MultiSelectorCellModelFactory factory;

    public MultiSelectorCell(MultiSelectorCellModelFactory factory) {
        super(BrowserEvents.CLICK, BrowserEvents.KEYDOWN);
        this.factory = factory;
    }

    @Override
    public boolean isEditing(com.google.gwt.cell.client.Cell.Context context, Element parent, SelectionPresetModel value) {
        return false;
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, SelectionPresetModel value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }

        switch (value.getState()) {
        case None:
            sb.append(Templates.INSTANCE.renderNonCheckedSelectorCell(Styles.MultiSelectorCheckbox.name(), Styles.MultiSelectorPresetSelector.name()));
            break;
        case Some:
        case Preset:
            sb.append(Templates.INSTANCE.renderNonCheckedSelectorCell(Styles.MultiSelectorCheckbox.name(), Styles.MultiSelectorPresetSelector.name()));
            break;
        case All:
            sb.append(Templates.INSTANCE.renderAllCheckedSelectorCell(Styles.MultiSelectorCheckbox.name(), Styles.MultiSelectorPresetSelector.name()));
        default:
            break;
        }
        return;
    }

    @Override
    public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, SelectionPresetModel value, NativeEvent event,
            ValueUpdater<SelectionPresetModel> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);

        Element target = event.getEventTarget().cast();

        if (BrowserEvents.CLICK.equals(event.getType())) {
            if (target.getClassName().contains(Styles.MultiSelectorCheckbox.name())) {
                SelectionPresetModel updatedValue = null;
                if (value.getState() == MultiSelectorState.All) {
                    updatedValue = factory.makeNone();
                } else if (value.getState() == MultiSelectorState.None || value.getState() == MultiSelectorState.Some
                        || value.getState() == MultiSelectorState.Preset) {
                    updatedValue = factory.makeAll();
                }
                if (valueUpdater != null) {
                    valueUpdater.update(updatedValue);
                }
            } else if (target.getClassName().contains(Styles.MultiSelectorPresetSelector.name())) {
                final PresetSelectorPopup popup = new PresetSelectorPopup(factory, valueUpdater);
                final int left = parent.getAbsoluteLeft();
                final int top = parent.getAbsoluteBottom() + 1;
                popup.setPopupPositionAndShow(new PositionCallback() {
                    @Override
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        popup.setPopupPosition(left, top);
                    }
                });
            }
        }
        event.preventDefault();
    }

}
