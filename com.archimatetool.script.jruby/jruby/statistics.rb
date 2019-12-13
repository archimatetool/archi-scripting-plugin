# Statistics

msg = "Model name: " + $model.name + "\n"
msg += "Number of ArchiMate elements: " + J('element').size.to_s + "\n"
msg += "Number of ArchiMate relationships: " + J('relationship').size.to_s + "\n"
msg += "Number of views: " + J('view').size.to_s

alert msg