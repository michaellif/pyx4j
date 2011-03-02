/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.client.ptapp.ui;

import java.util.ArrayList;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.TenantAsset;
import com.propertyvista.portal.domain.pt.TenantGuarantor;
import com.propertyvista.portal.domain.pt.TenantIncome;

import com.pyx4j.entity.client.ui.EditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.BoxFolderDecorator;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

@Singleton
public class FinancialViewForm extends BaseEntityForm<PotentialTenantFinancial> {

    private static I18n i18n = I18nFactory.getI18n(FinancialViewForm.class);

    private boolean readOnlyMode = false;

    public FinancialViewForm() {
        super(PotentialTenantFinancial.class);
    }

    public FinancialViewForm(EditableComponentFactory factory) {
        super(PotentialTenantFinancial.class, factory);
        readOnlyMode = true;
    }

    public boolean isReadOnlyMode() {
        return readOnlyMode;
    }

    @Override
    public IsWidget createContent() {
        FlowPanel main = new FlowPanel();
        main.add(new ViewHeaderDecorator(proto().incomes()));
        main.add(inject(proto().incomes()));
        main.add(new HTML());

        main.add(new ViewHeaderDecorator(proto().assets()));
        main.add(inject(proto().assets()));
        main.add(new HTML());

        main.add(new ViewHeaderDecorator(proto().guarantors()));
        main.add(inject(proto().guarantors()));
        main.add(new HTML());

        return main;
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (member == proto().incomes()) {
            return createIncomeFolderEditor();
        } else if (member == proto().assets()) {
            return createAssetFolderEditorColumns();
        } else if (member == proto().guarantors()) {
            return createGuarantorFolderEditorColumns();
        } else {
            return super.create(member);
        }
    }

    private CEntityFolder<TenantIncome> createIncomeFolderEditor() {

        return new CEntityFolder<TenantIncome>() {

            @Override
            protected FolderDecorator<TenantIncome> createFolderDecorator() {
                return new BoxFolderDecorator<TenantIncome>(SiteImages.INSTANCE.addRow());
            }

            @Override
            protected CEntityFolderItem<TenantIncome> createItem() {
                return new FinancialViewIncomeForm(FinancialViewForm.this);
            }

        };
    }

    private CEntityFolder<TenantAsset> createAssetFolderEditorColumns() {
        return new CEntityFolder<TenantAsset>() {

            private List<EntityFolderColumnDescriptor> columns;
            {
                TenantAsset proto = EntityFactory.getEntityPrototype(TenantAsset.class);
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto.assetType(), "120px"));
                columns.add(new EntityFolderColumnDescriptor(proto.percent(), "120px"));
                columns.add(new EntityFolderColumnDescriptor(proto.assetValue(), "120px"));
            }

            @Override
            protected FolderDecorator<TenantAsset> createFolderDecorator() {
                return new TableFolderDecorator<TenantAsset>(columns, SiteImages.INSTANCE.addRow(), "Add an asset");
            }

            @Override
            protected CEntityFolderItem<TenantAsset> createItem() {
                return createAssetRowEditor(columns);
            }

            private CEntityFolderItem<TenantAsset> createAssetRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<TenantAsset>(TenantAsset.class, columns) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), "Remove asset");
                    }

                    @Override
                    public void attachContent() {
                        super.attachContent();
                        get(proto().percent()).addValueValidator(new EditableValueValidator<Double>() {

                            @Override
                            public boolean isValid(CEditableComponent<Double, ?> component, Double value) {
                                return (value == null) || ((value >= 0) && (value <= 100));
                            }

                            @Override
                            public String getValidationMessage(CEditableComponent<Double, ?> component, Double value) {
                                return i18n.tr("Lorem ipsum dolor sit amet: 0% - 100%");
                            }

                        });
                    }
                };
            }

        };

    }

    private CEntityFolder<TenantGuarantor> createGuarantorFolderEditorColumns() {
        return new CEntityFolder<TenantGuarantor>() {

            private List<EntityFolderColumnDescriptor> columns;
            {
                TenantGuarantor proto = EntityFactory.getEntityPrototype(TenantGuarantor.class);
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto.relationship(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.firstName(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.middleName(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.lastName(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.birthDate(), "100px"));
                columns.add(new EntityFolderColumnDescriptor(proto.email(), "100px"));
            }

            @Override
            protected FolderDecorator<TenantGuarantor> createFolderDecorator() {
                return new TableFolderDecorator<TenantGuarantor>(columns, SiteImages.INSTANCE.addRow(), "Add guarantor");
            }

            @Override
            protected CEntityFolderItem<TenantGuarantor> createItem() {
                return createGuarantorRowEditor(columns);
            }

            private CEntityFolderItem<TenantGuarantor> createGuarantorRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<TenantGuarantor>(TenantGuarantor.class, columns) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), "Remove guarantor");
                    }

                };
            }

        };

    }
}
