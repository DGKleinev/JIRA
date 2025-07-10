//Take a value from a Field and paste to another Field. This update will be done for issues that match the JQL filter
//This script was used for a Field of type User.

import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.IssueFieldConstants
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption


CustomFieldManager cfm = ComponentAccessor.getCustomFieldManager()
IssueManager im = ComponentAccessor.getIssueManager()

String id_fieldCopy = "customfield_00000"
String id_fieldPaste = "customfield_00001"

def cf_fieldCopy = cfm.getCustomFieldObject( id_fieldCopy )
def cf_fieldPaste = cfm.getCustomFieldObject( id_fieldPaste )

ApplicationUser gmt_user = Users.getByName("admin_user")

IssueFieldConstants constants = new IssueFieldConstants()

//You can set your filter here
final String jqlSearch = """project = JIRA AND "name_fieldCopy" is not EMPTY and "name_fieldPaste" is EMPTY  AND issuetype = issuetype"""

Issues.search( jqlSearch ).each{ issue ->
    MutableIssue currentIssue = im.getIssueByCurrentKey( issue.getKey() )

    def value_fieldCopy = issue.getCustomFieldValue( cf_fieldCopy )
    currentIssue.setCustomFieldValue( cf_fieldPaste, value_fieldCopy )
    im.updateIssue( gmt_user, currentIssue, EventDispatchOption.ISSUE_UPDATED, false)
}
