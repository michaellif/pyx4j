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
package com.pyx4j.site.client.ui.prime.form;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CField;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.LabelPosition;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.FlexFormPanelTheme;

public class FormPanel implements IsWidget {

    private final BasicFlexFormPanel flexPanel;

    private final CForm<?> parent;

    public FormPanel(CForm<?> parent) {

        this.parent = parent;

        flexPanel = new BasicFlexFormPanel();

        flexPanel.getColumnFormatter().setStyleName(0, FlexFormPanelTheme.StyleName.FormFlexPanelLeftColumn.name());
        flexPanel.getColumnFormatter().setStyleName(1, FlexFormPanelTheme.StyleName.FormFlexPanelRightColumn.name());

    }

    @Override
    public Widget asWidget() {
        return flexPanel;
    }

    public void hr(int row, int col, int span) {
        flexPanel.setHR(row, col, span);
    }

    public void br(int row, int col, int span) {
        flexPanel.setBR(row, col, span);
    }

    public void h1(int row, int col, int span, String caption) {
        flexPanel.setH1(row, col, span, caption);
    }

    public void h1(int row, int col, int span, ImageResource image, String caption) {
        flexPanel.setH1(row, col, span, image, caption);
    }

    public void h1(int row, int col, int span, String caption, Widget actionWidget) {
        flexPanel.setH1(row, col, span, caption, actionWidget);
    }

    public void h1(int row, int col, int span, ImageResource image, String caption, Widget actionWidget) {
        flexPanel.setH1(row, col, span, image, caption, actionWidget);
    }

    public void h2(int row, int col, int span, String caption) {
        flexPanel.setH2(row, col, span, caption);
    }

    public void h2(int row, int col, int span, String caption, Widget actionWidget) {
        flexPanel.setH2(row, col, span, caption, actionWidget);
    }

    public void h3(int row, int col, int span, String caption) {
        flexPanel.setH3(row, col, span, caption);
    }

    public void h3(int row, int col, int span, String caption, Widget actionWidget) {
        flexPanel.setH3(row, col, span, caption, actionWidget);
    }

    public void h4(int row, int col, int span, String caption) {
        flexPanel.setH4(row, col, span, caption);
    }

    public void h4(int row, int col, int span, String caption, Widget actionWidget) {
        flexPanel.setH4(row, col, span, caption, actionWidget);
    }

    public void insert(int row, int col, int span, IsWidget widget) {
        flexPanel.setWidget(row, col, span, widget);
    }

    public void insert(int row, int col, int span, Widget widget) {
        flexPanel.setWidget(row, col, span, widget);
    }

    public void insert(int row, int col, Widget widget) {
        flexPanel.setWidget(row, col, widget);
    }

    public void insert(int row, int col, IsWidget widget) {
        flexPanel.setWidget(row, col, widget);
    }

    public CompOptions insert(int row, int col, int span, IObject<?> member) {
        CField<?, ?> comp = parent.inject(member);
        flexPanel.setWidget(row, col, span, comp);

        if (span == 1) {
            if (col == 0) {
                flexPanel.getFlexCellFormatter().setStyleName(row, col, FlexFormPanelTheme.StyleName.FormFlexPanelLeftCell.name());
            } else if (col == 1) {
                flexPanel.getFlexCellFormatter().setStyleName(row, col, FlexFormPanelTheme.StyleName.FormFlexPanelRightCell.name());
            }
        } else if (span == 2) {
            flexPanel.getFlexCellFormatter().setStyleName(row, col, FlexFormPanelTheme.StyleName.FormFlexPanelTwoRows.name());
        }

        return new CompOptions(comp, span == 2);
    }

    public CompOptions insert(int row, int col, IObject<?> member) {
        return insert(row, col, 1, member);
    }

    public class CompOptions {

        private final CField<?, ?> comp;

        private final boolean dual;

        public CompOptions(CField<?, ?> comp, boolean dual) {
            this.comp = comp;
            this.dual = dual;
        }

        public FieldDecoratorOptions decorate() {
            final FieldDecoratorOptions options = new FieldDecoratorOptions(dual);
            // Untill init() called, FieldDecoratorOptions can be updated.
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

    public class FieldDecoratorOptions extends FieldDecorator.Builder {

        public static final int LABEL_WIDTH = 180;

        public static final int CONTENT_WIDTH = 300;

        public static final int CONTENT_WIDTH_DUAL = 2 * CONTENT_WIDTH + LABEL_WIDTH;

        public FieldDecoratorOptions(boolean dual) {
            super();
            labelWidth(LABEL_WIDTH + "px");
            contentWidth(dual ? CONTENT_WIDTH_DUAL + "px" : CONTENT_WIDTH + "px");
            componentWidth(dual ? CONTENT_WIDTH_DUAL + "px" : CONTENT_WIDTH + "px");
        }

        public Builder componentWidth(int componentWidthPx) {
            return componentWidth(componentWidthPx + "px");
        }
    }

}
