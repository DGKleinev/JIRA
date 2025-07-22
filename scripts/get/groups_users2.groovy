import org.apache.log4j.Level
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.user.ApplicationUser


log.setLevel(Level.INFO)
def groupManager = ComponentAccessor.getGroupManager()
def userManager = ComponentAccessor.getUserManager()
def groups = ["Generic_Administrators", "Bet&Dig_Customer", "Gaming_Customer", "CCLO_Customer", "AFC_Customer", "OTH_Customer", "O&T_Demand", "BET_Developers", 
              "DIG_Developers", "Gaming_Developers", "SAP_Developers", "BI_Developers", "BIP_Developers", "BET_HDU", "DIG_HDU", "Gaming_HDU", "Program_PM", 
              "Product_PM", "Project Member", "O&T_Tester", "Program_Users", "Product_Users", "Operations_Users", "Sys.DBA_Operations", "Sys.DBA_PM", 
              "Sys.Ops_Operations", "Sys.Ops_PM", "NOC_Operations", "NOC_PM", "Generic_Vendor", "jira-administrators", "jira-software-users"]

Set <ApplicationUser> users = userManager.getAllApplicationUsers() as Set <ApplicationUser>
StringBuilder builder = new StringBuilder()
builder.append("<table border = 1><tr><td>Status User</td><td><b>Full Name</b></td><td><b>Username</b></td><td><b>Groups</b></td></tr>")

def result = "<table border = 1><tr><td><b>Group Name</b></td><td><b>N# Members</b></td><td><b>Full Name</b></td><td><b>Username</b></td><td><b>Status Member</b></td></tr>"
users.each{ user ->
	builder.append("<tr><td>")
    if(user.active){
        builder.append("Active</td><td>${user.displayName}</td><td>${user.getUsername()}</td><td>")
        List <String> userGroups = groupManager?.getGroupNamesForUser (user) as List
        //List <String> intersectGroups = userGroups.intersect(groups)
        
        // builder.append("${intersectGroups}")
         builder.append("${userGroups}")
        /*if(userGroups.size() != 0){
            for( String currentGroup : userGroups ){
                builder.append("${user}, ${currentGroup};")
            }
        }else{
            builder.append("${user}")
        }
        */
    }else{
        builder.append("Inactive</td><td>${user.displayName}</td><td>${user.getUsername()}")
    }
    builder.append("</td></tr>")
    
}
builder.append("</td></tr>")
return builder

/*
groupManager.allGroups.each{ group ->

    ArrayList <String>  members = groupManager.getUserNamesInGroup(group) as ArrayList
    if(members.size() == 0){
        result += "<tr><td>${group.name}</td><td>0</td><td>-</td><td>-</td></tr>"
    }else{
        for(String member : members){
            log.warn(member)
            ApplicationUser cUser = userManager.getUserByName(member)      
            result += "<tr><td>${group.name}</td><td>${members.size()}</td><td>${cUser.getDisplayName()}</td><td>${cUser.getUsername()}</td><td>${cUser.isActive()}</td></tr>"
            //log.info("${group.name} members $members") 
            //result += "<tr> <td>${group.name}</td><td>$members</td></tr>"
        }
    }
}
result += "</table>"
*/
return result
