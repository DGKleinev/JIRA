//remove all groups from never logged in users

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.security.groups.GroupManager
import com.atlassian.jira.security.login.LoginManager
import com.atlassian.jira.project.Project
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.security.roles.*
    import com.atlassian.crowd.embedded.api.Group

UserManager user = ComponentAccessor.getUserManager()
GroupManager groups = ComponentAccessor.getGroupManager()
LoginManager login = ComponentAccessor.getComponent(LoginManager)
UserUtil userUtil = ComponentAccessor.getUserUtil()

def tot = user.getAllUsers()
int comodo = 0
List <String> usersTo_exclude = new ArrayList <String> (Arrays.asList("user1","user2","user3"))

StringBuilder builder=new StringBuilder()
builder.append("<table border = 1><tr><td><b>Active</b></td><td><b>Full Name</b></td><td><b>Username</b></td><td><b>Directory</b></td><td><b>Last Login</b></td><td><b>Day</b></td><td><b>Month</b></td><td><b>Year</b></td></tr>")


for(def currentUser : tot){
    String fullName = currentUser.displayName	
    String username = currentUser.getUsername()
    if( !usersTo_exclude.contains(username)){
        // long loginCount  = login.getLoginInfo(username)?.getLoginCount()
        boolean isActive = currentUser.isActive()
        Long loginTime = login?.getLoginInfo(username)?.getLastLoginTime()
        builder.append("<tr><td>${isActive}</td><td>${fullName}</td><td>${username}</td><td></td>")
        if(loginTime != null){
            Date loginDate = new Date(loginTime)
            Calendar cal = Calendar.getInstance()
            cal.setTime(loginDate)
            builder.append("<td>${loginDate}</td><td>${cal.get(Calendar.DAY_OF_MONTH)}</td><td>${cal.get(Calendar.MONTH) + 1}</td><td>${cal.get(Calendar.YEAR)}")
        }else{

            builder.append("<td>never logged in</td><td>-</td><td>-</td><td>-")
            List <Group> groupsToRemove = new ArrayList <Group> ()
            groupsToRemove.addAll(groups.getGroupsForUser( currentUser ))
            userUtil.removeUserFromGroups( groupsToRemove, currentUser )

        }
        //Date loginDate = loginTime != null ? new Date(loginTime) : new Date(0)
        builder.append("</td></tr>")
        comodo++
            } else {
        log.warn("user escluso: ${username}")
    }
}
builder.append("</table>")
return builder
