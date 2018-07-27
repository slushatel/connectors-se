package org.talend.components.azure.table.input;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.input.Producer;
import org.talend.sdk.component.api.meta.Documentation;

import org.talend.components.azure.service.AzureConnectionService;

@Documentation("TODO fill the documentation for this source")
public class InputTableSource implements Serializable {
    private final InputTableMapperConfiguration configuration;
    private final AzureConnectionService service;
    private final JsonBuilderFactory jsonBuilderFactory;
    private int size = 0;

    public InputTableSource(@Option("configuration") final InputTableMapperConfiguration configuration,
                        final AzureConnectionService service,
                        final JsonBuilderFactory jsonBuilderFactory) {
        this.configuration = configuration;
        this.service = service;
        this.jsonBuilderFactory = jsonBuilderFactory;
    }

    @PostConstruct
    public void init() {
        System.out.println("Init");
    }

    @Producer
    public JsonObject next() {
        if (size++ < 1) {
            return jsonBuilderFactory.createObjectBuilder().add("someId", "someValue").build();
        } else {
            // this is the method allowing you to go through the dataset associated
            // to the component configuration
            //
            // return null means the dataset has no more data to go through
            // you can use the jsonBuilderFactory to create new JsonObjects.
            return null;
        }
    }

    @PreDestroy
    public void release() {
        System.out.println("Release");
    }
}