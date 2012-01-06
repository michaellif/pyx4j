/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 4, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.common;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CButton;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.activity.crud.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.IListerView;
import com.pyx4j.site.client.ui.crud.lister.ListerBase.ItemSelectionHandler;
import com.pyx4j.site.client.ui.crud.lister.ListerInternalViewImplBase;
import com.pyx4j.widgets.client.dialog.OkCancelDialog;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.VistaCrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.crud.building.SelectedBuildingLister;
import com.propertyvista.crm.client.ui.crud.unit.SelectedUnitLister;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.services.SelectBuildingCrudService;
import com.propertyvista.crm.rpc.services.SelectUnitCrudService;
import com.propertyvista.domain.policy.DefaultPoliciesNode;
import com.propertyvista.domain.policy.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.PolicyNode;
import com.propertyvista.domain.policy.dto.PolicyDTOBase;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

public abstract class PolicyDTOTabPanelBasedEditorForm<POLICY_DTO extends PolicyDTOBase> extends CrmEntityForm<POLICY_DTO> {

    private static final I18n i18n = I18n.get(PolicyDTOEditorForm.class);

    private VistaTabLayoutPanel tabPanel;

    @SuppressWarnings("unchecked")
    private static final List<NodeTypeWrapper<?>> NODE_TYPE_OPTIONS = Arrays.asList(//@formatter:off
// reserved for future:            
//                NodeTypeWrapper.wrap(AptUnit.class),
//                NodeTypeWrapper.wrap(Floorplan.class),
                NodeTypeWrapper.wrap(Building.class),
                NodeTypeWrapper.wrap(Complex.class),
                NodeTypeWrapper.wrap(Province.class),
                NodeTypeWrapper.wrap(Country.class),
                NodeTypeWrapper.wrap(OrganizationPoliciesNode.class));//@formatter:on

    public PolicyDTOTabPanelBasedEditorForm(Class<POLICY_DTO> policyDTOClass, final IEditableComponentFactory factory) {
        super(policyDTOClass, factory);
    }

    @Override
    public IsWidget createContent() {
        tabPanel = new VistaTabLayoutPanel(VistaCrmTheme.defaultTabHeight, Unit.EM);
        tabPanel.setSize("100%", "100%");

        tabPanel.add(new CrmScrollPanel(createScopeTab()), i18n.tr("Scope"));

        for (TabDescriptor d : createCustomTabPanels()) {
            tabPanel.add(new CrmScrollPanel(d.widget), d.caption);
        }

        return tabPanel;
    }

    @Override
    public void setActiveTab(int index) {
        tabPanel.selectTab(index);
    }

    @Override
    public int getActiveTab() {
        return tabPanel.getSelectedIndex();
    }

    protected abstract List<TabDescriptor> createCustomTabPanels();

    private Widget createScopeTab() {
        FormFlexPanel content = new FormFlexPanel();
        content.setHeight("3em");
        int row = -1;

        final CComboBox<NodeTypeWrapper<?>> selectPolicyScopeBox = new CComboBox<NodeTypeWrapper<?>>();
        selectPolicyScopeBox.setEditable(isEditable());
        selectPolicyScopeBox.setOptions(NODE_TYPE_OPTIONS);
        selectPolicyScopeBox.setMandatory(true);
        selectPolicyScopeBox.addValueChangeHandler(new ValueChangeHandler<NodeTypeWrapper<?>>() {
            @Override
            public void onValueChange(ValueChangeEvent<NodeTypeWrapper<?>> event) {
                if (event.getValue() != null) {
                    get(proto().node()).setVisible(true);

                    if (!event.getValue().getType().equals(getValue().node().getInstanceValueClass())) {
                        get(proto().node()).populate(EntityFactory.create(event.getValue().getType()));
                    }
                } else {
                    get(proto().node()).setVisible(false);
                }
            }
        });

        content.setWidget(++row, 0, new DecoratorBuilder(selectPolicyScopeBox).customLabel(i18n.tr("Scope")).labelWidth(8).componentWidth(10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().node(), new PolicyNodeEditor())).customLabel(i18n.tr("Applied to")).labelWidth(8)
                .componentWidth(15).build());

