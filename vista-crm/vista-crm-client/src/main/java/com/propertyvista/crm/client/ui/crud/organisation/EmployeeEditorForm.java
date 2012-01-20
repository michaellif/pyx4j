/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.organisation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.CEntityHyperlink;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData;
import com.pyx4j.entity.client.ui.datatable.filter.DataTableFilterData.Operators;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.services.SelectEmployeeListService;
import com.propertyvista.crm.rpc.services.SelectPortfolioListService;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.person.Name;

public class EmployeeEditorForm extends CrmEntityForm<EmployeeDTO> {

    private static final I18n i18n = I18n.get(EmployeeEditorForm.class);

    private final VistaTabLayoutPanel tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);

    public EmployeeEditorForm() {
        this(false);
    }

    public EmployeeEditorForm(boolean viewMode) {
        super(EmployeeDTO.class, viewMode);
    }

    @Override
    public IsWidget createContent() {

        tabPanel.add(createGeneralTab(), i18n.tr("General"));
        tabPanel.add(createDetailsTab(), i18n.tr("Details"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    public IsWidget createGeneralTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().title()), 20).build());

        main.setBR(++row, 0, 1);

        if (isEditable()) {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().namePrefix()), 5).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().firstName()), 15).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().middleName()), 10).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().lastName()), 20).build());
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name().maidenName()), 20).build());
        } else {
            main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().name(), new CEntityLabel<Name>()), 25).customLabel(i18n.tr("Employee")).build());
            get(proto().name()).asWidget().getElement().getStyle().setFontWeight(FontWeight.BOLDER);
            get(proto().name()).asWidget().getElement().getStyle().setFontSize(1.1, Unit.EM);
        }

        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().sex()), 7).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().birthDate()), 9).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().homePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().mobilePhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().workPhone()), 15).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().email()), 25).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().description()), 50).build());

        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().enabled()), 5).build());
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().behaviors()), 20).build());

        return new CrmScrollPanel(main);
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    public IsWidget createDetailsTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setH1(++row, 0, 2, i18n.tr("Assigned Portfolios"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().accessAllBuildings()), 5).build());
        main.setWidget(++row, 0, inject(proto().portfolios(), new PortfolioFolder()));

        main.setH1(++row, 0, 2, i18n.tr("Managed Employees"));
        main.setWidget(++row, 0, inject(proto().employees(), new EmployeeFolder()));

        return new CrmScrollPanel(main);
    }

    private class PortfolioFolder extends VistaTableFolder<Portfolio> {

        public PortfolioFolder() {
            super(Portfolio.class, EmployeeEditorForm.this.isEditable());
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            return java.util.Arrays.asList(new EntityFolderColumnDescriptor(proto().name(), "15em"),

            new EntityFolderColumnDescriptor(proto().description(), "20em"));
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof Portfolio) {
                return new CEntityFolderRowEditor<Portfolio>(Portfolio.class, columns()) {
                    @Override
                    protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
                        CComponent<?, ?> comp = null;
                        if (proto().name() == column.getObject()) {
                            if (isEditable()) {
                                comp = inject(column.getObject(), new CLabel());
                            } else {
                                comp = new CHyperlink(new Command() {
                                    @Override
                                    public void execute() {
                                        AppSite.getPlaceController().goTo(
                                                AppSite.getHistoryMapper().createPlace(CrmSiteMap.Organization.Portfolio.class)
                                                        .formViewerPlace(getValue().id().getValue()));
                                    }
                                });
                                comp = inject(column.getObject(), comp);
                            }
                        } else if (proto().description() == column.getObject()) {
                            comp = inject(column.getObject(), new CLabel());
                        } else {
                            comp = super.createCell(column);
                        }
                        return comp;
                    }
                };
            } else {
                return super.create(member);
            }
        }

        @Override
        protected IFolderDecorator<Portfolio> createDecorator() {
            return new VistaTableFolderDecorator<Portfolio>(this, this.isEditable()) {
                {
                    setShowHeader(false);
                }
            };
        }

        @Override
        protected void addItem() {
            new PortfolioSelectorDialog(getValue()) {
                @Override
                public boolean onClickOk() {
                    for (Portfolio portfolio : getSelectedItems()) {
                        addItem(portfolio);
                    }
                    return true;
                }
            }.show();
        }
    }

    private class EmployeeFolder extends VistaTableFolder<Employee> {

        public EmployeeFolder() {
            super(Employee.class, EmployeeEditorForm.this.isEditable());
        }

        @Override
        public List<EntityFolderColumnDescriptor> columns() {
            ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
            columns.add(new EntityFolderColumnDescriptor(proto().name(), "20em"));
            columns.add(new EntityFolderColumnDescriptor(proto().title(), "20em"));
            return columns;
        }

        @Override
        public CComponent<?, ?> create(IObject<?> member) {
            if (member instanceof Employee) {
                return new CEntityFolderRowEditor<Employee>(Employee.class, columns()) {

                    @Override
                    protected CComponent<?, ?> createCell(EntityFolderColumnDescriptor column) {
                        CComponent<?, ?> comp = null;
                        if (proto().title() == column.getObject()) {
                            comp = inject(column.getObject(), new CLabel());
                        } else if (proto().name() == column.getObject()) {
                            if (isEditable()) {
                                comp = inject(column.getObject(), new CEntityLabel<Name>());
                            } else {
                                comp = inject(column.getObject(), new CEntityHyperlink<Name>(new Command() {
                                    @Override
                                    public void execute() {
                                        AppSite.getPlaceController().goTo(
                                                AppSite.getHistoryMapper().createPlace(CrmSiteMap.Organization.Employee.class)
                                                        .formViewerPlace(getValue().id().getValue()));
                                    }
                                }));
                            }
                        } else {
                            comp = inject(column.getObject(), new CLabel());
                        }

                        return comp;
                    }
                };
            } else {
                return super.create(member);
            }
        }

        @Override
        protected IFolderDecorator<Employee> createDecorator() {
            return new VistaTableFolderDecorator<Employee>(this, this.isEditable()) {
                {
                    setShowHeader(false);
                }
            };
        }

        @Override
        protected void addItem() {
            new EmployeeSelectorDialog(getValue()) {

                @Override
                protected void setPreDefinedFilters(java.util.List<DataTableFilterData> preDefinedFilters) {
                    // add restriction for papa/mama employee, so that he/she won't be able manage himself :)
                    // FIXME: somehow we need to forbid circular references. maybe only server side (if someone wants to be a smart ass)
                    preDefinedFilters.add(new DataTableFilterData(proto().id().getPath(), Operators.isNot, EmployeeEditorForm.this.getValue().id().getValue()));
                    super.setPreDefinedFilters(preDefinedFilters);
                };

                @Override
                public boolean onClickOk() {
                    for (Employee employee : getSelectedItems()) {
                        addItem(employee);
                    }
                    return true;
                }
            }.show();
        }
    };

    private abstract class PortfolioSelectorDialog extends EntitySelectorDialog<Portfolio> {

        public PortfolioSelectorDialog(List<Portfolio> alreadySelected) {
            super(Portfolio.class, true, alreadySelected, i18n.tr("Select Portfolio"));
        }

        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().name()).build(),
                    new MemberColumnDescriptor.Builder(proto().description()).wordWrap(true).build()                    
            ); //@formatter:on
        }

        @Override
        protected AbstractListService<Portfolio> getSelectService() {
            return GWT.<AbstractListService<Portfolio>> create(SelectPortfolioListService.class);
        }

        @Override
        protected String width() {
            return "700px";
        }

        @Override
        protected String height() {
            return "400px";
        }

    }

    private abstract class EmployeeSelectorDialog extends EntitySelectorDialog<Employee> {

        public EmployeeSelectorDialog(List<Employee> alreadySelected) {
            super(Employee.class, true, alreadySelected, i18n.tr("Select Employee"));
        }
        
        @Override
        protected List<ColumnDescriptor> defineColumnDescriptors() {
            return Arrays.asList(//@formatter:off                    
                    new MemberColumnDescriptor.Builder(proto().title()).build(),
                    new MemberColumnDescriptor.Builder(proto().name().namePrefix()).build(),
                    new MemberColumnDescriptor.Builder(proto().name().firstName()).build(),
                    new MemberColumnDescriptor.Builder(proto().name().lastName()).build(),
                    new MemberColumnDescriptor.Builder(proto().name().nameSuffix()).build()
            ); //@formatter:on
        }

        @Override
        protected AbstractListService<Employee> getSelectService() {
            return GWT.<AbstractListService<Employee>> create(SelectEmployeeListService.class);
        }

        @Override
        protected String width() {
            return "700px";
        }

        @Override
        protected String height() {
            return "400px";
        }
    }

}
