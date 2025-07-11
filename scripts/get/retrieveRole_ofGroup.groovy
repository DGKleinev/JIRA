//Search in each project if is present a specific and return his role in that group

import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.security.roles.ProjectRoleActors
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.scheme.Scheme


  StringBuilder builder = new StringBuilder()
	builder.append("<table border = 1><tr><td><b>Project Name</b></td><td><b>Project Key</b></td><td><b>Role</b></td><td><b>Member</b></td></tr>")

	def projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
    def userManager = ComponentAccessor.getUserManager()
	ProjectManager projectManager = ComponentAccessor.getProjectManager()
    def projects = projectManager.getProjects()
    def roles = projectRoleManager.getProjectRoles()
    
    Map <String, HashMap <String, ArrayList<String>>> projectInfo = new HashMap <String, HashMap <String, ArrayList<String>>>()
    for (int i = 0; i < projects.size(); i++){
        def currentProject = projects[i]
    	Map <String, ArrayList<String>> roleGroups = new HashMap <String, ArrayList<String>>()
        
        String currentKey = currentProject.getKey()
        for( int j = 0; j < roles.size(); j++){
    		ArrayList <String> groups = new ArrayList <String>()
            
            ProjectRole currentRole = roles[j]
            ProjectRoleActors actors = projectRoleManager.getProjectRoleActors (currentRole, currentProject)

            actors.roleActors.each{ actor ->
                def us = actor.getUsers()
                
                if(actor.getType().contains("atlassian-group-role-actor")){
                    if(actor.getParameter().toString().equalsIgnoreCase("groupToSearch")){ //modify here
                        builder.append("<tr><td>${currentProject.name}</td><td>${currentKey}</td>")
                        builder.append("<td>${roles[j]}<td>${actor.getParameter()}</td>")
                        groups.add(actor.getParameter())
                    }
                }

            }
            if(groups.size() > 0){
            	roleGroups.put(currentRole.getName(), groups)
            }
        
        }
    	projectInfo.put(currentKey, roleGroups)
    }
    
    
                builder.append("</table>")
    
    return builder
