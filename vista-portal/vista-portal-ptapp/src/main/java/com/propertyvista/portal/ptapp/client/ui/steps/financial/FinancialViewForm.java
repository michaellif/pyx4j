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
package com.propertyvista.portal.ptapp.client.ui.steps.financial;

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

import com.pyx4j.commons.HtmlUtils;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityEditor;
import com.pyx4j.entity.client.ui.flex.folder.BoxFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderItemDecorator;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;

import com.propertyvista.common.client.ui.components.CMoney;
import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.decorations.DecorationUtils;
import com.propertyvista.common.client.ui.decorations.VistaDecoratorsFlowPanel;
import com.propertyvista.common.client.ui.decorations.VistaHeaderBar;
import com.propertyvista.domain.financial.Money;
import com.propertyvista.domain.tenant.income.IIncomeInfo;
import com.propertyvista.domain.tenant.income.PersonalAsset;
import com.propertyvista.domain.tenant.income.PersonalAsset.AssetType;
import com.propertyvista.domain.tenant.income.PersonalIncome;
import com.propertyvista.domain.tenant.income.TenantGuarantor;
import com.propertyvista.domain.util.ValidationUtils;
import com.propertyvista.portal.ptapp.client.resources.PortalImages;
import com.propertyvista.portal.ptapp.client.ui.decorations.BoxReadOnlyFolderDecorator;
import com.propertyvista.portal.ptapp.client.ui.decorations.BoxReadOnlyFolderItemDecorator;
import com.propertyvista.portal.rpc.ptapp.dto.TenantFinancialDTO;

public class FinancialViewForm extends CEntityEditor<TenantFinancialDTO> {

    private static I18n i18n = I18nFactory.getI18n(FinancialViewForm.class);

    private boolean summaryViewMode = false;

    public FinancialViewForm() {
        super(TenantFinancialDTO.class, new VistaEditorsComponentFactory());
    }

    public FinancialViewForm(IEditableComponentFactory factory) {
        super(TenantFinancialDTO.class, factory);
        summaryViewMode = true;
    }

    public boolean isSummaryViewMode() {
        return summaryViewMode;
    }

