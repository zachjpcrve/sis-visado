function newPopup(url) {
					popupWindow = window.open(
						'','popUpWindow','height=700,width=800,left=10,top=10,resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no,status=yes');
					popupWindow.document.write('<iframe src="'+url+'" />');					
};

function exportList(nombrereporte, nombreExtension){
		//alert(nombrereporte+nombreExtension);
	   var msg = '¿ Está seguro que desea exportar la lista?';
	      if (confirm(msg)) {
		  var jsonFrm = {
				jfNombreReporte : nombrereporte,
				jfNombreExtension : nombreExtension
				};
		$.ajax({
			type: 'POST',
			url: '../../descargararchivo?accion=descargarArchivoPDF',
			data: jsonFrm,
			success: function(data) {
				window.open('../../../files/'+ nombrereporte+ '.' + nombreExtension);
						},
			error:function(t){
			   		alert('Ocurrio un error en la descarga');
					} 
			});
		} 
};

function validaNumero(e, ent, obj, fn) {
	//Se captura la tecla presionada             
	var tecla = (document.all) ? e.keyCode : e.which;
	var patron;
	var te;
	var result;
	
	//Tecla de retroceso (para poder borrar)       
	if (tecla == 8) {
		return true;
	}
	if (ent == '0') {
		if (fn == '1') {
			if (obj.value == '') {
				return true;
			}
			var puntos = obj.value.split('.');
			if (puntos.length > 2) {
				obj.value = '';
				obj.focus();
			}
		}
		if (tecla == 46) {
			if (obj.value.indexOf('.') != -1) {
				return false;
			}
			return true;
		}
	} else {
		if (fn == '1') {
			if (obj.value.indexOf('.') != -1) {
				obj.value = '';
				obj.focus();
			}
		}
	}
		//Dejar la línea de patron que se necesite y borrar el resto             
	//patron =/[A-Za-z]/; // Solo acepta letras             
	patron = /\d/; // Solo acepta números       //estaba activa      
	//patron = /\w/; // Acepta números y letras             
	//patron = /\D/; // No acepta números             
	//
	te = String.fromCharCode(tecla);
	
	if(isTecla(tecla)){
		return true;
	}
	
	if (fn == '1') {
		te = obj.value;
	}

	result = patron.test(te);
	if (result == false) {
		if (fn == '1') {
			if (obj.value == '') {
				return true;
			}
			obj.value = '';
			obj.focus();
		}
	}
	return result;
};

function isTecla(tecla){
	if(tecla>0){
		//Teclas de direccionamiento
		if(tecla>=37 && tecla<=40){
			return true;
		}
		return false;
	}
	return true;
};

function validaNumeroPorTipoDoi(e, ent, obj, fn) {

		//document.getElementById("formRegistrar:cboTipoDoi2_input").value;
	var txtCodigo= document.getElementById("formRegistrar:inputHidden").value;
	//alert('txtCodigo ' +txtCodigo);
	//Se captura la tecla presionada             
	var tecla = (document.all) ? e.keyCode : e.which;
	var patron;
	var te;
	var result;
	//Tecla de retroceso (para poder borrar)       
	if (tecla == 8) {
		return true;
	}
	if (ent == '0') {
		if (fn == '1') {
			if (obj.value == '') {
				return true;
			}
			var puntos = obj.value.split('.');
			if (puntos.length > 2) {
				obj.value = '';
				obj.focus();
			}
		}
		if (tecla == 46) {
			if (obj.value.indexOf('.') != -1) {
				return false;
			}
			return true;
		}
	} else {
		if (fn == '1') {
			if (obj.value.indexOf('.') != -1) {
				obj.value = '';
				obj.focus();
			}
		}
	}
	if(txtCodigo=='N'){
		//Dejar la línea de patron que se necesite y borrar el resto             
	//patron =/[A-Za-z]/; // Solo acepta letras             
	 patron = /\d/; // Solo acepta números  
      }else if(txtCodigo=='LN'){
     patron = /\w/; // Acepta números y letras  
      } else if(txtCodigo==''){
     patron = /\d/; // Solo acepta números  
      }
	           
	//patron = /\D/; // No acepta números             
	//
	te = String.fromCharCode(tecla);
	
	if(isTecla(tecla)){
		return true;
	}
	
	if (fn == '1') {
		te = obj.value;
	}
	result = patron.test(te);
	if (result == false) {
		if (fn == '1') {
			if (obj.value == '') {
				return true;
			}
			obj.value = '';
			obj.focus();
		}
	}

	return result;
};

//Funcion que se encarga de la invocacion del nuevo applet de escaneo (NAE) 05.02.2013
function invocarEscaner(urlApplet){
	var winId1;
	//alert("urlApplet==> "+urlApplet);
	//var rutaAppletFinal = document.getElementById("formSolicitudRegistro:linkEscaner").value;
	winId1 = window.open(urlApplet,'NAE', "status=yes,menubar=no,scrollbars=yes,resizable=yes,toolbar=no,screenX=0,screenY=0,left=0,top=0,width=100,height=100" );
	return false;
}
