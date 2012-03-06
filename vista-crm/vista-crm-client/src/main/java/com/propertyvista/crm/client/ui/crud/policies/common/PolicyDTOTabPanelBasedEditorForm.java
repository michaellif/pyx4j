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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.CPolymorphicEntityEditorTEMP;
import com.pyx4j.entity.client.ui.CEntityComboBox;
import com.pyx4j.entity.client.ui.CEntityLabel;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaTabLayoutPanel;
import com.propertyvista.crm.client.themes.CrmTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.decorations.CrmScrollPanel;
import com.propertyvista.domain.policy.framework.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.framework.PolicyDTOBase;
import com.propertyvista.domain.policy.framework.PolicyNode;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.ref.Province;

public abstract class PolicyDTOTabPanelBasedEditorForm<POLICY_DTO extends PolicyDTOBase> extends CrmEntityForm<POLICY_DTO> {

    private static final I18n i18n = I18n.get(PolicyDTOTabPanelBasedEditorForm.class);

    private VistaTabLayoutPanel tabPanel;

    private CComboBox<NodeType> selectPolicyScopeBox;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static final List<NodeType> AVAILABLE_NODE_TYPES = Arrays.asList(//@formatter:off
// reserved for future:
//                AptUnit.class,
//                Floorplan.class,
                new NodeType.Builder(Building.class).build(),
                new NodeType.Builder(Complex.class).build(),
                new NodeType.Builder(Province.class).build(),
//                new NodeType.Builder(Country.class).build(),
                new NodeType.Builder(OrganizationPoliciesNode.class).hasOnlyOneInstance().build()
    );//@formatter:on

    public PolicyDTOTabPanelBasedEditorForm(Class<POLICY_DTO> policyDTOClass, final boolean viewMode) {
        super(policyDTOClass, viewMode);
    }

