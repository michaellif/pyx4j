/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-26
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.charges;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CNumberField;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.forms.client.validators.ValidationError;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.lease.LeaseParticipant;
import com.propertyvista.domain.tenant.lease.LeaseParticipant.Role;
import com.propertyvista.portal.domain.ptapp.TenantCharge;

public class ChargeSplitListFolder extends VistaTableFolder<TenantCharge> {

    private static final I18n i18n = I18n.get(ChargeSplitListFolder.class);

    public static final ArrayList<EntityFolderColumnDescriptor> COLUMNS = new ArrayList<EntityFolderColumnDescriptor>();

    private final boolean editable;

    static {
        TenantCharge proto = EntityFactory.getEntityPrototype(TenantCharge.class);
        COLUMNS.add(new EntityFolderColumnDescriptor(proto.tenant().customer().person().name(), "33em"));
        COLUMNS.add(new EntityFolderColumnDescriptor(proto.tenant().percentage(), "7em"));
        COLUMNS.add(new EntityFolderColumnDescriptor(proto.amount(), "7em"));
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return COLUMNS;
    }

    ChargeSplitListFolder(boolean modifiable) {
        super(TenantCharge.class, false);
        this.editable = modifiable;
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (member instanceof TenantCharge) {
            return new TenantChargeEditor();
        }
        return super.create(member);
    }

    @Override
    public void addValidations() {
        this.addValueValidator(new EditableValueValidator<IList<TenantCharge>>() {

            @Override
            public ValidationError isValid(CComponent<IList<TenantCharge>, ?> component, IList<TenantCharge> value) {
                int totalPrc = 0;
                for (TenantCharge charge : value) {
                    if (charge.tenant().role().getValue() == LeaseParticipant.Role.Applicant) {
                        continue; // Ignore main applicant, since it is read-only!
                    }
                    Integer p = charge.tenant().percentage().getValue();
                    if (p != null) {
                        totalPrc += p.intValue();
                    }
                }
                return totalPrc <= 100 ? null : new ValidationError(i18n.tr("Sum Of All Percentages Cannot Exceed 100%"));
            }
        });
    }

    private class TenantChargeEditor extends CEntityFolderRowEditor<TenantCharge> {

        public TenantChargeEditor() {
            super(TenantCharge.class, COLUMNS);
            setViewable(true);
        }

        @Override
        protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
            if (column.getObject() == proto().tenant().percentage() && editable) {
                CComponent<?, ?> comp = inject(column.getObject());
                comp.inheritViewable(false); // always not viewable!
                return comp;
            } else if (column.getObject() == proto().tenant().customer().person().name()) {
                return inject(column.getObject(), new CEntityLabel<Name>());
            }
            return super.createCell(column);
        }

        @Override
        protected Widget createCellDecorator(EntityFolderColumnDescriptor column, CComponent<?, ?> component, String width) {
            Widget w = super.createCellDecorator(column, component, width);

            if (column.getObject() == proto().tenant().percentage()) {
                FlowPanel wrap = new FlowPanel();
                wrap.add(DecorationUtils.inline(w, "3em", "right"));
                wrap.add(DecorationUtils.inline(new HTML("%"), "1em"));
                wrap.setWidth(width);
                return wrap;
            }

            return w;
        }

        @Override
        protected void propagateValue(TenantCharge entity, boolean fireEvent, boolean populate) {
            super.propagateValue(entity, fireEvent, populate);
            if ((getValue().tenant().role().getValue() == LeaseParticipant.Role.Applicant)) {
                get(proto().tenant().percentage()).setEditable(false);
                get(proto().tenant().percentage()).setViewable(true);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public void addValidations() {
            CComponent<Integer, ?> prc = get(proto().tenant().percentage());
            if (prc instanceof CNumberField) {
                ((CNumberField<Integer>) prc).setRange(0, 100);
            }
        }
    }
}
