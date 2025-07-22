import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.fields.config.manager.PrioritySchemeManager
import com.atlassian.jira.issue.security.IssueSecuritySchemeManager

def issueTypeSchemeManager = ComponentAccessor.issueTypeSchemeManager
def wfSchemeManager = ComponentAccessor.getWorkflowSchemeManager()
def issueTypeScreenSchemeManager = ComponentAccessor.issueTypeScreenSchemeManager
def fieldSchemeManager = ComponentAccessor.fieldLayoutManager
def priorityScheme = ComponentAccessor.getComponent(PrioritySchemeManager)
def permissionSchemeManager = ComponentAccessor.permissionSchemeManager
def issueSecuritySchemeManager = ComponentAccessor.getComponent(IssueSecuritySchemeManager)
def notificationScheme = ComponentAccessor.getNotificationSchemeManager()

ProjectManager projectManager = ComponentAccessor.getProjectManager()
StringBuilder builder=new StringBuilder()
builder.append("<table border = 1><tr><td><b>Project Name</b></td><td><b>Project Key</b></td><td><b>Archived</b></td><td><b>Project Category</b></td>")

builder.append("<td><b>issue Type Scheme</b></td><td><b>Workflow Scheme</b></td><td><b>Workflow Scheme</b></td>"+
               "<td><b>Field Configuration Scheme</b></td><td><b>Priority Scheme</b></td><td><b>Pemission Scheme</b></td>"+
               "<td><b>Issue Security Scheme</b></td><td><b>Issue Notification Scheme</b></td></tr>")
//def targetProject = projectManager.getProjectObjByKey("IMOINT")
def allProjects = projectManager.getProjectObjects() + projectManager.getArchivedProjects()
allProjects.each{targetProject ->
    builder.append("<tr><td>${targetProject.name}</td><td>${targetProject.key}</td><td>${targetProject.archived}</td><td>${targetProject.projectCategory?.name}</td>")
    builder.append("<td>${issueTypeSchemeManager.getConfigScheme(targetProject).name}</td>")
    builder.append("<td>${wfSchemeManager.getWorkflowScheme(targetProject)?.name}</td>")
    builder.append("<td>${issueTypeScreenSchemeManager.getIssueTypeScreenScheme(targetProject)?.name}</td>")
    builder.append("<td>${fieldSchemeManager.getFieldConfigurationScheme(targetProject)?.name}</td>")
    builder.append("<td>${priorityScheme.getScheme(targetProject)?.name}</td>")
    builder.append("<td>${permissionSchemeManager.getSchemeFor(targetProject)?.name}</td>")
    builder.append("<td>${issueSecuritySchemeManager.getSchemeFor(targetProject)?.name}</td>")
    builder.append("<td>${notificationScheme.getSchemeFor(targetProject)?.name}</td></tr>")
}
builder.append("</table>")
return builder
