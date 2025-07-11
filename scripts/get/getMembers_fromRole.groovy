//get members of a specific role in all active projects

import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.security.roles.ProjectRoleActors
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.scheme.Scheme


    StringBuilder builder = new StringBuilder()
	builder.append("<table border = 1><tr><td><b>Project Name</b></td><td><b>Project Key</b></td><td><b>Role</b></td><td><b>Member</b></td><td><b>Type</b></td></tr>")

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
                
                //log.warn(roles[j])
                
                if(roles[j].toString().equals("Project Fictive Team User")){
                    builder.append("<tr><td>${currentProject.name}</td><td>${currentKey}</td>")

                    def u = actor.getUsers()
                    if(actor.getType().contains("atlassian-group-role-actor")){
                        builder.append("<td>${roles[j]}</td><td>${actor.getParameter()}</td>")
                        groups.add(actor.getParameter())
                        builder.append("<td>Group</td>")
                    }else{
                        for (def us : u){
                            if (us.getKey().toString().equals(actor.getParameter())){
                                builder.append("<td>${roles[j]}</td><td>${us.getUsername()}</td>")
                                groups.add(us.getUsername())
                            }
                        }
                        builder.append("<td>User</td></tr>")
                        
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
