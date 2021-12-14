package com.pestov.notsoimdb.model;

import android.graphics.drawable.Drawable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;

public class Film
{
	private String title;
	private Drawable poster;
	private Date releaseDate;
	private long budget;
	private long gross;
	private ArrayList<Genre> genres;
	private ArrayList<CrewMember> actors;
	private ArrayList<CrewMember> directors;
	private ArrayList<CrewMember> writers;
	private String description;
	private Duration runtime;

	public Film(String title, Drawable poster, Date releaseDate, long budget, long gross, ArrayList<Genre> genres, ArrayList<CrewMember> actors, ArrayList<CrewMember> directors, ArrayList<CrewMember> writers, String description, Duration runtime)
	{
		this.title = title;
		this.poster = poster;
		this.releaseDate = releaseDate;
		this.budget = budget;
		this.gross = gross;
		this.genres = genres;
		this.actors = actors;
		this.directors = directors;
		this.writers = writers;
		this.description = description;
		this.runtime = runtime;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public Drawable getPoster()
	{
		return poster;
	}

	public void setPoster(Drawable poster)
	{
		this.poster = poster;
	}

	public Date getReleaseDate()
	{
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate)
	{
		this.releaseDate = releaseDate;
	}

	public long getBudget()
	{
		return budget;
	}

	public void setBudget(long budget)
	{
		this.budget = budget;
	}

	public long getGross()
	{
		return gross;
	}

	public void setGross(long gross)
	{
		this.gross = gross;
	}

	public ArrayList<Genre> getGenres()
	{
		return genres;
	}

	public void setGenres(ArrayList<Genre> genres)
	{
		this.genres = genres;
	}

	public ArrayList<CrewMember> getActors()
	{
		return actors;
	}

	public void setActors(ArrayList<CrewMember> actors)
	{
		this.actors = actors;
	}

	public ArrayList<CrewMember> getDirectors()
	{
		return directors;
	}

	public void setDirectors(ArrayList<CrewMember> directors)
	{
		this.directors = directors;
	}

	public ArrayList<CrewMember> getWriters()
	{
		return writers;
	}

	public void setWriters(ArrayList<CrewMember> writers)
	{
		this.writers = writers;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Duration getRuntime()
	{
		return runtime;
	}

	public void setRuntime(Duration runtime)
	{
		this.runtime = runtime;
	}
	
	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder("Film{");
		sb.append("title='").append(title).append('\'');
		sb.append(", poster=").append(poster);
		sb.append(", releaseDate=").append(releaseDate);
		sb.append(", budget=").append(budget);
		sb.append(", gross=").append(gross);
		sb.append(", genres=").append(genres);
		sb.append(", actors=").append(actors);
		sb.append(", directors=").append(directors);
		sb.append(", writers=").append(writers);
		sb.append(", description='").append(description).append('\'');
		sb.append(", runtime=").append(runtime);
		sb.append('}');
		return sb.toString();
	}
}
