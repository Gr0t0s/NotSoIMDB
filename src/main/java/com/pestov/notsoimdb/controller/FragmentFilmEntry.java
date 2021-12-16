package com.pestov.notsoimdb.controller;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.pestov.notsoimdb.R;
import com.pestov.notsoimdb.model.Film;

public class FragmentFilmEntry extends Fragment implements View.OnClickListener
{
	private Film film;
	private TextView tvTitle;
	private ImageView ivPoster;
	private MainActivity mainActivity;
	
	public FragmentFilmEntry()
	{
		// Required empty public constructor
	}
	
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_film_entry, container, false);
		tvTitle = view.findViewById(R.id.tvTitle);
		ivPoster = view.findViewById(R.id.ivPoster);
		tvTitle.setText(film.getTitle());
		ivPoster.setImageDrawable(film.getPoster());
		view.setOnClickListener(this);
		mainActivity = (MainActivity) getActivity();
		return view;
	}
	
	public void setFilm(Film film)
	{
		this.film = film;
	}
	
	@RequiresApi(api = Build.VERSION_CODES.M)
	@Override
	public void onClick(View v)
	{
		FragmentFilm fragmentFilm = new FragmentFilm();
		fragmentFilm.setFilm(film);
		FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.hide(getParentFragment())
				.add(R.id.frameMain, fragmentFilm, "FragmentFilm")
				.addToBackStack(null)
				.commit();
		//Hide keyboard
		InputMethodManager inputMethodManager = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	
}
