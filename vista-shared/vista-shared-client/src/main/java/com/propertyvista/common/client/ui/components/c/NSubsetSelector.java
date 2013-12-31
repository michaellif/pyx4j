/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 30, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.c;

import java.text.ParseException;
import java.util.Set;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.IFormat;
import com.pyx4j.forms.client.ui.NFocusField;

public class NSubsetSelector<OPTION_TYPE> extends NFocusField<Set<OPTION_TYPE>, SubsetSelector<OPTION_TYPE>, CSubsetSelector<OPTION_TYPE>, HTML> {

    private final IFormat<OPTION_TYPE> format;

    public NSubsetSelector(CSubsetSelector<OPTION_TYPE> cComponent, IFormat<OPTION_TYPE> format) {
        super(cComponent);
        this.format = format;
    }

    @Override
    public void setNativeValue(Set<OPTION_TYPE> value) {
        if (isViewable()) {
            getViewer().setHTML(buildView(value));
        } else {
            getEditor().setValue(value);
        }
    }

    @Override
    public Set<OPTION_TYPE> getNativeValue() throws ParseException {
        if (isViewable()) {
            assert false : "getNativeValue() shouldn't be called in viewable mode";
            return null;
        } else {
            return getEditor().getValue();
        }
    }

    @Override
    protected SubsetSelector<OPTION_TYPE> createEditor() {
        return new SubsetSelector<OPTION_TYPE>(getCComponent().getLayout(), getCComponent().getOptions());
    }

    @Override
    protected void onEditorCreate() {
        super.onEditorCreate();
        getEditor().addValueChangeHandler(new ValueChangeHandler<Set<OPTION_TYPE>>() {
            @Override
            public void onValueChange(ValueChangeEvent<Set<OPTION_TYPE>> event) {
                getCComponent().onEditingStop();
            }
        });
        setTabIndex(getCComponent().getTabIndex());
    }

    @Override
    protected HTML createViewer() {
        return new HTML();
    }

    private String buildView(Set<OPTION_TYPE> value) {
        if (value == null) {

            return "<div></div>";

        } else {
            StringBuilder builder = new StringBuilder();

            switch (getCComponent().getLayout()) {
            case Vertical:
                for (OPTION_TYPE item : value) {
                    // TODO format from CComponent
                    builder.append("<div>").append(new SafeHtmlBuilder().appendEscaped(format.format(item)).toSafeHtml().asString()).append("</div>");
                }
                break;
            default:
                // TODO implement view for horizontal layout  
                throw new Error("Layout '" + getCComponent().getLayout().name() + "' hasn't been implemented yet, sorry :(");
            }

            return builder.toString();
        }
    }
}
