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
package com.propertyvista.crm.client.ui.tools.l1generation.datagrid;

import java.util.List;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.commons.css.IStyleName;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

public class MultiSelectorCell extends AbstractEditableCell<MultiSelectorCellModel, MultiSelectorCellModel> {

    private static final I18n i18n = I18n.get(MultiSelectorCell.class);

    public enum Styles implements IStyleName {

        MultiSelectorCheckbox, MultiSelectorPresetSelector, MultiSlectorPresetMenu

    }

    public static class PresetSelectorPopup extends PopupPanel {

        public PresetSelectorPopup(final List<Object> presets, final ValueUpdater<MultiSelectorCellModel> valueUpdater) {
            super(true); // set auto-hide = true;
            setStyleName(Styles.MultiSlectorPresetMenu.name());

            VerticalPanel panel = new VerticalPanel();
            panel.add(new Button(i18n.tr("All"), new Command() {
                @Override
                public void execute() {
                    MultiSelectorCellModel value = new MultiSelectorCellModel(presets);
                    value.setSelectAll();
                    valueUpdater.update(value);
                }
            }));
            panel.add(new Button(i18n.tr("None"), new Command() {
                @Override
                public void execute() {
                    MultiSelectorCellModel value = new MultiSelectorCellModel(presets);
                    value.setSelectNone();
                    valueUpdater.update(value);
                }
            }));
            for (final Object p : presets) {
                panel.add(new Button(p.toString(), new Command() {
                    @Override
                    public void execute() {
                        MultiSelectorCellModel value = new MultiSelectorCellModel(presets);
                        value.setSelectSome(p);
                        valueUpdater.update(value);
                    }
                }));
            }
            setWidget(panel);
        }
    }

    public MultiSelectorCell() {
        super(BrowserEvents.CLICK, BrowserEvents.KEYDOWN);
    }

    @Override
    public boolean isEditing(com.google.gwt.cell.client.Cell.Context context, Element parent, MultiSelectorCellModel value) {
        return false;
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, MultiSelectorCellModel value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }

        if (value.getState() == MultiSelectorState.None) {
            sb.appendHtmlConstant("<input type=\"checkbox\" class=\"" + Styles.MultiSelectorCheckbox.name() + "\">");
        }
        if (value.getState() == MultiSelectorState.All) {
            sb.appendHtmlConstant("<input type=\"checkbox\" class=\"" + Styles.MultiSelectorCheckbox.name() + "\" checked>");
        }
        if (value.getState() == MultiSelectorState.Some) {
            sb.appendHtmlConstant("<span class=\"" + Styles.MultiSelectorCheckbox.name() + "\">some</span>"); // TODO draw something beautiful 
        }

        sb.appendHtmlConstant("<span class=\"" + Styles.MultiSelectorPresetSelector.name() + "\">\u25BC</span>");

        sb.appendHtmlConstant("</div>");
        return;
    }

    @Override
    public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, MultiSelectorCellModel value, NativeEvent event,
            ValueUpdater<MultiSelectorCellModel> valueUpdater) {
        super.onBrowserEvent(context, parent, value, event, valueUpdater);

        Element target = event.getEventTarget().cast();

        if (BrowserEvents.CLICK.equals(event.getType())) {

            if (target.getClassName().contains(Styles.MultiSelectorCheckbox.name())) {
                MultiSelectorCellModel updatedValue = new MultiSelectorCellModel(value.presets());
                if (value.getState() == MultiSelectorState.All || value.getState() == MultiSelectorState.Some) {
                    updatedValue.setSelectNone();
                } else {
                    updatedValue.setSelectAll();
                }
                if (valueUpdater != null) {
                    valueUpdater.update(updatedValue);
                }
            } else if (target.getClassName().contains(Styles.MultiSelectorPresetSelector.name())) {
                final PresetSelectorPopup popup = new PresetSelectorPopup(value.presets(), valueUpdater);
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
