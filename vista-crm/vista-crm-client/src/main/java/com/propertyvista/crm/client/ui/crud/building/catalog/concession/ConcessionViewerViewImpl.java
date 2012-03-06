/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.building.catalog.concession;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.crm.client.ui.crud.CrmViewerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.selections.version.ConcessionVersionService;
import com.propertyvista.domain.financial.offering.Concession;
import com.propertyvista.domain.financial.offering.Concession.ConcessionV;

public class ConcessionViewerViewImpl extends CrmViewerViewImplBase<Concession> implements ConcessionViewerView {

    private static final I18n i18n = I18n.get(ConcessionViewerViewImpl.class);

    private final Button selectVersion;

    private final Button finalizeButton;

    public ConcessionViewerViewImpl() {
        super(CrmSiteMap.Properties.Concession.class, new ConcessionEditorForm(true));

        selectVersion = new Button(i18n.tr("Select Version"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                new VersionSelectorDialog().show();
            }
        });
        addToolbarItem(selectVersion.asWidget());

        finalizeButton = new Button(i18n.tr("Finalize"), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.finalize();
            }
        });
        addToolbarItem(finalizeButton.asWidget());
    }

    private class VersionSelectorDialog extends EntitySelectorDialog<ConcessionV> {

        public VersionSelectorDialog() {
            super(ConcessionV.class, false, Collections.<ConcessionV> emptyList(), i18n.tr("Select Version"));
            setParentFiltering(form.getValue().getPrimaryKey());
            setWidth("700px");
        }

        @Override
        public boolean onClickOk() {
            if (getSelectedItems().isEmpty()) {
                return false;
            } else {
                for (ConcessionV selected : getSelectedItems()) {
                    presenter.view(new Key(selected.holder().getPrimaryKey().asLong(), (selected.fromDate().isNull() ? 0 : selected.fromDate().getValue()
                            .getTime())));
                    break;
                }
                return true;
            }
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().versionNumber()).build(),
                    new MemberColumnDescriptor.Builder(proto().fromDate()).build()
                );//@formatter:on
        }

        @Override
        protected AbstractListService<ConcessionV> getSelectService() {
            return GWT.<AbstractListService<ConcessionV>> create(ConcessionVersionService.class);
        }
    }
}