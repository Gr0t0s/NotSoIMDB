package com.pestov.notsoimdb.model;

import android.graphics.drawable.Drawable;

import java.util.Date;

public class CrewMember
{
	private long id;
	private String name;
	private Drawable photo;
	private Gender gender;
	private Date birthDate;
	private Date deathDate;
	private String bio;
	
	public CrewMember(long id, String name, Drawable photo, Gender gender, Date birthDate, Date deathDate, String bio)
	{
		this.id = id;
		this.name = name;
		this.photo = photo;
		this.gender = gender;
		this.birthDate = birthDate;
		this.deathDate = deathDate;
		this.bio = bio;
	}
	
	public long getId()
	{
		return id;
	}
	
	public CrewMember setId(long id)
	{
		this.id = id;
		return this;
	}
	
	public String getName()
	{
		return name;
	}
	
	public CrewMember setName(String name)
	{
		this.name = name;
		return this;
	}
	
	public Drawable getPhoto()
	{
		return photo;
	}
	
	public CrewMember setPhoto(Drawable photo)
	{
		this.photo = photo;
		return this;
	}
	
	public Gender getGender()
	{
		return gender;
	}
	
	public CrewMember setGender(Gender gender)
	{
		this.gender = gender;
		return this;
	}
	
	public Date getBirthDate()
	{
		return birthDate;
	}
	
	public CrewMember setBirthDate(Date birthDate)
	{
		this.birthDate = birthDate;
		return this;
	}
	
	public Date getDeathDate()
	{
		return deathDate;
	}
	
	public CrewMember setDeathDate(Date deathDate)
	{
		this.deathDate = deathDate;
		return this;
	}
	
	public String getBio()
	{
		return bio;
	}
	
	public CrewMember setBio(String bio)
	{
		this.bio = bio;
		return this;
	}
	
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder("CrewMember{");
		sb.append("name='").append(name).append('\'');
		sb.append(", photo=").append(photo);
		sb.append(", gender=").append(gender);
		sb.append(", birthDate=").append(birthDate);
		sb.append(", deathDate=").append(deathDate);
		sb.append(", bio='").append(bio).append('\'');
		sb.append('}');
		return sb.toString();
	}
	
}
