/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Singleton;
import com.propertyvista.portal.domain.pt.Charges;
import com.propertyvista.portal.rpc.pt.ChargesSharedCalculation;

import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.shared.IObject;

@Singleton
public class ChargesViewForm extends BaseEntityForm<Charges> {

    private ChargesViewFormBase baseForm;

    @SuppressWarnings("rawtypes")
    private ValueChangeHandler valueChangeHandler = null;

    public ChargesViewForm() {
        super(Charges.class);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void createContent() {
        valueChangeHandler = new ValueChangeHandler() {

            @Override
            public void onValueChange(ValueChangeEvent event) {
                ChargesSharedCalculation.calculateCharges(getValue());
                setValue(getValue());
            }
        };

        baseForm = new ChargesViewFormBase(this, valueChangeHandler);

        FlowPanel main = new FlowPanel();
        baseForm.createContent(main, proto());
        setWidget(main);
    }

    @Override
    protected CEntityFolder<?> createMemberFolderEditor(IObject<?> member) {
        CEntityFolder<?> editor = baseForm.createMemberFolderEditor(member);
        return (editor != null ? editor : super.createMemberFolderEditor(member));
    }
}
