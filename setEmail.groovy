import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.user.search.UserSearchService
import com.atlassian.jira.bc.user.search.UserSearchParams

UserManager um = ComponentAccessor.UserManager
UserSearchService uss = ComponentAccessor.UserSearchService
UserService us = ComponentAccessor.getComponent( UserService.class )
StringBuilder builder = new StringBuilder()
builder.append("<table border = 1><tr><td><b>Name</b></td><td><b>Previous Email</b></td><td><b>Current Email</b></td><td><b>User Directory</b></td><td><b>Modified</b></td></tr>")

int limitValue = 10000
//Build a search with 100,000 results where users are inactive
def userSearchBuilder = new UserSearchParams.Builder(limitValue)
UserSearchParams usp = userSearchBuilder.allowEmptyQuery(true)
        .includeActive(true)
        .includeInactive(true)
        .canMatchEmail(true)
        .limitResults(limitValue)
        .build()

String match_query = "@domain.com"
List <ApplicationUser> users = uss.findUsers( match_query, usp)
List <String> notIncluded = Arrays.asList( "devnull@operations-domain.com", "null@domain.com", "devnull@domain.com", "devnull@domain-operations.com", "ams-tech@domain.com" )
for( ApplicationUser user : users ){
    String currentName = user.getDisplayName()
    String currentEmail = user.getEmailAddress().toLowerCase()
    def directory = user.getDirectoryId()
    builder.append("<tr><td>${currentName}</td><td>${currentEmail}</td>")

    if( currentEmail.contains("domain.com") && !notIncluded.contains( currentEmail ) && directory != 11200 ){
        String newEmail = currentEmail.split('@')[0].concat( "@newDomain.com" )
        builder.append("<td>${newEmail}</td><td>Directory Name1</td><td>Yes</td>")
        ApplicationUser modifiedUser = us.newUserBuilder( user ).emailAddress( newEmail ).build()
        um.updateUser( modifiedUser )

    }else{
        builder.append("<td>${currentEmail}</td>")
        
        if(notIncluded.contains( currentEmail) ){
            builder.append("<td>Directory Name1</td><td>No</td>")
        }
        else if( directory == 11200 ){
            builder.append("<td>Directory Name2</td><td>No</td>")
        }
        else{
            builder.append("<td>otheDirectory</td>")
        }
    }
    builder.append("</tr>")
    
}
builder.append("</table>")
return builder
