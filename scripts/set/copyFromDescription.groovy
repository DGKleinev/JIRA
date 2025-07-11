//copy the Description and paste it on a Multi-line text Field

import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.IssueFieldConstants
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption


CustomFieldManager cfm = ComponentAccessor.getCustomFieldManager()
IssueManager im = ComponentAccessor.getIssueManager()

String id_textField = "customfield_11079"
def cf_textField = cfm.getCustomFieldObject( id_textField )
ApplicationUser gmt_user = Users.getByName("admin_user")

IssueFieldConstants constants = new IssueFieldConstants()

final String jqlSearch = """project = xyz AND description is not EMPTY and issuetype = issueType"""

Issues.search( jqlSearch ).each{ issue ->
    MutableIssue currentIssue = im.getIssueByCurrentKey(issue.getKey())

    def descr = issue.getDescription().toString()
    currentIssue.setCustomFieldValue( cf_textField, descr )
    im.updateIssue( gmt_user, currentIssue, EventDispatchOption.ISSUE_UPDATED, false)
}

