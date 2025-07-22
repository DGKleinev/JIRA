import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.issue.security.IssueSecurityLevelManager
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.issue.security.IssueSecuritySchemeManager
import com.atlassian.jira.event.type.EventDispatchOption

ProjectManager projectManager = ComponentAccessor.getProjectManager()
IssueSecurityLevelManager issueSecurityManager = ComponentAccessor.getIssueSecurityLevelManager()
IssueSecuritySchemeManager issueSecuritySchemeManager = ComponentAccessor.getComponent(IssueSecuritySchemeManager)
UserManager userManager = ComponentAccessor.getUserManager()

def fictiveUser = ComponentAccessor.userManager.getUserByName("gmt_jirauser")
def scheme = issueSecuritySchemeManager.getIssueSecurityLevelSchemes().find { it.name == 'Global Issue Security Scheme'}

projectManager.getProjectObjects().each{ currentProject ->
    	issueSecuritySchemeManager.setSchemeForProject(currentProject, scheme.id)
    
}