    @Override
    public IsWidget createContent() {

        FlowPanel main = new FlowPanel();

//        main.add(createHeader(proto().incomes()));
//        main.add(inject(proto().incomes2(), createIncomeFolderEditor2()));
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
            return new VistaHeaderBar(member);
        }
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if ((!isSummaryViewMode()) && member.getValueClass().equals(Money.class)) {
            return new CMoney();
        } else {
            return super.create(member);
        }
    }

    @Override
    public void populate(TenantFinancialDTO value) {
        super.populate(value);
    }

    @Override
    public void addValidations() {
        this.addValueValidator(new EditableValueValidator<TenantFinancialDTO>() {

            @Override
            public boolean isValid(CEditableComponent<TenantFinancialDTO, ?> component, TenantFinancialDTO value) {
                return (value.assets().size() > 0) || (value.incomes().size() > 0);
            }

            @Override
            public String getValidationMessage(CEditableComponent<TenantFinancialDTO, ?> component, TenantFinancialDTO value) {
                return i18n.tr("At least one source of income or one asset is required");
            }
        });
    }

    private CEntityFolder<IIncomeInfo> createIncomeFolderEditor2() {

        return new CEntityFolder<IIncomeInfo>(IIncomeInfo.class) {

            @Override
            protected IFolderDecorator<IIncomeInfo> createDecorator() {
                if (isSummaryViewMode()) {
                    return new BoxReadOnlyFolderDecorator<IIncomeInfo>() {
                        @Override
                        public void setComponent(CEntityFolder w) {
                            super.setComponent(w);
                            this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
                        }
                    };
                } else {
                    return new BoxFolderDecorator<IIncomeInfo>(PortalImages.INSTANCE.add(), PortalImages.INSTANCE.addHover(), i18n.tr("Add an income source"));
                }
            }

            @Override
            protected CEntityFolderItemEditor<IIncomeInfo> createItem() {
                return new FinancialViewIncomeForm2(summaryViewMode);
            }

        };
    }

    private CEntityFolder<PersonalIncome> createIncomeFolderEditor() {

        return new CEntityFolder<PersonalIncome>(PersonalIncome.class) {

            @Override
            protected IFolderDecorator<PersonalIncome> createDecorator() {
                if (isSummaryViewMode()) {
                    return new BoxReadOnlyFolderDecorator<PersonalIncome>() {
                        @Override
                        public void setComponent(CEntityFolder w) {
                            super.setComponent(w);
                            this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
                        }
                    };
                } else {
                    return new BoxFolderDecorator<PersonalIncome>(PortalImages.INSTANCE.add(), PortalImages.INSTANCE.addHover(),
                            i18n.tr("Add an income source"));
                }
            }

            @Override
            protected CEntityFolderItemEditor<PersonalIncome> createItem() {
                return new FinancialViewIncomeForm(summaryViewMode);
            }

        };
    }

    private CEntityFolder<PersonalAsset> createAssetFolderEditorColumns() {
        return new CEntityFolder<PersonalAsset>(PersonalAsset.class) {

            private List<EntityFolderColumnDescriptor> columns;
            {
                columns = new ArrayList<EntityFolderColumnDescriptor>();
                columns.add(new EntityFolderColumnDescriptor(proto().assetType(), "15em"));
                columns.add(new EntityFolderColumnDescriptor(proto().percent(), "7em"));
                columns.add(new EntityFolderColumnDescriptor(proto().assetValue(), "15em"));
            }

            @Override
            protected IFolderDecorator<PersonalAsset> createDecorator() {
                if (isSummaryViewMode()) {
                    return new BoxReadOnlyFolderDecorator<PersonalAsset>() {
                        @Override
                        public void setComponent(CEntityFolder w) {
                            super.setComponent(w);
                            this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
                        }
                    };
                } else {
                    return new TableFolderDecorator<PersonalAsset>(columns, PortalImages.INSTANCE.add(), PortalImages.INSTANCE.addHover(),
                            i18n.tr("Add an asset"));
                }
            }

            @Override
            protected CEntityFolderItemEditor<PersonalAsset> createItem() {
                return new CEntityFolderRowEditor<PersonalAsset>(PersonalAsset.class, columns) {

                    @Override
                    public IFolderItemDecorator<PersonalAsset> createDecorator() {
                        if (isSummaryViewMode()) {
                            return new BoxReadOnlyFolderItemDecorator<PersonalAsset>(false);
                        } else {
                            return new TableFolderItemDecorator<PersonalAsset>(PortalImages.INSTANCE.del(), PortalImages.INSTANCE.delHover(),
                                    i18n.tr("Remove asset"));
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

                        get(proto().assetType()).addValueChangeHandler(new ValueChangeHandler<PersonalAsset.AssetType>() {

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
            protected IFolderDecorator<TenantGuarantor> createDecorator() {
                if (isSummaryViewMode()) {
                    return new BoxReadOnlyFolderDecorator<TenantGuarantor>() {
                        @Override
                        public void setComponent(CEntityFolder w) {
                            super.setComponent(w);
                            this.getElement().getStyle().setPaddingLeft(1, Unit.EM);
                        }
                    };
                } else {
                    return new BoxFolderDecorator<TenantGuarantor>(PortalImages.INSTANCE.add(), PortalImages.INSTANCE.addHover(), i18n.tr("Add guarantor"));
                }
            }

            @Override
            protected CEntityFolderItemEditor<TenantGuarantor> createItem() {
                return createGuarantorRowEditor();
            }

            private CEntityFolderItemEditor<TenantGuarantor> createGuarantorRowEditor() {
                return new CEntityFolderItemEditor<TenantGuarantor>(TenantGuarantor.class) {

                    @Override
                    public IsWidget createContent() {
                        VistaDecoratorsFlowPanel main = new VistaDecoratorsFlowPanel(summaryViewMode);
                        if (isSummaryViewMode()) {
                            FlowPanel person = DecorationUtils.formFullName(this, proto());
                            person.getElement().getStyle().setFontWeight(FontWeight.BOLDER);
                            person.getElement().getStyle().setFontSize(1.1, Unit.EM);
                            main.add(person);
                        } else {
                            main.add(inject(proto().name().firstName()), 12);
                            main.add(inject(proto().name().middleName()), 12);
                            main.add(inject(proto().name().lastName()), 20);
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
                    public IFolderItemDecorator<TenantGuarantor> createDecorator() {
                        if (isSummaryViewMode()) {
                            return new BoxReadOnlyFolderItemDecorator<TenantGuarantor>(false);
                        } else {
                            return new BoxFolderItemDecorator<TenantGuarantor>(PortalImages.INSTANCE.del(), PortalImages.INSTANCE.delHover(),
                                    i18n.tr("Remove guarantor"));
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
