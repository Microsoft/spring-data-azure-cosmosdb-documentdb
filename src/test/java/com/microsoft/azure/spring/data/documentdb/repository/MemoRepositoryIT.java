/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.documentdb.repository;

import com.microsoft.azure.spring.data.documentdb.TestUtils;
import com.microsoft.azure.spring.data.documentdb.domain.Memo;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestRepositoryConfig.class)
public class MemoRepositoryIT {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    private static final String ID_1 = "id_1";
    private static final String MESSAGE_1 = "first message";
    private static final String DATE_STR_1 = "1/1/2000";

    private static final String ID_2 = "id_2";
    private static final String MESSAGE_2 = "second message";
    private static final String DATE_STR_2 = "1/1/2001";

    private static Date DATE_1;
    private static Date DATE_2;
    private static Memo TEST_MEMO_1;
    private static Memo TEST_MEMO_2;

    @Autowired
    MemoRepository repository;

    @BeforeClass
    public static void init() throws ParseException {
        DATE_1 = DATE_FORMAT.parse(DATE_STR_1);
        DATE_2 = DATE_FORMAT.parse(DATE_STR_2);
        TEST_MEMO_1 = new Memo(ID_1, MESSAGE_1, DATE_1);
        TEST_MEMO_2 = new Memo(ID_2, MESSAGE_2, DATE_2);
    }

    @Before
    public void setup() {
        repository.save(TEST_MEMO_1);
        repository.save(TEST_MEMO_2);
    }

    @After
    public void cleanup() {
        repository.deleteAll();
    }

    @Test
    public void testFindAll() {
        final List<Memo> result = TestUtils.toList(repository.findAll());

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void testFindByDate() throws ParseException {
        final List<Memo> result = repository.findMemoByDate(DATE_FORMAT.parse(DATE_STR_1));

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getId().equals(ID_1));
        assertThat(result.get(0).getMessage().equals(MESSAGE_1));
        assertThat(result.get(0).getDate().equals(DATE_1));
    }
}
