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

Issue currentIssue = issue
ApplicationUser jiramaintenance = uManager.getUserByName("devjira_cm")
List <IssueLink> outwardLinks = new ArrayList  ()
outwardLinks.addAll( ilManager.getOutwardLinks(currentIssue.id) )
List <String> blueStatuses = new ArrayList  (Arrays.asList("In Progress"))
int transition_toOnHold = 91



for( IssueLink link : outwardLinks){
  
    if(link.getIssueLinkType().getInward().equals("jira_subtask_inward")){
        long issueChildID = link.getDestinationId()
        Issue issueChild = iManager.getIssueObject(issueChildID)
        String issueType = issueChild.getIssueType().getName()
        String status = issueChild.getStatus().getName()

        if(issueType.equals("Sub-task") && blueStatuses.contains(status)){

            def transitionValidationResult = iService.validateTransition(jiramaintenance, issueChildID, transition_toOnHold, new IssueInputParametersImpl())

            if(transitionValidationResult){
                iService.transition(jiramaintenance, transitionValidationResult)
                log.warn("issue mossa: " + issueChild.key)
            }
        }
    }
}
