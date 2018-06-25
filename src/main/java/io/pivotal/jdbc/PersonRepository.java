package io.pivotal.jdbc;

import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.AllArgsConstructor;

@Repository
@AllArgsConstructor
public class PersonRepository {

	private final JdbcTemplate template;
	
	public Stream<Person> allPeople() {
		String sql = "SELECT * FROM people";
		return template
		        .query(sql, (rs, rowNum) ->
		        	Person.builder()
			            .id(UUID.fromString(rs.getString("id")))
			        	.firstName(rs.getString("first_name"))
			            .lastName(rs.getString("last_name"))
			            .age(rs.getInt("age"))
			            .build()
	            )
		        .stream();
	}

	public UUID savePerson(Person person) {
		String sql = "INSERT INTO people (id, first_name, last_name, age) VALUES (?, ?, ?, ?)";
		UUID key = UUID.randomUUID();
		template
			.update(sql, 
					UUID.randomUUID(), 
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
			            .id(UUID.fromString(rs.getString("id")))
			        	.firstName(rs.getString("first_name"))
			            .lastName(rs.getString("last_name"))
			            .age(rs.getInt("age"))
			            .build()
				);
		
	}

}
