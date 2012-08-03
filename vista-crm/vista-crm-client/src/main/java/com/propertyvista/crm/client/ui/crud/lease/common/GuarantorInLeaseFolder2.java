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

import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.tenant.Guarantor2;
import com.propertyvista.domain.tenant.Tenant2;

public class GuarantorInLeaseFolder2 extends VistaTableFolder<Guarantor2> {

    static final I18n i18n = I18n.get(GuarantorInLeaseFolder2.class);

    public GuarantorInLeaseFolder2() {
        super(Guarantor2.class, false);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().participantId(), "7em"),
                new EntityFolderColumnDescriptor(proto().customer().person().name(), "30em"),
                new EntityFolderColumnDescriptor(proto().role(), "15em"),
                new EntityFolderColumnDescriptor(proto().tenant(), "15em"),
                new EntityFolderColumnDescriptor(proto().relationship(), "15em"));
          //@formatter:on
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof Guarantor2) {
            return new GuarantorInLeaseViewer();
        }
        return super.create(member);
    }

    private class GuarantorInLeaseViewer extends CEntityFolderRowEditor<Guarantor2> {

        public GuarantorInLeaseViewer() {
            super(Guarantor2.class, columns());
            setEditable(false);
            setViewable(true);
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (proto().customer().person().name() == column.getObject()) {
                return inject(proto().customer().person().name(), new NameEditor(i18n.tr("Guarantor"), Guarantor2.class) {
                    @Override
                    public Key getLinkKey() {
                        return GuarantorInLeaseViewer.this.getValue().getPrimaryKey();
                    }
                });
            } else if (proto().tenant() == column.getObject()) {
                return inject(proto().tenant(), new CEntityCrudHyperlink<Tenant2>(AppPlaceEntityMapper.resolvePlace(Tenant2.class)));
            }
            return super.createCell(column);
        }
    }
}