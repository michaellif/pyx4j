/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 1, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.summary;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.lease.LeaseTermParticipant;
import com.propertyvista.dto.TenantInLeaseDTO;

public class TenantViewFolder extends VistaTableFolder<TenantInLeaseDTO> {

    public TenantViewFolder() {
        super(TenantInLeaseDTO.class, false);
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        if (member instanceof TenantInLeaseDTO) {
            return new TenantViewer();
        }
        return super.create(member);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().leaseParticipant().customer().person().name(), "20em"));
        columns.add(new EntityFolderColumnDescriptor(proto().leaseParticipant().customer().person().birthDate(), "9em"));
        columns.add(new EntityFolderColumnDescriptor(proto().leaseParticipant().customer().person().email(), "21em"));
        columns.add(new EntityFolderColumnDescriptor(proto().relationship(), "10em"));
        columns.add(new EntityFolderColumnDescriptor(proto().role(), "10em"));
        return columns;
    }

    private class TenantViewer extends CEntityFolderRowEditor<TenantInLeaseDTO> {

        private Widget relationship;

        public TenantViewer() {
            super(TenantInLeaseDTO.class, columns());
        }

        @Override
        protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
            CComponent<?> comp = null;
            if (proto().leaseParticipant().customer().person().name() == column.getObject()) {
                comp = inject(column.getObject(), new CEntityLabel<Name>());
            } else {
                comp = super.createCell(column);
            }

            if (proto().relationship() == column.getObject()) {
                relationship = comp.asWidget();
            }

            return comp;
        }

        @Override
        protected void onValueSet(boolean populate) {
            super.onValueSet(populate);

            if (getValue().role().getValue() == LeaseTermParticipant.Role.Applicant) {
                relationship.setVisible(false);
            }
        }
    }
}
