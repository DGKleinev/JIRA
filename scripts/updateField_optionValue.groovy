//This script update a field that has options as values (RadioButton, ChechBox, Select List, ecc)

import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.customfields.manager.OptionsManager
import com.atlassian.jira.issue.customfields.option.Option

CustomFieldManager cfm = ComponentAccessor.getCustomFieldManager()
IssueManager im = ComponentAccessor.getIssueManager()
OptionsManager om = ComponentAccessor.getOptionsManager()

String id_selectListField = "customfield_11111"
def cf_selectListField = cfm.getCustomFieldObject( id_selectListField )
ApplicationUser gmt_user = Users.getByName("admin_user")
final String jqlSearch = """project = xyz AND type = issueType AND cf_selectListField is EMPTY """

def issues = Issues.search( jqlSearch )
if( !issues ) return

issues.each{ issue ->
    MutableIssue currentIssue = im.getIssueByCurrentKey(issue.getKey())
    def config = cf_selectListField.getRelevantConfig( currentIssue )
    def option = om.getOptions( config ).find{ it.toString() == "Operations & Technology" }

    if( option ){
        currentIssue.setCustomFieldValue( cf_selectListField, option )
        im.updateIssue( gmt_user, currentIssue, EventDispatchOption.ISSUE_UPDATED, false)
    }
}
