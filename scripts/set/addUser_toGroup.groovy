import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.groups.GroupManager
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.user.ApplicationUser 

UserManager userManager = ComponentAccessor.getUserManager()
GroupManager groupManager = ComponentAccessor.getGroupManager()
List user_group = Arrays.asList(
)

Map <String, String> map = new HashMap<String, String>()
List <String> users = new ArrayList <String> ()
//List <String> groups = new ArrayList <String> ()

for (String split_concat : user_group){
    String [] splitString = split_concat.split("#")
    String user = splitString[0]
    String [] groups = splitString[1].split(", ")
    ApplicationUser currentUser = userManager?.getUserByName(user)
    if (currentUser != null){
        for (int i = 0; i < groups.size(); i++){
            def group = groupManager.getGroup(groups[i])
            //log.warn("il valore utente: ${user} e il valore gruppo: ${group.getName()}")
            groupManager.addUserToGroup(currentUser, group)
        
    	}
    }else{
        log.warn("${user} non è presente tra gli utenti, non è stato profilato")
    }

   

}

