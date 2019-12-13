$model = model
$console = console
$selection = selection
$jArchiModel = jArchiModel
$jArchiFS = jArchiFS
$Browser = Browser
$shell = shell
$workbench = workbench
$workbenchwindow = workbenchwindow

def jArchi (obj=nil)
    if obj == $selection
        return obj
    end
    
    if obj
        return $model.find(obj)
    else
        return $model.find("")
    end
end

alias J jArchi

def alert(message)
   org.eclipse.jface.dialogs.MessageDialog.openInformation($shell, "Archi", message)
end

