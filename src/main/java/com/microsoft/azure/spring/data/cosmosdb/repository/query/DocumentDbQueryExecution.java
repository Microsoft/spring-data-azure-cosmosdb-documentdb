/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.cosmosdb.repository.query;

import com.microsoft.azure.spring.data.cosmosdb.core.DocumentDbOperations;
import com.microsoft.azure.spring.data.cosmosdb.core.query.DocumentDbPageRequest;
import com.microsoft.azure.spring.data.cosmosdb.core.query.DocumentQuery;
import com.microsoft.azure.spring.data.cosmosdb.repository.support.DocumentDbEntityInformation;
import org.springframework.data.domain.Pageable;

public interface DocumentDbQueryExecution {
    Object execute(DocumentQuery query, Class<?> type, String collection);

    final class MultiEntityExecution implements DocumentDbQueryExecution {

        private final DocumentDbOperations operations;

        public MultiEntityExecution(DocumentDbOperations operations) {
            this.operations = operations;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object execute(DocumentQuery query, Class<?> type, String collection) {
            final DocumentDbEntityInformation information = new DocumentDbEntityInformation(type);

            return operations.find(query, collection, type, information.getPartitionKeyFieldName());
        }
    }

    final class ExistsExecution implements DocumentDbQueryExecution {

        private final DocumentDbOperations operations;

        public ExistsExecution(DocumentDbOperations operations) {
            this.operations = operations;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object execute(DocumentQuery query, Class<?> type, String collection) {
            final DocumentDbEntityInformation information = new DocumentDbEntityInformation(type);

            return operations.exists(query, collection, type, information.getPartitionKeyFieldName());
        }
    }

    final class DeleteExecution implements DocumentDbQueryExecution {

        private final DocumentDbOperations operations;

        public DeleteExecution(DocumentDbOperations operations) {
            this.operations = operations;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object execute(DocumentQuery query, Class<?> type, String collection) {
            final DocumentDbEntityInformation information = new DocumentDbEntityInformation(type);

            return operations.delete(query, collection, type, information.getPartitionKeyFieldName());
        }
    }

    final class PagedExecution implements DocumentDbQueryExecution {
        private final DocumentDbOperations operations;
        private final Pageable pageable;

        public PagedExecution(DocumentDbOperations operations, Pageable pageable) {
            this.operations = operations;
            this.pageable = pageable;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object execute(DocumentQuery query, Class<?> type, String collection) {
            if (pageable.getPageNumber() != 0 && !(pageable instanceof DocumentDbPageRequest)) {
                throw new IllegalStateException("Not the first page but Pageable is not a valid " +
                        "DocumentDbPageRequest, requestContinuation is required for non first page request");
            }

            query.with(pageable);

            final DocumentDbEntityInformation information = new DocumentDbEntityInformation(type);

            return operations.paginationQuery(query, collection, type, information.getPartitionKeyFieldName());
        }
    }
}
