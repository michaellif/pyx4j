package com.pyx4j.workflow.attempt1.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pyx4j.workflow.attempt1.Condition;
import com.pyx4j.workflow.attempt1.Task;

public abstract class TaskImpl implements Task {
    protected TaskStatus status;

    private final List<Condition> condList;

    private final Map<Class<? extends ValueType>, Boolean> valueTypeMap;

    private final String name;

    public TaskImpl(String name) {
        this.name = name;
        condList = new ArrayList<Condition>();
        valueTypeMap = new HashMap<Class<? extends ValueType>, Boolean>();
        status = TaskStatus.New;
    }

    @Override
    public boolean isTrue() {
        return isTrue(ValueType.class);
    }

    @Override
    public boolean isTrue(Class<? extends ValueType> typeClass) {
        Boolean value = valueTypeMap.get(typeClass);
        return value == null ? false : value;
    }

    @Override
    public void setCondValue(Class<? extends ValueType> typeClass, boolean value) {
        valueTypeMap.put(typeClass, value);
    }

    @Override
    public void addCondition(Condition cond) {
        condList.add(cond);
    }

    @Override
    public List<Condition> getConditions() {
        return condList;
    }

    @Override
    public TaskStatus getStatus() {
        return status;
    }

    @Override
    public abstract void execute();

    public String getName() {
        return name;
    }
}
