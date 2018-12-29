package io.pivotal.jdbc;

import java.util.UUID;
import javax.annotation.Generated;

public class Person {

	private UUID id;
	private String firstName;
	private String lastName;
	private Integer age;
	
	@Generated("SparkTools")
	private Person(Builder builder) {
		this.id = builder.id;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.age = builder.age;
	}
	
	/**
	 * Creates builder to build {@link Person}.
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}
	/**
	 * Builder to build {@link Person}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private UUID id;
		private String firstName;
		private String lastName;
		private Integer age;

		private Builder() {
		}

		public Builder withId(UUID id) {
			this.id = id;
			return this;
		}

		public Builder withFirstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		public Builder withLastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		public Builder withAge(Integer age) {
			this.age = age;
			return this;
		}

		public Person build() {
			return new Person(this);
		}
	}
	
	public UUID getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Integer getAge() {
		return age;
	}
	
	
}
