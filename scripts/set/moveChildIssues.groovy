import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.bc.issue.IssueService
import com.atlassian.jira.issue.IssueInputParametersImpl
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.link.IssueLink
import com.atlassian.jira.issue.link.IssueLinkManager

IssueManager iManager = ComponentAccessor.getIssueManager()
IssueService iService = ComponentAccessor.getIssueService()
UserManager uManager = ComponentAccessor.getUserManager()
IssueLinkManager ilManager = ComponentAccessor.getIssueLinkManager()

Issue currentIssue = issue //iManager.getIssueObject("AWP-1")
ApplicationUser gmt_user = uManager.getUserByName("gmt_jirauser")
List <IssueLink> inwardLinks = new ArrayList <IssueLink> ()
inwardLinks.addAll( ilManager.getInwardLinks(currentIssue.id) )
int transition_toLive = 171
//int status_Live = 7

for( IssueLink link : inwardLinks){

    if(link.getIssueLinkType().getInward().equals("Is parent of")){
        long issueChildID = link.getSourceId()
        Issue issueChild = iManager.getIssueObject(issueChildID)
        String issueType = issueChild.getIssueType().getName()
        String status = issueChild.getStatus().getName()
        
        if(issueType.equals("CR") && !status.equals("Live")){
			def transitionValidationResult = iService.validateTransition(gmt_user, issueChildID, transition_toLive, new IssueInputParametersImpl())
            
            if(transitionValidationResult){
            	iService.transition(gmt_user, transitionValidationResult)
                log.warn("issue mossa: " + issueChild.key)
            }
        }   
    }   
}
