import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.issue.security.IssueSecurityLevelManager
import com.atlassian.jira.project.ProjectManager
import static com.atlassian.jira.config.properties.APKeys.JIRA_SEARCH_VIEWS_MAX_LIMIT
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.search.SearchException
import com.atlassian.jira.config.properties.ApplicationProperties
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.DocumentIssueImpl
import com.atlassian.jira.issue.security.IssueSecuritySchemeManager
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.util.ImportUtils
import com.atlassian.jira.issue.index.IssueIndexingService

ProjectManager projectManager = ComponentAccessor.getProjectManager()
IssueSecurityLevelManager issueSecurityManager = ComponentAccessor.getIssueSecurityLevelManager()
IssueSecuritySchemeManager issueSecuritySchemeManager = ComponentAccessor.getComponent(IssueSecuritySchemeManager)
UserManager userManager = ComponentAccessor.getUserManager()
IssueManager issueM = ComponentAccessor.getIssueManager()

def appProperties = ComponentAccessor.getComponentOfType( ApplicationProperties )
def fictiveUser = ComponentAccessor.userManager.getUserByName("gmt_jirauser")
def currentProject = projectManager.getProjectByCurrentKey("BETFLAG")
log.warn(currentProject)
//projectManager.getProjectObjects().each{ currentProject ->
def currentSecurityScheme = issueSecuritySchemeManager.getSchemeFor(currentProject)
def securityLevel = issueSecurityManager.getIssueSecurityLevels(currentSecurityScheme.id).find {it.name == "Vendor Role limitation"}
def issues

log.warn(issues)
//set
Issue currentIssue = ComponentAccessor.getIssueManager().getIssueObject( "BETFLAG-8" )

currentIssue.setSecurityLevelId(securityLevel.id)     
issueM.updateIssue(fictiveUser, currentIssue, EventDispatchOption.DO_NOT_DISPATCH, false)

//index
boolean isIndex = ImportUtils.isIndexIssues()
ImportUtils.setIndexIssues(true);
IssueIndexingService IssueIndexingService = (IssueIndexingService) ComponentAccessor.getComponent(IssueIndexingService.class);
IssueIndexingService.reIndex(currentIssue)

ImportUtils.setIndexIssues(isIndex)



//}


