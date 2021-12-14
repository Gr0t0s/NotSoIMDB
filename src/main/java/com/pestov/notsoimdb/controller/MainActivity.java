package com.pestov.notsoimdb.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.pestov.notsoimdb.R;
import com.pestov.notsoimdb.model.CrewMember;
import com.pestov.notsoimdb.model.Film;
import com.pestov.notsoimdb.model.Gender;
import com.pestov.notsoimdb.model.Genre;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
	
	SQLiteDatabase db;
	FragmentMain fragmentMain;
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.US);
	private final String DB_LOG_TAG = "SQLiteDB";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fragmentMain = new FragmentMain();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frameMain, fragmentMain, "FragmentMain")
				.commit();
		new GetFilmsTask(fragmentMain).execute();
	}
	
	public void getFilmsByCrewMember(FragmentCrewMember fragmentCrewMember)
	{
		new GetFilmsByCrewMemberTask(fragmentCrewMember).execute();
	}
	
	public void getFilmsByTitle(FragmentMain fragmentMain)
	{
		new GetFilmsByTitleTask(fragmentMain).execute();
	}
	
	private class SQLiteConnector extends SQLiteOpenHelper
	{
		
		public SQLiteConnector(Context context, String name, int version)
		{
			super(context, name, null, version);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			try
			{
				db.execSQL("create table Genders (_id integer primary key autoincrement, gender varchar(16) not null);");
				db.execSQL("create table Genres (_id integer primary key autoincrement, genre varchar(16) not null);");
				db.execSQL("create table CrewMembers (_id integer primary key autoincrement, name varchar(128) not null, photoFileName varchar(64), genderId integer not null, birthDate integer not null, deathDate integer, bio text not null, foreign key (genderId) references Genders(_id));");
				db.execSQL("create table Films (_id integer primary key autoincrement, title varchar(128) not null, posterFileName varchar(64) not null, releaseDate integer not null, budget integer, gross integer, description text not null, runtime integer not null);");
				db.execSQL("create table GenreLists (filmId integer, genreId integer, primary key (filmId, genreId), foreign key (filmId) references Films(_id), foreign key (genreId) references Genres(_id));");
				db.execSQL("create table DirectorLists (filmId integer, crewMemberId integer, primary key (filmId, crewMemberId), foreign key (filmId) references Films(_id), foreign key (crewMemberId) references CrewMembers(_id));");
				db.execSQL("create table ActorLists (filmId integer, crewMemberId integer, primary key (filmId, crewMemberId), foreign key (filmId) references Films(_id), foreign key (crewMemberId) references CrewMembers(_id));");
				db.execSQL("create table WriterLists (filmId integer, crewMemberId integer, primary key (filmId, crewMemberId), foreign key (filmId) references Films(_id), foreign key (crewMemberId) references CrewMembers(_id));");
			}
			catch(Exception e)
			{
				Log.d(DB_LOG_TAG, "Exception creating tables", e);
			}
			db.beginTransaction();
			try
			{
				//---ADD data to DB---
				ContentValues aContentValues = new ContentValues();
				//Genders
				for (Gender gender : Gender.values())
				{
					aContentValues.put("gender", String.valueOf(gender));
					db.insertOrThrow("Genders", null, aContentValues);
				}
				//Genres
				aContentValues.clear();
				for (Genre genre : Genre.values())
				{
					aContentValues.put("genre", String.valueOf(genre));
					db.insertOrThrow("Genres", null, aContentValues);
				}
				aContentValues.clear();
				//CrewMembers
				XmlPullParser xpp = XmlPullParserFactory.newInstance().newPullParser();
				xpp.setInput(getAssets().open("xml/crew_members.xml"), "utf-8");
				int eventType = xpp.getEventType();
				String tagname;
				Cursor result;
				while (eventType != XmlPullParser.END_DOCUMENT)
				{
					tagname = xpp.getName();
					switch (eventType)
					{
						case XmlPullParser.START_TAG:
							switch (tagname)
							{
								case "CrewMembers":
								case "CrewMember":
									break;
								case "gender":
									result = db.rawQuery("select _id from Genders where gender like '" + xpp.nextText() + "';", null);
									result.moveToNext();
									aContentValues.put("genderId", result.getInt(result.getColumnIndexOrThrow("_id")));
									result.close();
									break;
								case "birthDate":
								case "deathDate":
									aContentValues.put(tagname, sdf.parse(xpp.nextText()).getTime());
									break;
								default:
									aContentValues.put(tagname, xpp.nextText());
									break;
							}
							break;
						case XmlPullParser.END_TAG:
							tagname = xpp.getName();
							if (tagname.equals("CrewMember"))
							{
								db.insertOrThrow("CrewMembers", null, aContentValues);
								aContentValues.clear();
							}
					}
					eventType = xpp.next();
				}
				//Films
				ContentValues aContentValuesTmp = new ContentValues();
				ArrayList<ContentValues> aContentValuesGenreList = new ArrayList<>();
				ArrayList <ContentValues> aContentValuesDirectorList = new ArrayList<>();
				ArrayList <ContentValues> aContentValuesActorList = new ArrayList<>();
				ArrayList <ContentValues> aContentValuesWriterList = new ArrayList<>();
				xpp.setInput(getAssets().open("xml/films.xml"), "utf-8");
				eventType = xpp.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT)
				{
					tagname = xpp.getName();
					switch (eventType)
					{
						case XmlPullParser.START_TAG:
							switch (tagname)
							{
								case "Films":
								case "Film":
									break;
								case "releaseDate":
									aContentValues.put(tagname, sdf.parse(xpp.nextText()).getTime());
									break;
								case "budget":
								case "gross":
									aContentValues.put(tagname, Long.valueOf(xpp.nextText()));
									break;
								case "genre":
									result = db.rawQuery("select _id from Genres where genre like '" + xpp.nextText() + "';", null);
									result.moveToNext();
									aContentValuesTmp.put("genreId", result.getInt(result.getColumnIndexOrThrow("_id")));
									aContentValuesGenreList.add(new ContentValues(aContentValuesTmp));
									aContentValuesTmp.clear();
									result.close();
									break;
								case "actor":
								case "director":
								case "writer":
									result = db.rawQuery("select _id from CrewMembers where name like '" + xpp.nextText() + "';", null);
									result.moveToNext();
									aContentValuesTmp.put("crewMemberId", result.getInt(result.getColumnIndexOrThrow("_id")));
									switch (tagname)
									{
										case "actor":
											aContentValuesActorList.add(new ContentValues(aContentValuesTmp));
											break;
										case "director":
											aContentValuesDirectorList.add(new ContentValues(aContentValuesTmp));
											break;
										case "writer":
											aContentValuesWriterList.add(new ContentValues(aContentValuesTmp));
											break;
									}
									aContentValuesTmp.clear();
									result.close();
									break;
								case "runtime":
									aContentValues.put(tagname, Integer.valueOf(xpp.nextText()));
									break;
								default:
									aContentValues.put(tagname, xpp.nextText());
									break;
							}
							break;
						case XmlPullParser.END_TAG:
							tagname = xpp.getName();
							if (tagname.equals("Film"))
							{
								long filmId = db.insertOrThrow("Films", null, aContentValues);
								for (ContentValues iterator : aContentValuesGenreList)
								{
									iterator.put("filmId", filmId);
									db.insertOrThrow("GenreLists", null, iterator);
								}
								for (ContentValues iterator : aContentValuesActorList)
								{
									iterator.put("filmId", filmId);
									db.insertOrThrow("ActorLists", null, iterator);
								}
								for (ContentValues iterator : aContentValuesDirectorList)
								{
									iterator.put("filmId", filmId);
									db.insertOrThrow("DirectorLists", null, iterator);
								}
								for (ContentValues iterator : aContentValuesWriterList)
								{
									iterator.put("filmId", filmId);
									db.insertOrThrow("WriterLists", null, iterator);
								}
								aContentValuesGenreList.clear();
								aContentValuesActorList.clear();
								aContentValuesDirectorList.clear();
								aContentValuesWriterList.clear();
								aContentValues.clear();
							}
					}
					eventType = xpp.next();
				}
				db.setTransactionSuccessful();
			}
			catch (Exception e)
			{
				Log.d(DB_LOG_TAG, "Exception inserting values", e);
			}
			finally
			{
				db.endTransaction();
			}
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			if(newVersion==2 && oldVersion==1)
			{
				//Оновлення таблиць в разі застарілої версії бази
			}
		}
	}
	
	public class GetFilmsTask extends AsyncTask<Void, Void, Void>
	{
		
		private FragmentMain fragmentMain;
		private ArrayList<Film> films;
		
		public GetFilmsTask(FragmentMain fragmentMain)
		{
			this.fragmentMain = fragmentMain;
		}
		
		@RequiresApi(api = Build.VERSION_CODES.O)
		@Override
		protected Void doInBackground(Void... voids)
		{
			films = queryFilms("select distinct _id, title, posterFileName, releaseDate, budget, gross, description, runtime from Films order by releaseDate desc;", null);
			//Deleting DB for testing purposes
			//TODO: Remove after testing
			//deleteDatabase("NotSoIMDb");
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused)
		{
			if (films != null)
				fragmentMain.fillEntries(films);
		}
		
	}
	
	public class GetFilmsByCrewMemberTask extends AsyncTask<Void, Void, Void>
	{
		
		private FragmentCrewMember fragmentCrewMember;
		private ArrayList<Film> films;
		
		public GetFilmsByCrewMemberTask(FragmentCrewMember fragmentCrewMember)
		{
			this.fragmentCrewMember = fragmentCrewMember;
		}
		
		@RequiresApi(api = Build.VERSION_CODES.O)
		@Override
		protected Void doInBackground(Void... voids)
		{
			String crewMemberId = String.valueOf(fragmentCrewMember.getCrewMember().getId());
			films = queryFilms("select distinct Films._id, title, posterFileName, releaseDate, budget, gross, description, runtime from Films join ActorLists on Films._id = ActorLists.filmId join DirectorLists on Films._id = DirectorLists.filmId join WriterLists on Films._id = WriterLists.filmId where ActorLists.crewMemberId = ? or DirectorLists.crewMemberId = ? or WriterLists.crewMemberId = ? order by releaseDate desc;", new String[]{crewMemberId, crewMemberId, crewMemberId});
			//Deleting DB for testing purposes
			//TODO: Remove after testing
			//deleteDatabase("NotSoIMDb");
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused)
		{
			if (films != null)
				fragmentCrewMember.fillEntries(films);
		}
		
	}
	
	public class GetFilmsByTitleTask extends AsyncTask<Void, Void, Void>
	{
		
		private FragmentMain fragmentMain;
		private ArrayList<Film> films;
		
		public GetFilmsByTitleTask(FragmentMain fragmentMain)
		{
			this.fragmentMain = fragmentMain;
		}
		
		@RequiresApi(api = Build.VERSION_CODES.O)
		@Override
		protected Void doInBackground(Void... voids)
		{
			films = queryFilms("select distinct _id, title, posterFileName, releaseDate, budget, gross, description, runtime from Films where title like '%" + fragmentMain.getQueryString() + "%' order by releaseDate desc;", null);
			//Deleting DB for testing purposes
			//TODO: Remove after testing
			//deleteDatabase("NotSoIMDb");
			return null;
		}
		
		@Override
		protected void onPostExecute(Void unused)
		{
			if (films != null)
				fragmentMain.fillEntries(films);
		}
		
	}
	
	@RequiresApi(api = Build.VERSION_CODES.O)
	public ArrayList<Film> queryFilms(String query, String[] args)
	{
		SQLiteConnector connector = new SQLiteConnector(MainActivity.this, "NotSoIMDb", 1); //контекст поточного Актівіті, назва бази, версія
		Cursor result, result1;
		ArrayList<Film> films = new ArrayList<>();
		try
		{
			SQLiteDatabase db = connector.getReadableDatabase();
			ArrayList<Genre> genres = new ArrayList<>();
			ArrayList<CrewMember> actors = new ArrayList<>();
			ArrayList<CrewMember> directors = new ArrayList<>();
			ArrayList<CrewMember> writers = new ArrayList<>();
			result = db.rawQuery(query, args);
			Log.d("TEST", query);
			while (result.moveToNext())
			{
				//Спочатку беремо індекси полів
				int idIndex = result.getColumnIndexOrThrow("_id");
				int titleIndex = result.getColumnIndexOrThrow("title");
				int posterFileNameIndex = result.getColumnIndexOrThrow("posterFileName");
				int releaseDateIndex = result.getColumnIndexOrThrow("releaseDate");
				int budgetIndex = result.getColumnIndexOrThrow("budget");
				int grossIndex = result.getColumnIndexOrThrow("gross");
				int descriptionIndex = result.getColumnIndexOrThrow("description");
				int runtimeIndex = result.getColumnIndexOrThrow("runtime");
				//Витягуємо дані з полів за індексом
				//Getting the genres
				result1 = db.rawQuery("select genre from Films join GenreLists on Films._id = GenreLists.filmId join Genres on GenreLists.genreId = Genres._id where Films._id = " + result.getLong(idIndex) + ";", null);
				while (result1.moveToNext())
				{
					int genreIndex = result1.getColumnIndexOrThrow("genre");
					genres.add(Genre.valueOf(result1.getString(genreIndex)));
				}
				result1.close();
				//Getting the actors
				result1 = db.rawQuery("select CrewMembers._id, name, photoFileName, gender, birthDate, deathDate, bio from Films join ActorLists on Films._id = ActorLists.filmId join CrewMembers on ActorLists.crewMemberId = CrewMembers._id join Genders on CrewMembers.genderId = Genders._id where Films._id = " + result.getLong(idIndex) + ";", null);
				while (result1.moveToNext())
				{
					int crewMemberIdIndex = result.getColumnIndexOrThrow("_id");
					int nameIndex = result1.getColumnIndexOrThrow("name");
					int photoFileNameIndex = result1.getColumnIndexOrThrow("photoFileName");
					int genderIndex = result1.getColumnIndexOrThrow("gender");
					int birthDateIndex = result1.getColumnIndexOrThrow("birthDate");
					int deathDateIndex = result1.getColumnIndexOrThrow("deathDate");
					int bioIndex = result1.getColumnIndexOrThrow("bio");
					actors.add(new CrewMember(
							result1.getLong(crewMemberIdIndex),
							result1.getString(nameIndex),
							Drawable.createFromStream(getAssets().open("images/" + result1.getString(photoFileNameIndex)), result1.getString(photoFileNameIndex)),
							Gender.valueOf(result1.getString(genderIndex)),
							Date.from(Instant.ofEpochMilli(result1.getLong(birthDateIndex))),
							result1.getLong(deathDateIndex) == 0 ? null : Date.from(Instant.ofEpochMilli(result1.getLong(deathDateIndex))),
							result1.getString(bioIndex))
					);
				}
				result1.close();
				//Getting the directors
				result1 = db.rawQuery("select CrewMembers._id, name, photoFileName, gender, birthDate, deathDate, bio from Films join DirectorLists on Films._id = DirectorLists.filmId join CrewMembers on DirectorLists.crewMemberId = CrewMembers._id join Genders on CrewMembers.genderId = Genders._id where Films._id = " + result.getLong(idIndex) + ";", null);
				while (result1.moveToNext())
				{
					int crewMemberIdIndex = result.getColumnIndexOrThrow("_id");
					int nameIndex = result1.getColumnIndexOrThrow("name");
					int photoFileNameIndex = result1.getColumnIndexOrThrow("photoFileName");
					int genderIndex = result1.getColumnIndexOrThrow("gender");
					int birthDateIndex = result1.getColumnIndexOrThrow("birthDate");
					int deathDateIndex = result1.getColumnIndexOrThrow("deathDate");
					int bioIndex = result1.getColumnIndexOrThrow("bio");
					directors.add(new CrewMember(
							result1.getLong(crewMemberIdIndex),
							result1.getString(nameIndex),
							Drawable.createFromStream(getAssets().open("images/" + result1.getString(photoFileNameIndex)), result1.getString(photoFileNameIndex)),
							Gender.valueOf(result1.getString(genderIndex)),
							Date.from(Instant.ofEpochMilli(result1.getLong(birthDateIndex))),
							result1.getLong(deathDateIndex) == 0 ? null : Date.from(Instant.ofEpochMilli(result1.getLong(deathDateIndex))),
							result1.getString(bioIndex))
					);
				}
				result1.close();
				//Getting the writers
				result1 = db.rawQuery("select CrewMembers._id, name, photoFileName, gender, birthDate, deathDate, bio from Films join WriterLists on Films._id = WriterLists.filmId join CrewMembers on WriterLists.crewMemberId = CrewMembers._id join Genders on CrewMembers.genderId = Genders._id where Films._id = " + result.getLong(idIndex) + ";", null);
				while (result1.moveToNext())
				{
					int crewMemberIdIndex = result.getColumnIndexOrThrow("_id");
					int nameIndex = result1.getColumnIndexOrThrow("name");
					int photoFileNameIndex = result1.getColumnIndexOrThrow("photoFileName");
					int genderIndex = result1.getColumnIndexOrThrow("gender");
					int birthDateIndex = result1.getColumnIndexOrThrow("birthDate");
					int deathDateIndex = result1.getColumnIndexOrThrow("deathDate");
					int bioIndex = result1.getColumnIndexOrThrow("bio");
					writers.add(new CrewMember(
							result1.getLong(crewMemberIdIndex),
							result1.getString(nameIndex),
							Drawable.createFromStream(getAssets().open("images/" + result1.getString(photoFileNameIndex)), result1.getString(photoFileNameIndex)),
							Gender.valueOf(result1.getString(genderIndex)),
							Date.from(Instant.ofEpochMilli(result1.getLong(birthDateIndex))),
							result1.getLong(deathDateIndex) == 0 ? null : Date.from(Instant.ofEpochMilli(result1.getLong(deathDateIndex))),
							result1.getString(bioIndex))
					);
				}
				result1.close();
				//Creating Film objects
				films.add(new Film(
						result.getString(titleIndex),
						Drawable.createFromStream(getAssets().open("images/" + result.getString(posterFileNameIndex)), result.getString(posterFileNameIndex)),
						Date.from(Instant.ofEpochMilli(result.getLong(releaseDateIndex))),
						result.getLong(budgetIndex),
						result.getLong(grossIndex),
						new ArrayList<>(genres),
						new ArrayList<>(actors),
						new ArrayList<>(directors),
						new ArrayList<>(writers),
						result.getString(descriptionIndex),
						Duration.ofMinutes(result.getInt(runtimeIndex))
				));
				genres.clear();
				actors.clear();
				directors.clear();
				writers.clear();
			}
			result.close();
		}
		catch (Exception e)
		{
			Log.d(DB_LOG_TAG, "Error reading DB", e);
			films = null;
		}
		if (db != null)
			db.close();
		return films;
	}
	
}