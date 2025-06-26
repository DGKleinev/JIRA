import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.user.search.UserSearchService
import com.atlassian.jira.bc.user.search.UserSearchParams

UserManager um = ComponentAccessor.UserManager
UserSearchService uss = ComponentAccessor.UserSearchService
UserService us = ComponentAccessor.getComponent( UserService.class )

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
List <ApplicationUser> igt_users = uss.findUsers( match_query, usp)
List <String> notIncluded = Arrays.asList( "devnull@operations-domain.com", "null@domain.com", "devnull@domain.com", "devnull@domain-operations.com" )
List <String> emails = new ArrayList <String> ()
for( ApplicationUser user : igt_users ){
    String currentEmail = user.getEmailAddress().toLowerCase()
    def directory = user.getDirectoryId()
    log.warn( "${currentEmail}")
    if( currentEmail.contains("igt.com") && !notIncluded.contains( currentEmail ) && directory != 11100 ){
        String newEmail = currentEmail.split('@')[0].concat( "@newDomain.com" )
        ApplicationUser modifiedUser = us.newUserBuilder( user ).emailAddress( newEmail ).build()
        um.updateUser( modifiedUser )
        emails.add( currentEmail)

    }
    
}
return emails
