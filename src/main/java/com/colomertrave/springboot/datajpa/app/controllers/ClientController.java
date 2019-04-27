package com.colomertrave.springboot.datajpa.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.core.io.Resource;
import com.colomertrave.springboot.datajpa.app.models.entities.Client;
import com.colomertrave.springboot.datajpa.app.models.services.IClientService;
import com.colomertrave.springboot.datajpa.app.models.services.IUploadFileService;
import com.colomertrave.springboot.datajpa.app.utils.PageRender;

@Controller

// Guardamos el objeto client en la sesión
// Cada vez que se invoca el create o el edit recupera el cliente y se lo pasa a la vista.
// En el método save se tiene que eliminar la sesión pues ya se ha concluido el ciclo.
@SessionAttributes("client") 

public class ClientController {

	private static final int ROWS_PER_PAGE = 4;

	@Autowired @Qualifier("clientServiceJpa")
	private IClientService clientService;
	
	@Autowired
	private IUploadFileService uploadFileService;
		
	// ******************* PRESENT ALL USER DETAILS WHEN ID FIELD IS CLICKED AT CLIENT LIST PAGE
	@GetMapping(value="/client-details/{id}")
	public String clientDetails(@PathVariable(value="id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		
		Optional<Client> client = clientService.findOne(id);
		
		if (!client.isPresent()) {
			flash.addFlashAttribute("error", "Client ID does not exists in BD!");
			return "redirect:/list";
		}
		
		model.put("client", client.get());
		model.put("title", "Client details: " + client.get().getName());
		
		return "client-details";
	}
	
	// ******************* DOWNLOAD USER PHOTO
	@GetMapping(value="/uploads/{filename:.+}")
	public ResponseEntity<Resource> downloadPhoto(@PathVariable String filename, RedirectAttributes flash) {
		
		Resource recurso = null;
		try {
			recurso = uploadFileService.load(filename);			
		} catch (MalformedURLException e) {
			throw new RuntimeException(e.getMessage());
		}
		
		String attach = "attachment; filename=\"" + recurso.getFilename() + "\"";
		
		return ResponseEntity
				.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, attach)
				.body(recurso);
	}
		
	// ******************* PRESENT FORM CREATE NEW USER
	@RequestMapping(value="/create-form-client")
	public String create(Map<String, Object> model) {
		
		Client client = new Client();
		
		model.put("client", client);
		model.put("title", "Create client form");
		
		return "form-client";
	}
	
	// ******************* PRESENT FORM CREATE/EDIT A CLIENT
	@RequestMapping(value="/edit-form-client/{id}")
	public String edit(@PathVariable(value="id") long id, Map<String, Object> model, RedirectAttributes flash) {
		
		Optional<Client> client = null;
		
		if(id > 0) { // We are editing an existent client
			client = clientService.findOne(id);
			
			if (!client.isPresent()) {
				flash.addFlashAttribute("error", "Client ID does not exists in BD!");
				return "redirect:/list";
			}
		} else {
			flash.addFlashAttribute("error", "Client ID must be a positive integer value");
			return "redirect:/list";
		}			
		
		model.put("client", client.get());
		model.put("title", "Edit client form");
		
		return "form-client";
	}
	
	// ******************* SAVE CLIENT AND SHOW LIST CLIENTS
	@RequestMapping(value="/save-form-client", method=RequestMethod.POST)
	public String save(@Valid Client client, BindingResult result, Model model, 
			@RequestParam("file") MultipartFile photo,
			RedirectAttributes flash, SessionStatus status) 
	{
		
		if(result.hasErrors()) {
			model.addAttribute("title", "Client form");
			return "form-client";
		}
		
		if(!photo.isEmpty()) {
			
			Long cliId = client.getId();
			Client clientBD = clientService.findOne(cliId).get();
			
			String cliPhoto = clientBD.getPhoto();			
			
			// Si el cliente ya tenía una foto, entonces tenemos que eliminar la antigua
			if (cliId != null && cliPhoto != null && cliPhoto.length() > 0) {
				uploadFileService.delete(cliPhoto);
			}
			
			// Hemos de copiar la imagen recibida en la ubicación de las subidas
			String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copy(photo);
				flash.addFlashAttribute("info",
						"Picture " + photo.getOriginalFilename() + " has been uploaded successfully");
				client.setPhoto(uniqueFilename);
			} catch (IOException e) {
				e.printStackTrace();
			}												
		}
		
		clientService.save(client);
		status.setComplete(); // Eliminar el objeto client de la sesión
		flash.addFlashAttribute("success", "Client saved successfully");
		
		return "redirect:list";
	}

	
	
	// ******************* SHOW LIST CLIENTS
	@RequestMapping(value="/list")
	public String list(@RequestParam(name="page", defaultValue="0") int page, Model model) {
		
		// 'page' es la página en curso
		Pageable pageRequest = PageRequest.of(page, ROWS_PER_PAGE);
		Page<Client> clients = clientService.findAll(pageRequest);		
		
		PageRender<Client> pageRender = new PageRender<>("/list", clients);
		
		model.addAttribute("title", "List of clients");
		model.addAttribute("clients", clients);
		model.addAttribute("page", pageRender);
		return "list";
	}
	
	// ****************** DELETE CLIENT
	@RequestMapping(value="/delete-client/{id}", method=RequestMethod.GET)
	public String delete(@PathVariable(value="id") long id, RedirectAttributes flash) {
		
		if(id > 0) {
			
			Client client = clientService.findOne(id).get();
			
			if (client == null) {
				flash.addFlashAttribute("error", "The client ID does not exists in the DB!");
				return "redirect:/list";
			}			
			
			clientService.delete(id);
			flash.addFlashAttribute("success", "Client deleted successfully");
			
			if (uploadFileService.delete(client.getPhoto())) {
				flash.addFlashAttribute("info", "Picture " + client.getPhoto() + " has been deleted successfully!");
			}
		}
		
		return "redirect:/list";
	}

}
