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
package org.talend.components.marketo.dataset;

import lombok.Data;

import static org.talend.components.marketo.service.MarketoService.ACTIVITIES_LIST;

import java.util.List;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.action.Suggestable;
import org.talend.sdk.component.api.configuration.condition.ActiveIf;
import org.talend.sdk.component.api.configuration.constraint.Pattern;
import org.talend.sdk.component.api.configuration.type.DataSet;
import org.talend.sdk.component.api.configuration.ui.DefaultValue;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.configuration.ui.widget.Structure;
import org.talend.sdk.component.api.meta.Documentation;

@Data
@DataSet(MarketoInputDataSet.NAME)
@GridLayout({ //
        @GridLayout.Row({ "dataStore" }), //
        @GridLayout.Row({ "entity", "leadAction", "otherAction" }), //
        @GridLayout.Row({ "leadSelector", "leadKeyName", "leadKeyValues" }), //
        @GridLayout.Row({ "leadListIdOrName" }), //
        @GridLayout.Row({ "leadId", "leadIds", "assetIds", "listId" }), //
        @GridLayout.Row({ "customObjectName" }), //
        @GridLayout.Row({ "activityTypeIds" }), //
        @GridLayout.Row({ "filterType", "filterValues" }), //
        @GridLayout.Row({ "useCompoundKey", "compoundKey" }), //
        @GridLayout.Row({ "sinceDateTime" }), //
        @GridLayout.Row({ "fields" }), //
        @GridLayout.Row({ "batchSize" }) //
})
@Documentation("Marketo Source DataSet")
public class MarketoInputDataSet extends MarketoDataSet {

    public static final String NAME = "MarketoInputDataSet";

    public enum LeadAction {
        getLead,
        getMultipleLeads,
        getLeadActivity,
        getLeadChanges,
        describeLead
    }

    public enum LeadSelector {
        key,
        list
    }

    public enum ListAction {
        list,
        get,
        isMemberOf,
        getLeads
    }

    public enum OtherEntityAction {
        describe,
        list,
        get
    }

    /*
     * Lead DataSet parameters
     */
    @Option
    @DefaultValue(value = "getLead")
    @ActiveIf(target = "entity", value = { "Lead" })
    @Documentation("Lead Action")
    private LeadAction leadAction;

    @Option
    @ActiveIf(target = "entity", value = { "Lead" })
    @ActiveIf(target = "leadAction", value = "getLead")
    @Documentation("Lead Id")
    private Integer leadId;

    @Option
    @ActiveIf(target = "entity", value = { "Lead" })
    @ActiveIf(target = "leadAction", value = "getMultipleLeads")
    @Documentation("Lead Selector")
    private LeadSelector leadSelector;

    @Option
    @ActiveIf(target = "entity", value = { "Lead" })
    @ActiveIf(target = "leadAction", value = "getMultipleLeads")
    @ActiveIf(target = "leadSelector", value = "key")
    @Documentation("Key Name")
    private String leadKeyName;

    @Option
    @ActiveIf(target = "entity", value = { "Lead" })
    @ActiveIf(target = "leadAction", value = "getMultipleLeads")
    @ActiveIf(target = "leadSelector", value = "key")
    @Documentation("Values (comma separated)")
    private String leadKeyValues;

    // List access
    @Option
    @ActiveIf(target = "entity", value = { "Lead" })
    @ActiveIf(target = "leadAction", value = "getMultipleLeads")
    @ActiveIf(target = "leadSelector", value = "list")
    @Documentation("Lead List Id or Name")
    private String leadListIdOrName;

    /*
     * List Entity DataSet Parameters
     */

    @Option
    @DefaultValue(value = "list")
    @ActiveIf(target = "entity", value = { "List" })
    @Documentation("List Action")
    private ListAction listAction;

    @Option
    @ActiveIf(target = "entity", value = { "List" })
    @Documentation("List Name : Comma-separated list of static list names to return.")
    private String name;

    @Option
    @ActiveIf(target = "entity", value = { "List" })
    @Documentation("List ids : Comma-separated list of static list ids to return.")
    private String listIds;

    @Option
    @ActiveIf(target = "entity", value = { "List" })
    @Documentation("Program Name : Comma-separated list of program names.")
    private String programName;

    @Option
    @ActiveIf(target = "entity", value = { "List" })
    @Documentation("Workspace Name : Comma-separated list of workspace names.")
    private String workspaceName;

    // Changes & Activities
    @Option
    @Pattern("/^[a-zA-Z\\-]+$/")
    @ActiveIf(target = "entity", value = { "Lead" })
    @ActiveIf(target = "leadAction", value = { "getLeadChanges", "getLeadActivity" })
    @Documentation("Since Date Time")
    private String sinceDateTime;

    @Option
    @ActiveIf(target = "entity", value = { "Lead" })
    @ActiveIf(target = "leadAction", value = { "getLeadChanges", "getLeadActivity" })
    @Documentation("Lead Ids (comma separated list)")
    private String leadIds;

    @Option
    @ActiveIf(target = "entity", value = { "Lead" })
    @ActiveIf(target = "leadAction", value = { "getLeadChanges", "getLeadActivity" })
    @Documentation("Static List Id")
    private Integer listId;

    @Option
    @ActiveIf(target = "entity", value = { "Lead" })
    @ActiveIf(target = "leadAction", value = { "getLeadChanges", "getLeadActivity" })
    @Documentation("Asset Ids")
    private String assetIds;

    @Option
    @ActiveIf(target = "entity", value = { "Lead" })
    @ActiveIf(target = "leadAction", value = "getLeadActivity")
    @Suggestable(ACTIVITIES_LIST)
    @Structure
    @Documentation("Activity Type Ids")
    private List<String> activityTypeIds;

    /*
     * Other Entities DataSet parameters
     */

    @Option
    @DefaultValue(value = "describe")
    @ActiveIf(target = "entity", value = { "CustomObject", "Company", "Opportunity", "OpportunityRole" })
    @Documentation("Action")
    private OtherEntityAction otherAction;

    @Option
    @ActiveIf(target = "entity", value = { "CustomObject" })
    @ActiveIf(target = "otherAction", value = { "get", "describe" })
    @Documentation("Custom Object Name")
    private String customObjectName;

    @Option
    @ActiveIf(target = "entity", value = { "CustomObject", "Company", "Opportunity", "OpportunityRole" })
    @ActiveIf(target = "otherAction", value = { "get" })
    @Documentation("Filter Type")
    private String filterType;

    @Option
    @ActiveIf(target = "entity", value = { "CustomObject", "Company", "Opportunity", "OpportunityRole" })
    @ActiveIf(target = "otherAction", value = { "get" })
    @Documentation("Filter Values")
    private String filterValues;

    @Option
    @ActiveIf(target = "entity", value = { "CustomObject" })
    @ActiveIf(target = "otherAction", value = { "get" })
    @Documentation("Use Compound Key")
    private Boolean useCompoundKey;

    @Option
    @ActiveIf(target = "entity", value = { "CustomObject" })
    @ActiveIf(target = "otherAction", value = { "get" })
    @Documentation("Compound Key")
    // private Map<String, String> compoundKey;
    private Object compoundKey;

    @Option
    @Documentation("Fields")
    private String fields;

}