        // register handler that propagates scope/node type to the scope box on form population
        addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals(PropertyChangeEvent.PropertyName.repopulated)) {
                    @SuppressWarnings("unchecked")
                    NodeTypeWrapper<?> policyScope = getValue() == null || getValue().node().isNull() ? null : NodeTypeWrapper
                            .wrap((Class<? extends PolicyNode>) getValue().node().getInstanceValueClass());

                    if (policyScope == null || policyScope.getType().equals(DefaultPoliciesNode.class)) {
                        selectPolicyScopeBox.setValue(null, true);
                    } else {
                        selectPolicyScopeBox.setValue(policyScope, true);
                    }
                }
            }
        });

        return content;
    }

    /**
     * A component that just shows the string view of a node and in edit mode and allows to choose the node via dialog that shows a list of nodes.
     * 
     * @author ArtyomB
     */
    private class PolicyNodeEditor extends CEntityEditor<PolicyNode> {

        public PolicyNodeEditor() {
            super(PolicyNode.class);
        }

        @Override
        public IsWidget createContent() {
            FormFlexPanel content = new FormFlexPanel();

            int col = -1;
            if (isEditable()) {
                content.setWidget(0, ++col, new CButton(i18n.tr("Select") + "...", new Command() {
                    @Override
                    public void execute() {
                        OkCancelDialog selectScopeDialog = selectScopeDialogOf(getValue().getInstanceValueClass());
                        if (selectScopeDialog != null) {
                            selectScopeDialog.show();
                        } else {
                            Window.alert("NOT IMPLEMENTED YET: here be node selection dialog for node type: " + (getValue().getInstanceValueClass().getName()));
                        }
                    }
                }));

                content.getFlexCellFormatter().getElement(0, col).getStyle().setPaddingRight(2d, Unit.EM);
            }

            final CLabel scopeRepresentation = new CLabel();
            scopeRepresentation.setWidth("20em");
            content.setWidget(0, ++col, scopeRepresentation);

            // TODO not sure why I use both property change handler and value change handler, value change supposed to be enough            
            addPropertyChangeHandler(new PropertyChangeHandler() {
                @Override
                public void onPropertyChange(PropertyChangeEvent event) {
                    if (event.getPropertyName().equals(PropertyChangeEvent.PropertyName.repopulated)) {
                        scopeRepresentation.setValue(!getValue().isEmpty() ? getValue().cast().getStringView() : i18n.tr("Nothing selected"));
                    }
                }
            });

            addValueChangeHandler(new ValueChangeHandler<PolicyNode>() {
                @Override
                public void onValueChange(ValueChangeEvent<PolicyNode> event) {
                    scopeRepresentation.setValue(!event.getValue().isEmpty() ? event.getValue().cast().getStringView() : i18n.tr("Nothing selected"));
                }
            });

            return content;
        }

        private <E extends IEntity> OkCancelDialog selectScopeDialogOf(Class<E> policyNodeClass) {
            PolicyNodeSetter policyNodeSetter = new PolicyNodeSetter() {
                @Override
                public void setPolicyNode(PolicyNode policyNode) {
                    PolicyNodeEditor.this.setValue(policyNode);
                }
            };

            if (AptUnit.class.equals(policyNodeClass)) {
                return new AptUnitSelectDialog(policyNodeSetter);
            } else if (Building.class.equals(policyNodeClass)) {
                return new BuildingSelectDialog(policyNodeSetter);
            }
            return null;
        }
    }

    public static class TabDescriptor {

        public final Widget widget;

        public final String caption;

        public TabDescriptor(Widget widget, String caption) {
            this.widget = widget;
            this.caption = caption;
        }
    }

    private static class NodeTypeWrapper<T extends PolicyNode> {

        private final String toString;

        private final Class<T> nodeType;

        public static <TYPE extends PolicyNode> NodeTypeWrapper<TYPE> wrap(Class<TYPE> nodeType) {
            return new NodeTypeWrapper<TYPE>(nodeType);
        }

        private NodeTypeWrapper(Class<T> nodeType) {
            this.nodeType = nodeType;
            this.toString = EntityFactory.getEntityMeta(nodeType).getCaption();
        }

        @Override
        public String toString() {
            return toString;
        }

        public Class<T> getType() {
            return nodeType;
        }
    }

    private interface PolicyNodeSetter {
        void setPolicyNode(PolicyNode policyNode);
    }

    private abstract static class ListerBasedSelectDialog<E extends PolicyNode> extends OkCancelDialog {

        private final PolicyNodeSetter policyNodeSetter;

        private final DeckLayoutPanel containerPanel;

        public ListerBasedSelectDialog(Class<E> clazz, PolicyNodeSetter policyNodeSetter) {
            super(i18n.tr("Select {0}...", EntityFactory.getEntityMeta(clazz).getCaption()));
            this.policyNodeSetter = policyNodeSetter;
            this.containerPanel = new DeckLayoutPanel();
            this.containerPanel.setSize("800px", "600px");
            setBody(containerPanel);
        }

        @Override
        public boolean onClickOk() {
            PolicyNode policyNode = getSelectedItem();
            if (policyNode != null) {
                policyNodeSetter.setPolicyNode(getSelectedItem());
                return true;
            } else {
                return false;
            }
        }

        protected DeckLayoutPanel getContainerPanel() {
            return containerPanel;
        }

        protected abstract E getSelectedItem();
    }

    private static class AptUnitSelectDialog extends ListerBasedSelectDialog<AptUnit> {

        private final IListerView<AptUnit> unitListerView;

        // FIXME this class is not finished yet!
        public AptUnitSelectDialog(PolicyNodeSetter policyNodeSetter) {
            super(AptUnit.class, policyNodeSetter);

            unitListerView = new ListerInternalViewImplBase<AptUnit>(new SelectedUnitLister());
            @SuppressWarnings("unchecked")
            final ListerActivityBase<AptUnit> unitListerActivity = new ListerActivityBase<AptUnit>(new CrmSiteMap.Settings.Policies(), unitListerView,
                    (AbstractCrudService<AptUnit>) GWT.create(SelectUnitCrudService.class), AptUnit.class);
            unitListerActivity.start(getContainerPanel(), new SimpleEventBus());

            final IListerView<Building> buildingListerView = new ListerInternalViewImplBase<Building>(new SelectedBuildingLister());
            @SuppressWarnings("unchecked")
            final ListerActivityBase<Building> buildingListerActivity = new ListerActivityBase<Building>(new CrmSiteMap.Settings.Policies(),
                    buildingListerView, (AbstractCrudService<Building>) GWT.create(SelectBuildingCrudService.class), Building.class);
            buildingListerActivity.start(getContainerPanel(), new SimpleEventBus());

            buildingListerView.getLister().addItemSelectionHandler(new ItemSelectionHandler<Building>() {

                @Override
                public void onSelect(Building selectedItem) {
                    //                     
                }
            });
            getContainerPanel().setSize("800px", "300px");
        }

        @Override
        protected AptUnit getSelectedItem() {
            return unitListerView.getLister().getSelectedItem();
        }
    }

    private static class BuildingSelectDialog extends ListerBasedSelectDialog<Building> {

        private final IListerView<Building> buildingListerView;

        public BuildingSelectDialog(PolicyNodeSetter policyNodeSetter) {
            super(Building.class, policyNodeSetter);

            buildingListerView = new ListerInternalViewImplBase<Building>(new SelectedBuildingLister());
            @SuppressWarnings("unchecked")
            final ListerActivityBase<Building> buildingListerActivity = new ListerActivityBase<Building>(new CrmSiteMap.Settings.Policies(),
                    buildingListerView, (AbstractCrudService<Building>) GWT.create(SelectBuildingCrudService.class), Building.class);
            buildingListerActivity.start(getContainerPanel(), new SimpleEventBus());
        }

        @Override
        protected Building getSelectedItem() {
            return buildingListerView.getLister().getSelectedItem();
        }
    }
}
