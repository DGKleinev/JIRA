import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.IssueFactory
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.project.Project
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.onresolve.jira.groovy.user.FormField
import com.atlassian.jira.issue.comments.CommentManager
import com.atlassian.jira.issue.comments.Comment
import com.atlassian.jira.security.PermissionManager
import com.atlassian.jira.security.plugin.ProjectPermissionKey
import com.atlassian.jira.issue.label.LabelManager
import com.atlassian.jira.issue.label.Label
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.issue.link.IssueLinkManager
import groovy.transform.Field

@Field ProjectManager projectManager = ComponentAccessor.getProjectManager()
@Field CustomFieldManager cfManager = ComponentAccessor.getCustomFieldManager()
UserManager userManager = ComponentAccessor.getUserManager()
@Field IssueManager issueManager = ComponentAccessor.getIssueManager()
@Field IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager()

final List <String> removedProjects = ["App Mobile", "Cast", "Servizi Backend Interactive", "Servizi Sport Interactive", "Siti Interactive", "Siti Sport", "SAP"]
final List <String> newProjects = ["Pegasos - FE APP", "CAST - Development", "Pegasos - BE Product Integration", "Pegasos - FE Web", "SAP ECC"]

Issue currentIssue = issue
//Issue currentIssue = ComponentAccessor.getIssueManager().getIssueObject( "DIG-4" )
ApplicationUser gmt_user = userManager.getUserByName("gmt_jirauser");

def linkedIssueCF = cfManager.getCustomFieldObject("customfield_10236")
def primaryCF = cfManager.getCustomFieldObject("customfield_10228")
def secondaryCF = cfManager.getCustomFieldObject("customfield_10249")

def secondaryCFValue = currentIssue.getCustomFieldValue(secondaryCF)
def linkedIssueCFValue = currentIssue.getCustomFieldValue(linkedIssueCF)
def primaryCFValue = currentIssue.getCustomFieldValue(primaryCF).toString()

def allIssueCloned = []
def allProjectIssueCloned = []

String sLinkedIssue = linkedIssueCFValue.toString()
if(!(sLinkedIssue.equals("null") || sLinkedIssue.equals(null))){
    sLinkedIssue.split(", ").each{
                Issue linkedIssue = issueManager.getIssueObject(it)
        allIssueCloned.add(linkedIssue)
        def issueProjectId = linkedIssue.projectId
        def project = projectManager.getProjectObj(issueProjectId)
        allProjectIssueCloned.add(projectManager.getProjectObj(issueProjectId))
    }
}
log.warn(linkedIssueCFValue)


def allProjectHas = new HashSet  ()

if(removedProjects.contains(primaryCFValue)){
    if(primaryCFValue.equals(removedProjects[0])){
        allProjectHas.add(projectManager.getProjectObjByName(newProjects[0]))

    }else if(primaryCFValue.equals(removedProjects[1])){
        allProjectHas.add(projectManager.getProjectObjByName(newProjects[1]))

    }else if(primaryCFValue.equals(removedProjects[2]) || primaryCFValue.equals(removedProjects[3])){
        allProjectHas.add(projectManager.getProjectObjByName(newProjects[2]))

    }else if(primaryCFValue.equals(removedProjects[4]) || primaryCFValue.equals(removedProjects[5])){
        allProjectHas.add(projectManager.getProjectObjByName(newProjects[3]))

    }else if(primaryCFValue.equals(removedProjects[6])){
        allProjectHas.add(projectManager.getProjectObjByName(newProjects[4]))

    }
}else{
    allProjectHas.add(projectManager.getProjectObjByName(primaryCFValue))
}


secondaryCFValue.each{
    log.warn(it)
    if(removedProjects.contains(it.toString())){
        if(it.toString().equals(removedProjects[0])){
            allProjectHas.add(projectManager.getProjectObjByName(newProjects[0]))

        }else if(it.toString().equals(removedProjects[1])){
            allProjectHas.add(projectManager.getProjectObjByName(newProjects[1]))

        }else if(it.toString().equals(removedProjects[2]) || it.toString().equals(removedProjects[3])){
            allProjectHas.add(projectManager.getProjectObjByName(newProjects[2]))

        }else if(it.toString().equals(removedProjects[4]) || it.toString().equals(removedProjects[5])){
            allProjectHas.add(projectManager.getProjectObjByName(newProjects[3]))
            log.warn("entro qui ${allProjectHas}")

        }else if(it.toString().equals(removedProjects[6])){
            allProjectHas.add(projectManager.getProjectObjByName(newProjects[4]))
        }
    }else{
            allProjectHas.add(projectManager.getProjectObjByName(it.toString()))
    }

}

log.warn(allProjectHas)
log.warn(allProjectIssueCloned)
def commons = allProjectIssueCloned.intersect(allProjectHas)

def difference = allProjectIssueCloned.plus(allProjectHas)
difference.removeAll(commons)

def onlyLinked = new ArrayList(difference)
onlyLinked.removeAll(allProjectHas)

def onlyChanged =  new ArrayList(difference)
onlyChanged.removeAll(allProjectIssueCloned)

log.warn("Commons: " + commons)
log.warn("Difference: " + difference)
log.warn("Only Liked: " + onlyLinked)
log.warn("Only Changed: " + onlyChanged)

// se non ci sono differenze ritorna
if (!difference) {
    log.warn("sono uguali")
    return
}

