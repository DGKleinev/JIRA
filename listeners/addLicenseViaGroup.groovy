import com.atlassian.crowd.event.user.UserAuthenticatedEvent
import com.atlassian.jira.component.ComponentAccessor

final groupManager = ComponentAccessor.groupManager
final userManager = ComponentAccessor.userManager
final groupToCheck = groupManager.getGroup("groupLicense")

def userEvent = event as UserAuthenticatedEvent
def authUser = userManager.getUserByName(userEvent.user?.name)

def authUserGroups = groupManager.getGroupNamesForUser(authUser)
log.warn(authUserGroups)

if(authUserGroups.isEmpty()){
    log.warn("The user ${authUser} doesn't have any groups, so we don't grant the license to him")
    return
    
}

if(!groupManager.isUserInGroup(authUser, groupToCheck)){
    log.warn("We grant the license to user ${authUser}")
    groupManager.addUserToGroup(authUser, groupToCheck)
}
