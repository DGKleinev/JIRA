import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLinkManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.customfields.CustomFieldType

CustomFieldManager cfm = ComponentAccessor.getCustomFieldManager()
IssueLinkManager ilm = ComponentAccessor.getIssueLinkManager()
IssueManager im = ComponentAccessor.getIssueManager()

ApplicationUser gmt_user = Users.getByName("gmt_jirauser");
String id_parentLink = "customfield_10800"
CustomField parentLink = cfm.getCustomFieldObject( id_parentLink )
CustomFieldType parentLinkType = parentLink.getCustomFieldType()


final String jqlSearch = """issueLinkType = "Is parent of" AND issuetype = Initiative"""

Issues.search( jqlSearch ).each{ issue ->
    Set <IssueLink> links = new HashSet <IssueLink> ()
    List <IssueLink> src_parentLink = new ArrayList <IssueLink>()
    List <IssueLink> dst_parentLink = new ArrayList <IssueLink>()

    src_parentLink.addAll(ilm.getInwardLinks(issue.getId()).findAll{ it.getLinkTypeId() == 10300})
    dst_parentLink.addAll(ilm.getOutwardLinks(issue.getId()).findAll{ it.getLinkTypeId() == 10300})
   
    for( int i = 0; i < src_parentLink.size(); i++){
        if( src_parentLink.get(i) != null)
            links.add( src_parentLink.get(i) )
    }
    for( int i = 0; i < dst_parentLink.size(); i++){
        if( dst_parentLink.get(i) != null)
            links.add( dst_parentLink.get(i) )
    }

    for( IssueLink currentLink : links){
        MutableIssue childIssue = (currentLink.getSourceObject().getKey().equals(issue.getKey()) ? currentLink.getDestinationObject() : currentLink.getSourceObject()) as MutableIssue
        String value_parentLink = childIssue.getCustomFieldValue( parentLink )
        def newParentLink = issue

        if( value_parentLink == null){
            newParentLink = parentLinkType.getSingularObjectFromString((newParentLink as Issue).getKey())
            childIssue.setCustomFieldValue( parentLink, newParentLink )
            im.updateIssue( gmt_user, childIssue, EventDispatchOption.ISSUE_UPDATED, false)

        }
    }
}
