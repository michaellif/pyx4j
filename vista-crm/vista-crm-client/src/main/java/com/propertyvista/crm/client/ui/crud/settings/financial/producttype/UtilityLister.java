/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-09
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.financial.producttype;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.DefaultAsyncCallback;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.dialog.ConfirmDecline;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.crm.rpc.services.building.catalog.UtilityCrudService;
import com.propertyvista.domain.property.asset.Utility;

public class UtilityLister extends ListerBase<Utility> {

    private static final I18n i18n = I18n.get(UtilityLister.class);

    public UtilityLister() {
        super(Utility.class, false, true);

        getDataTablePanel().getDataTable().setHasCheckboxColumn(true);

        addActionItem(new Button(i18n.tr("Delete Checked"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                MessageDialog.confirm(i18n.tr("Confirm"), i18n.tr("Do you really want to delete checked items?"), new ConfirmDecline() {
                    @Override
                    public void onConfirmed() {
                        for (Utility item : getDataTablePanel().getDataTable().getCheckedItems()) {
                            getPresenter().delete(item.getPrimaryKey());
                        }
                    }

                    @Override
                    public void onDeclined() {
                        // TODO Auto-generated method stub
                    }
                });
            }
        }));

        setColumnDescriptors(//@formatter:off
            new MemberColumnDescriptor.Builder(proto().name()).build()
        );//@formatter:on
    }

    @Override
    protected void onItemNew() {
        new NewUtility() {
            @Override
            public boolean onClickOk() {
                Utility entity = EntityFactory.create(Utility.class);
                entity.name().setValue(getName());
                GWT.<UtilityCrudService> create(UtilityCrudService.class).save(new DefaultAsyncCallback<Key>() {
                    @Override
                    public void onSuccess(Key result) {
                        getLister().restoreState();
                    }
                }, entity);
                return true;
            }
        }.show();
    }

    private abstract class NewUtility extends OkCancelDialog {

        private final CTextField name = new CTextField();

        public NewUtility() {
            super(i18n.tr("New Utility"));
            setBody(createBody());
            setSize("350px", "100px");
        }

        protected Widget createBody() {
            getOkButton().setEnabled(true);

            VerticalPanel content = new VerticalPanel();
            content.add(new HTML(i18n.tr("Please enter the name") + ":"));
            content.add(name);

            name.setWidth("100%");
            content.setWidth("100%");
            return content.asWidget();
        }

        public String getName() {
            return name.getValue();
        }
    }
}
