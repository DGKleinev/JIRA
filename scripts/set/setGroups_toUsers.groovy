//With this script we add some users to similar or differents groups.

import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.security.roles.ProjectRoleActors
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.security.roles.ProjectRoleManager

def userToAdd = """
Username;Role;Group@
Username;Role1,Role2;Group1,Group2@
"""
String [] users_info = userToAdd.split("@")

for (int i = 0; i < users_info.length - 1; i++){
    String [] info = users_info[i].split(";")
    String username = info[0].substring(1)

    ArrayList <String> user_groups
    ArrayList <String> user_projects
    ArrayList <String> roles
    if(info.length != 3){
        log.warn("${username} wasn't updated it because the role or the project are missing")
    }else{
        String [] groups = info[1]?.split(",")
        String [] projects = info[2]?.split(",")

        roles = getRoles(groups)
        user_projects = getGroups(roles, projects)
        addUsersToGroups(username, user_projects)
        log.warn("index ${i}: ${username}, the roles are: ${roles}, and they're present in ${projects}. The groups are: ${user_projects}")       
    }
   
}

private ArrayList <String> getGroups(ArrayList<String> roles, String [] projects){
	def projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
	ProjectManager projectManager = ComponentAccessor.getProjectManager()
    ArrayList <String> groups = new ArrayList <String>()
    
    for (int i = 0; i < projects.length; i++){
        String pName = getProjects(projects[i])
        def currentProject = projectManager.getProjectObjByName(pName)
        String key = currentProject.getKey()
        
		for( int j = 0; j < roles.size(); j++){
            ProjectRole role = projectRoleManager.getProjectRole(roles[j])
            ProjectRoleActors actors = projectRoleManager.getProjectRoleActors (role, currentProject)
            
			actors.roleActors.each{ actor ->
                if(roles[j] == "Developers"){
                    if(actor.getParameter() == "${key}_DevOps"){
						groups.add(actor.getParameter()) 
                    }
                }else{
                    if(actor.getParameter() == "${key}_${roles[j]}"){
                        groups.add(actor.getParameter())
                    }
                }
            }
        
        }
    }
    return groups
}

private ArrayList <String> getRoles(String [] roles){
	ArrayList <String> roleProject = new ArrayList <String>()
    for (int i = 0; i < roles.size(); i++){
        if( roles[i].equals("Cliente")){
            roleProject.add("Customer")
        }
        else if( roles[i].equals("Operations") || roles[i].equals("Sviluppo")){
            roleProject.add("Developers")
        }

        else if( roles[i].equals("Team Leader") || roles[i].equals("Admin")){
            roleProject.add("Project Manager")
        }
        else{
            roleProject.add(roles[i])
        }
    }  
    return roleProject
}

private String getProjects(String project){
    String projectName
    if(project.equals("AFC")){
        projectName = "Administration, Finance & Control"
    }
    else if(project.equals("CC&LO")){
        projectName = "Customer Care & Logistica"
    }
    else if(project.equals("O&T")){
        projectName = "Operations & Technology"
    }
    else{
        projectName = project
    } 
    return projectName
}

void addUsersToGroups (String user, ArrayList <String> groupsName) {
    def groupManager = ComponentAccessor.getGroupManager()
    def userManager = ComponentAccessor.getUserManager()
    def currUser = userManager?.getUserByName(user)
    if(currUser == null){
        log.warn("error, the ${user}" doesn't exist)
    }else{
      if (currUser.active) {
            groupsName.each{
                def group = groupManager.getGroup(it)
            	groupManager.addUserToGroup(currUser,group)
            }
    	}  
    }        
}
