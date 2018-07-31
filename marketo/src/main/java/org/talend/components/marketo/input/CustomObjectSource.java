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
import static org.talend.components.marketo.MarketoApiConstants.HEADER_CONTENT_TYPE_APPLICATION_JSON;
import static org.talend.components.marketo.MarketoApiConstants.REQUEST_PARAM_QUERY_METHOD_GET;

import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonReaderFactory;
import javax.json.JsonWriterFactory;

import org.slf4j.Logger;
import org.talend.components.marketo.dataset.MarketoInputDataSet;
import org.talend.components.marketo.service.AuthorizationClient;
import org.talend.components.marketo.service.CustomObjectClient;
import org.talend.components.marketo.service.I18nMessage;

public class CustomObjectSource extends MarketoSource {

    private final CustomObjectClient customObjectClient;

    public CustomObjectSource(MarketoInputDataSet dataSet, //
            final I18nMessage i18n, //
            final JsonBuilderFactory jsonFactory, //
            final JsonReaderFactory jsonReader, //
            final JsonWriterFactory jsonWriter, //
            final AuthorizationClient authorizationClient, //
            final CustomObjectClient customObjectClient) {
        super(dataSet, i18n, jsonFactory, jsonReader, jsonWriter, authorizationClient);
        this.customObjectClient = customObjectClient;
        this.customObjectClient.base(this.dataSet.getDataStore().getEndpoint());
    }

    @Override
    public JsonObject runAction() {
        switch (dataSet.getOtherAction()) {
        case describe:
            return describeCustomObjects();
        case list:
            return listCustomObjects();
        case get:
            return getCustomObjects();
        }
        throw new RuntimeException(i18n.invalidOperation());
    }

    private JsonObject listCustomObjects() {
        String names = dataSet.getFilterValues();
        return handleResponse(customObjectClient.listCustomObjects(accessToken, names));
    }

    private JsonObject describeCustomObjects() {
        String name = dataSet.getCustomObjectName();
        return handleResponse(customObjectClient.describeCustomObjects(accessToken, name));
    }

    private transient static final Logger LOG = getLogger(CustomObjectSource.class);

    private JsonObject getCustomObjects() {
        String name = dataSet.getCustomObjectName();
        String filterType = dataSet.getFilterType();
        String filterValues = dataSet.getFilterValues();
        String fields = dataSet.getFields();
        Integer batchSize = dataSet.getBatchSize();
        if (dataSet.getUseCompoundKey()) {
            JsonObject payload = generateCompoundKeyPayload(filterType, fields);
            return handleResponse(customObjectClient.getCustomObjectsWithCompoundKey(HEADER_CONTENT_TYPE_APPLICATION_JSON, name,
                    REQUEST_PARAM_QUERY_METHOD_GET, accessToken, nextPageToken, payload));

        } else {

            return handleResponse(customObjectClient.getCustomObjects(accessToken, name, filterType, filterValues, fields,
                    batchSize, nextPageToken));
        }
    }
}
