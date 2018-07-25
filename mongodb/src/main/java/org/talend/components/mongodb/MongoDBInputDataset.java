package org.talend.components.mongodb;

import lombok.Data;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Structure;
import org.talend.sdk.component.api.configuration.ui.widget.TextArea;
import org.talend.sdk.component.api.meta.Documentation;

import java.util.List;

@Data
@DataSet("query")
@GridLayout(value = { @GridLayout.Row("dataStore"), @GridLayout.Row("collection"), @GridLayout.Row("schema"),
        @GridLayout.Row("query"), @GridLayout.Row("limite") })
@Documentation("MongoDBInputDataset")
public class MongoDBInputDataset {

    @Option
    @Documentation("datastore")
    private MongoDBDataStore dataStore;

    @Option
    @Documentation("collection")
    private String collection;

    @Option
    @Structure
    @Documentation("schema")
    private List<String> schema;

    @Option
    @TextArea
    @Documentation("query")
    private String query;

    @Option
    @Documentation("limite")
    private int limite;

}
