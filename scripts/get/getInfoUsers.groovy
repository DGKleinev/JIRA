/**get the following user's properties: 
  *Active: returns true if the user is an active user, otherwise false
  *Full Name
  *Username
  *Email
  *Issue Count: returns the count of how many issue creates the user
  *Jira Service Desk: returns true if the user has the Jira Service Desk License, else false
  *Jira Software: returns true if the user has the Jira Software Desk License, else false
  *Directory: returns the directory's name where is present the user
  *Last Login
**/
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.security.groups.GroupManager
import com.atlassian.jira.security.login.LoginManager
import com.atlassian.jira.security.roles.*
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.issue.search.SearchQuery
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.jql.query.IssueIdCollector
import com.atlassian.query.Query
import com.atlassian.jira.application.ApplicationAuthorizationService
import com.atlassian.jira.application.ApplicationKeys

UserManager user = ComponentAccessor.getUserManager()
GroupManager groups = ComponentAccessor.getGroupManager()
LoginManager login = ComponentAccessor.getComponent(LoginManager)
ApplicationAuthorizationService applicationAuthorizationService = ComponentAccessor.getComponent(ApplicationAuthorizationService)

def tot = user.getAllApplicationUsers()

StringBuilder builder=new StringBuilder()
builder.append("<table border = 1><tr><td><b>Active</b></td><td><b>Full Name</b></td><td><b>Username</b></td><td><b>Email</b></td><td><b>Issue Count</b></td><td><b>Jira Service Desk</b></td><td><b>Jira Software</b></td><td><b>Directory</b></td><td><b>Last Login</b></td><td><b>Day</b></td><td><b>Month</b></td><td><b>Year</b></td></tr>")

 int getIssueCountByJQL(String username) {
      def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
      SearchService searchService = ComponentAccessor.getComponent(SearchService)
      def issueManager = ComponentAccessor.getIssueManager()
      def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
      String jql = """reporter = "${username}" OR assignee = "${username}" """
      Query query = jqlQueryParser.parseQuery(jql)
      SearchQuery searchQuery = SearchQuery.create(query, user)
      IssueIdCollector collector = new IssueIdCollector()
      int issueCount = searchService.searchCount(user, query) as int
      return issueCount   
    }

for(def currentUser : tot){
  String fullName = currentUser.displayName
  String username = currentUser.getUsername()
  String email = currentUser.getEmailAddress()
  String directory = currentUser.getDirectoryId() == 1 ? "Directory A" : "Directory B"
  boolean jiraSoftware = applicationAuthorizationService.canUseApplication(currentUser, ApplicationKeys.SOFTWARE)
  boolean jiraServiceDesk = applicationAuthorizationService.canUseApplication(currentUser, ApplicationKeys.SERVICE_DESK)
  
  int issueCount = getIssueCountByJQL(username)
  boolean isActive = currentUser.isActive()
  Long loginTime = login?.getLoginInfo(username)?.getLastLoginTime()
  builder.append("<tr><td>${isActive}</td><td>${fullName}</td><td>${username}</td><td>${email}</td><td>${issueCount}</td><td>${jiraServiceDesk}</td><td>${jiraSoftware}</td><td>${directory}</td>")
  if(loginTime != null){
     Date loginDate = new Date(loginTime)
     Calendar cal = Calendar.getInstance()
     cal.setTime(loginDate)
     builder.append("<td>${loginDate}</td><td>${cal.get(Calendar.DAY_OF_MONTH)}</td><td>${cal.get(Calendar.MONTH) + 1}</td><td>${cal.get(Calendar.YEAR)}")
  }else{
      builder.append("<td>never logged in</td><td>-</td><td>-</td><td>-")
  }
  builder.append("</td></tr>")
}
builder.append("</table>")
return builder
