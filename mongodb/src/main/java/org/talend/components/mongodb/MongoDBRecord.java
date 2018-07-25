package org.talend.components.mongodb;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.bson.Document;
import org.talend.sdk.component.api.service.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MongoDBRecord implements IndexedRecord {

    @Service
    private Messages i18n;

    Document document;

    List<String> fields;

    public MongoDBRecord(Document document, List<String> schema) {
        this.document = document;
        this.fields = schema;
        if (schema == null || schema.size() == 0) {
            this.fields = new ArrayList<String>();
            fields.addAll(document.keySet());
        } else {
            this.fields = schema;
        }
    }

    @Override
    public void put(int i, Object v) {
        throw new UnsupportedOperationException("MongoDBRecord does not support put operation");
    }

    @Override
    public Object get(int i) {
        return document.get(fields.get(i));
    }

    @Override
    public Schema getSchema() {

        final SchemaBuilder.FieldAssembler<Schema> mongoDOcument = SchemaBuilder.record("MongoDocument").fields();
        for (String field : fields) {
            mongoDOcument.name(field).type().stringType();
        }
        return mongoDOcument.endRecord();
    }
}
