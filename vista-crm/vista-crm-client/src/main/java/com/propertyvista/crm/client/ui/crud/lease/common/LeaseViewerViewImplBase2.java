/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-06-29
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.components.boxes.LeaseTermSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.lease.LeaseViewerView2;
import com.propertyvista.crm.client.ui.crud.lease.LeaseViewerViewImpl2;
import com.propertyvista.crm.client.ui.crud.lease.common.deposit.DepositLifecycleLister;
import com.propertyvista.dto.DepositLifecycleDTO;
import com.propertyvista.dto.LeaseDTO2;

public class LeaseViewerViewImplBase2<DTO extends LeaseDTO2> extends CrmViewerViewImplBase<DTO> implements LeaseViewerViewBase2<DTO> {

    private static final I18n i18n = I18n.get(LeaseViewerViewImpl2.class);

    protected final IListerView<DepositLifecycleDTO> depositLister;

    private final Button viewCurrentTerm;

    private final Button viewHistoricTerm;

    public LeaseViewerViewImplBase2(Class<? extends CrudAppPlace> placeClass) {
        super(placeClass, true);

        depositLister = new ListerInternalViewImplBase<DepositLifecycleDTO>(new DepositLifecycleLister());

        viewCurrentTerm = new Button(i18n.tr("View Current Term"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                ((LeaseViewerView2.Presenter) getPresenter()).viewTerm(getForm().getValue().currentLeaseTerm());
            }
        });
        addHeaderToolbarTwoItem(viewCurrentTerm.asWidget());

        viewHistoricTerm = new Button(i18n.tr("View Historic Term"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new LeaseTermSelectorDialog() {
                    {
//                        setParentFiltering(getForm().getValue().getPrimaryKey());
                    }

                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItems().isEmpty()) {
                            ((LeaseViewerView2.Presenter) getPresenter()).viewTerm(getSelectedItems().get(0));
                        }
                        return !getSelectedItems().isEmpty();
                    }
                }.show();
            }
        });
        addHeaderToolbarTwoItem(viewHistoricTerm.asWidget());
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    public void populate(DTO value) {
        super.populate(value);
    }

    @Override
    public IListerView<DepositLifecycleDTO> getDepositListerView() {
        return depositLister;
    }
}
