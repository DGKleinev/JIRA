//retrieve the components present in each group with his project Lead

import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser

def projectManager = ComponentAccessor.getProjectManager()
StringBuilder builder=new StringBuilder()

builder.append("<table border = 1 cellpadding='5' style='border-collapse:collapse'><tr><td><b>Project Name</b></td><td><b>Project Key</b></td>")
builder.append("<td><b>Project Lead</b></td><td><b>Email</b></td><td><b>Name</b></td><td><b>N. Components</b></td><td><b>Component</b></td><td><b>Component Lead</b></td>")

def allProjects = projectManager.getProjectObjects()
allProjects.each{targetProject ->

    def components = targetProject.getComponents()
    List <ApplicationUser> componentLeadList = new ArrayList<>()
    if (components.size() == 0){
        builder.append("<tr><td>${targetProject.name}</td><td>${targetProject.key}</td><td>${targetProject.getProjectLead()}</td>")
        builder.append("<td>${targetProject.getProjectLead()?.getEmailAddress()}</td><td>${targetProject.getProjectLead()?.getDisplayName()}</td><td>${components.size()}</td><td></td><td></td>")
    }else{
log.warn(components)

        components.each{it ->
            builder.append("<tr><td>${targetProject.name}</td><td>${targetProject.key}</td><td>${targetProject.getProjectLead()}</td>")
            builder.append("<td>${targetProject.getProjectLead()?.getEmailAddress()}</td><td>${targetProject.getProjectLead()?.getDisplayName()}</td><td>")
            builder.append("${components.size()}</td><td>${it.getName()}</td><td>${it.getComponentLead()}</td>")
        }
    }
    builder.append("</tr>")

}

builder.append("</table>")
return builder
