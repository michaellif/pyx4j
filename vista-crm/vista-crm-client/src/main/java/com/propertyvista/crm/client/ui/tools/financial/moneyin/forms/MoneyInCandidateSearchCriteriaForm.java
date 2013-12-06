/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-12-05
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.financial.moneyin.forms;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.LabelPosition;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.tools.common.selectors.CBuildingSelector;
import com.propertyvista.crm.rpc.dto.financial.autopayreview.moneyin.MoneyInCandidateSearchCriteriaDTO;

public class MoneyInCandidateSearchCriteriaForm extends CEntityForm<MoneyInCandidateSearchCriteriaDTO> {

    public MoneyInCandidateSearchCriteriaForm() {
        super(MoneyInCandidateSearchCriteriaDTO.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel panel = new FlowPanel();
//        panel.getElement().getStyle().setPadding(5, Unit.PX);
//        panel.getElement().getStyle().setOverflow(Overflow.SCROLL);

        panel.add(new FormDecoratorBuilder(inject(proto().buildingCriteria(), new CBuildingSelector())).componentWidth("300px").contentWidth("300px")
                .labelWidth("300px").labelPosition(LabelPosition.top).build());
        panel.add(new FormDecoratorBuilder(inject(proto().unit())).componentWidth("50px").contentWidth("50px").labelWidth("50px")
                .labelPosition(LabelPosition.top).build());
        panel.add(new FormDecoratorBuilder(inject(proto().lease())).componentWidth("100px").contentWidth("100px").labelWidth("100px")
                .labelPosition(LabelPosition.top).build());
        panel.add(new FormDecoratorBuilder(inject(proto().tenantCriteria())).componentWidth("200px").contentWidth("200px").labelWidth("200px")
                .labelPosition(LabelPosition.top).build());

        return panel;
    }

}
