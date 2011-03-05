/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-26
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.DecorationUtils;
import com.propertyvista.portal.domain.pt.TenantCharge;

import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.forms.client.ui.CNumberField;
import com.pyx4j.forms.client.ui.CNumberLabel;

public class ChargeSplitListFolder extends CEntityFolder<TenantCharge> {

    private static I18n i18n = I18nFactory.getI18n(ChargeSplitListFolder.class);

    @SuppressWarnings("rawtypes")
    private final ValueChangeHandler valueChangeHandler;

    @SuppressWarnings("rawtypes")
    ChargeSplitListFolder(ValueChangeHandler valueChangeHandler) {
        this.valueChangeHandler = valueChangeHandler;
    }

    @Override
    protected FolderDecorator<TenantCharge> createFolderDecorator() {
        return new BoxReadOnlyFolderDecorator<TenantCharge>() {

            @Override
            public void setFolder(CEntityFolder<?> w) {
                super.setFolder(w);
                this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
            }
        };
    }

    @Override
    protected CEntityFolderItem<TenantCharge> createItem() {

        return new CEntityFolderItem<TenantCharge>(TenantCharge.class) {

            @Override
            public FolderItemDecorator createFolderItemDecorator() {
                return new BoxReadOnlyFolderItemDecorator(!isFirst());
            }

            @SuppressWarnings({ "unchecked", "rawtypes" })
            @Override
            public IsWidget createContent() {

                FlowPanel main = new FlowPanel();
                main.add(DecorationUtils.inline(inject(proto().tenant()), "250px", null));
                main.add(DecorationUtils.inline(new HTML("%"), "10px", "right"));

                if (isFirst() || valueChangeHandler == null) {
                    CNumberLabel fixedPrc = new CNumberLabel();
                    bind(fixedPrc, proto().percentage());
                    main.add(DecorationUtils.inline(fixedPrc, "40px", "right"));
                } else {
                    main.add(DecorationUtils.inline(inject(proto().percentage()), "40px", "right"));
                }

                main.add(DecorationUtils.inline(inject(proto().charge()), "100px", "right"));
                if (valueChangeHandler != null) {
                    get(proto().percentage()).addValueChangeHandler(valueChangeHandler);
                    ((CNumberField) get(proto().percentage())).setRange(0, 100);
                }

                return main;
            }
        };
    }
}
