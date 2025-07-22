import org.apache.log4j.Level
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.user.ApplicationUser


log.setLevel(Level.INFO)
def groupManager = ComponentAccessor.getGroupManager()
def userManager = ComponentAccessor.getUserManager()

def result = "<table border = 1><tr><td><b>Group Name</b></td><td><b>N# Members</b></td><td><b>Full Name</b></td><td><b>Email</td></b><td><b>Username</b></td><td><b>Status Member</b></td></tr>"
groupManager.allGroups.each{ group ->

    ArrayList <String>  members = groupManager.getUserNamesInGroup(group) as ArrayList
    if(members.size() == 0){
        result += "<tr><td>${group.name}</td><td>0</td><td>-</td><td>-</td></tr>"
    }else{
        for(String member : members){
            log.warn(member)
            ApplicationUser cUser = userManager.getUserByName(member)      
            result += "<tr><td>${group.name}</td><td>${members.size()}</td><td>${cUser.getDisplayName()}</td><td>${cUser.getEmailAddress()}</td><td>${cUser.getUsername()}</td><td>${cUser.isActive()}</td></tr>"
            //log.info("${group.name} members $members") 
            //result += "<tr> <td>${group.name}</td><td>$members</td></tr>"
        }
    }
}
result += "</table>"
return result
