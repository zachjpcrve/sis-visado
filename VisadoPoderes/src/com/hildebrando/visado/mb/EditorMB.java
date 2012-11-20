package com.hildebrando.visado.mb;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "editorMB")
@SessionScoped
public class EditorMB {

	 private String value;  
	  
	    public String getValue() {  
	        return value;  
	    }  
	  
	    public void setValue(String value) {  
	        this.value = value;  
	    }  
	
}
