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

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.propertyvista.portal.client.ptapp.resources.SiteImages;
import com.propertyvista.portal.client.ptapp.ui.components.MoneyEditorForm;
import com.propertyvista.portal.client.ptapp.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.DecorationUtils;
import com.propertyvista.portal.client.ptapp.ui.decorations.ViewHeaderDecorator;
import com.propertyvista.portal.client.ptapp.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.portal.domain.Money;
import com.propertyvista.portal.domain.pt.PotentialTenantFinancial;
import com.propertyvista.portal.domain.pt.TenantAsset;
import com.propertyvista.portal.domain.pt.TenantAsset.AssetType;
import com.propertyvista.portal.domain.pt.TenantGuarantor;
import com.propertyvista.portal.domain.pt.TenantIncome;
import com.propertyvista.portal.domain.util.ValidationUtils;

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.BoxFolderDecorator;
import com.pyx4j.entity.client.ui.flex.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.CEntityFolderItem;
import com.pyx4j.entity.client.ui.flex.CEntityFolderRow;
import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.FolderDecorator;
import com.pyx4j.entity.client.ui.flex.FolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.TableFolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

@Singleton
public class FinancialViewForm extends CEntityForm<PotentialTenantFinancial> {

    private static I18n i18n = I18nFactory.getI18n(FinancialViewForm.class);

    private boolean summaryViewMode = false;

    public FinancialViewForm() {
        super(PotentialTenantFinancial.class, new VistaEditorsComponentFactory());
    }

    public FinancialViewForm(IEditableComponentFactory factory) {
        super(PotentialTenantFinancial.class, factory);
        summaryViewMode = true;
    }

    public boolean isSummaryViewMode() {
        return summaryViewMode;
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

        if (isSummaryViewMode()) {
            main.setWidth("670px");
        } else {
            main.setWidth("700px");
        }
        return main;
    }

    private Widget createHeader(IObject<?> member) {
        if (isSummaryViewMode()) {
            HTML header = new HTML(HtmlUtils.h3(member.getMeta().getCaption()));
            header.getElement().getStyle().setMarginTop(1, Unit.EM);
            return header;
        } else {
            return new ViewHeaderDecorator(member);
        }
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if ((!isSummaryViewMode()) && member.getValueClass().equals(Money.class)) {
            return new MoneyEditorForm();
        } else {
            return super.create(member);
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
                if (isSummaryViewMode()) {
                    return new BoxReadOnlyFolderDecorator<TenantIncome>() {
                        @Override
                        public void setFolder(CEntityFolder<?> w) {
                            super.setFolder(w);
                            this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
                        }
                    };
                } else {
                    return new BoxFolderDecorator<TenantIncome>(SiteImages.INSTANCE.addRow(), SiteImages.INSTANCE.addRowHover(),
                            i18n.tr("Add an income source"));
                }
            }

            @Override
            protected CEntityFolderItem<TenantIncome> createItem() {
                return new FinancialViewIncomeForm(summaryViewMode);
            }

        };
    }

    private CEntityFolder<TenantAsset> createAssetFolderEditorColumns() {
        return new CEntityFolder<TenantAsset>(TenantAsset.class) {

            private List<EntityFolderColumnDescriptor> columns;
            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().assetType(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().percent(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().assetValue(), "15em"));
            }

            @Override
            protected FolderDecorator<TenantAsset> createFolderDecorator() {
                if (isSummaryViewMode()) {
                    return new BoxReadOnlyFolderDecorator<TenantAsset>() {
                        @Override
                        public void setFolder(CEntityFolder<?> w) {
                            super.setFolder(w);
                            this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
                        }
                    };
                } else {
                    return new TableFolderDecorator<TenantAsset>(columns, SiteImages.INSTANCE.addRow(), SiteImages.INSTANCE.addRowHover(),
                            i18n.tr("Add an asset"));
                }
            }

            @Override
            protected CEntityFolderItem<TenantAsset> createItem() {
                return new CEntityFolderRow<TenantAsset>(TenantAsset.class, columns) {

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        if (isSummaryViewMode()) {
                            return new BoxReadOnlyFolderItemDecorator(false);
                        } else {
                            return new TableFolderItemDecorator(SiteImages.INSTANCE.delRow(), SiteImages.INSTANCE.delRowHover(), i18n.tr("Remove asset"));
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

                        get(proto().assetType()).addValueChangeHandler(new ValueChangeHandler<TenantAsset.AssetType>() {

                            @Override
                            public void onValueChange(ValueChangeEvent<AssetType> event) {
                                if (get(proto().percent()).getValue() == null) {
                                    get(proto().percent()).setValue(100d);
                                }

                            }
                        });
                    }
                };
            }
        };

    }

    private CEntityFolder<TenantGuarantor> createGuarantorFolderEditorColumns() {
        return new CEntityFolder<TenantGuarantor>(TenantGuarantor.class) {

            @Override
            protected FolderDecorator<TenantGuarantor> createFolderDecorator() {
                if (isSummaryViewMode()) {
                    return new BoxReadOnlyFolderDecorator<TenantGuarantor>() {
                        @Override
                        public void setFolder(CEntityFolder<?> w) {
                            super.setFolder(w);
                            this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
                        }
                    };
                } else {
                    return new BoxFolderDecorator<TenantGuarantor>(SiteImages.INSTANCE.addRow(), SiteImages.INSTANCE.addRowHover(), i18n.tr("Add guarantor"));
                }
            }

            @Override
            protected CEntityFolderItem<TenantGuarantor> createItem() {
                return createGuarantorRowEditor();
            }

            private CEntityFolderItem<TenantGuarantor> createGuarantorRowEditor() {
                return new CEntityFolderItem<TenantGuarantor>(TenantGuarantor.class) {

                    @Override
                    public IsWidget createContent() {
                        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(summaryViewMode);
                        if (isSummaryViewMode()) {
                            FlowPanel person = DecorationUtils.formFullName(this, proto());
                            person.getElement().getStyle().setFontWeight(FontWeight.BOLDER);
                            person.getElement().getStyle().setFontSize(1.1, Unit.EM);
                            main.add(person);
                        } else {
                            main.add(inject(proto().firstName()), 12);
                            main.add(inject(proto().middleName()), 12);
                            main.add(inject(proto().lastName()), 20);
                        }
                        main.add(inject(proto().homePhone()), 15);
                        main.add(inject(proto().mobilePhone()), 15);
                        main.add(inject(proto().workPhone()), 15);
                        main.add(inject(proto().birthDate()), 8);
                        main.add(inject(proto().email()), 15);
                        main.add(new HTML());
                        return main;
                    }

                    @Override
                    public FolderItemDecorator createFolderItemDecorator() {
                        if (isSummaryViewMode()) {
                            return new BoxReadOnlyFolderItemDecorator(false);
                        } else {
                            return new BoxFolderItemDecorator(SiteImages.INSTANCE.delRow(), SiteImages.INSTANCE.delRowHover(), i18n.tr("Remove guarantor"));
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
