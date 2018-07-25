package org.talend.components.mongodb;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.healthcheck.HealthCheck;
import org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus;

import java.util.logging.Logger;

import static org.talend.sdk.component.api.service.healthcheck.HealthCheckStatus.Status.OK;

@Slf4j
@Service
public class MongoDBService {

    MongoClient mongo;

    public MongoClient getConnection(final MongoDBDataStore datasetore) {
        // disable the log4j of mongodb
        Logger.getLogger("org.mongodb.driver").setLevel(java.util.logging.Level.OFF);

        MongoClientOptions clientOptions = datasetore.isUse_SSL()
                ? new MongoClientOptions.Builder().socketFactory(javax.net.ssl.SSLSocketFactory.getDefault()).build()
                : new MongoClientOptions.Builder().build();

        ServerAddress serverAddress = new ServerAddress(datasetore.getServer(), datasetore.getPort());

        if (datasetore.isAuthentication()) {
            MongoCredential mongoCredential = null;
            String username = datasetore.getUsername();
            String database = datasetore.getDatabase();
            char[] pass = datasetore.getPassword().toCharArray();
            switch (datasetore.getAuthentication_mechanism()) {
            case NEGOTIATE_MEC:
                mongoCredential = MongoCredential.createCredential(username, database, pass);
                break;
            case PLAIN_MEC:
                mongoCredential = MongoCredential.createPlainCredential(username, database, pass);
                break;
            case SCRAMSHA1_MEC:
                mongoCredential = MongoCredential.createScramSha1Credential(username, database, pass);
                break;
            // case KERBEROS_MEC:
            // // TODO impliment
            // System.setProperty("java.security.krb5.realm", "krbRealm");
            // System.setProperty("java.security.krb5.kdc", "krbKdc");
            // System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
            // mongoCredential = MongoCredential.createGSSAPICredential("krbUserPrincipal");
            // break;
            }
            mongo = new MongoClient(serverAddress, mongoCredential, clientOptions);
        } else {
            mongo = new MongoClient(serverAddress, clientOptions);
        }

        // TODO useReplicaSet

        mongo.addOption(Bytes.QUERYOPTION_NOTIMEOUT);
        // log.info("Connecting to " + mongo.getServerAddressList() + ".");

        return mongo;
    }

    public MongoCollection<Document> getCollection(final MongoDBInputDataset dataset) {
        MongoDatabase db = mongo.getDatabase(dataset.getDataStore().getDatabase());
        log.debug("Retrieving records from the datasource.");
        MongoCollection<Document> collection = db.getCollection(dataset.getCollection());
        return collection;
    }

    public void close() {
        if (mongo != null) {
            mongo.close();
        }
    }

    @HealthCheck("basic.healthcheck")
    public HealthCheckStatus testConnection(final MongoDBDataStore datasetore, final Messages i18n) {
        try {
            if (mongo == null) {
                mongo = this.getConnection(datasetore);
            } else {
                mongo.isLocked();
            }
        } catch (Exception e) {
            return new HealthCheckStatus(HealthCheckStatus.Status.KO, i18n.healthCheckFailed(e.getLocalizedMessage()));
        }

        return new HealthCheckStatus(OK, i18n.healthCheckOk());
    }
}
