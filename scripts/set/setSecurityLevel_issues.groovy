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
SearchService searchService = ComponentAccessor.getComponent( SearchService.class )

def appProperties = ComponentAccessor.getComponentOfType( ApplicationProperties )
def fictiveUser = ComponentAccessor.userManager.getUserByName("gmt_jirauser")
def maxResults = appProperties.asMap().get(JIRA_SEARCH_VIEWS_MAX_LIMIT) as Integer

projectManager.getProjectObjects().each{ currentProject ->
    String nameProject = currentProject.key
    String jqlFilter = "project = ${nameProject} AND level is EMPTY"
    SearchService.ParseResult parseResult = searchService.parseQuery (fictiveUser, jqlFilter)
    def currentSecurityScheme = issueSecuritySchemeManager.getSchemeFor(currentProject)
    def securityLevel = issueSecurityManager.getIssueSecurityLevels(currentSecurityScheme.id).find {it.name == "Vendor Role limitation"}
    def issues
    if(parseResult.isValid()){
        try{
            def results = searchService.search(fictiveUser, parseResult.query, new PagerFilter(maxResults))
            issues = results.results
            
            if(!issues.isEmpty()){
                log.warn(issues)
                issues.each{
                    //set
                    DocumentIssueImpl issueImpl = it as DocumentIssueImpl
                    Issue currentIssue = issueM.getIssueByCurrentKey(issueImpl.getKey())
               		currentIssue.setSecurityLevelId(securityLevel?.id)     
                    issueM.updateIssue(fictiveUser, currentIssue, EventDispatchOption.DO_NOT_DISPATCH, false)

                    //index
                    boolean isIndex = ImportUtils.isIndexIssues()
                    ImportUtils.setIndexIssues(true);
                    IssueIndexingService IssueIndexingService = (IssueIndexingService) ComponentAccessor.getComponent(IssueIndexingService.class);
                    IssueIndexingService.reIndex(currentIssue)

                    ImportUtils.setIndexIssues(isIndex)

               
                }
            }
            
        } catch (SearchException e){
            
        }
    }
}