//se c'è una issue linkata in più --> nella modifica è stato tolto uno o più progetti dai selezionati
if (onlyLinked) {
    String comment = "Le seguenti issue: "
    onlyLinked.each{
        def issueRemoved = allIssueCloned.find{element -> element.toString().contains(it.getKey().toString()) }
        comment += issueRemoved.toString() + ", "
        allIssueCloned.remove(issueRemoved)

        issueLinkManager.getInwardLinks(currentIssue.getId()).each {issueLink ->
            if (issueLink.getSourceObject().getKey().toString().equals(issueRemoved.getKey().toString())) {
                issueLinkManager.removeIssueLink(issueLink, gmt_user)
            }
        }

        addComment(issueRemoved.getKey().toString(), gmt_user, "issue rimossa dalla CR Programma ${currentIssue.getKey().toString()}")
    }
    addComment(currentIssue.getKey().toString(), gmt_user, comment + "non sono più parte di questa CR Programma")


}
 ArrayList createdIssues = []
 allIssueCloned.each{
    createdIssues.add(it.toString())
 }

//se c'è una issue linkata in meno --> nella modifica è stato aggiunto uno o più progetti dai selezionati
if (onlyChanged) {
    //crea una nuova issue nei progetti dichiarati
    onlyChanged.each{
        if(it != null){
                createdIssues.add(cloneIssue(currentIssue, it.getKey().toString(), gmt_user))
        }
    }
}

//cambia il cf linked issue con i nuovi valori
def modifiedValue = new ModifiedValue(null, createdIssues.toString().substring(1, createdIssues.toString().length() - 1))
linkedIssueCF.updateValue(null, currentIssue, modifiedValue, new DefaultIssueChangeHolder())
issueManager.updateIssue(gmt_user, currentIssue, EventDispatchOption.ISSUE_UPDATED, false /*sendMail*/)


public void addComment (String issueKey, ApplicationUser gmt_user, String comment) { // String  username = null :> optional parameter
    Issue myIssue = issueManager.getIssueObject(issueKey)
    ComponentAccessor.getCommentManager().create(myIssue, gmt_user, comment, true)
}

String cloneIssue (Issue myIssue, String project, ApplicationUser gmt_user) {
    def actualStartDate = cfManager.getCustomFieldObject("customfield_10201")
    def storyPoints = cfManager.getCustomFieldObject("customfield_10106")
    def plannedQuarter = cfManager.getCustomFieldObject("customfield_11115")

    def actualStartDateValue = myIssue.getCustomFieldValue(actualStartDate)
    def storyPointsValue = myIssue.getCustomFieldValue(storyPoints)
    def plannedQuarterValue = myIssue.getCustomFieldValue(plannedQuarter)

    Project destinationProject = projectManager.getProjectObjByKey(project)
    IssueFactory issueFactory = ComponentAccessor.getIssueFactory()
    String linkID = 10300
    String issuetypeID = 10102

    MutableIssue newissue = issueFactory.getIssue();
    newissue.setSummary ("[DEV] - " + myIssue.getSummary());
    newissue.setProjectObject(destinationProject);
    newissue.setIssueTypeId(issuetypeID)
    // newissue.setDescription(myIssue.getDescription());
    //JIRA-128 newissue.setDescription(setFields_inDescription( myIssue.getKey(), newissue.getKey(), gmt_user))
    newissue.setReporter(myIssue.getReporter());
    newissue.setAssignee(myIssue.getAssignee());
    newissue.setCustomFieldValue(actualStartDate, actualStartDateValue)
    newissue.setCustomFieldValue(storyPoints, storyPointsValue)
    newissue.setCustomFieldValue(plannedQuarter, plannedQuarterValue)
    newissue.setDueDate(myIssue.getDueDate())
    issueManager.createIssueObject(gmt_user, newissue)
    issueLinkManager.createIssueLink(newissue.getId(), myIssue.getId(), Long.parseLong(linkID), Long.valueOf(0), gmt_user)
    pasteCommentsandLabels(myIssue.key, newissue.key, gmt_user)
    issueManager.updateIssue(gmt_user, newissue, EventDispatchOption.DO_NOT_DISPATCH, false);

    return newissue.getKey().toString()
}

//JIRA-21
public void pasteCommentsandLabels (String srcKey, String destKey, ApplicationUser gmt_user){
        CommentManager commentManager = ComponentAccessor.getCommentManager()
    PermissionManager permissionManager = ComponentAccessor.getPermissionManager()
    LabelManager labelManager = ComponentAccessor.getComponent(LabelManager)
    Project destProject = projectManager.getProjectByCurrentKey(destKey.split("-")[0])
    Issue srcIssue = issueManager.getIssueObject(srcKey)
    Issue destIssue = issueManager.getIssueObject(destKey)
    List  comments = commentManager.getComments(srcIssue)
    ProjectPermissionKey permissionKey = new ProjectPermissionKey("ADD_COMMENTS")
    Set labels = labelManager.getLabels(srcIssue.id)

    for (Label currentLabel : labels) {
        labelManager.addLabel(gmt_user, destIssue.id, currentLabel.toString(), false)
    }

    for (Comment currentComment : comments){
        def currentAuthor = currentComment.getAuthorApplicationUser()
        String body = currentComment.getBody()
        String groupLevel = currentComment.getGroupLevel()
        Long roleLevelId = currentComment.getRoleLevelId()
                Date created = currentComment.getCreated()
        Date updated = currentComment.getUpdated()
        boolean createCommentPermission = permissionManager.hasPermission(permissionKey, destProject, currentAuthor)
        if(!body.contains("Le seguenti issue")){

            if (createCommentPermission){
                commentManager.create(destIssue, currentAuthor, gmt_user,
                                        body, groupLevel, roleLevelId, created, updated, true )
            } else{
                String newBody = """Creato da: ${currentAuthor}. ${body}"""
                commentManager.create(destIssue, gmt_user, gmt_user,
                                        body, groupLevel, roleLevelId, created, updated, true )
            }
        }
    }

}
