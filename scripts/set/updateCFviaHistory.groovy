//This script update a Multi-line Text Field if the field is empty. The copied value is taken from the History of the Issue, it takes the last modify from the History

import com.atlassian.jira.issue.changehistory.ChangeHistory
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.IssueFieldConstants
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager
import com.atlassian.jira.issue.changehistory.ChangeHistoryItem
import com.atlassian.jira.issue.history.ChangeItemBean


CustomFieldManager cfm = ComponentAccessor.getCustomFieldManager()
IssueManager im = ComponentAccessor.getIssueManager()
ChangeHistoryManager chm = ComponentAccessor.getChangeHistoryManager()

String id_textField = "customfield_11207"
def cf_textField = cfm.getCustomFieldObject( id_textField )
ApplicationUser gmt_user = Users.getByName("admin_user")

IssueFieldConstants constants = new IssueFieldConstants()

final String jqlSearch = """project in (xyz) and type = issueType"""
Issues.search( jqlSearch ).each{ issue ->
    MutableIssue currentIssue = im.getIssueByCurrentKey(issue.getKey())
    String val_textField = currentIssue.getCustomFieldValue( cf_textField )

    if( val_textField.equals("") || val_textField.equals("null") || val_textField.equals("-") || val_textField.equals(".") || val_textField.equals(null) ){
        List <ChangeHistory> changeHistories = new ArrayList <ChangeHistory> ()
        changeHistories.addAll(chm.getChangeHistoriesForUser(issue, gmt_user))

        for( ChangeHistory changeHistory : changeHistories) {
            List <ChangeItemBean> record = new ArrayList <ChangeItemBean> ()
            record.addAll( changeHistory.getChangeItemBeans() )

            for( ChangeItemBean row : record ){
                if( row.getField().equals("Esigenza")){
                    if(row.getToString().equals("") || row.getToString().equals("null") || row.getToString().equals("-") || row.getToString().equals(".") || row.getToString().equals(null) ){
                        if( !( row.getFromString().equals("null") || row.getFromString().equals(null) )){
                            log.warn("Values from ticket ${issue.getKey()}: ${row.getFromString()}")
                            currentIssue.setCustomFieldValue( cf_textField, row.getFromString() )
                            im.updateIssue( gmt_user, currentIssue, EventDispatchOption.ISSUE_UPDATED, false)
                        }
                    }
                }
            }
        }
    }
}
