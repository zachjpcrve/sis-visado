package com.hildebrando.visado.converter;

import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import com.hildebrando.visado.modelo.TiivsPersona;

public class PersonaDataModal extends ListDataModel<TiivsPersona> implements SelectableDataModel<TiivsPersona>{

	public PersonaDataModal() {
		super();
		
	}

	public PersonaDataModal(List<TiivsPersona> list) {
		super(list);
		
	}
	
	@Override
	public TiivsPersona getRowData(String arg0) {
		   List<TiivsPersona> valores = (List<TiivsPersona>) getWrappedData();

	        for (TiivsPersona val : valores) {
	            if (val.getCodPer()== Integer.parseInt(arg0))
	                return val;
	        }

	        return null;
	}

	@Override
	public Object getRowKey(TiivsPersona arg0) {
		return arg0.getCodPer();
	}

}
