package org.talend.components.mongodb;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MongDBInputEmitterTest {

    MongoDBInputDataset dataset;

    MongoDBInputEmitter emitter;

    MongoDBService service;

    @Before
    public void before() {
        MongoDBDataStore datastore = new MongoDBDataStore();
        datastore.setServer("localhost");
        datastore.setPort(27017);
        datastore.setDatabase("testdb");
        dataset = new MongoDBInputDataset();
        dataset.setDataStore(datastore);
        dataset.setQuery("{}");
        dataset.setCollection("personalstakesTEST651_12");
        service = new MongoDBService();
        emitter = new MongoDBInputEmitter(dataset, service);
    }

    @Test
    public void testInit() {
        emitter.init();
        // no exception indicate success
        Assert.assertTrue(true);
    }

    @Test
    public void testNext() {
        emitter.init();
        Assert.assertNotNull(emitter.next());
    }
}
