package com.pestov.notsoimdb.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.pestov.notsoimdb.R;
import com.pestov.notsoimdb.model.Film;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FragmentMain extends Fragment implements View.OnClickListener, View.OnFocusChangeListener, View.OnScrollChangeListener
{
	
	private EditText etSearch;
	private Button btnSearch;
	private ScrollView svFilms;
	private LinearLayout llFilmList;
	private ProgressBar pbFilmLoading;
	private String queryString;
	private boolean inSearch;
	private MainActivity mainActivity;
	
	public FragmentMain()
	{
		// Required empty public constructor
	}
	
	@RequiresApi(api = Build.VERSION_CODES.M)
	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
	{
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_main, container, false);
		//Hide the status and navigation bars
		view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE);
		etSearch = view.findViewById(R.id.etSearch);
		btnSearch = view.findViewById(R.id.btnSearch);
		svFilms = view.findViewById(R.id.svFilms);
		llFilmList = view.findViewById(R.id.llFilmList);
		pbFilmLoading = view.findViewById(R.id.pbFilmLoading);
		etSearch.setOnFocusChangeListener(this);
		btnSearch.setOnClickListener(this);
		svFilms.setOnScrollChangeListener(this);
		inSearch = false;
		mainActivity = (MainActivity) requireActivity();
		mainActivity.getFilms(this);
		return view;
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
			llFilmList.addView(frameLayout);
			//Replacing it with a new entry
			FragmentFilmEntry fragmentFilmEntry = new FragmentFilmEntry();
			fragmentFilmEntry.setFilm(film);
			ft.replace(id, fragmentFilmEntry);
			id++;
		}
		ft.commit();
		pbFilmLoading.setVisibility(View.GONE);
	}
	
	@SuppressLint("NonConstantResourceId")
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btnSearch:
				queryString = etSearch.getText().toString();
				etSearch.clearFocus();
				//Hide keyboard
				InputMethodManager inputMethodManager = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
				if (queryString.equals(""))
				{
					if (inSearch)
					{
						llFilmList.removeAllViews();
						pbFilmLoading.setVisibility(View.VISIBLE);
						mainActivity.getFilms(this);
						inSearch = false;
					}
					break;
				}
				llFilmList.removeAllViews();
				pbFilmLoading.setVisibility(View.VISIBLE);
				mainActivity.getFilmsByTitle(this);
				inSearch = true;
				break;
		}
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus)
	{
		//Hide the status and navigation bars
		v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE);
		switch (v.getId())
		{
			case R.id.etSearch:
				if (etSearch.isFocused())
				{
					etSearch.setText("");
					etSearch.setTextColor(getResources().getColor(R.color.text));
					btnSearch.setVisibility(View.VISIBLE);
				}
				else
				{
					if (etSearch.getText().toString().equals(""))
						etSearch.setText(getResources().getText(R.string.search));
					etSearch.setTextColor(getResources().getColor(R.color.gray));
					btnSearch.setVisibility(View.GONE);
				}
				break;
		}
	}
	
	public String getQueryString()
	{
		return queryString;
	}
	
	@Override
	public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY)
	{
		//Hide the status and navigation bars
		v.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE);
	}
	
}
