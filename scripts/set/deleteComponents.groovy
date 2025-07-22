import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.project.component.ProjectComponentManager

String [] projects = ["SYNAWP", "BCNES", "MNTAC", "SRCR", "SVSRGS", "LNBPD"]
deleteComponents(projects)

void deleteComponents (String [] projects){
    ProjectManager projectManager = ComponentAccessor.getProjectManager()
    ProjectComponentManager componentManager = ComponentAccessor.getProjectComponentManager()

    def currentProject
    def components
    for(int i = 0; i < projects.size(); i++){
        currentProject = projectManager.getProjectByCurrentKey(projects[i])
        components = currentProject.getComponents()

        if(components.size() != 0){
            components.each{ currentComponent -> 
                componentManager.delete(currentComponent.getId())
            }
        }
    }

}
