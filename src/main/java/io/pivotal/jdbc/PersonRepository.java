package io.pivotal.jdbc;

import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PersonRepository {

	private final JdbcTemplate template;
	
	public PersonRepository(JdbcTemplate template) {
		super();
		this.template = template;
	}

	public Stream<Person> allPeople() {
		String sql = "SELECT * FROM people";
		return template
		        .query(sql, (rs, rowNum) ->
		        	Person.builder()
			            .withId(UUID.fromString(rs.getString("id")))
			        	.withFirstName(rs.getString("first_name"))
			            .withLastName(rs.getString("last_name"))
			            .withAge(rs.getInt("age"))
			            .build()
	            )
		        .stream();
	}

	public UUID savePerson(Person person) {
		String sql = "INSERT INTO people (id, first_name, last_name, age) VALUES (?, ?, ?, ?)";
		UUID key = UUID.randomUUID();
		template
			.update(sql, 
					key, 
					person.getFirstName(), 
					person.getLastName(), 
					person.getAge());
		return key;
	}

	public Person getPerson(UUID personId) {
		String sql = "SELECT * FROM people WHERE id = ?";
		return template
				.queryForObject(sql, new Object[] { personId }, (rs, rownNum) ->
					Person.builder()
			            .withId(UUID.fromString(rs.getString("id")))
			        	.withFirstName(rs.getString("first_name"))
			            .withLastName(rs.getString("last_name"))
			            .withAge(rs.getInt("age"))
			            .build()
				);
	}

}
