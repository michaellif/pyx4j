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

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityHyperlink;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.lease.Guarantor;
import com.propertyvista.domain.tenant.lease.Tenant;
import com.propertyvista.domain.tenant.lease.LeaseTermGuarantor;

public class GuarantorInLeaseFolder extends VistaTableFolder<LeaseTermGuarantor> {

    static final I18n i18n = I18n.get(GuarantorInLeaseFolder.class);

    public GuarantorInLeaseFolder() {
        super(LeaseTermGuarantor.class, false);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().leaseParticipant().participantId(), "7em"),
                new EntityFolderColumnDescriptor(proto().leaseParticipant().customer().person().name(), "25em"),
                new EntityFolderColumnDescriptor(proto().role(), "10em"),
                new EntityFolderColumnDescriptor(proto().tenant(), "25em"),
                new EntityFolderColumnDescriptor(proto().relationship(), "15em"));
          //@formatter:on
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof LeaseTermGuarantor) {
            return new GuarantorInLeaseViewer();
        }
        return super.create(member);
    }

    private class GuarantorInLeaseViewer extends CEntityFolderRowEditor<LeaseTermGuarantor> {

        public GuarantorInLeaseViewer() {
            super(LeaseTermGuarantor.class, columns());
            setEditable(false);
            setViewable(true);
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (proto().leaseParticipant().customer().person().name() == column.getObject()) {
                return inject(proto().leaseParticipant().customer().person().name(), new CEntityHyperlink<Name>(null, new Command() {
                    @Override
                    public void execute() {
                        AppSite.getPlaceController().goTo(
                                AppPlaceEntityMapper.resolvePlace(Guarantor.class).formViewerPlace(
                                        GuarantorInLeaseViewer.this.getValue().leaseParticipant().getPrimaryKey()));
                    }
                }));
            } else if (proto().tenant() == column.getObject()) {
                return inject(proto().tenant(), new CEntityCrudHyperlink<Tenant>(AppPlaceEntityMapper.resolvePlace(Tenant.class)));
            }
            return super.createCell(column);
        }
    }
}