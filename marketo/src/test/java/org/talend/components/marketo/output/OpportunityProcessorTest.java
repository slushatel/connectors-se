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

import static org.junit.Assert.*;
import static org.talend.components.marketo.MarketoApiConstants.ATTR_CODE;
import static org.talend.components.marketo.MarketoApiConstants.ATTR_DEDUPE_FIELDS;
import static org.talend.components.marketo.MarketoApiConstants.ATTR_EXTERNAL_OPPORTUNITY_ID;
import static org.talend.components.marketo.MarketoApiConstants.ATTR_LEAD_ID;
import static org.talend.components.marketo.MarketoApiConstants.ATTR_REASONS;
import static org.talend.components.marketo.MarketoApiConstants.ATTR_ROLE;
import static org.talend.components.marketo.MarketoApiConstants.ATTR_SEQ;

import javax.json.JsonObject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.talend.components.marketo.dataset.MarketoDataSet.MarketoEntity;
import org.talend.components.marketo.dataset.MarketoOutputDataSet.DeleteBy;
import org.talend.components.marketo.dataset.MarketoOutputDataSet.OutputAction;
import org.talend.components.marketo.dataset.MarketoOutputDataSet.SyncMethod;
import org.talend.sdk.component.junit.http.junit5.HttpApi;
import org.talend.sdk.component.junit5.WithComponents;

@HttpApi(useSsl = true)
@WithComponents("org.talend.components.marketo")
class OpportunityProcessorTest extends MarketoProcessorBaseTest {

    public static final String OPPORTUNITY_101 = "opportunity101";

    private JsonObject dataOR;

    private JsonObject dataNotExistOR;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        outputDataSet.setEntity(MarketoEntity.Opportunity);
        outputDataSet.setSyncMethod(SyncMethod.createOrUpdate);
        outputDataSet.setDedupeBy(ATTR_DEDUPE_FIELDS);
        outputDataSet.setDeleteBy(DeleteBy.dedupeFields);
        // create our opportunity
        data = jsonFactory.createObjectBuilder().add(ATTR_EXTERNAL_OPPORTUNITY_ID, OPPORTUNITY_101).build();
        outputDataSet.setAction(OutputAction.sync);
        initProcessor();
        processor.map(data, main -> assertEquals(0, main.getInt(ATTR_SEQ)), reject -> fail(FAIL_REJECT));
        // create our opportunityRole
        dataOR = jsonFactory.createObjectBuilder() //
                .add(ATTR_EXTERNAL_OPPORTUNITY_ID, OPPORTUNITY_101) //
                .add(ATTR_ROLE, "newCust") //
                .add(ATTR_LEAD_ID, 4) //
                .build();
        outputDataSet.setEntity(MarketoEntity.OpportunityRole);
        outputDataSet.setAction(OutputAction.sync);
        initProcessor();
        processor.map(dataOR, main -> assertEquals(0, main.getInt(ATTR_SEQ)), reject -> fail(FAIL_REJECT));
        //
        dataNotExist = jsonFactory.createObjectBuilder().add(ATTR_EXTERNAL_OPPORTUNITY_ID, "XxXOppportunityXxX").build();
        dataNotExistOR = jsonFactory.createObjectBuilder() //
                .add(ATTR_EXTERNAL_OPPORTUNITY_ID, "XxXOppportunityXxX").add(ATTR_ROLE, "newCust") //
                .add(ATTR_LEAD_ID, 4) //
                .build();
        //
        outputDataSet.setEntity(MarketoEntity.Opportunity);
    }

    private void initProcessor() {
        processor = new MarketoProcessor(outputDataSet, i18n, jsonFactory, jsonReader, jsonWriter, authorizationClient,
                leadClient, listClient, companyClient, customObjectClient, opportunityClient);
        processor.init();
    }

    @ParameterizedTest
    @ValueSource(strings = { "Opportunity", "OpportunityRole" })
    void testSyncOpportunity(String entity) {
        outputDataSet.setEntity(MarketoEntity.valueOf(entity));
        outputDataSet.setAction(OutputAction.sync);
        initProcessor();
        processor.map("Opportunity".equals(entity) ? data : dataOR, main -> assertEquals(0, main.getInt(ATTR_SEQ)),
                reject -> fail(FAIL_REJECT));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Opportunity", "OpportunityRole" })
    void testSyncOpportunityFail(String entity) {
        outputDataSet.setEntity(MarketoEntity.valueOf(entity));
        outputDataSet.setAction(OutputAction.sync);
        outputDataSet.setSyncMethod(SyncMethod.updateOnly);
        initProcessor();
        processor.map("Opportunity".equals(entity) ? dataNotExist : dataNotExistOR, main -> fail(FAIL_MAIN),
                reject -> assertEquals("1013", reject.getJsonArray(ATTR_REASONS).get(0).asJsonObject().getString(ATTR_CODE)));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Opportunity", "OpportunityRole" })
    void testDeleteOpportunity(String entity) {
        outputDataSet.setEntity(MarketoEntity.valueOf(entity));
        outputDataSet.setAction(OutputAction.delete);
        initProcessor();
        processor.map("Opportunity".equals(entity) ? data : dataOR, main -> assertEquals(0, main.getInt(ATTR_SEQ)),
                reject -> fail(FAIL_REJECT));
    }

    @ParameterizedTest
    @ValueSource(strings = { "Opportunity", "OpportunityRole" })
    void testDeleteOpportunityFail(String entity) {
        outputDataSet.setEntity(MarketoEntity.valueOf(entity));
        outputDataSet.setAction(OutputAction.delete);
        initProcessor();
        processor.map("Opportunity".equals(entity) ? dataNotExist : dataNotExistOR, main -> fail(FAIL_MAIN),
                reject -> assertEquals("1013", reject.getJsonArray(ATTR_REASONS).get(0).asJsonObject().getString(ATTR_CODE)));
    }

}
