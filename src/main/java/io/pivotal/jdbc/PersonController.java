package io.pivotal.jdbc;

import java.net.URI;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		try {
			return ResponseEntity.ok().body(repository.getPerson(id));
		} catch (EmptyResultDataAccessException erdae) {
			log.info("Could not find person with id = {}", id);
			return ResponseEntity.notFound().build();
		}
	}
	
	@PostMapping("/person")
	public ResponseEntity<String> savePerson(@RequestBody Person person) {
		try {
			URI uri = 
					UriComponentsBuilder
						.fromPath("/person/{id}")
						.buildAndExpand(repository.savePerson(person))
						.toUri();
			return ResponseEntity.created(uri).build();
		} catch (DataIntegrityViolationException dive) {
			log.info("Could not save person! \n Exception -> \n\t {}", dive.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}
}
