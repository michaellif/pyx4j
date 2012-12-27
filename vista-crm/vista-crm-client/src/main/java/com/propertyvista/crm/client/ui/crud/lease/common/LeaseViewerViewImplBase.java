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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

import com.pyx4j.entity.shared.criterion.PropertyCriterion;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.rpc.CrudAppPlace;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.components.boxes.LeaseTermSelectorDialog;
import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.client.ui.crud.lease.LeaseViewerViewImpl;
import com.propertyvista.domain.tenant.lease.LeaseTerm;
import com.propertyvista.dto.LeaseDTO;

public class LeaseViewerViewImplBase<DTO extends LeaseDTO> extends CrmViewerViewImplBase<DTO> implements LeaseViewerViewBase<DTO> {

    private static final I18n i18n = I18n.get(LeaseViewerViewImpl.class);

    protected final Button termsButton;

    protected final MenuItem viewFutureTerm;

    public LeaseViewerViewImplBase(Class<? extends CrudAppPlace> placeClass) {
        super(placeClass, true);

        termsButton = new Button(i18n.tr("View Term"));
        Button.ButtonMenuBar viewsMenu = termsButton.createMenu();
        termsButton.setMenu(viewsMenu);
        addHeaderToolbarItem(termsButton.asWidget());

        MenuItem viewCurrentTerm = new MenuItem(i18n.tr("Current"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerViewBase.Presenter) getPresenter()).viewTerm(getForm().getValue().currentTerm());
            }
        });
        viewsMenu.addItem(viewCurrentTerm);

        viewFutureTerm = new MenuItem(i18n.tr("Future"), new Command() {
            @Override
            public void execute() {
                ((LeaseViewerViewBase.Presenter) getPresenter()).viewTerm(getForm().getValue().nextTerm());
            }
        });
        viewsMenu.addItem(viewFutureTerm);

        MenuItem viewHistoricTerms = new MenuItem(i18n.tr("Historic..."), new Command() {
            @Override
            public void execute() {
                new LeaseTermSelectorDialog() {
                    {
                        setParentFiltering(getForm().getValue().getPrimaryKey());
                        addFilter(PropertyCriterion.ne(proto().status(), LeaseTerm.Status.Offer));
                    }

                    @Override
                    public boolean onClickOk() {
                        if (!getSelectedItems().isEmpty()) {
                            ((LeaseViewerViewBase.Presenter) getPresenter()).viewTerm(getSelectedItems().get(0));
                        }
                        return !getSelectedItems().isEmpty();
                    }
                }.show();
            }
        });
        viewsMenu.addItem(viewHistoricTerms);
    }

    @Override
    public void reset() {
        viewFutureTerm.setVisible(false);

        super.reset();
    }

    @Override
    public void populate(DTO value) {
        super.populate(value);

        viewFutureTerm.setVisible(!value.nextTerm().isNull());
    }
}
