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
import java.util.Date;
import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.validators.ValidationUtils;
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
        main.add(createHeader(proto().incomes()));
        main.add(inject(proto().incomes(), createIncomeFolderEditor()));
        main.add(new HTML());

        main.add(createHeader(proto().assets()));
        main.add(inject(proto().assets(), createAssetFolderEditorColumns()));
        main.add(new HTML());

        main.add(createHeader(proto().guarantors()));
        main.add(inject(proto().guarantors(), createGuarantorFolderEditorColumns()));
        main.add(new HTML());

        SimplePanel padder = new SimplePanel();
        if (isReadOnlyMode()) {
            padder.getElement().getStyle().setBackgroundColor("white");
            padder.getElement().getStyle().setBorderWidth(1, Unit.PX);
            padder.getElement().getStyle().setBorderStyle(BorderStyle.SOLID);
            padder.getElement().getStyle().setBorderColor("#bbb");
            padder.getElement().getStyle().setMarginBottom(0.5, Unit.EM);

            main.getElement().getStyle().setPaddingTop(0.5, Unit.EM);
            main.getElement().getStyle().setPaddingBottom(0.5, Unit.EM);
            main.getElement().getStyle().setPaddingLeft(15, Unit.PX);
            main.getElement().getStyle().setPaddingRight(15, Unit.PX);
            main.setWidth("670");
        }

        padder.setWidget(main);
        padder.setWidth("700px");
        return padder;
    }

    private Widget createHeader(IObject<?> member) {
        if (isReadOnlyMode()) {
            return new HTML("<h4>" + member.getMeta().getCaption() + "</h4>");
        } else {
            return new ViewHeaderDecorator(member);
        }
    }

    @Override
    public void addValidations() {
        this.addValueValidator(new EditableValueValidator<PotentialTenantFinancial>() {

            @Override
            public boolean isValid(CEditableComponent<PotentialTenantFinancial, ?> component, PotentialTenantFinancial value) {
                return (value.assets().size() > 0) || (value.incomes().size() > 0);
            }

            @Override
            public String getValidationMessage(CEditableComponent<PotentialTenantFinancial, ?> component, PotentialTenantFinancial value) {
                return i18n.tr("At least one source of income or one asset is required");
            }
        });
    }

    private CEntityFolder<TenantIncome> createIncomeFolderEditor() {

        return new CEntityFolder<TenantIncome>(TenantIncome.class) {

            @Override
            protected FolderDecorator<TenantIncome> createFolderDecorator() {
                if (isReadOnlyMode()) {
                    return new BoxReadOnlyFolderDecorator<TenantIncome>() {
                        @Override
                        public void setFolder(CEntityFolder<?> w) {
                            super.setFolder(w);
                            this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
                        }
                    };
                } else {
                    return new BoxFolderDecorator<TenantIncome>(SiteImages.INSTANCE.addRow(), i18n.tr("Add an income source"));
                }
            }

            @Override
            protected CEntityFolderItem<TenantIncome> createItem() {
                return new FinancialViewIncomeForm(readOnlyMode);
            }

        };
    }

    private CEntityFolder<TenantAsset> createAssetFolderEditorColumns() {
        return new CEntityFolder<TenantAsset>(TenantAsset.class) {

            private List<EntityFolderColumnDescriptor> columns;
            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().assetType(), "180px"));
                columns.add(new EntityFolderColumnDescriptor(proto().percent(), "120px"));
                columns.add(new EntityFolderColumnDescriptor(proto().assetValue(), "120px"));
            }

            @Override
            protected FolderDecorator<TenantAsset> createFolderDecorator() {
                if (isReadOnlyMode()) {
                    return new BoxReadOnlyFolderDecorator<TenantAsset>() {
                        @Override
                        public void setFolder(CEntityFolder<?> w) {
                            super.setFolder(w);
                            this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
                        }
                    };
                } else {
                    return new TableFolderDecorator<TenantAsset>(columns, SiteImages.INSTANCE.addRow(), i18n.tr("Add an asset"));
                }
            }

            @Override
            protected CEntityFolderItem<TenantAsset> createItem() {
                return createAssetRowEditor(columns);
            }

            @Override
            protected void createNewEntity(TenantAsset newEntity, AsyncCallback<TenantAsset> callback) {
                newEntity.percent().setValue(100d);
                super.createNewEntity(newEntity, callback);
            }

            private CEntityFolderItem<TenantAsset> createAssetRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<TenantAsset>(TenantAsset.class, columns) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        if (isReadOnlyMode()) {
                            return new BoxReadOnlyFolderItemDecorator(false);
                        } else {
                            return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), i18n.tr("Remove asset"));
                        }
                    }

                    @Override
                    public void addValidations() {
                        get(proto().percent()).addValueValidator(new EditableValueValidator<Double>() {

                            @Override
                            public boolean isValid(CEditableComponent<Double, ?> component, Double value) {
                                return (value == null) || ((value >= 0) && (value <= 100));
                            }

                            @Override
                            public String getValidationMessage(CEditableComponent<Double, ?> component, Double value) {
                                return i18n.tr("Value can not increase 100%");
                            }

                        });
                    }
                };
            }

        };

    }

    private CEntityFolder<TenantGuarantor> createGuarantorFolderEditorColumns() {
        return new CEntityFolder<TenantGuarantor>(TenantGuarantor.class) {

            private List<EntityFolderColumnDescriptor> columns;
            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().relationship(), "9em"));
                columns.add(new EntityFolderColumnDescriptor(proto().firstName(), "8em"));
                columns.add(new EntityFolderColumnDescriptor(proto().middleName(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().lastName(), "12em"));
                columns.add(new EntityFolderColumnDescriptor(proto().birthDate(), "7.2em"));
                columns.add(new EntityFolderColumnDescriptor(proto().email(), "10em"));
            }

            @Override
            protected FolderDecorator<TenantGuarantor> createFolderDecorator() {
                if (isReadOnlyMode()) {
                    return new BoxReadOnlyFolderDecorator<TenantGuarantor>() {
                        @Override
                        public void setFolder(CEntityFolder<?> w) {
                            super.setFolder(w);
                            this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
                        }
                    };
                } else {
                    return new TableFolderDecorator<TenantGuarantor>(columns, SiteImages.INSTANCE.addRow(), i18n.tr("Add guarantor"));
                }
            }

            @Override
            protected CEntityFolderItem<TenantGuarantor> createItem() {
                return createGuarantorRowEditor(columns);
            }

            private CEntityFolderItem<TenantGuarantor> createGuarantorRowEditor(final List<EntityFolderColumnDescriptor> columns) {
                return new CEntityFolderRow<TenantGuarantor>(TenantGuarantor.class, columns) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        if (isReadOnlyMode()) {
                            return new BoxReadOnlyFolderItemDecorator(false);
                        } else {
                            return new TableFolderItemDecorator(SiteImages.INSTANCE.removeRow(), i18n.tr("Remove guarantor"));
                        }
                    }

                    @Override
                    public void addValidations() {

                        get(proto().email()).setMandatory(true);

                        get(proto().birthDate()).addValueValidator(new EditableValueValidator<Date>() {

                            @Override
                            public boolean isValid(CEditableComponent<Date, ?> component, Date value) {
                                return ValidationUtils.isOlderThen18(value);
                            }

                            @Override
                            public String getValidationMessage(CEditableComponent<Date, ?> component, Date value) {
                                return i18n.tr("Guarantor should be at least 18 years old");
                            }
                        });
                    }
                };
            }

        };

    }
}
