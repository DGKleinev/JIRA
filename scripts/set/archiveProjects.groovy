import com.atlassian.jira.project.archiving.ArchivedProjectService.ValidationResult
import com.atlassian.jira.project.archiving.ArchivedProjectService
import com.atlassian.jira.component.ComponentAccessor


 //prendo: l'utente corrente e la chiave del progetto desiderato 
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
//def key = ComponentAccessor.getProjectManager().getProjectObjByKey("HMWK")

String [] keys = ["PROV","TESTTWO","TESTTHREE"]
//new File('projectKeys.txt').eachLine { line ->
  //  keys << line
//}

def archivedProjectService = ComponentAccessor.getComponent(ArchivedProjectService)
keys.each {
    //log.warn(it)
    ValidationResult validationResult = archivedProjectService.validateArchiveProject(user, it)

    if (validationResult.isValid()) {
        archivedProjectService.archiveProject(validationResult);
    }
}
/*
def archivedProjectService = ComponentAccessor.getComponent(ArchivedProjectService)
ValidationResult validationResult = archivedProjectService.validateArchiveProject(user, key.getKey())

if (validationResult.isValid()) {
    archivedProjectService.archiveProject(validationResult);
}
*/
