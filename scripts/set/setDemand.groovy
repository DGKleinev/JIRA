//scrivere uno script che aggiorni il campo 'Demand It' e i valori li prenda dal campo 'Demand':

import com.atlassian.jira.issue.search.SearchException
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.IssueFieldConstants
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.event.type.EventDispatchOption

SearchService searchService = ComponentAccessor.getComponentOfType(SearchService)
CustomFieldManager cfm = ComponentAccessor.getCustomFieldManager()
IssueManager im = ComponentAccessor.getIssueManager()

String demandID = "customfield_10209"
String demandITID = "customfield_11205"

def demandCF = cfm.getCustomFieldObject( demandID )
def demandITCF = cfm.getCustomFieldObject( demandITID )

ApplicationUser gmt_user = Users.getByName("gmt_jirauser")

IssueFieldConstants constants = new IssueFieldConstants()

final String jqlSearch = """Demand is not EMPTY and type = CR and project not in (VLT, "SKS Migration", Reporting,Others, Phoenix, PR_AWP, "Operations & Technology", ERP, DIG, "Business Support Systems", Betflag, "CAST - DB", "Betflag Sport", "Betflag Inserimento Giochi", "Betflag Grafica", "Betflag FE & APP", "Betflag Conto Gioco", "Betflag Database", "Betflag Data Analyst", "Betflag Casinò", "Betflag Back-office", "BI-Integration&ReportingService")"""
def parseResult = searchService.parseQuery( gmt_user, jqlSearch)
if(!parseResult.valid)
    return null

try{
    def results = searchService.search( gmt_user, parseResult.query, PagerFilter.unlimitedFilter)
    def issues = results.results
    issues.each{ issue ->
    // Issues.search( jqlSearch ).each{ issue ->
        MutableIssue currentIssue = im.getIssueByCurrentKey(issue.getKey())

        def org_demand = issue.getCustomFieldValue( demandCF )
        currentIssue.setCustomFieldValue( demandITCF, org_demand )
        im.updateIssue( gmt_user, currentIssue, EventDispatchOption.ISSUE_UPDATED, false)
    }
}catch( SearchException e){
    e.printStackTrace()
}
