/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 15, 2011
 * @author antonk
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.domain.pt.PotentialTenant.Relationship;
import com.propertyvista.portal.domain.pt.PotentialTenantInfo;
import com.propertyvista.portal.domain.pt.PotentialTenantList;

import com.pyx4j.commons.TimeUtils;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.utils.EntityGraph;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CTextField;
import com.pyx4j.forms.client.validators.EditableValueValidator;

@Singleton
public class TenantsViewForm extends CEntityForm<PotentialTenantList> {

    private static I18n i18n = I18nFactory.getI18n(TenantsViewForm.class);

    private int maxTenants;

    public TenantsViewForm() {
        super(PotentialTenantList.class);
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();
        main.add(inject(proto().tenants(), createTenantsEditorColumns()));
        addValidations();
        return main;
    }

    private void addValidations() {
        super.addValueValidator(new EditableValueValidator<PotentialTenantList>() {

            @Override
            public boolean isValid(CEditableComponent<PotentialTenantList, ?> component, PotentialTenantList value) {
                return !EntityGraph.hasBusinessDuplicates(getValue().tenants());
            }

            @Override
            public String getValidationMessage(CEditableComponent<PotentialTenantList, ?> component, PotentialTenantList value) {
                return i18n.tr("Duplicate tenants specified");
            }
        });

        maxTenants = proto().tenants().getMeta().getLength();
        super.addValueValidator(new EditableValueValidator<PotentialTenantList>() {

            @Override
            public boolean isValid(CEditableComponent<PotentialTenantList, ?> component, PotentialTenantList value) {
                int size = getValue().tenants().size();
                return (size <= maxTenants) && ((value.tenantsMaximum().isNull() || (size <= value.tenantsMaximum().getValue())));
            }

            @Override
            public String getValidationMessage(CEditableComponent<PotentialTenantList, ?> component, PotentialTenantList value) {
                return i18n.tr("Exceeded number of allowed tenants");
            }
        });
    }

    private CEntityFolder<PotentialTenantInfo> createTenantsEditorColumns() {

        return new CEntityFolder<PotentialTenantInfo>() {

            private List<EntityFolderColumnDescriptor> columns;
            {
                PotentialTenantInfo proto = EntityFactory.getEntityPrototype(PotentialTenantInfo.class);
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto.firstName(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto.middleName(), "6em"));
                columns.add(new EntityFolderColumnDescriptor(proto.lastName(), "10em"));
                columns.add(new EntityFolderColumnDescriptor(proto.birthDate(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto.email(), "11em"));
                columns.add(new EntityFolderColumnDescriptor(proto.relationship(), "9em"));
                columns.add(new EntityFolderColumnDescriptor(proto.dependant(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto.takeOwnership(), "7em"));
            }

            @Override
            protected FolderDecorator<PotentialTenantInfo> createFolderDecorator() {
                return new TableFolderDecorator<PotentialTenantInfo>(columns, SiteImages.INSTANCE.addRow(), i18n.tr("Add a person"));
            }

            @Override
            protected CEntityFolderItem<PotentialTenantInfo> createItem() {
                return createTenantRowEditor(columns);
            }

            private CEntityFolderItem<PotentialTenantInfo> createTenantRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<PotentialTenantInfo>(PotentialTenantInfo.class, columns) {

                    PotentialTenantInfo proto = EntityFactory.getEntityPrototype(PotentialTenantInfo.class);

                    @SuppressWarnings("rawtypes")
                    @Override
                    public IsWidget createContent() {
                        if (!isFirst()) {
                            return super.createContent();
                        } else {
                            FlowPanel main = new FlowPanel();
                            main.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
                            main.setWidth("100%");
                            for (EntityFolderColumnDescriptor column : columns) {
                                // Don't show dependent and takeOwnership 
                                if (column.getObject() == proto.dependant() || column.getObject() == proto.takeOwnership()) {
                                    continue;
                                }
                                //|| column.getObject() == proto.relationship()
                                CComponent<?> component = createCell(column);
                                component.setWidth("100%");
                                if (column.getObject() == proto.email()) {
                                    ((CEditableComponent) component).setEditable(false);
                                }
                                main.add(createDecorator(component, column.getWidth()));
                            }
                            return main;
                        }
                    }

                    @Override
                    public void attachContent() {
                        super.attachContent();
                        get(proto.birthDate()).addValueValidator(new EditableValueValidator<Date>() {

                            @Override
                            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                                Relationship relationship = getValue().relationship().getValue();
                                if ((relationship == Relationship.Applicant) || (relationship == Relationship.CoApplicant)) {
                                    Date now = new Date();
                                    @SuppressWarnings("deprecation")
                                    Date y18 = TimeUtils.createDate(now.getYear() - 18, now.getMonth(), now.getDay());
                                    return value.before(y18);
                                } else {
                                    return true;
                                }
                            }

                            @Override
                            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                                return i18n.tr("Applicant and co-applicant should be at least 18 years old");
                            }
                        });
                    }

                    @SuppressWarnings({ "unchecked", "rawtypes" })
                    @Override
                    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
                        CComponent<?> comp = null;
                        if (isFirst() && proto.relationship() == column.getObject()) {
                            CTextField textComp = new CTextField();
                            textComp.setEditable(false);
                            textComp.setValue(Relationship.Applicant.name());
                            comp = textComp;
                        } else {
                            comp = super.createCell(column);
                            if (proto.relationship() == column.getObject()) {
                                Collection<Relationship> relationships = EnumSet.allOf(Relationship.class);
                                relationships.remove(Relationship.Applicant);
                                ((CComboBox) comp).setOptions(relationships);
                            }
                        }
                        return comp;
                    }

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), i18n.tr("Remove person"), !isFirst());
                    }

                };
            }

        };
    }

}
