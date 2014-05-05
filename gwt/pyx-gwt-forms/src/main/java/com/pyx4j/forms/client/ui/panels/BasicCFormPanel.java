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
 * Created on Apr 24, 2014
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.ui.panels;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.LabelPosition;
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;

public class BasicCFormPanel extends TwoColumnFormPanel {

    private final CForm<?> parent;

    public BasicCFormPanel(CForm<?> parent) {
        this(parent, false);
    }

    public BasicCFormPanel(CForm<?> parent, boolean collapsed) {
        super(collapsed);
        this.parent = parent;
    }

    public CompOptions append(Location location, IObject<?> member) {
        CField<?, ?> comp = parent.inject(member);
        super.append(location, comp);
        return new CompOptions(comp, location);
    }

    public CompOptions append(Location location, IObject<?> member, CField<?, ?> comp) {
        comp = parent.inject(member, comp);
        super.append(location, comp);
        return new CompOptions(comp, location);
    }

    public void append(Location location, IObject<?> member, CComponent<?, ?, ?> comp) {
        comp = parent.inject(member, comp);
        super.append(location, comp);
    }

    public CompOptions append(Location location, CField<?, ?> comp) {
        super.append(location, comp);
        return new CompOptions(comp, location);
    }

    public class CompOptions {

        private final CField<?, ?> comp;

        private final Location location;

        public CompOptions(CField<?, ?> comp, Location location) {
            this.comp = comp;
            this.location = location;
        }

        public FieldDecoratorOptions decorate() {
            Widget handlerPanel = comp.asWidget().getParent();
            handlerPanel.setStyleName(TwoColumnFormPanelTheme.StyleName.FormPanelCell.name());
            switch (location) {
            case Left:
                handlerPanel.addStyleDependentName(TwoColumnFormPanelTheme.StyleDependent.left.name());
                break;
            case Right:
                handlerPanel.addStyleDependentName(TwoColumnFormPanelTheme.StyleDependent.right.name());
                break;
            case Dual:
                handlerPanel.addStyleDependentName(TwoColumnFormPanelTheme.StyleDependent.dual.name());
                break;
            }
            final FieldDecoratorOptions options = new FieldDecoratorOptions(location == Location.Dual);
            // Until init() method called, FieldDecoratorOptions can be updated.
            comp.setDecorator(new FieldDecorator(options) {
                @Override
                protected void updateViewable() {
                    if (getLabelPosition() != LabelPosition.top) {
                        if (getComponent().isViewable()) {
                            options.labelAlignment(Alignment.left);
                            options.useLabelSemicolon(false);
                        } else {
                            options.labelAlignment(Alignment.right);
                            options.useLabelSemicolon(true);
                        }
                    }
                    updateCaption();
                    updateLabelAlignment();
                    super.updateViewable();
                }
            });
            return options;
        }
    }

    public class FieldDecoratorOptions extends FieldDecorator.Builder<FieldDecoratorOptions> {

        public FieldDecoratorOptions(boolean dual) {
            super();
        }

        public FieldDecoratorOptions componentWidth(int componentWidthPx) {
            return componentWidth(componentWidthPx + "px");
        }

        public FieldDecoratorOptions labelWidth(int labelWidthPx) {
            return labelWidth(labelWidthPx + "px");
        }

    }
}
