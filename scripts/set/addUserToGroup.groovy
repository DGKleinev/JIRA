import com.atlassian.jira.security.groups.GroupManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.crowd.embedded.api.Group

GroupManager gm = ComponentAccessor.getGroupManager()

List <String> users = Arrays.asList("username1", "username2")
List <String> groupsName = Arrays.asList("group1", "group2", "group3")
List <Group> groups = new ArrayList <Group> ()

for( String groupName : groupsName ){
    try{
        Group group = Groups.getByName( groupName )
        groups.add( group )
        log.warn( "${groupName} added")

    }catch( Exception e ){
        log.warn( "the group ${groupName} doesn't exist" )
    }
}

for( String username : users ){
    try{
        ApplicationUser currentUser = Users.getByName( username )
        for( Group g : groups ){
            if( !gm.isUserInGroup( currentUser, g ) ){
                gm.addUserToGroup( currentUser, g )
                log.warn( "the user ${username} is added in the group ${g.getName()}" )
            }else{
                log.warn( "the user ${username} is already present in the group ${g.getName()}" )
            }

        }

    }catch( Exception e){
        log.warn("username: ${username} doesn't exist")

    }
}
