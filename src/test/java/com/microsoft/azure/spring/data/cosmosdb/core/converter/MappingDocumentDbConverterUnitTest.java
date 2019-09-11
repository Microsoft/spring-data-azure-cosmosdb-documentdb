/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.cosmosdb.core.converter;

import com.azure.data.cosmos.CosmosItemProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.spring.data.cosmosdb.common.TestConstants;
import com.microsoft.azure.spring.data.cosmosdb.core.convert.MappingDocumentDbConverter;
import com.microsoft.azure.spring.data.cosmosdb.core.mapping.DocumentDbMappingContext;
import com.microsoft.azure.spring.data.cosmosdb.domain.Address;
import com.microsoft.azure.spring.data.cosmosdb.domain.Memo;
import com.microsoft.azure.spring.data.cosmosdb.domain.Importance;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class MappingDocumentDbConverterUnitTest {
    private static final SimpleDateFormat DATE = new SimpleDateFormat(TestConstants.DATE_FORMAT);
    private static final SimpleDateFormat TIMEZONE_DATE = new SimpleDateFormat(TestConstants.DATE_TIMEZONE_FORMAT);

    MappingDocumentDbConverter dbConverter;
    DocumentDbMappingContext mappingContext;
    ObjectMapper objectMapper;

    @Mock
    ApplicationContext applicationContext;

    @Before
    public void setup() {
        mappingContext = new DocumentDbMappingContext();
        objectMapper = new ObjectMapper();

        mappingContext.setApplicationContext(applicationContext);
        mappingContext.afterPropertiesSet();
        mappingContext.getPersistentEntity(Address.class);

        dbConverter = new MappingDocumentDbConverter(mappingContext, objectMapper);
    }

    @Test
    public void covertAddressToDocumentCorrectly() {
        final Address testAddress = new Address(TestConstants.POSTAL_CODE, TestConstants.CITY, TestConstants.STREET);
        final CosmosItemProperties cosmosItemProperties = dbConverter.writeCosmosItemProperties(testAddress);

        assertThat(cosmosItemProperties.id()).isEqualTo(testAddress.getPostalCode());
        assertThat(cosmosItemProperties.getString(TestConstants.PROPERTY_CITY)).isEqualTo(testAddress.getCity());
        assertThat(cosmosItemProperties.getString(TestConstants.PROPERTY_STREET)).isEqualTo(testAddress.getStreet());
    }

    @Test
    public void convertDocumentToAddressCorrectly() {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(TestConstants.PROPERTY_CITY, TestConstants.CITY);
        jsonObject.put(TestConstants.PROPERTY_STREET, TestConstants.STREET);

        final CosmosItemProperties cosmosItemProperties = new CosmosItemProperties(jsonObject.toString());
        cosmosItemProperties.id(TestConstants.POSTAL_CODE);

        final Address address = dbConverter.read(Address.class, cosmosItemProperties);

        assertThat(address.getPostalCode()).isEqualTo(TestConstants.POSTAL_CODE);
        assertThat(address.getCity()).isEqualTo(TestConstants.CITY);
        assertThat(address.getStreet()).isEqualTo(TestConstants.STREET);
    }

    @Test
    public void canWritePojoWithDateToDocument() throws ParseException {
        final Memo memo = new Memo(TestConstants.ID_1, TestConstants.MESSAGE, DATE.parse(TestConstants.DATE_STRING),
                Importance.NORMAL);
        final CosmosItemProperties cosmosItemProperties = dbConverter.writeCosmosItemProperties(memo);

        assertThat(cosmosItemProperties.id()).isEqualTo(memo.getId());
        assertThat(cosmosItemProperties.getString(TestConstants.PROPERTY_MESSAGE)).isEqualTo(memo.getMessage());
        assertThat(cosmosItemProperties.getLong(TestConstants.PROPERTY_DATE)).isEqualTo(memo.getDate().getTime());
    }

    @Test
    public void canReadPojoWithDateFromDocument() throws ParseException {
        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(TestConstants.PROPERTY_MESSAGE, TestConstants.MESSAGE);

        final long date = DATE.parse(TestConstants.DATE_STRING).getTime();
        jsonObject.put(TestConstants.PROPERTY_DATE, date);

        final CosmosItemProperties cosmosItemProperties = new CosmosItemProperties(jsonObject.toString());
        cosmosItemProperties.id(TestConstants.ID_1);

        final Memo memo = dbConverter.read(Memo.class, cosmosItemProperties);
        assertThat(cosmosItemProperties.id()).isEqualTo(memo.getId());
        assertThat(cosmosItemProperties.getString(TestConstants.PROPERTY_MESSAGE)).isEqualTo(TestConstants.MESSAGE);
        assertThat(cosmosItemProperties.getLong(TestConstants.PROPERTY_DATE)).isEqualTo(date);
    }

    @Test
    public void convertDateValueToMilliSeconds() throws ParseException {
        final Date date = TIMEZONE_DATE.parse(TestConstants.DATE_TIMEZONE_STRING);
        final long time = (Long) MappingDocumentDbConverter.toDocumentDBValue(date);

        assertThat(time).isEqualTo(TestConstants.MILLI_SECONDS);
    }
}

