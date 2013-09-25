
package com.bbva.consulta.reniec.util;


/**
 * @author dmclemente
 * @version 1.0
 */
public class Persona {
	private int id;
	private String nombre;
	private String apellidoPaterno;
	private String apellidoMaterno;
	private String nombreCompleto;
	private String numerodocIdentidad;
	private String direccion;	
	private String fechaNac;
	private String telefono;
	private String email;

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}

	/**
	 * @param nombre
	 *            the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @return the apellidoPaterno
	 */
	public String getApellidoPaterno() {
		return apellidoPaterno;
	}

	/**
	 * @param apellidoPaterno
	 *            the apellidoPaterno to set
	 */
	public void setApellidoPaterno(String apellidoPaterno) {
		this.apellidoPaterno = apellidoPaterno;
	}

	/**
	 * @return the apellidoMaterno
	 */
	public String getApellidoMaterno() {
		return apellidoMaterno;
	}

	/**
	 * @param apellidoMaterno
	 *            the apellidoMaterno to set
	 */
	public void setApellidoMaterno(String apellidoMaterno) {
		this.apellidoMaterno = apellidoMaterno;
	}

	/**
	 * @return the nombreCompleto
	 */
	public String getNombreCompleto() {
		return nombreCompleto;
	}

	/**
	 * @param nombreCompleto
	 *            the nombreCompleto to set
	 */
	public void setNombreCompleto(String nombreCompleto) {
		this.nombreCompleto = nombreCompleto;
	}
	
	/**
	 * @return the direccion
	 */
	public String getDireccion() {
		return direccion;
	}

	/**
	 * @param direccion
	 *            the direccion to set
	 */
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	/**
	 * @return the numerodocIdentidad
	 */
	public String getNumerodocIdentidad() {
		return numerodocIdentidad;
	}

	/**
	 * @param numerodocIdentidad the numerodocIdentidad to set
	 */
	public void setNumerodocIdentidad(String numerodocIdentidad) {
		this.numerodocIdentidad = numerodocIdentidad;
	}

	/**
	 * @return the fechaNac
	 */
	public String getFechaNac() {
		return fechaNac;
	}

	/**
	 * @param fechaNac the fechaNac to set
	 */
	public void setFechaNac(String fechaNac) {
		this.fechaNac = fechaNac;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the telefono
	 */
	public String getTelefono() {
		return telefono;
	}

	/**
	 * @param telefono the telefono to set
	 */
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	
}