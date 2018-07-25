package org.talend.components.mongodb;

import com.mongodb.MongoClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MongoDBServiceTest {

    MongoDBDataStore datastore;

    @Before
    public void before() {
        datastore = new MongoDBDataStore();
        datastore.setServer("localhost");
        datastore.setPort(27017);
        datastore.setDatabase("testdb");
    }

    @Test
    public void testGetConnection() {
        // null Authentication
        MongoDBService service = new MongoDBService();
        MongoClient mongo = service.getConnection(datastore);
        Assert.assertNotNull(mongo);

    }

}
