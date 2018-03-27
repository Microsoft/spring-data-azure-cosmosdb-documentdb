/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.core.query;

import java.util.ArrayList;
import java.util.List;


public class Criteria {

    public enum CriteriaType {
        AND_CONDITION,
        OR_CONDITION,
        IS_EQUAL,
        IS_LESS_THAN,
        IS_LESS_THAN_OR_EQUAL,
        IS_GREATER_THAN,
        IS_GREATER_THAN_OR_EQUAL,
        BETWEEN,
        CONTAINING,
        ENDING_WITH,
        EXISTS,
        IS_EMPTY,
        IS_NULL,
        LIKE,
        NEAR,
        REGEX,
        STARTING_WITH,
        IN
    };
    
    private final CriteriaType criteriaType;
    private final List<Criteria> criteriaList = new ArrayList<>();

    private String conditionSubject;
    private List<Object> conditionValues;
    private boolean shouldIgnoreCase;
    private boolean negated;

    private Criteria(CriteriaType criteriaType) {
        this.criteriaType = criteriaType;
        this.shouldIgnoreCase = false;
        this.negated = false;
    }

    public static Criteria and(Criteria left, Criteria right) {

        final Criteria criteria = new Criteria(CriteriaType.AND_CONDITION);
        
        criteria.criteriaList.add(left);
        criteria.criteriaList.add(right);
        
        return criteria;
    }

    public static Criteria or(Criteria left, Criteria right) {

        final Criteria criteria = new Criteria(CriteriaType.OR_CONDITION);
        
        criteria.criteriaList.add(left);
        criteria.criteriaList.add(right);
        
        return criteria;
    }

    public static Criteria value(String conditionSubject, CriteriaType condition, List<Object> conditionValues) {
        return value(conditionSubject, condition, conditionValues, false, false);
    }

    public static Criteria value(String conditionSubject, CriteriaType condition,
            List<Object> conditionValues, boolean ignoreCase) {
        return value(conditionSubject, condition, conditionValues, ignoreCase, false);
    }
    
    public static Criteria value(String conditionSubject, CriteriaType condition,
            List<Object> conditionValues, boolean ignoreCase, boolean negated) {

        final Criteria criteria = new Criteria(condition);
        
        criteria.conditionSubject = conditionSubject;
        criteria.conditionValues = conditionValues;
        criteria.shouldIgnoreCase = ignoreCase;
        
        return criteria;
    }

    
    public List<Criteria> getCriteriaList() {
        return criteriaList;
    }
    
    public String getConditionSubject() {
        return conditionSubject;
    }

    public List<Object> getConditionValues() {
        return conditionValues;
    }

    public boolean shouldIgnoreCase() {
        return shouldIgnoreCase;
    }

    public CriteriaType getCriteriaType() {
        return criteriaType;
    }
    
    public boolean isNegated() {
        return negated;
    }

    public void accept(Visitor visitor) {
        
        visitor.visitCriteria(this);
    }
    
    public interface Visitor {
        public void visitCriteria(Criteria criteria);
    }
}

//public class Criteria implements CriteriaDefinition {
//
//    private String key;
//    private Object value;
//    private List<Criteria> criteriaChain;
//
//    public Criteria(String key) {
//        this.criteriaChain = new ArrayList<>();
//        this.criteriaChain.add(this);
//        this.key = key;
//    }
//
//    protected Criteria(List<Criteria> criteriaChain, String key) {
//        this.criteriaChain = criteriaChain;
//        this.criteriaChain.add(this);
//        this.key = key;
//    }
//
//    public Object getCriteriaObject() {
//        return value;
//    }
//
//    public String getKey() {
//        return key;
//    }
//
//    public static Criteria where(String key) {
//        return new Criteria(key);
//    }
//
//    public Criteria is(Object o) {
//        this.value = o;
//        return this;
//    }
//
//    public Criteria and(String key) {
//        return new Criteria(this.criteriaChain, key);
//    }
//
//    public List<Criteria> getCriteriaChain() {
//        return criteriaChain;
//    }
//}
