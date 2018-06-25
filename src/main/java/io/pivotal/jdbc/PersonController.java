package io.pivotal.jdbc;

import java.net.URI;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class PersonController {

	private final PersonRepository repository;
	
	@GetMapping("/person")
	public ResponseEntity<Stream<Person>> listPeople() {
		return ResponseEntity.ok().body(repository.allPeople());
	}
	
	@GetMapping("/person/{id}")
	public ResponseEntity<Person> getPerson(@PathVariable("id") UUID id) {
		Person result = repository.getPerson(id);
		return result != null 
				? ResponseEntity.ok().body(result)
						: ResponseEntity.notFound().build();
	}
	
	@PostMapping("/person")
	public ResponseEntity<String> savePerson(@RequestBody Person person) {
		URI uri = 
				UriComponentsBuilder
					.fromPath("/person/{id}")
					.buildAndExpand(repository.savePerson(person))
					.toUri();
		return ResponseEntity.created(uri).build();
	}
}
