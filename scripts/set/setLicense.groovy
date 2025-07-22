import com.atlassian.crowd.event.user.UserAuthenticatedEvent
import com.atlassian.crowd.manager.directory.DirectoryManager
import com.atlassian.jira.component.ComponentAccessor

final directoryManager = ComponentAccessor.getComponent(DirectoryManager)
final groupManager = ComponentAccessor.groupManager
final userManager = ComponentAccessor.userManager
final groupToCheck = groupManager.getGroup("jira-users_access-only")
//final directories = [10501, 10500, 10600] //ALM LDAP server (ou=People,uid=e0) - ALM LDAP server (ou=People,uid=e1) - ALM LDAP server PRODUZIONE (ou=People,uid=e)
//final serviceActor = ComponentAccessor.userManager.getUserByKey("devjira_cm")
//ComponentAccessor.jiraAuthenticationContext.loggedInUser = serviceActor

def userEvent = event as UserAuthenticatedEvent
def authUser = userManager.getUserByName(userEvent.user?.name)
//def directoryId = authUser.directoryId

def authUserGroups = groupManager.getGroupNamesForUser(authUser)
if(authUserGroups.isEmpty())
    return

//if (!groupManager.isUserInGroup(authUser, groupToCheck) && !(directoryId in directories)) {
if (!groupManager.isUserInGroup(authUser, groupToCheck)) {
    
    def group_suspended = "jira-users_suspended"
    def isSuspended = groupManager.getGroup(group_suspended)
    if(groupManager.isUserInGroup(authUser, isSuspended)){
        return
    }
    
    def forbiddenGroups = ["IOTA-I2B_jira-users_default-access", "I2B_IIM_MailUsers"]
    def intersect = authUserGroups.intersect(forbiddenGroups)
    if(intersect.size() != authUserGroups.size()){
        groupManager.addUserToGroup(authUser, groupToCheck)
    }
}
