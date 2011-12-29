/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 28, 2011
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.numberofids;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.CEntityEditor;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.events.PropertyChangeEvent;
import com.pyx4j.forms.client.events.PropertyChangeHandler;
import com.pyx4j.forms.client.ui.CComboBox;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.client.ui.policy.PolicyFormFactory;
import com.propertyvista.domain.policy.OrganizationPoliciesNode;
import com.propertyvista.domain.policy.Policy;
import com.propertyvista.domain.policy.PolicyNode;
import com.propertyvista.domain.policy.dto.PolicyDTOBase;
import com.propertyvista.domain.property.asset.Complex;
import com.propertyvista.domain.property.asset.Floorplan;
import com.propertyvista.domain.property.asset.building.Building;
import com.propertyvista.domain.property.asset.unit.AptUnit;
import com.propertyvista.domain.ref.Country;
import com.propertyvista.domain.ref.Province;

public class PolicyDTOEditorForm<P extends Policy, POLICY_DTO extends PolicyDTOBase> extends CrmEntityForm<POLICY_DTO> {

    private static final I18n i18n = I18n.get(PolicyDTOEditorForm.class);

    private SimplePanel policyEditorFormPanel;

    private final Class<P> policyClass;

    private final Class<POLICY_DTO> policyDTOClass;

    @SuppressWarnings("unchecked")
    private static final List<NodeType<?>> NODE_TYPE_OPTIONS = Arrays.asList(//@formatter:off
                NodeType.wrap(AptUnit.class),
                NodeType.wrap(Floorplan.class),
                NodeType.wrap(Building.class),
                NodeType.wrap(Complex.class),
                NodeType.wrap(Province.class),
                NodeType.wrap(Country.class),
                NodeType.wrap(OrganizationPoliciesNode.class));//@formatter:on

    public PolicyDTOEditorForm(Class<P> policyClass, Class<POLICY_DTO> policyDTOClass, final IEditableComponentFactory factory) {
        super(policyDTOClass, factory);
        this.policyClass = policyClass;
        this.policyDTOClass = policyDTOClass;
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();
        int row = -1;

        final CComboBox<NodeType<?>> selectPolicyScopeBox = new CComboBox<PolicyDTOEditorForm.NodeType<?>>();
        selectPolicyScopeBox.setEditable(isEditable());
        selectPolicyScopeBox.setOptions(NODE_TYPE_OPTIONS);
        selectPolicyScopeBox.addValueChangeHandler(new ValueChangeHandler<NodeType<?>>() {
            @Override
            public void onValueChange(ValueChangeEvent<NodeType<?>> event) {
                Window.alert(event.getValue() == null ? "null" : event.getValue().toString());
            }
        });

        addPropertyChangeHandler(new PropertyChangeHandler() {
            @Override
            public void onPropertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName().equals(PropertyChangeEvent.PropertyName.repopulated)) {
                    NodeType<?> selectedPolicyScope = getValue() == null || getValue().node().isNull() ? null : NodeType
                            .wrap((Class<? extends PolicyNode>) getValue().node().getInstanceValueClass());
                    selectPolicyScopeBox.setValue(selectedPolicyScope, true);
                }
            }
        });

        content.setWidget(++row, 0, new DecoratorBuilder(selectPolicyScopeBox).customLabel(i18n.tr("Scope")).labelWidth(5).componentWidth(10).build());

        policyEditorFormPanel = new SimplePanel();
        policyEditorFormPanel.setSize("100%", "100%");
        content.setWidget(++row, 0, policyEditorFormPanel);
        return content;
    }

    @Override
    protected void onPopulate() {
        super.onPopulate();

        // create an editor for the specific policy class and propagate the value        
        final CEntityEditor<P> policyEditorForm = (CEntityEditor<P>) PolicyFormFactory.createPolicyEditorForm(policyClass, isEditable());

        adopt(policyEditorForm); // TODO review this: i'm not sure what it does, something with validation
        policyEditorForm.initContent();
        policyEditorForm.populate(getValue().duplicate(policyClass));
        policyEditorForm.addValueChangeHandler(new ValueChangeHandler<P>() {
            @Override
            public void onValueChange(ValueChangeEvent<P> event) {
                // that's really cruel but I don't see another way to do it
                for (String memberName : event.getValue().getEntityMeta().getMemberNames()) {
                    getValue().setMemberValue(memberName, event.getValue().getMemberValue(memberName));
                }
                setValue(getValue(), true); // just to fire the event;
            }
        });

        policyEditorFormPanel.clear();
        policyEditorFormPanel.setWidget(policyEditorForm);
    }

    private static class NodeType<T extends PolicyNode> {
        private final String toString;

        private final Class<T> nodeType;

        public static <TYPE extends PolicyNode> NodeType<TYPE> wrap(Class<TYPE> nodeType) {
            return new NodeType<TYPE>(nodeType);
        }

        private NodeType(Class<T> nodeType) {
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
}
