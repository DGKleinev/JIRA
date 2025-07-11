//With this script you will retrieve all the screens that use a specific custom field
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.fields.screen.FieldScreenManager
import com.atlassian.jira.issue.fields.screen.FieldScreen
import com.atlassian.jira.issue.fields.screen.FieldScreenLayoutItem

def customFieldManager = ComponentAccessor.getCustomFieldManager()
def fieldScreenManager = ComponentAccessor.getFieldScreenManager()

def builder = new StringBuilder()
builder << "<table border='1'><tr><td><b>Custom Field</b></td><td><b>Screens</b></td></tr>"

// Build a map: custom field name → list of screen names
def fieldToScreensMap = [:].withDefault { [] }

fieldScreenManager.getFieldScreens().each { FieldScreen screen ->
    screen.getTabs().each { tab ->
        tab.getFieldScreenLayoutItems().each { item ->
            def fieldName = item.getOrderableField()?.getName()
            fieldToScreensMap[fieldName] << screen.getName()
        }
    }
}

// Now iterate through custom fields and print their related screens
customFieldManager.getCustomFieldObjects().each { CustomField cf ->
    def cfName = cf.getName()
    def screens = fieldToScreensMap[cfName]?.unique()?.sort()

    builder << "<tr><td>${cfName}</td><td>"
    screens?.each { screenName -> builder << "${screenName}<br/>" }
    builder << "</td></tr>"
}

builder << "</table>"
return builder.toString()
