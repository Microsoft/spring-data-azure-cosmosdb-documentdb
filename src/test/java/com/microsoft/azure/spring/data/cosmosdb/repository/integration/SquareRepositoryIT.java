/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.cosmosdb.repository.integration;

import com.microsoft.azure.spring.data.cosmosdb.common.TestUtils;
import com.microsoft.azure.spring.data.cosmosdb.domain.inheritance.Square;
import com.microsoft.azure.spring.data.cosmosdb.repository.repository.SquareRepository;
import com.microsoft.azure.spring.data.cosmosdb.repository.TestRepositoryConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestRepositoryConfig.class)
public class SquareRepositoryIT {
    @Autowired
    SquareRepository repository;

    private Square square1 = new Square("id_1", 1, 1);
    private Square square2 = new Square("id_2", 2, 4);

    @Before
    public void setup() {
        repository.save(square1);
        repository.save(square2);
    }

    @After
    public void cleanup() {
        repository.deleteAll();
    }

    @Test
    public void  testFindAll() {
        final List<Square> result = TestUtils.toList(repository.findAll());

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testSaveAsync() {
        this.repository.deleteAll();
        final Square square = new Square("id", 10, 12);

        this.repository.saveAsync(square).subscribe(a -> {
            assertThat(a).isEqualTo(square);
            assertThat(this.repository.findById(square.getId()).isPresent()).isTrue();
            assertThat(this.repository.findById(square.getId()).get()).isEqualTo(square);
        });

        square.setLength(203);

        this.repository.saveAsync(square).subscribe(a -> {
            assertThat(a).isEqualTo(square);
            assertThat(this.repository.findById(square.getId()).isPresent()).isTrue();
            assertThat(this.repository.findById(square.getId()).get()).isEqualTo(square);
        });
    }

    @Test
    public void testFindIncludeInheritedFields() {
        final Optional<Square> result = repository.findById(square1.getId());

        assertThat(result.get()).isNotNull();
        assertThat(result.get().getId()).isEqualTo(square1.getId());
        assertThat(result.get().getLength()).isEqualTo(square1.getLength());
        assertThat(result.get().getArea()).isEqualTo(square1.getArea());
    }
}
