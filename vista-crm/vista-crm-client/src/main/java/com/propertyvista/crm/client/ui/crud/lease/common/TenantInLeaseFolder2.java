/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.CEntityHyperlink;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.Tenant2;
import com.propertyvista.domain.tenant.lease.LeaseParticipant2;

public class TenantInLeaseFolder2 extends VistaTableFolder<Tenant2> {

    static final I18n i18n = I18n.get(TenantInLeaseFolder2.class);

    public TenantInLeaseFolder2() {
        super(Tenant2.class, false);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().participantId(), "7em"),
                new EntityFolderColumnDescriptor(proto().customer().person().name(), "25em"),
                new EntityFolderColumnDescriptor(proto().role(), "10em"),
                new EntityFolderColumnDescriptor(proto().relationship(), "15em"),
                new EntityFolderColumnDescriptor(proto().percentage(), "5em"));
          //@formatter:on
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Tenant2) {
            return new TenantInLeaseViewer();
        }
        return super.create(member);
    }

    private class TenantInLeaseViewer extends CEntityFolderRowEditor<Tenant2> {

        public TenantInLeaseViewer() {
            super(Tenant2.class, columns());
            setEditable(false);
            setViewable(true);
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (proto().customer().person().name() == column.getObject()) {
                return inject(proto().customer().person().name(), new CEntityHyperlink<Name>(new Command() {
                    @Override
                    public void execute() {
                        AppSite.getPlaceController().goTo(
                                AppPlaceEntityMapper.resolvePlace(Tenant2.class).formViewerPlace(TenantInLeaseViewer.this.getValue().getPrimaryKey()));
                    }
                }));
            }
            return super.createCell(column);
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            if (getValue().role().getValue() == LeaseParticipant2.Role.Applicant) {
                get(proto().relationship()).setVisible(false);
            }
        }
    }
}