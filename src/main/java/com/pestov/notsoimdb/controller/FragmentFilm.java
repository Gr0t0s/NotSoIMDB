package com.pestov.notsoimdb.controller;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.pestov.notsoimdb.R;
import com.pestov.notsoimdb.model.CrewMember;
import com.pestov.notsoimdb.model.Film;
import com.pestov.notsoimdb.model.Genre;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FragmentFilm extends Fragment implements View.OnClickListener, View.OnScrollChangeListener
{
	private Film film;
	private final SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
	private final int CREW_MEMBER_TV_ID_RES = 1;
	private boolean tvDescriptionExpanded = false;
	
	private ScrollView svFilm;
	private TextView tvTitle;
	private ImageView ivPoster;
	private TextView tvReleaseDate;
	private LinearLayout llGenres;
	private TextView tvDescription;
	private TextView tvExpandable;
	private LinearLayout llDirectors;
	private LinearLayout llWriters;
	private LinearLayout llActors;
	private TextView tvRuntime;
	private TextView tvBudget;
	private TextView tvGross;
	private MainActivity mainActivity;
	
	public FragmentFilm()
	{
		// Required empty public constructor
	}
	
	@RequiresApi(api = Build.VERSION_CODES.O)
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_film, container, false);
		//Hide the status and navigation bars
		view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE);
		svFilm = view.findViewById(R.id.svFilm);
		tvTitle = view.findViewById(R.id.tvTitle);
		ivPoster = view.findViewById(R.id.ivPoster);
		tvReleaseDate = view.findViewById(R.id.tvReleaseDate);
		llGenres = view.findViewById(R.id.llGenres);
		tvDescription = view.findViewById(R.id.tvDescription);
		tvExpandable = view.findViewById(R.id.tvExpandable);
		llDirectors = view.findViewById(R.id.llDirectors);
		llWriters = view.findViewById(R.id.llWriters);
		llActors = view.findViewById(R.id.llActors);
		tvRuntime = view.findViewById(R.id.tvRuntime);
		tvBudget = view.findViewById(R.id.tvBudget);
		tvGross = view.findViewById(R.id.tvGross);
		
		svFilm.setOnScrollChangeListener(this);
		tvTitle.setText(film.getTitle());
		ivPoster.setImageDrawable(film.getPoster());
		tvReleaseDate.setText(sdf.format(film.getReleaseDate()));
		//Adding genres
		for (Genre genre : film.getGenres())
		{
			TextView tv = new TextView(getContext());
			tv.setText(genre.toString());
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			tv.setTextColor(getResources().getColor(R.color.text));
			llGenres.addView(tv);
		}
		tvDescription.setText(film.getDescription());
		tvDescription.setOnClickListener(this);
		//Adding directors
		for (CrewMember crewMember : film.getDirectors())
		{
			TextView tv = new TextView(getContext());
			tv.setId(CREW_MEMBER_TV_ID_RES);
			tv.setText(crewMember.getName());
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			tv.setTextColor(getResources().getColor(R.color.text_accent));
			tv.setTag(crewMember);
			tv.setOnClickListener(this);
			llDirectors.addView(tv);
		}
		//Adding writers
		for (CrewMember crewMember : film.getWriters())
		{
			TextView tv = new TextView(getContext());
			tv.setId(CREW_MEMBER_TV_ID_RES);
			tv.setText(crewMember.getName());
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			tv.setTextColor(getResources().getColor(R.color.text_accent));
			tv.setTag(crewMember);
			tv.setOnClickListener(this);
			llWriters.addView(tv);
		}
		//Adding actors
		for (CrewMember crewMember : film.getActors())
		{
			TextView tv = new TextView(getContext());
			tv.setId(CREW_MEMBER_TV_ID_RES);
			tv.setText(crewMember.getName());
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
			tv.setTextColor(getResources().getColor(R.color.text_accent));
			tv.setTag(crewMember);
			tv.setOnClickListener(this);
			llActors.addView(tv);
		}
		tvRuntime.setText(new StringBuilder()
				.append(film.getRuntime().toHours())
				.append(" hours ")
				.append(film.getRuntime().toMinutes() - film.getRuntime().toHours() * 60)   //Would have made it simpler with toXxxPart(), if not for it requiring API level 31
				.append(" minutes")
		);
		NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
		currencyFormatter.setCurrency(Currency.getInstance("USD"));
		currencyFormatter.setMaximumFractionDigits(0);
		tvBudget.setText(currencyFormatter.format(film.getBudget()));
		tvGross.setText(currencyFormatter.format(film.getGross()));
		mainActivity = (MainActivity) requireActivity();
		return view;
	}
	
	
	public void setFilm(Film film)
	{
		this.film = film;
	}
	
	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.tvDescription:
				if (tvDescriptionExpanded)
				{
					tvDescription.setMaxLines(5);
					tvExpandable.setVisibility(View.VISIBLE);
				}
				else
				{
					tvDescription.setMaxLines(200);
					tvExpandable.setVisibility(View.GONE);
				}
				tvDescriptionExpanded = !tvDescriptionExpanded;
				break;
			case CREW_MEMBER_TV_ID_RES:
				FragmentCrewMember fragmentCrewMember = new FragmentCrewMember();
				fragmentCrewMember.setCrewMember((CrewMember) v.getTag());
				FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
				Fragment fragmentFilm = fragmentManager.findFragmentByTag("FragmentFilm");
				fragmentManager.beginTransaction()
						.hide(fragmentFilm)
						.add(R.id.frameMain, fragmentCrewMember, "FragmentCrewMember")
						.addToBackStack(null)
						.commit();
				break;
		}
	}
	
	@Override
	public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
	{
		//Hide the status and navigation bars
		v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE);
	}
	
}
