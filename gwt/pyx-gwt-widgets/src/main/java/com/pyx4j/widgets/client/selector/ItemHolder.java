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
 * Created on Sep 11, 2014
 * @author arminea
 */
package com.pyx4j.widgets.client.selector;

import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

import com.pyx4j.commons.IFormatter;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.ImageButton;
import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.style.theme.WidgetsTheme;

public class ItemHolder<E> extends Composite {

    private static final I18n i18n = I18n.get(ItemHolder.class);

    private E item;

    private FlowPanel panel;

    private SelectorListBoxValuePanel<E> parent;

    private ImageButton removeButton;

    private boolean removable;

    private IFormatter<E, String> valueFormatter;

    private IFormatter<E, String> tooltipFormatter;

    private Label label;

    public ItemHolder(final E item, IFormatter<E, String> valueFormatter) {
        this(item, valueFormatter, valueFormatter);
    }

    public ItemHolder(final E item, IFormatter<E, String> valueFormatter, IFormatter<E, String> tooltipFormatter) {
        this(item, valueFormatter, tooltipFormatter, true);
    }

    public ItemHolder(final E item, IFormatter<E, String> valueFormatter, boolean removable) {
        this(item, valueFormatter, valueFormatter, removable);
    }

    public ItemHolder(final E item, IFormatter<E, String> valueFormatter, IFormatter<E, String> tooltipFormatter, boolean removable) {
        super();
        this.item = item;
        this.valueFormatter = valueFormatter;
        this.tooltipFormatter = tooltipFormatter;

        panel = new FlowPanel();
        panel.setStyleName(WidgetsTheme.StyleName.SelectedItemHolder.name());

        label = new Label();
        label.setStyleName(WidgetsTheme.StyleName.SelectedItemHolderLabel.name());
        panel.add(label);

        setLabel(item);

        setRemovable(removable);

        initWidget(panel);
    }

    protected void setLabel(E item) {
        label.setText(getValueFormatter().format(item));
        label.setTitle(getTooltipFormatter().format(item));
    }

    public void setSelectorListBoxValuePanel(SelectorListBoxValuePanel<E> parent) {
        this.parent = parent;
    }

    public void setRemovable(boolean removable) {

        if (this.removable != removable) {
            if (removeButton != null) {
                panel.remove(removeButton);
                removeButton = null;
            }

            if (removable) {
                removeButton = new ImageButton(ImageFactory.getImages().delButton(), new Command() {

                    @Override
                    public void execute() {
                        parent.removeItem(item);
                        onRemove();
                    }

                });

                //Prevent focus grabbing on 'Remove' Button
                removeButton.addMouseDownHandler(new MouseDownHandler() {

                    @Override
                    public void onMouseDown(MouseDownEvent event) {
                        event.preventDefault();
                        parent.setFocus(true);
                    }
                });

                removeButton.setTitle(i18n.tr("Remove"));
                removeButton.addStyleName(WidgetsTheme.StyleName.SelectedItemClose.name());
                panel.add(removeButton);
            }

            this.removable = removable;
        }
    }

    public E getItem() {
        return item;
    }

    protected void onRemove() {
    }

    protected IFormatter<E, String> getValueFormatter() {
        return valueFormatter;
    }

    protected IFormatter<E, String> getTooltipFormatter() {
        return tooltipFormatter;
    }
}
