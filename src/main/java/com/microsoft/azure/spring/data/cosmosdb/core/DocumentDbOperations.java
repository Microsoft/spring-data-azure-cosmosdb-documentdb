/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.cosmosdb.core;

import com.microsoft.azure.cosmosdb.DocumentCollection;
import com.microsoft.azure.cosmosdb.PartitionKey;
import com.microsoft.azure.spring.data.cosmosdb.core.convert.MappingDocumentDbConverter;
import com.microsoft.azure.spring.data.cosmosdb.core.query.DocumentQuery;
import com.microsoft.azure.spring.data.cosmosdb.repository.support.DocumentDbEntityInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import rx.Observable;

import java.util.List;
import java.util.Optional;

public interface DocumentDbOperations {

    String getCollectionName(Class<?> entityClass);

    DocumentCollection createCollectionIfNotExists(DocumentDbEntityInformation information);

    <T> List<T> findAll(Class<T> entityClass);

    <T> List<T> findAll(String collectionName, Class<T> entityClass);

    <T> Optional<T> findById(String collectionName, Object id, Class<T> entityClass, PartitionKey key);

    <T> T insert(String collectionName, T objectToSave, PartitionKey partitionKey);

    <T> void upsert(String collectionName, T object, PartitionKey partitionKey);

    void deleteById(String collectionName, Object id, PartitionKey partitionKey);

    void deleteAll(String collectionName, List<String> partitionKeyNames);

    void deleteCollection(String collectionName);

    <T> List<T> delete(DocumentQuery query, Class<T> entityClass, String collectionName);

    <T> List<T> find(DocumentQuery query, Class<T> entityClass, String collectionName);

    <T> Boolean exists(DocumentQuery query, Class<T> entityClass, String collectionName);

    <T> Page<T> findAll(Pageable pageable, Class<T> domainClass, String collectionName);

    <T> Page<T> paginationQuery(DocumentQuery query, Class<T> domainClass, String collectionName);

    <T> Observable<Page<T>> paginationQueryAsync(DocumentQuery query, Class<T> domainClass, String collectionName);

    long count(String collectionName);

    <T> long count(DocumentQuery query, Class<T> domainClass, String collectionName);

    MappingDocumentDbConverter getConverter();

    <T> Observable<T> insertAsync(String collectionName, T domain, PartitionKey key);

    <T> Observable<T> upsertAsync(String collectionName, T domain, PartitionKey key);

    <T> Observable<T> findByIdAsync(String collectionName, Object id, Class<T> entityClass, PartitionKey key);

    Observable<Object> deleteByIdAsync(String collectionName, Object id, PartitionKey key);

    <T> Observable<T> deleteAllAsync(String collectionName, List<String> partitionKeyNames);

    <T> Observable<Page<T>> findAllAsync(Pageable pageable, Class<T> domainClass, String collectionName);

    Observable<Long> countAsync(String collectionName);

    <T> Observable<Long> countAsync(DocumentQuery query, Class<T> domainClass, String collectionName);
}
