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
package org.talend.components.marketo.input;

import static org.slf4j.LoggerFactory.getLogger;
import static org.talend.components.marketo.MarketoApiConstants.ATTR_FIELDS;
import static org.talend.components.marketo.MarketoApiConstants.ATTR_FILTER_TYPE;
import static org.talend.components.marketo.MarketoApiConstants.ATTR_INPUT;
import static org.talend.components.marketo.MarketoApiConstants.HEADER_CONTENT_TYPE_APPLICATION_JSON;
import static org.talend.components.marketo.MarketoApiConstants.REQUEST_PARAM_QUERY_METHOD_GET;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.json.JsonArray;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonReaderFactory;
import javax.json.JsonWriterFactory;

import org.apache.beam.sdk.repackaged.org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.talend.components.marketo.dataset.MarketoDataSet.MarketoEntity;
import org.talend.components.marketo.dataset.MarketoInputDataSet;
import org.talend.components.marketo.service.AuthorizationClient;
import org.talend.components.marketo.service.I18nMessage;
import org.talend.components.marketo.service.OpportunityClient;
import org.talend.sdk.component.api.configuration.Option;

public class OpportunitySource extends MarketoSource {

    private final OpportunityClient opportunityClient;

    private boolean isOpportunityRole;

    public OpportunitySource(@Option("configuration") final MarketoInputDataSet dataSet, //
            final I18nMessage i18n, //
            final JsonBuilderFactory jsonFactory, //
            final JsonReaderFactory jsonReader, //
            final JsonWriterFactory jsonWriter, //
            final AuthorizationClient authorizationClient, //
            final OpportunityClient opportunityClient) {
        super(dataSet, i18n, jsonFactory, jsonReader, jsonWriter, authorizationClient);
        this.opportunityClient = opportunityClient;
        this.opportunityClient.base(this.dataSet.getDataStore().getEndpoint());
        isOpportunityRole = MarketoEntity.OpportunityRole.equals(dataSet.getEntity());
    }

    @Override
    public JsonObject runAction() {
        switch (dataSet.getOtherAction()) {
        case describe:
            return describeOpportunity();
        case list:
        case get:
            return getOpportunity();
        }

        throw new RuntimeException(i18n.invalidOperation());
    }

    private transient static final Logger LOG = getLogger(OpportunitySource.class);

    private JsonObject getOpportunity() {
        String filterType = dataSet.getFilterType();
        String filterValues = dataSet.getFilterValues();
        String fields = dataSet.getFields();
        int batchSize = dataSet.getBatchSize();
        if (isOpportunityRole) {
            if (dataSet.getUseCompoundKey()) {

                JsonObject payload = generateCompoundKeyPayload(filterType, fields);

                return handleResponse(opportunityClient.getOpportunityRolesWithCompoundKey(HEADER_CONTENT_TYPE_APPLICATION_JSON,
                        REQUEST_PARAM_QUERY_METHOD_GET, accessToken, payload));
            } else {
                return handleResponse(opportunityClient.getOpportunityRoles(accessToken, filterType, filterValues, fields,
                        batchSize, nextPageToken));
            }
        } else {
            return handleResponse(
                    opportunityClient.getOpportunities(accessToken, filterType, filterValues, fields, batchSize, nextPageToken));
        }
    }

    private JsonObject describeOpportunity() {
        if (isOpportunityRole) {
            return handleResponse(opportunityClient.describeOpportunityRole(accessToken));
        } else {
            return handleResponse(opportunityClient.describeOpportunity(accessToken));
        }
    }
}
