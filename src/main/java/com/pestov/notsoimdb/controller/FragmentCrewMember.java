package com.pestov.notsoimdb.controller;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.pestov.notsoimdb.R;
import com.pestov.notsoimdb.model.CrewMember;
import com.pestov.notsoimdb.model.Film;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FragmentCrewMember extends Fragment implements View.OnClickListener, View.OnScrollChangeListener
{
	
	private CrewMember crewMember;
	private final SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
	private boolean tvBioExpanded = false;
	
	private ScrollView svCrewMember;
	private TextView tvName;
	private ImageView ivPhoto;
	private TextView tvGender;
	private LinearLayout llBirthDate;
	private TextView tvBirthDate;
	private LinearLayout llDeathDate;
	private TextView tvDeathDate;
	private TextView tvBio;
	private TextView tvExpandable;
	private ProgressBar pbFilmLoading;
	private LinearLayout llCrewMemberFilmList;
	
	public FragmentCrewMember()
	{
		// Required empty public constructor
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_crew_member, container, false);
		//Hide the status and navigation bars
		view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE);
		svCrewMember = view.findViewById(R.id.svCrewMember);
		tvName = view.findViewById(R.id.tvName);
		ivPhoto = view.findViewById(R.id.ivPhoto);
		tvGender = view.findViewById(R.id.tvGender);
		llBirthDate = view.findViewById(R.id.llBirthDate);
		tvBirthDate = view.findViewById(R.id.tvBirthDate);
		llDeathDate = view.findViewById(R.id.llDeathDate);
		tvDeathDate = view.findViewById(R.id.tvDeathDate);
		tvBio = view.findViewById(R.id.tvBio);
		tvExpandable = view.findViewById(R.id.tvExpandable);
		pbFilmLoading = view.findViewById(R.id.pbFilmLoading);
		llCrewMemberFilmList = view.findViewById(R.id.llCrewMemberFilmList);
		
		svCrewMember.setOnScrollChangeListener(this);
		tvName.setText(crewMember.getName());
		ivPhoto.setImageDrawable(crewMember.getPhoto());
		tvGender.setText(crewMember.getGender().toString());
		tvBirthDate.setText(sdf.format(crewMember.getBirthDate()));
		if (crewMember.getBirthDate() != null)
		{
			llBirthDate.setVisibility(View.VISIBLE);
			tvBirthDate.setText(sdf.format(crewMember.getBirthDate()));
		}
		if (crewMember.getDeathDate() != null)
		{
			llDeathDate.setVisibility(View.VISIBLE);
			tvDeathDate.setText(sdf.format(crewMember.getDeathDate()));
		}
		tvBio.setText(crewMember.getBio());
		tvBio.setOnClickListener(this);
		MainActivity mainActivity = (MainActivity) getActivity();
		mainActivity.getFilmsByCrewMember(this);
		return view;
	}
	
	public FragmentCrewMember setCrewMember(CrewMember crewMember)
	{
		this.crewMember = crewMember;
		return this;
	}
	
	public CrewMember getCrewMember()
	{
		return crewMember;
	}
	
	public void fillEntries(ArrayList<Film> films)
	{
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		int id = 1;
		for (Film film : films)
		{
			//Adding new fragment container to the list
			FrameLayout frameLayout = new FrameLayout(getContext());
			frameLayout.setId(id);
			llCrewMemberFilmList.addView(frameLayout);
			//Replacing it with a new entry
			FragmentFilmEntry fragmentFilmEntry = new FragmentFilmEntry();
			fragmentFilmEntry.setFilm(film);
			ft.replace(id, fragmentFilmEntry);
			id++;
		}
		ft.commit();
		pbFilmLoading.setVisibility(View.GONE);
	}
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.tvBio:
				if (tvBioExpanded)
				{
					tvBio.setMaxLines(5);
					tvExpandable.setVisibility(View.VISIBLE);
				}
				else
				{
					tvBio.setMaxLines(200);
					tvExpandable.setVisibility(View.GONE);
				}
				tvBioExpanded = !tvBioExpanded;
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
