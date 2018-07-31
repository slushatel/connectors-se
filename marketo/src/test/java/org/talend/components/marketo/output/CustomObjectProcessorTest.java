// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.marketo.output;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

import java.util.List;

import javax.json.JsonObject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.talend.components.marketo.dataset.MarketoDataSet.MarketoEntity;
import org.talend.components.marketo.dataset.MarketoOutputDataSet.DeleteBy;
import org.talend.components.marketo.dataset.MarketoOutputDataSet.OutputAction;
import org.talend.components.marketo.dataset.MarketoOutputDataSet.SyncMethod;
import org.talend.sdk.component.junit.JoinInputFactory;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.junit.http.junit5.HttpApi;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.runtime.output.Processor;

@HttpApi(useSsl = true)
@WithComponents("org.talend.components.marketo")
public class CustomObjectProcessorTest extends MarketoProcessorBaseTest {

    private static final String CUSTOMOBJECT_NAME = "car_c";

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        outputDataSet.setEntity(MarketoEntity.CustomObject);
        outputDataSet.setCustomObjectName(CUSTOMOBJECT_NAME);
        outputDataSet.setDedupeBy("dedupeFields");
        //
        data = jsonFactory.createObjectBuilder()//
                .add("customerid", "5") //
                .add("VIN", "ABC-DEF-12345-GIN") //
                .build();
        dataNotExist = jsonFactory.createObjectBuilder() //
                .add("customerId", "0") //
                .add("VIN", "ABC-DEF-12345-GIN") //
                .build();
        // we create a record
        outputDataSet.setAction(OutputAction.sync);
        outputDataSet.setSyncMethod(SyncMethod.createOrUpdate);
        initProcessor();
        processor.map(data, main -> {
        }, reject -> fail("Should not fail as createOrUpdate"));
    }

    private void initProcessor() {
        processor = new MarketoProcessor(outputDataSet, i18n, jsonFactory, jsonReader, jsonWriter, authorizationClient,
                leadClient, listClient, companyClient, customObjectClient, opportunityClient);
        processor.init();
    }

    @AfterEach
    void tearDown() {
        outputDataSet.setAction(OutputAction.delete);
        outputDataSet.setDeleteBy(DeleteBy.dedupeFields);
        initProcessor();
        processor.map(data, main -> {
        }, reject -> {
        });
    }

    @Test
    void testSyncCustomObject() {
        outputDataSet.setAction(OutputAction.sync);
        outputDataSet.setSyncMethod(SyncMethod.createOrUpdate);
        initProcessor();
        processor.map(data, main -> assertEquals(0, main.getInt("seq")), reject -> fail(FAIL_REJECT));
    }

    @Test
    void testDeleteCustomObject() {
        outputDataSet.setAction(OutputAction.delete);
        outputDataSet.setDeleteBy(DeleteBy.dedupeFields);
        initProcessor();
        processor.map(data, main -> assertEquals(0, main.getInt("seq")), reject -> fail(FAIL_REJECT));
    }

    @Test
    void testDeleteCustomObjectFail() {
        outputDataSet.setAction(OutputAction.delete);
        outputDataSet.setDeleteBy(DeleteBy.dedupeFields);
        initProcessor();
        processor.map(dataNotExist, main -> fail(FAIL_MAIN),
                reject -> assertEquals("1013", reject.getJsonArray("reasons").get(0).asJsonObject().getString("code")));
    }

    @Test
    void testSyncCustomObjectFail() {
        outputDataSet.setAction(OutputAction.sync);
        outputDataSet.setSyncMethod(SyncMethod.updateOnly);
        initProcessor();
        processor.map(dataNotExist, main -> fail(FAIL_MAIN),
                reject -> assertEquals("1013", reject.getJsonArray("reasons").get(0).asJsonObject().getString("code")));
    }

    @Test
    public void testSyncCustomObjectProcessor() {
        outputDataSet.setSyncMethod(SyncMethod.updateOnly);
        final Processor processor = component.createProcessor(MarketoProcessor.class, outputDataSet);
        final SimpleComponentRule.Outputs outputs = component.collect(processor,
                new JoinInputFactory().withInput("__default__", asList(data, data, dataNotExist)));
        assertEquals(2, outputs.size());
        List<JsonObject> mains = outputs.get(JsonObject.class, "__default__");
        assertEquals(2, mains.size());
        assertEquals(0, mains.get(0).getInt("seq"));
        List<JsonObject> rejects = outputs.get(JsonObject.class, "rejected");
        assertEquals(1, rejects.size());
        assertEquals(0, rejects.get(0).getInt("seq"));
        assertEquals("1013", rejects.get(0).getJsonArray("reasons").get(0).asJsonObject().getString("code"));
    }

}
