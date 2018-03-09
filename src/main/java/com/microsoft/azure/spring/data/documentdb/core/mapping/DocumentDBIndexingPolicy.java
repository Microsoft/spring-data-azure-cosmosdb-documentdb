/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.core.mapping;

import com.microsoft.azure.documentdb.IndexingMode;
import org.springframework.data.annotation.Persistent;

import java.lang.annotation.*;

@Persistent
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DocumentDBIndexingPolicy {
    boolean automatic() default true;
    IndexingMode mode() default IndexingMode.Consistent;
}
