/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-11-28
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.datagrid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SelectionPresetModel {

    private final List<Object> presets;

    private final MultiSelectorState selectedState;

    private final Object selectedPreset;

    public static class MultiSelectorCellModelFactory {

        private final List<Object> presets;

        public MultiSelectorCellModelFactory(List<Object> presets) {
            this.presets = Collections.unmodifiableList(new ArrayList<Object>(presets));
        }

        public SelectionPresetModel makeAll() {
            return new SelectionPresetModel(MultiSelectorState.All, null, presets);
        }

        public SelectionPresetModel makeNone() {
            return new SelectionPresetModel(MultiSelectorState.None, null, presets);
        }

        public SelectionPresetModel makeSome() {
            return new SelectionPresetModel(MultiSelectorState.Some, null, presets);
        }

        public SelectionPresetModel makePreset(Object preset) {
            return new SelectionPresetModel(MultiSelectorState.Preset, preset, presets);
        }

        public SelectionPresetModel makeModel(MultiSelectorState state, Object preset) {
            return new SelectionPresetModel(state, state != MultiSelectorState.Preset ? null : preset, presets);
        }

    }

    private SelectionPresetModel(MultiSelectorState state, Object preset, List<Object> presets) {
        this.presets = presets;
        this.selectedState = state;
        this.selectedPreset = preset;
    }

    public MultiSelectorState getState() {
        return selectedState;
    }

    public Object getPreset() {
        return selectedPreset;
    }

    public List<Object> presets() {
        return presets;
    }

}