    @Override
    public IsWidget createContent() {
        tabPanel = new VistaTabLayoutPanel(CrmTheme.defaultTabHeight, Unit.EM);
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

        selectPolicyScopeBox = new CComboBox<NodeType>();
        selectPolicyScopeBox.setEditable(isEditable());
        selectPolicyScopeBox.inheritViewable(false);
        selectPolicyScopeBox.setOptions(AVAILABLE_NODE_TYPES);
        selectPolicyScopeBox.setMandatory(true);
        // add value change handler that resets the node when node type is changed
        selectPolicyScopeBox.addValueChangeHandler(new ValueChangeHandler<NodeType>() {
            @Override
            public void onValueChange(ValueChangeEvent<NodeType> event) {
                if (event.getValue() != null) {
                    // disable node selector if it's a root node (Organization/PMC)
                    Class<? extends PolicyNode> selectedNodeType = event.getValue().getType();
                    boolean isOrganizationPoliciesNodeSelected = selectedNodeType.equals(OrganizationPoliciesNode.class);
                    get(proto().node()).setVisible(!isOrganizationPoliciesNodeSelected);

                    // if selected node was changed, populate the polymorphic node editor with the empty node of the correct type
                    if (isOrganizationPoliciesNodeSelected) {
                        getValue().node().set(EntityFactory.create(OrganizationPoliciesNode.class));
                    } else if (!selectedNodeType.equals(getValue().node().getInstanceValueClass())) {
                        get(proto().node()).populate(EntityFactory.create(selectedNodeType));
                    }
                } else {
                    get(proto().node()).setVisible(false);
                }
            }
        });

        content.setWidget(++row, 0, new DecoratorBuilder(selectPolicyScopeBox).customLabel(i18n.tr("Scope")).labelWidth(8).componentWidth(10).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().node(), new PolicyNodeEditor())).customLabel(i18n.tr("Applied to")).labelWidth(8)
                .componentWidth(15).build());
        if (isEditable()) {
            get(proto().node()).inheritViewable(false);
        }
        // register handler that propagates scope/node type to the scope box on form population
        addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals(PropertyChangeEvent.PropertyName.repopulated)) {
                    @SuppressWarnings({ "rawtypes" })
                    NodeType policyScope = null;
                    if (getValue() != null && !getValue().node().isNull()) {
                        @SuppressWarnings("unchecked")
                        Class<? extends PolicyNode> populatedType = (Class<? extends PolicyNode>) getValue().node().getInstanceValueClass();
                        for (@SuppressWarnings("rawtypes")
                        NodeType type : AVAILABLE_NODE_TYPES) {
                            if (type.getType().equals(populatedType)) {
                                policyScope = type;
                            }
                        }
                        if (policyScope == null) {
                            throw new Error("got unsupported or unknown policy scope:" + getValue().getInstanceValueClass().getName());
                        }
                    }

                    if (isNewEntity()) {
                        for (NodeType nodeType : AVAILABLE_NODE_TYPES) {
                            if (nodeType.getType().equals(OrganizationPoliciesNode.class)) {
                                policyScope = nodeType;
                                break;
                            }
                        }
                    }
                    selectPolicyScopeBox.setValue(policyScope, true);
                    selectPolicyScopeBox.setViewable(!isNewEntity());
                    get(proto().node()).setViewable(!isNewEntity());
                }
            }
        });

        return content;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();
        if (isNewEntity()) {
            get(proto().node()).setValue(EntityFactory.create(OrganizationPoliciesNode.class));
            get(proto().node()).populate(EntityFactory.create(OrganizationPoliciesNode.class));
        }
    }

    private boolean isNewEntity() {
        return getValue().getPrimaryKey() == null;
    }

    /**
     * A component that to choose the polymorphic PolicyNode
     * 
     * @author ArtyomB
     */
    private static class PolicyNodeEditor extends CPolymorphicEntityEditorTEMP<PolicyNode> {

        private Map<Class<? extends PolicyNode>, CComponent<?, ?>> nodeTypeToComponentMap;

        public PolicyNodeEditor() {
            super(PolicyNode.class);
        }

        @Override
        public void applyViewabilityRules() {
            super.applyViewabilityRules();
            if (getValue() != null) {
                Class<? extends PolicyNode> nodeType = (Class<? extends PolicyNode>) getValue().getInstanceValueClass();
                if (nodeTypeToComponentMap.containsKey(nodeType)) {
                    CComponent<PolicyNode, ?> comp = (CComponent<PolicyNode, ?>) nodeTypeToComponentMap.get(nodeType);
                    comp.setViewable(isViewable());
                }
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public IsWidget createContent() {
            FormFlexPanel content = new FormFlexPanel();

            nodeTypeToComponentMap = new HashMap<Class<? extends PolicyNode>, CComponent<?, ?>>();
            for (NodeType nodeType : AVAILABLE_NODE_TYPES) {
                if (!nodeType.hasOnlyOneInstance()) {
                    CComponent<? extends PolicyNode, ?> comp = null;
                    if (isEditable()) {
                        CEntityComboBox<? extends PolicyNode> comboBox = new CEntityComboBox(nodeType.getType());
                        comboBox.addValueChangeHandler(new ValueChangeHandler() {
                            @Override
                            public void onValueChange(ValueChangeEvent event) {
                                setValue((PolicyNode) event.getValue());
                            }
                        });
                        comp = comboBox;
                        comp.inheritViewable(false);
                    } else {
                        comp = new CEntityLabel();
                    }
                    comp.setVisible(false);
                    comp.setWidth("30em");
                    nodeTypeToComponentMap.put(nodeType.getType(), comp);
                }
            }

            int row = -1;
            for (CComponent<?, ?> nodeComponent : nodeTypeToComponentMap.values()) {
                content.setWidget(++row, 0, nodeComponent);
            }

            addPropertyChangeHandler(new PropertyChangeHandler() {
                @Override
                public void onPropertyChange(PropertyChangeEvent event) {
                    if (event.getPropertyName().equals(PropertyChangeEvent.PropertyName.repopulated)) {
                        for (CComponent<?, ?> nodeComponent : nodeTypeToComponentMap.values()) {
                            nodeComponent.setVisible(false);
                            nodeComponent.setMandatory(false);
                        }
                        Class<? extends PolicyNode> nodeType = (Class<? extends PolicyNode>) getValue().getInstanceValueClass();
                        if (nodeTypeToComponentMap.containsKey(nodeType)) {
                            CComponent<PolicyNode, ?> comp = (CComponent<PolicyNode, ?>) nodeTypeToComponentMap.get(nodeType);
                            comp.setVisible(true);
                            comp.setMandatory(true);
                            comp.setValue((PolicyNode) getValue().cast());
                        }
                    }
                }
            });

            return content;
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

    public static class NodeType<T extends PolicyNode> {

        private final String toString;

        private final Class<T> nodeType;

        private final boolean hasOnlyOneInstance;

        private NodeType(Class<T> nodeType, boolean hasOnlyOneInstance) {
            this.nodeType = nodeType;
            this.hasOnlyOneInstance = hasOnlyOneInstance;
            this.toString = EntityFactory.getEntityMeta(nodeType).getCaption();
        }

        @Override
        public String toString() {
            return toString;
        }

        public Class<T> getType() {
            return nodeType;
        }

        public boolean hasOnlyOneInstance() {
            return this.hasOnlyOneInstance;
        }

        public static class Builder<P extends PolicyNode> {

            private final Class<P> nodeType;

            private boolean hasOnlyOneInstance;

            public Builder(Class<P> nodeType) {
                this.nodeType = nodeType;
                this.hasOnlyOneInstance = false;
            }

            public Builder<P> hasOnlyOneInstance() {
                this.hasOnlyOneInstance = true;
                return this;
            }

            public Builder<P> hasManyInstances() {
                this.hasOnlyOneInstance = false;
                return this;
            }

            public NodeType<P> build() {
                return new NodeType<P>(nodeType, hasOnlyOneInstance);
            }
        }
    }
}
