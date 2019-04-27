package com.colomertrave.springboot.datajpa.app.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;

public class PageRender<T> {

	private String url;
	private Page<T> page;
	
	private int totalPaginas;
	private int numElementosPorPagina;
	private int paginaActual;
	
	private List<PageItem> paginas;
	
	public PageRender(String url, Page<T> page) {
		this.url = url;
		this.page = page;
		paginas = new ArrayList<>();
		
		totalPaginas = page.getTotalPages();
		numElementosPorPagina = page.getSize();
		paginaActual = page.getNumber() + 1; // +1 porque su valor inicial es 0
		
		controlarCasuisticaPaginacion();
	}

	private void controlarCasuisticaPaginacion() {
		
		int desde, hasta;
		
		// Por ejemplo si tenemos 5 páginas y queremos ver 10 elementos por página, entonces...
		// el paginador mostrará desde la página 1 hasta la página 5
		if (totalPaginas <= numElementosPorPagina) {
			desde = 1;
			hasta = totalPaginas;
		} else { // Paginar por rango porque hay muchas páginas
			
			// Por ejemplo si tenemos 20 páginas y estamos en la 6, dado que estamos en la primera mitad del total de páginas
			// el paginador mostrará desde la página 1 hasta la página 20
			if(paginaActual <= numElementosPorPagina / 2) {
				desde = 1;			
			} else if(paginaActual >= totalPaginas + numElementosPorPagina / 2) {
				desde = totalPaginas - numElementosPorPagina + 1;
			} else {
				desde = paginaActual - numElementosPorPagina / 2;
			}
			hasta = numElementosPorPagina;
		}		
		
		for (int i=0; i< hasta; i++)
			paginas.add(new PageItem(desde + i, paginaActual == desde + i));
	}

	public String getUrl() {
		return url;
	}

	public int getTotalPaginas() {
		return totalPaginas;
	}

	public int getPaginaActual() {
		return paginaActual;
	}

	public List<PageItem> getPaginas() {
		return paginas;
	}
	
	public boolean isFirst() {
		return page.isFirst();
	}
	
	public boolean isLast() {
		return page.isLast();
	}
	
	public boolean isHasNext() {
		return page.hasNext();
	}
	
	public boolean isHasPrevious() {
		return page.hasPrevious();
	}
}
