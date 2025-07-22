import com.atlassian.jira.bc.JiraServiceContextImpl
import com.atlassian.jira.bc.portal.PortalPageService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.portal.PortalPage
import com.atlassian.jira.portal.PortalPage.Builder
import com.atlassian.jira.portal.PortalPageManager
import com.atlassian.jira.user.ApplicationUser


def portalPageService = ComponentAccessor.getComponent(PortalPageService.class)
def portalPageManager = ComponentAccessor.getComponent(PortalPageManager.class)
// def user = ComponentAccessor.jiraAuthenticationContext.user
def userName = "e2597"
def user = ComponentAccessor.userManager.getUserByKey(userName)
log.warn(user)

def idOfPortalPage = 31971
def dashboardName = "My dashboard (" + userName + ")"
def portalPageTest = portalPageService.getPortalPage(new JiraServiceContextImpl(user), idOfPortalPage)
PortalPage.Builder builder = new Builder()
builder.portalPage(portalPageTest)
builder.name(dashboardName)
PortalPage newP = builder.build()
portalPageManager.update(newP)
