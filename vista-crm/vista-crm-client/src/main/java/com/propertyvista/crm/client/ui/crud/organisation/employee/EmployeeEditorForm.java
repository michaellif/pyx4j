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
package com.propertyvista.crm.client.ui.crud.organisation.employee;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.Key;
import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.client.ui.datatable.ColumnDescriptor;
import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.rpc.AbstractListService;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CHyperlink;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.crud.lister.EntitySelectorDialog;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.organisation.employee.EmployeeFolder.ParentEmployeeGetter;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.company.EmployeeDTO;
import com.propertyvista.crm.rpc.services.SelectPortfolioListService;
import com.propertyvista.domain.company.Portfolio;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.security.VistaCrmBehavior;

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

        tabPanel.add(createInfoTab(), i18n.tr("Personal Information"));
        if (isEditable()) {
            tabPanel.add(createPasswordTab(), i18n.tr("Password"));
        }
        tabPanel.add(createPriviligesTab(), i18n.tr("Priviliges"));

        tabPanel.setDisableMode(isEditable());
        tabPanel.setSize("100%", "100%");
        return tabPanel;
    }

    public IsWidget createInfoTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().title()), 20).build());

        main.setBR(++row, 0, 1);

        if (SecurityController.checkBehavior(VistaCrmBehavior.Organization) & isEditable()) {
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

        boolean isViewable = !SecurityController.checkBehavior(VistaCrmBehavior.Organization);
        if (isEditable()) {
            get(proto().sex()).setViewable(isViewable);
            get(proto().birthDate()).setViewable(isViewable);
            get(proto().homePhone()).setViewable(isViewable);
            get(proto().mobilePhone()).setViewable(isViewable);
            get(proto().workPhone()).setViewable(isViewable);
            get(proto().email()).setViewable(isViewable);
            get(proto().description()).setViewable(isViewable);
        }

        return new CrmScrollPanel(main);
    }

    public IsWidget createPasswordTab() {
        FormFlexPanel main = new FormFlexPanel();
        main.setWidget(0, 0, new Button(i18n.tr("Change Password"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        }));
        return main;
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    public IsWidget createPriviligesTab() {
        FormFlexPanel main = new FormFlexPanel();

        int row = -1;
        main.setBR(++row, 0, 1);
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().enabled()), 5).build());

        main.setH1(++row, 0, 2, i18n.tr("Roles"));
        main.setWidget(++row, 0, inject(proto().roles(), new CrmRoleFolder(isEditable())));

        main.setH1(++row, 0, 1, i18n.tr("Portfolios"));
        main.setWidget(++row, 0, new DecoratorBuilder(inject(proto().accessAllBuildings()), 5).build());
        main.setWidget(++row, 0, inject(proto().portfolios(), new PortfolioFolder()));

        main.setH1(++row, 0, 1, i18n.tr("Subordinates"));
        main.setWidget(++row, 0, inject(proto().employees(), new EmployeeFolder(isEditable(), new ParentEmployeeGetter() {
            @Override
            public Key getParentId() {
                return getValue() != null ? getValue().getPrimaryKey() : null;
            }
        })));

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

}
